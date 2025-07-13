package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import inc.yowyob.rental_api_reactive.persistence.repository.SubscriptionPlanReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionPlanReactiveService {

    private final SubscriptionPlanReactiveRepository subscriptionPlanRepository;

    /**
     * Récupère tous les plans d'abonnement actifs
     */
    public Flux<SubscriptionPlan> getAllActivePlans() {
        log.debug("Fetching all active subscription plans");
        return subscriptionPlanRepository.findAllActive()
            .doOnNext(plan -> log.debug("Found active plan: {}", plan.getName()))
            .doOnComplete(() -> log.info("Successfully fetched all active plans"));
    }

    /**
     * Récupère un plan d'abonnement par ID
     */
    public Mono<SubscriptionPlan> getPlanById(UUID planId) {
        log.debug("Fetching subscription plan with ID: {}", planId);
        return subscriptionPlanRepository.findById(planId)
            .doOnNext(plan -> log.debug("Found plan: {}", plan.getName()))
            .doOnSuccess(plan -> {
                if (plan != null) {
                    log.info("Successfully fetched plan: {}", planId);
                } else {
                    log.warn("Plan not found: {}", planId);
                }
            });
    }

    /**
     * Récupère les plans populaires
     */
    public Flux<SubscriptionPlan> getPopularPlans() {
        log.debug("Fetching popular subscription plans");
        return subscriptionPlanRepository.findPopularPlans()
            .doOnNext(plan -> log.debug("Found popular plan: {}", plan.getName()))
            .doOnComplete(() -> log.info("Successfully fetched popular plans"));
    }

    /**
     * Récupère les plans standards (non personnalisés)
     */
    public Flux<SubscriptionPlan> getStandardPlans() {
        log.debug("Fetching standard subscription plans");
        return subscriptionPlanRepository.findStandardPlans()
            .doOnNext(plan -> log.debug("Found standard plan: {}", plan.getName()))
            .doOnComplete(() -> log.info("Successfully fetched standard plans"));
    }

    /**
     * Sauvegarde un plan d'abonnement
     */
    public Mono<SubscriptionPlan> savePlan(SubscriptionPlan plan) {
        log.info("Saving subscription plan: {}", plan.getName());
        return subscriptionPlanRepository.save(plan)
            .doOnSuccess(savedPlan -> log.info("Successfully saved plan: {}", savedPlan.getId()));
    }

    /**
     * Vérifie si un plan existe par nom
     */
    public Mono<Boolean> existsByName(String name) {
        log.debug("Checking if plan exists with name: {}", name);
        return subscriptionPlanRepository.findAllActive()
            .filter(plan -> name.equals(plan.getName()))
            .hasElements()
            .doOnNext(exists -> log.debug("Plan exists with name {}: {}", name, exists));
    }

    /**
     * Supprime un plan d'abonnement (soft delete)
     */
    public Mono<SubscriptionPlan> deactivatePlan(UUID planId) {
        log.info("Deactivating subscription plan: {}", planId);
        return subscriptionPlanRepository.findById(planId)
            .switchIfEmpty(Mono.error(new RuntimeException("Plan not found: " + planId)))
            .map(plan -> {
                plan.setIsActive(false);
                return plan;
            })
            .flatMap(subscriptionPlanRepository::save)
            .doOnSuccess(plan -> log.info("Successfully deactivated plan: {}", planId));
    }
}
