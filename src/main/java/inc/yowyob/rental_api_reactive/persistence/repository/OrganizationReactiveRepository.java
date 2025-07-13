package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrganizationReactiveRepository extends ReactiveCassandraRepository<Organization, UUID> {

    /**
     * Trouve une organisation par nom
     */
    @Query("SELECT * FROM organizations WHERE name = ?0 ALLOW FILTERING")
    Mono<Organization> findByName(String name);

    /**
     * Trouve les organisations actives
     */
    @Query("SELECT * FROM organizations WHERE is_active = true ALLOW FILTERING")
    Flux<Organization> findAllActive();

    /**
     * Trouve les organisations par propriétaire
     */
    @Query("SELECT * FROM organizations WHERE owner_id = ?0 ALLOW FILTERING")
    Flux<Organization> findByOwnerId(UUID ownerId);

    /**
     * Vérifie si une organisation existe par nom
     */
    @Query("SELECT COUNT(*) FROM organizations WHERE name = ?0 ALLOW FILTERING")
    Mono<Long> countByName(String name);

    /**
     * Trouve les organisations par secteur d'activité
     */
    @Query("SELECT * FROM organizations WHERE business_sector = ?0 ALLOW FILTERING")
    Flux<Organization> findByBusinessSector(String businessSector);
}
