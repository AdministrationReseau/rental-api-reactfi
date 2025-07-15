package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import inc.yowyob.rental_api_reactive.application.dto.OrganizationType;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository réactif pour l'entité Organization
 */
@Repository
public interface OrganizationReactiveRepository extends ReactiveCassandraRepository<Organization, UUID> {

    /**
     * Trouve une organisation par nom
     */
    @Query("SELECT * FROM organizations WHERE name = ?0 ALLOW FILTERING")
    Mono<Organization> findByName(String name);

    /**
     * Trouve les organisations par propriétaire
     */
    @Query("SELECT * FROM organizations WHERE owner_id = ?0 ALLOW FILTERING")
    Flux<Organization> findByOwnerId(UUID ownerId);

    /**
     * Trouve les organisations actives
     */
    @Query("SELECT * FROM organizations WHERE is_active = true ALLOW FILTERING")
    Flux<Organization> findAllActive();

    /**
     * Trouve les organisations vérifiées
     */
    @Query("SELECT * FROM organizations WHERE is_verified = true ALLOW FILTERING")
    Flux<Organization> findAllVerified();

    /**
     * Trouve les organisations actives et vérifiées
     */
    @Query("SELECT * FROM organizations WHERE is_active = true AND is_verified = true ALLOW FILTERING")
    Flux<Organization> findAllActiveAndVerified();

    /**
     * Trouve les organisations par type
     */
    @Query("SELECT * FROM organizations WHERE organization_type = ?0 ALLOW FILTERING")
    Flux<Organization> findByOrganizationType(OrganizationType organizationType);

    /**
     * Trouve les organisations par ville
     */
    @Query("SELECT * FROM organizations WHERE city = ?0 ALLOW FILTERING")
    Flux<Organization> findByCity(String city);

    /**
     * Trouve les organisations par pays
     */
    @Query("SELECT * FROM organizations WHERE country = ?0 ALLOW FILTERING")
    Flux<Organization> findByCountry(String country);

    /**
     * Trouve les organisations par ville et pays
     */
    @Query("SELECT * FROM organizations WHERE city = ?0 AND country = ?1 ALLOW FILTERING")
    Flux<Organization> findByCityAndCountry(String city, String country);

    /**
     * Trouve les organisations par région
     */
    @Query("SELECT * FROM organizations WHERE region = ?0 ALLOW FILTERING")
    Flux<Organization> findByRegion(String region);

    /**
     * Vérifie si un nom d'organisation existe
     */
    @Query("SELECT COUNT(*) FROM organizations WHERE name = ?0 ALLOW FILTERING")
    Mono<Long> countByName(String name);

    /**
     * Vérifie si un numéro d'enregistrement existe
     */
    @Query("SELECT COUNT(*) FROM organizations WHERE registration_number = ?0 ALLOW FILTERING")
    Mono<Long> countByRegistrationNumber(String registrationNumber);

    /**
     * Vérifie si un numéro fiscal existe
     */
    @Query("SELECT COUNT(*) FROM organizations WHERE tax_number = ?0 ALLOW FILTERING")
    Mono<Long> countByTaxNumber(String taxNumber);

    /**
     * Trouve les organisations avec un abonnement expirant bientôt
     */
    @Query("SELECT * FROM organizations WHERE subscription_expires_at > ?0 AND subscription_expires_at <= ?1 ALLOW FILTERING")
    Flux<Organization> findWithExpiringSubscription(LocalDateTime now, LocalDateTime inThirtyDays);

    /**
     * Trouve les organisations avec un abonnement expiré
     */
    @Query("SELECT * FROM organizations WHERE subscription_expires_at < ?0 ALLOW FILTERING")
    Flux<Organization> findWithExpiredSubscription(LocalDateTime now);

    /**
     * Trouve les organisations par plan d'abonnement
     */
    @Query("SELECT * FROM organizations WHERE subscription_plan_id = ?0 ALLOW FILTERING")
    Flux<Organization> findBySubscriptionPlanId(UUID subscriptionPlanId);

    /**
     * Trouve les organisations inactives depuis une date
     */
    @Query("SELECT * FROM organizations WHERE last_activity_at < ?0 ALLOW FILTERING")
    Flux<Organization> findInactiveSince(LocalDateTime cutoffDate);

    /**
     * Trouve les organisations avec auto-renouvellement activé
     */
    @Query("SELECT * FROM organizations WHERE subscription_auto_renew = true ALLOW FILTERING")
    Flux<Organization> findWithAutoRenewal();

    /**
     * Trouve les organisations avec un nombre minimum de véhicules
     */
    @Query("SELECT * FROM organizations WHERE current_vehicles >= ?0 ALLOW FILTERING")
    Flux<Organization> findWithMinimumVehicles(int minVehicles);

    /**
     * Trouve les organisations avec un chiffre d'affaires mensuel minimum
     */
    @Query("SELECT * FROM organizations WHERE monthly_revenue >= ?0 ALLOW FILTERING")
    Flux<Organization> findByMinimumMonthlyRevenue(double minRevenue);

    /**
     * Trouve les organisations créées dans une période
     */
    @Query("SELECT * FROM organizations WHERE created_at >= ?0 AND created_at <= ?1 ALLOW FILTERING")
    Flux<Organization> findCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Trouve les organisations nécessitant une vérification
     */
    @Query("SELECT * FROM organizations WHERE is_verified = false AND is_active = true ALLOW FILTERING")
    Flux<Organization> findAwaitingVerification();

    /**
     * Compte les organisations par type
     */
    @Query("SELECT COUNT(*) FROM organizations WHERE organization_type = ?0 ALLOW FILTERING")
    Mono<Long> countByOrganizationType(OrganizationType organizationType);

    /**
     * Compte les organisations actives
     */
    @Query("SELECT COUNT(*) FROM organizations WHERE is_active = true ALLOW FILTERING")
    Mono<Long> countActive();

    /**
     * Trouve les organisations par devise
     */
    @Query("SELECT * FROM organizations WHERE currency = ?0 ALLOW FILTERING")
    Flux<Organization> findByCurrency(String currency);

    /**
     * Trouve les organisations par fuseau horaire
     */
    @Query("SELECT * FROM organizations WHERE timezone = ?0 ALLOW FILTERING")
    Flux<Organization> findByTimezone(String timezone);

    /**
     * Méthodes par défaut pour des opérations complexes
     */

    /**
     * Vérifie si une organisation existe par nom (boolean)
     */
    default Mono<Boolean> existsByName(String name) {
        return countByName(name).map(count -> count > 0);
    }

    /**
     * Vérifie si un numéro d'enregistrement existe (boolean)
     */
    default Mono<Boolean> existsByRegistrationNumber(String registrationNumber) {
        return countByRegistrationNumber(registrationNumber).map(count -> count > 0);
    }

    /**
     * Vérifie si un numéro fiscal existe (boolean)
     */
    default Mono<Boolean> existsByTaxNumber(String taxNumber) {
        return countByTaxNumber(taxNumber).map(count -> count > 0);
    }

    /**
     * Trouve les organisations les plus performantes
     */
    default Flux<Organization> findTopPerformingOrganizations(int limit) {
        return findAllActive()
            .sort((o1, o2) -> Double.compare(
                o2.getMonthlyRevenue() != null ? o2.getMonthlyRevenue() : 0.0,
                o1.getMonthlyRevenue() != null ? o1.getMonthlyRevenue() : 0.0
            ))
            .take(limit);
    }

    /**
     * Trouve les organisations récemment créées
     */
    default Flux<Organization> findRecentlyCreated(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return findCreatedBetween(cutoff, LocalDateTime.now());
    }

    /**
     * Trouve les organisations avec une forte utilisation
     */
    default Flux<Organization> findHighUtilizationOrganizations(double threshold) {
        return findAllActive()
            .filter(org -> org.getVehicleUsagePercentage() >= threshold ||
                org.getAgencyUsagePercentage() >= threshold);
    }

    /**
     * Trouve les organisations nécessitant une attention
     */
    default Flux<Organization> findOrganizationsNeedingAttention() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return findAllActive()
            .filter(org ->
                !org.getIsVerified() ||
                    org.isSubscriptionExpiringSoon() ||
                    (org.getLastActivityAt() != null && org.getLastActivityAt().isBefore(thirtyDaysAgo))
            );
    }
}
