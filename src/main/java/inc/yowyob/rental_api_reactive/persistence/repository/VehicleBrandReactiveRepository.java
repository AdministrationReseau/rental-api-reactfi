package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.VehicleBrand;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository réactif pour les marques de véhicules
 */
@Repository
public interface VehicleBrandReactiveRepository extends ReactiveCassandraRepository<VehicleBrand, UUID> {

    /**
     * Trouve toutes les marques actives
     */
    @Query("SELECT * FROM vehicle_brands WHERE is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<VehicleBrand> findAllActive();

    /**
     * Trouve une marque par nom (case insensitive)
     */
    @Query("SELECT * FROM vehicle_brands WHERE name = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<VehicleBrand> findByName(String name);

    /**
     * Vérifie si une marque existe par nom
     */
    @Query("SELECT COUNT(*) FROM vehicle_brands WHERE name = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countByName(String name);

    /**
     * Trouve les marques par pays d'origine
     */
    @Query("SELECT * FROM vehicle_brands WHERE country_origin = ?0 AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Flux<VehicleBrand> findByCountryOrigin(String countryOrigin);

    /**
     * Trouve les marques créées récemment
     */
    @Query("SELECT * FROM vehicle_brands WHERE created_at >= ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<VehicleBrand> findRecentlyCreated(LocalDateTime since);

    /**
     * Méthode par défaut pour vérifier l'existence
     */
    default Mono<Boolean> existsByName(String name) {
        return countByName(name).map(count -> count > 0);
    }
}
