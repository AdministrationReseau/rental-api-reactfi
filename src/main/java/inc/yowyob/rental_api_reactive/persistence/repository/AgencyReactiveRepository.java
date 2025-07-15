package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.Agency;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository réactif pour l'entité Agency
 */
@Repository
public interface AgencyReactiveRepository extends ReactiveCassandraRepository<Agency, UUID> {

    /**
     * Trouve toutes les agences d'une organisation
     */
    @Query("SELECT * FROM agencies WHERE organization_id = ?0 ALLOW FILTERING")
    Flux<Agency> findByOrganizationId(UUID organizationId);

    /**
     * Trouve les agences actives d'une organisation
     */
    @Query("SELECT * FROM agencies WHERE organization_id = ?0 AND is_active = true ALLOW FILTERING")
    Flux<Agency> findActiveByOrganizationId(UUID organizationId);

    /**
     * Trouve les agences par gestionnaire
     */
    @Query("SELECT * FROM agencies WHERE manager_id = ?0 ALLOW FILTERING")
    Flux<Agency> findByManagerId(UUID managerId);

    /**
     * Trouve les agences actives par gestionnaire
     */
    @Query("SELECT * FROM agencies WHERE manager_id = ?0 AND is_active = true ALLOW FILTERING")
    Flux<Agency> findActiveByManagerId(UUID managerId);

    /**
     * Trouve les agences par ville
     */
    @Query("SELECT * FROM agencies WHERE city = ?0 ALLOW FILTERING")
    Flux<Agency> findByCity(String city);

    /**
     * Trouve les agences par pays
     */
    @Query("SELECT * FROM agencies WHERE country = ?0 ALLOW FILTERING")
    Flux<Agency> findByCountry(String country);

    /**
     * Trouve les agences par ville et pays
     */
    @Query("SELECT * FROM agencies WHERE city = ?0 AND country = ?1 ALLOW FILTERING")
    Flux<Agency> findByCityAndCountry(String city, String country);

    /**
     * Trouve les agences actives par ville
     */
    @Query("SELECT * FROM agencies WHERE city = ?0 AND is_active = true ALLOW FILTERING")
    Flux<Agency> findActiveByCityAndCountry(String city, String country);

    /**
     * Compte les agences d'une organisation
     */
    @Query("SELECT COUNT(*) FROM agencies WHERE organization_id = ?0 ALLOW FILTERING")
    Mono<Long> countByOrganizationId(UUID organizationId);

    /**
     * Compte les agences actives d'une organisation
     */
    @Query("SELECT COUNT(*) FROM agencies WHERE organization_id = ?0 AND is_active = true ALLOW FILTERING")
    Mono<Long> countActiveByOrganizationId(UUID organizationId);

    /**
     * Vérifie si une agence existe par nom dans une organisation
     */
    @Query("SELECT COUNT(*) FROM agencies WHERE organization_id = ?0 AND name = ?1 ALLOW FILTERING")
    Mono<Long> countByOrganizationIdAndName(UUID organizationId, String name);

    /**
     * Trouve les agences ouvertes 24h/24
     */
    @Query("SELECT * FROM agencies WHERE is_24_hours = true ALLOW FILTERING")
    Flux<Agency> find24HourAgencies();

    /**
     * Trouve les agences avec géofencing activé
     */
    @Query("SELECT * FROM agencies WHERE geofence_zone_id IS NOT NULL OR geofence_radius > 0 ALLOW FILTERING")
    Flux<Agency> findAgenciesWithGeofencing();

    /**
     * Trouve les agences dans une région géographique
     */
    @Query("SELECT * FROM agencies WHERE latitude >= ?0 AND latitude <= ?1 AND longitude >= ?2 AND longitude <= ?3 ALLOW FILTERING")
    Flux<Agency> findAgenciesInBounds(double minLat, double maxLat, double minLng, double maxLng);

    /**
     * Trouve les agences avec réservation en ligne activée
     */
    @Query("SELECT * FROM agencies WHERE allow_online_booking = true AND is_active = true ALLOW FILTERING")
    Flux<Agency> findAgenciesWithOnlineBooking();

    /**
     * Trouve les agences par organisation et région
     */
    @Query("SELECT * FROM agencies WHERE organization_id = ?0 AND region = ?1 ALLOW FILTERING")
    Flux<Agency> findByOrganizationIdAndRegion(UUID organizationId, String region);

    /**
     * Trouve les agences nécessitant une mise à jour des statistiques
     */
    @Query("SELECT * FROM agencies WHERE updated_at < ?0 ALLOW FILTERING")
    Flux<Agency> findAgenciesNeedingStatsUpdate(String cutoffDate);

    /**
     * Trouve une agence par nom dans une organisation
     */
    @Query("SELECT * FROM agencies WHERE organization_id = ?0 AND name = ?1 ALLOW FILTERING")
    Mono<Agency> findByOrganizationIdAndName(UUID organizationId, String name);

    /**
     * Trouve les agences actives d'une organisation avec pagination
     */
    @Query("SELECT * FROM agencies WHERE organization_id = ?0 AND is_active = true ALLOW FILTERING")
    Flux<Agency> findActiveByOrganizationIdWithLimit(UUID organizationId);

    /**
     * Trouve les agences par type de devise
     */
    @Query("SELECT * FROM agencies WHERE currency = ?0 ALLOW FILTERING")
    Flux<Agency> findByCurrency(String currency);

    /**
     * Trouve les agences par fuseau horaire
     */
    @Query("SELECT * FROM agencies WHERE timezone = ?0 ALLOW FILTERING")
    Flux<Agency> findByTimezone(String timezone);

    /**
     * Trouve les agences avec un chiffre d'affaires mensuel minimum
     */
    @Query("SELECT * FROM agencies WHERE monthly_revenue >= ?0 ALLOW FILTERING")
    Flux<Agency> findByMinimumMonthlyRevenue(double minRevenue);

    /**
     * Vérifie si une organisation a atteint sa limite d'agences
     */
    default Mono<Boolean> hasReachedAgencyLimit(UUID organizationId, int maxAgencies) {
        return countActiveByOrganizationId(organizationId)
            .map(count -> count >= maxAgencies);
    }

    /**
     * Trouve les agences les plus performantes d'une organisation
     */
    default Flux<Agency> findTopPerformingAgencies(UUID organizationId, int limit) {
        return findActiveByOrganizationId(organizationId)
            .sort((a1, a2) -> Double.compare(
                a2.getMonthlyRevenue() != null ? a2.getMonthlyRevenue() : 0.0,
                a1.getMonthlyRevenue() != null ? a1.getMonthlyRevenue() : 0.0
            ))
            .take(limit);
    }
}
