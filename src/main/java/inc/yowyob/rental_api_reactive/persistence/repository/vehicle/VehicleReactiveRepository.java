package inc.yowyob.rental_api_reactive.persistence.repository.vehicle;

import inc.yowyob.rental_api_reactive.application.dto.vehicle.VehicleStatus;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.VehicleType;
import inc.yowyob.rental_api_reactive.persistence.entity.vehicle.Vehicle;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository réactif pour les véhicules
 */
@Repository
public interface VehicleReactiveRepository extends ReactiveCassandraRepository<Vehicle, UUID> {

    /**
     * Trouve tous les véhicules actifs
     */
    @Query("SELECT * FROM vehicles WHERE is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findAllActive();

    /**
     * Trouve les véhicules par organisation
     */
    @Query("SELECT * FROM vehicles WHERE organization_id = ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findByOrganizationId(UUID organizationId);

    /**
     * Trouve les véhicules actifs par organisation
     */
    @Query("SELECT * FROM vehicles WHERE organization_id = ?0 AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findActiveByOrganizationId(UUID organizationId);

    /**
     * Trouve les véhicules par agence
     */
    @Query("SELECT * FROM vehicles WHERE agency_id = ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findByAgencyId(UUID agencyId);

    /**
     * Trouve les véhicules actifs par agence
     */
    @Query("SELECT * FROM vehicles WHERE agency_id = ?0 AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findActiveByAgencyId(UUID agencyId);

    /**
     * Trouve les véhicules par statut
     */
    @Query("SELECT * FROM vehicles WHERE status = ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findByStatus(VehicleStatus status);

    /**
     * Trouve les véhicules disponibles par organisation
     */
    @Query("SELECT * FROM vehicles WHERE organization_id = ?0 AND status = 'AVAILABLE' AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findAvailableByOrganizationId(UUID organizationId);

    /**
     * Trouve les véhicules disponibles par agence
     */
    @Query("SELECT * FROM vehicles WHERE agency_id = ?0 AND status = 'AVAILABLE' AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findAvailableByAgencyId(UUID agencyId);

    /**
     * Trouve les véhicules par marque
     */
    @Query("SELECT * FROM vehicles WHERE brand_id = ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findByBrandId(UUID brandId);

    /**
     * Trouve les véhicules par type
     */
    @Query("SELECT * FROM vehicles WHERE vehicle_type = ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findByVehicleType(VehicleType vehicleType);

    /**
     * Trouve les véhicules par plaque d'immatriculation
     */
    @Query("SELECT * FROM vehicles WHERE license_plate = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<Vehicle> findByLicensePlate(String licensePlate);

    /**
     * Vérifie si une plaque d'immatriculation existe
     */
    @Query("SELECT COUNT(*) FROM vehicles WHERE license_plate = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countByLicensePlate(String licensePlate);

    /**
     * Trouve les véhicules dans une fourchette de prix journalier
     */
    @Query("SELECT * FROM vehicles WHERE daily_price >= ?0 AND daily_price <= ?1 AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findByDailyPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Trouve les véhicules par année
     */
    @Query("SELECT * FROM vehicles WHERE year >= ?0 AND year <= ?1 AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findByYearBetween(Integer minYear, Integer maxYear);

    /**
     * Trouve les véhicules nécessitant une maintenance
     */
    @Query("SELECT * FROM vehicles WHERE next_maintenance_date <= ?0 AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> findVehiclesNeedingMaintenance(LocalDateTime beforeDate);

    /**
     * Compte les véhicules par organisation
     */
    @Query("SELECT COUNT(*) FROM vehicles WHERE organization_id = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countByOrganizationId(UUID organizationId);

    /**
     * Compte les véhicules actifs par organisation
     */
    @Query("SELECT COUNT(*) FROM vehicles WHERE organization_id = ?0 AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countActiveByOrganizationId(UUID organizationId);

    /**
     * Compte les véhicules par agence
     */
    @Query("SELECT COUNT(*) FROM vehicles WHERE agency_id = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countByAgencyId(UUID agencyId);

    /**
     * Compte les véhicules par statut et organisation
     */
    @Query("SELECT COUNT(*) FROM vehicles WHERE organization_id = ?0 AND status = ?1 AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countByOrganizationIdAndStatus(UUID organizationId, VehicleStatus status);

    /**
     * Recherche textuelle dans modele et description
     */
    @Query("SELECT * FROM vehicles WHERE model LIKE ?0 OR description LIKE ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<Vehicle> searchByModelOrDescription(String searchTerm);

    /**
     * Méthode par défaut pour vérifier l'existence d'une plaque
     */
    default Mono<Boolean> existsByLicensePlate(String licensePlate) {
        return countByLicensePlate(licensePlate).map(count -> count > 0);
    }
}
