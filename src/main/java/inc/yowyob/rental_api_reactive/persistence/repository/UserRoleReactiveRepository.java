package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.UserRole;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRoleReactiveRepository extends ReactiveCassandraRepository<UserRole, UUID> {

    /**
     * Trouve les rôles d'un utilisateur
     */
    @Query("SELECT * FROM user_roles WHERE user_id = ?0")
    Flux<UserRole> findByUserId(UUID userId);

    /**
     * Trouve les utilisateurs ayant un rôle spécifique
     */
    @Query("SELECT * FROM user_roles WHERE role_id = ?0 ALLOW FILTERING")
    Flux<UserRole> findByRoleId(UUID roleId);

    /**
     * Trouve les rôles actifs d'un utilisateur
     */
    @Query("SELECT * FROM user_roles WHERE user_id = ?0 AND is_active = true")
    Flux<UserRole> findActiveByUserId(UUID userId);

    /**
     * Supprime tous les rôles d'un utilisateur
     */
    @Query("DELETE FROM user_roles WHERE user_id = ?0")
    Mono<Void> deleteByUserId(UUID userId);

    /**
     * Trouve une relation spécifique utilisateur-rôle
     */
    @Query("SELECT * FROM user_roles WHERE user_id = ?0 AND role_id = ?1")
    Mono<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);
}
