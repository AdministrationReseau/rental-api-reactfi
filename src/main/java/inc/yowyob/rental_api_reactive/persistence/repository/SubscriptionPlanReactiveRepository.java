package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SubscriptionPlanReactiveRepository extends ReactiveCassandraRepository<SubscriptionPlan, UUID> {

    /**
     * Trouve tous les plans actifs triés par ordre
     */
    @Query("SELECT * FROM subscription_plans WHERE is_active = true ALLOW FILTERING")
    Flux<SubscriptionPlan> findAllActive();

    /**
     * Trouve les plans populaires
     */
    @Query("SELECT * FROM subscription_plans WHERE is_popular = true AND is_active = true ALLOW FILTERING")
    Flux<SubscriptionPlan> findPopularPlans();

    /**
     * Trouve les plans non personnalisés
     */
    @Query("SELECT * FROM subscription_plans WHERE is_custom = false AND is_active = true ALLOW FILTERING")
    Flux<SubscriptionPlan> findStandardPlans();
}
