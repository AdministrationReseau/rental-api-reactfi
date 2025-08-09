package inc.yowyob.rental_api_reactive.persistence.repository.driver;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import inc.yowyob.rental_api_reactive.application.dto.driver.DriverStatus;
import inc.yowyob.rental_api_reactive.persistence.entity.Driver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverReactiveRepository extends ReactiveCassandraRepository<Driver, UUID> {

    /**
     * Récupère tous les chauffeurs associés à un utilisateur donné
     */
    @Query("SELECT * FROM drivers WHERE user_id = ?0 ALLOW FILTERING")
    Mono<Driver> findByUserId(UUID userId);


    /**
     * Récupère les chauffeurs par statut et agence
     */
    @Query("SELECT * FROM drivers WHERE agency_id = ?0 AND status = 'AVAILABLE' ALLOW FILTERING")
    Flux<Driver> findAvailableDriversByAgencyId(UUID agencyId); // Nouvelle méthode

    /**
     * Récupère les chauffeurs par localisation
     */
    @Query("SELECT * FROM drivers WHERE location = ?0 ALLOW FILTERING")
    List<Driver> findByLocation(String location);

    
    /**
     * Récupère les chauffeurs ayant une note minimale
     */
    @Query("SELECT * FROM drivers WHERE rating >= ?0 ALLOW FILTERING")
    List<Driver> findByMinimumRating(double rating);

    /**
     * Récupère les chauffeurs assignés à un véhicule spécifique
     */
    @Query("SELECT * FROM drivers WHERE vehicle_assigned CONTAINS ?0 ALLOW FILTERING")
    List<Driver> findByVehicleAssigned(UUID vehicleId);

    List<Driver> findByOrganizationId(UUID organizationId);

        /**
     * Trouve les conducteurs par organisation et pagination
     */
    Flux<Driver> findByOrganizationId(UUID organizationId, Pageable pageable);

        /**
     * Trouve les conducteurs par agence et pagination
     */
    Slice<Driver> findByAgencyId(UUID agencyId, Pageable pageable);

 
    /**
     * Récupère les chauffeurs par statut
     */
    @Query("SELECT * FROM drivers WHERE status = ?0 ALLOW FILTERING")
    Flux<Driver> findByStatus(DriverStatus status);

    /**
     * Récupère les chauffeurs disponibles par organisation
     */
    @Query("SELECT * FROM drivers WHERE organization_id = ?0 AND status = 'AVAILABLE' ALLOW FILTERING")
    Flux<Driver> findAvailableDriversByOrganizationId(UUID organizationId);

    /**
     * Récupère les chauffeurs en service par organisation
     */
    @Query("SELECT * FROM drivers WHERE organization_id = ?0 AND status = 'ON_DUTY' ALLOW FILTERING")
    Flux<Driver> findOnDutyDriversByOrganizationId(UUID organizationId);

    /**
     * Récupère les chauffeurs hors service par organisation
     */
    @Query("SELECT * FROM drivers WHERE organization_id = ?0 AND status = 'OFF_DUTY' ALLOW FILTERING")
    Flux<Driver> findOffDutyDriversByOrganizationId(UUID organizationId);

    /**
     * Récupère les chauffeurs en congé par organisation
     */
    @Query("SELECT * FROM drivers WHERE organization_id = ?0 AND status = 'ON_LEAVE' ALLOW FILTERING")
    Flux<Driver> findOnLeaveDriversByOrganizationId(UUID organizationId);

 
    /**
     * Récupère tous les chauffeurs actifs (ON_DUTY et AVAILABLE) par organisation
     */
    @Query("SELECT * FROM drivers WHERE organization_id = ?0 AND status IN ('ON_DUTY', 'AVAILABLE') ALLOW FILTERING")
    Flux<Driver> findActiveDriversByOrganizationId(UUID organizationId);

    /**
     * Compte les chauffeurs par statut pour une organisation
     */
    @Query("SELECT COUNT(*) FROM drivers WHERE organization_id = ?0 AND status = ?1 ALLOW FILTERING")
    Mono<Long> countByOrganizationIdAndStatus(UUID organizationId, DriverStatus status);

    /**
     * Récupère les chauffeurs par statut et agence
     */
    @Query("SELECT * FROM drivers WHERE agency_id = ?0 AND status = ?1 ALLOW FILTERING")
    Flux<Driver> findByAgencyIdAndStatus(UUID agencyId, DriverStatus status);
}
