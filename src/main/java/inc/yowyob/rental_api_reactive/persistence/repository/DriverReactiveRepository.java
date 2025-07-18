package inc.yowyob.rental_api_reactive.persistence.repository;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import inc.yowyob.rental_api_reactive.persistence.entity.Driver;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface DriverReactiveRepository extends ReactiveCassandraRepository<Driver, UUID> {

    /**
     * Récupère tous les chauffeurs associés à un utilisateur donné
     */
    @Query("SELECT * FROM drivers WHERE user_id = ?0 ALLOW FILTERING")
    Mono<Driver> findByUserId(UUID userId);

    /**
     * Récupère les chauffeurs par statut
     */
    // @Query("SELECT * FROM drivers WHERE status = ?0 ALLOW FILTERING")
    // Flux<Driver> findByStatus(DriverStatus status);

    /**
     * Récupère les chauffeurs disponibles
     */
    @Query("SELECT * FROM drivers WHERE available = true ALLOW FILTERING")
    Flux<Driver> findAvailableDrivers();

    /**
     * Récupère les chauffeurs par localisation
     */
    @Query("SELECT * FROM drivers WHERE location = ?0 ALLOW FILTERING")
    Flux<Driver> findByLocation(String location);

    /**
     * Récupère les chauffeurs ayant une note minimale
     */
    @Query("SELECT * FROM drivers WHERE rating >= ?0 ALLOW FILTERING")
    Flux<Driver> findByMinimumRating(double rating);

    /**
     * Récupère les chauffeurs assignés à un véhicule spécifique
     */
    @Query("SELECT * FROM drivers WHERE vehicle_assigned CONTAINS ?0 ALLOW FILTERING")
    Flux<Driver> findByVehicleAssigned(UUID vehicleId);

    Flux<Driver> findByOrganizationId(UUID organizationId);

        /**
     * Trouve les conducteurs par organisation et pagination
     */
    Flux<Driver> findByOrganizationId(UUID organizationId, Pageable pageable);

        /**
     * Trouve les conducteurs par agence et pagination
     */
    Flux<Driver> findByAgencyId(UUID agencyId, Pageable pageable);
}