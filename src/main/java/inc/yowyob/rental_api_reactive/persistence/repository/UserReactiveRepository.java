package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository réactif pour l'entité User (Mis à jour)
 */
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
     * Trouve les utilisateurs actifs uniquement
     */
    @Query("SELECT * FROM users WHERE is_active = true ALLOW FILTERING")
    Flux<User> findActiveUsers();

    /**
     * Trouve les utilisateurs non supprimés
     */
    @Query("SELECT * FROM users WHERE is_deleted = false ALLOW FILTERING")
    Flux<User> findNonDeletedUsers();

    // === RECHERCHES PAR ORGANISATION ===

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
     * Compte les utilisateurs d'une organisation
     */
    @Query("SELECT COUNT(*) FROM users WHERE organization_id = ?0 ALLOW FILTERING")
    Mono<Long> countByOrganizationId(UUID organizationId);

    // === RECHERCHES PAR AGENCE ===

    /**
     * Trouve les utilisateurs par agence
     */
    @Query("SELECT * FROM users WHERE agency_id = ?0 ALLOW FILTERING")
    Flux<User> findByAgencyId(UUID agencyId);

    /**
     * Trouve les utilisateurs actifs par agence
     */
    @Query("SELECT * FROM users WHERE agency_id = ?0 AND is_active = true ALLOW FILTERING")
    Flux<User> findActiveByAgencyId(UUID agencyId);

    /**
     * Compte les utilisateurs d'une agence
     */
    @Query("SELECT COUNT(*) FROM users WHERE agency_id = ?0 ALLOW FILTERING")
    Mono<Long> countByAgencyId(UUID agencyId);

    /**
     * Trouve les utilisateurs d'une organisation sans agence assignée
     */
    @Query("SELECT * FROM users WHERE organization_id = ?0 AND agency_id = null ALLOW FILTERING")
    Flux<User> findUnassignedByOrganizationId(UUID organizationId);

    /**
     * Trouve les utilisateurs par type
     */
    @Query("SELECT * FROM users WHERE user_type = ?0 ALLOW FILTERING")
    Flux<User> findByUserType(UserType userType);

    /**
     * Trouve les utilisateurs par type et organisation
     */
    @Query("SELECT * FROM users WHERE organization_id = ?0 AND user_type = ?1 ALLOW FILTERING")
    Flux<User> findByOrganizationIdAndUserType(UUID organizationId, UserType userType);

    /**
     * Trouve les utilisateurs par type et agence
     */
    @Query("SELECT * FROM users WHERE agency_id = ?0 AND user_type = ?1 ALLOW FILTERING")
    Flux<User> findByAgencyIdAndUserType(UUID agencyId, UserType userType);

    /**
     * Trouve tout le personnel (AGENCY_MANAGER, RENTAL_AGENT, DRIVER)
     */
    @Query("SELECT * FROM users WHERE user_type IN ('AGENCY_MANAGER', 'RENTAL_AGENT', 'DRIVER') ALLOW FILTERING")
    Flux<User> findAllPersonnel();

    /**
     * Trouve le personnel par organisation
     */
    @Query("SELECT * FROM users WHERE organization_id = ?0 AND user_type IN ('AGENCY_MANAGER', 'RENTAL_AGENT', 'DRIVER') ALLOW FILTERING")
    Flux<User> findPersonnelByOrganizationId(UUID organizationId);

    /**
     * Trouve le personnel par agence
     */
    @Query("SELECT * FROM users WHERE agency_id = ?0 AND user_type IN ('AGENCY_MANAGER', 'RENTAL_AGENT', 'DRIVER') ALLOW FILTERING")
    Flux<User> findPersonnelByAgencyId(UUID agencyId);

    // === RECHERCHES PAR INFORMATIONS EMPLOYÉ ===

    /**
     * Trouve un utilisateur par ID employé
     */
    @Query("SELECT * FROM users WHERE employee_id = ?0 ALLOW FILTERING")
    Mono<User> findByEmployeeId(String employeeId);

    /**
     * Trouve les utilisateurs par département
     */
    @Query("SELECT * FROM users WHERE department = ?0 ALLOW FILTERING")
    Flux<User> findByDepartment(String department);

    /**
     * Trouve les utilisateurs par superviseur
     */
    @Query("SELECT * FROM users WHERE supervisor_id = ?0 ALLOW FILTERING")
    Flux<User> findBySupervisorId(UUID supervisorId);

    /**
     * Trouve les utilisateurs par organisation et département
     */
    @Query("SELECT * FROM users WHERE organization_id = ?0 AND department = ?1 ALLOW FILTERING")
    Flux<User> findByOrganizationIdAndDepartment(UUID organizationId, String department);

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

    /**
     * Trouve les utilisateurs verrouillés
     */
    @Query("SELECT * FROM users WHERE locked_until > dateof(now()) ALLOW FILTERING")
    Flux<User> findLockedUsers();

    /**
     * Trouve les utilisateurs qui doivent changer leur mot de passe
     */
    @Query("SELECT * FROM users WHERE must_change_password = true ALLOW FILTERING")
    Flux<User> findUsersRequiringPasswordChange();

    /**
     * Compte les utilisateurs par type dans une organisation
     */
    @Query("SELECT COUNT(*) FROM users WHERE organization_id = ?0 AND user_type = ?1 ALLOW FILTERING")
    Mono<Long> countByOrganizationIdAndUserType(UUID organizationId, UserType userType);

    /**
     * Compte les utilisateurs actifs par organisation
     */
    @Query("SELECT COUNT(*) FROM users WHERE organization_id = ?0 AND is_active = true ALLOW FILTERING")
    Mono<Long> countActiveByOrganizationId(UUID organizationId);

    /**
     * Compte le personnel par organisation
     */
    @Query("SELECT COUNT(*) FROM users WHERE organization_id = ?0 AND user_type IN ('AGENCY_MANAGER', 'RENTAL_AGENT', 'DRIVER') ALLOW FILTERING")
    Mono<Long> countPersonnelByOrganizationId(UUID organizationId);

    /**
     * Compte le personnel par agence
     */
    @Query("SELECT COUNT(*) FROM users WHERE agency_id = ?0 AND user_type IN ('AGENCY_MANAGER', 'RENTAL_AGENT', 'DRIVER') ALLOW FILTERING")
    Mono<Long> countPersonnelByAgencyId(UUID agencyId);

    // === RECHERCHES RÉCENTES ===

    /**
     * Trouve les utilisateurs créés récemment (30 derniers jours)
     */
    @Query("SELECT * FROM users WHERE created_at > dateof(now()) - 2592000000 ALLOW FILTERING")
    Flux<User> findRecentlyCreatedUsers();

    /**
     * Trouve les utilisateurs connectés récemment (7 derniers jours)
     */
    @Query("SELECT * FROM users WHERE last_login_at > dateof(now()) - 604800000 ALLOW FILTERING")
    Flux<User> findRecentlyLoggedInUsers();

    // === MÉTHODES DE MISE À JOUR BULK ===

    /**
     * Active tous les utilisateurs d'une organisation
     */
    @Query("UPDATE users SET is_active = true WHERE organization_id = ?0")
    Mono<Void> activateAllByOrganizationId(UUID organizationId);

    /**
     * Désactive tous les utilisateurs d'une organisation
     */
    @Query("UPDATE users SET is_active = false WHERE organization_id = ?0")
    Mono<Void> deactivateAllByOrganizationId(UUID organizationId);

    /**
     * Désassigne tous les utilisateurs d'une agence
     */
    @Query("UPDATE users SET agency_id = null WHERE agency_id = ?0")
    Mono<Void> unassignAllFromAgency(UUID agencyId);
}
