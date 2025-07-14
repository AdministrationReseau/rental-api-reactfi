package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.UserRole;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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

    /**
     * Compte les utilisateurs ayant un rôle spécifique
     */
    @Query("SELECT COUNT(*) FROM user_roles WHERE role_id = ?0 AND is_active = true ALLOW FILTERING")
    Mono<Long> countByRoleId(UUID roleId);

    /**
     * Compte les rôles actifs d'un utilisateur
     */
    @Query("SELECT COUNT(*) FROM user_roles WHERE user_id = ?0 AND is_active = true")
    Mono<Long> countActiveByUserId(UUID userId);

    /**
     * Trouve les assignations par organisation
     */
    @Query("SELECT * FROM user_roles WHERE organization_id = ?0 ALLOW FILTERING")
    Flux<UserRole> findByOrganizationId(UUID organizationId);

    /**
     * Trouve les assignations par agence
     */
    @Query("SELECT * FROM user_roles WHERE agency_id = ?0 ALLOW FILTERING")
    Flux<UserRole> findByAgencyId(UUID agencyId);

    /**
     * Trouve les assignations temporaires (avec date d'expiration)
     */
    @Query("SELECT * FROM user_roles WHERE expires_at IS NOT NULL ALLOW FILTERING")
    Flux<UserRole> findTemporaryAssignments();

    /**
     * Trouve les assignations qui expirent bientôt
     */
    @Query("SELECT * FROM user_roles WHERE is_active = true AND expires_at IS NOT NULL AND expires_at <= ?0 AND expires_at > ?1 ALLOW FILTERING")
    Flux<UserRole> findExpiringSoon(LocalDateTime expirationThreshold, LocalDateTime now);

    /**
     * Trouve les assignations expirées
     */
    @Query("SELECT * FROM user_roles WHERE is_active = true AND expires_at IS NOT NULL AND expires_at < ?0 ALLOW FILTERING")
    Flux<UserRole> findExpired(LocalDateTime now);

    /**
     * Trouve les assignations créées par un utilisateur spécifique
     */
    @Query("SELECT * FROM user_roles WHERE assigned_by = ?0 ALLOW FILTERING")
    Flux<UserRole> findByAssignedBy(UUID assignedBy);

    /**
     * Trouve les assignations dans une période donnée
     */
    @Query("SELECT * FROM user_roles WHERE assigned_at >= ?0 AND assigned_at <= ?1 ALLOW FILTERING")
    Flux<UserRole> findByAssignedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Compte les utilisateurs actifs dans une organisation
     */
    @Query("SELECT COUNT(DISTINCT user_id) FROM user_roles WHERE organization_id = ?0 AND is_active = true ALLOW FILTERING")
    Mono<Long> countDistinctActiveUsersByOrganizationId(UUID organizationId);

    /**
     * Trouve les rôles actifs par organisation et utilisateur
     */
    @Query("SELECT * FROM user_roles WHERE user_id = ?0 AND organization_id = ?1 AND is_active = true")
    Flux<UserRole> findActiveByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    /**
     * Vérifie si un utilisateur a un rôle spécifique
     */
    @Query("SELECT COUNT(*) FROM user_roles WHERE user_id = ?0 AND role_id = ?1 AND is_active = true")
    Mono<Long> countActiveByUserIdAndRoleId(UUID userId, UUID roleId);
}
