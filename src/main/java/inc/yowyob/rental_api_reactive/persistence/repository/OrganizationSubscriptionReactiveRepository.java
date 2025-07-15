package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.OrganizationSubscription;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrganizationSubscriptionReactiveRepository extends ReactiveCassandraRepository<OrganizationSubscription, UUID>  {

    Mono<OrganizationSubscription> findByOrganizationId(UUID organizationId);
}
