package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.User;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserReactiveRepository extends ReactiveCassandraRepository<User, UUID> {

    /**
     * Trouve un utilisateur par email
     */
    @Query("SELECT * FROM users WHERE email = ?0 ALLOW FILTERING")
    Mono<User> findByEmail(String email);

    /**
     * Vérifie si un email existe
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = ?0 ALLOW FILTERING")
    Mono<Long> countByEmail(String email);

    /**
     * Trouve les utilisateurs par organisation
     */
    @Query("SELECT * FROM users WHERE organization_id = ?0 ALLOW FILTERING")
    Flux<User> findByOrganizationId(UUID organizationId);

    /**
     * Trouve les utilisateurs actifs par organisation
     */
    @Query("SELECT * FROM users WHERE organization_id = ?0 AND is_active = true ALLOW FILTERING")
    Flux<User> findActiveByOrganizationId(UUID organizationId);

    /**
     * Trouve un utilisateur par token de réinitialisation
     */
    @Query("SELECT * FROM users WHERE password_reset_token = ?0 ALLOW FILTERING")
    Mono<User> findByPasswordResetToken(String token);

    /**
     * Trouve un utilisateur par token de vérification email
     */
    @Query("SELECT * FROM users WHERE email_verification_token = ?0 ALLOW FILTERING")
    Mono<User> findByEmailVerificationToken(String token);
}
