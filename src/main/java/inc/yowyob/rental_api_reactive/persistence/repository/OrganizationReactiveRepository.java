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
     * Trouve une organisation par propriétaire
     */
    @Query("SELECT * FROM organizations WHERE owner_id = ?0 ALLOW FILTERING")
    Mono<Organization> findByOwnerId(UUID ownerId);

    /**
     * Trouve les organisations actives
     */
    @Query("SELECT * FROM organizations WHERE is_active = true ALLOW FILTERING")
    Flux<Organization> findAllActive();

    /**
     * Vérifie si un nom d'organisation existe
     */
    @Query("SELECT COUNT(*) FROM organizations WHERE name = ?0 ALLOW FILTERING")
    Mono<Long> countByName(String name);

    /**
     * Trouve une organisation par numéro d'enregistrement
     */
    @Query("SELECT * FROM organizations WHERE registration_number = ?0 ALLOW FILTERING")
    Mono<Organization> findByRegistrationNumber(String registrationNumber);
}
