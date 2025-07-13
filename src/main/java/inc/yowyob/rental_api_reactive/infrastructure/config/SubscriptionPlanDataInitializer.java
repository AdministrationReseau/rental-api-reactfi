package inc.yowyob.rental_api_reactive.infrastructure.config;

import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import inc.yowyob.rental_api_reactive.persistence.repository.SubscriptionPlanReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Initialise les données de base pour les forfaits d'abonnement (Version Reactive)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionPlanDataInitializer implements CommandLineRunner {

    private final SubscriptionPlanReactiveRepository subscriptionPlanRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing subscription plans data (reactive)...");

        try {
            subscriptionPlanRepository.findAllActive()
                .hasElements()
                .flatMap(hasPlans -> {
                    if (hasPlans) {
                        log.info("Subscription plans already exist. Skipping initialization.");
                        return Mono.empty();
                    } else {
                        return createSubscriptionPlans()
                            .then(); // Convertit le Flux en Mono<Void>
                    }
                })
                .doOnSuccess(unused -> log.info("Subscription plans initialization completed successfully."))
                .doOnError(error -> log.error("Error during subscription plans initialization: {}", error.getMessage(), error))
                .subscribe();

        } catch (Exception e) {
            log.error("Error during subscription plans initialization: {}", e.getMessage(), e);
        }
    }

    private Flux<SubscriptionPlan> createSubscriptionPlans() {
        log.info("Creating default subscription plans...");

        return Flux.just(
                createTrialPlan(),
                createBasicPlan(),
                createPremiumPlan(),
                createEnterprisePlan()
            )
            .flatMap(subscriptionPlanRepository::save)
            .doOnNext(plan -> log.info("Created subscription plan: {} - {}", plan.getName(), plan.getId()));
    }

    private SubscriptionPlan createTrialPlan() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(UUID.randomUUID());
        plan.setName("GRATUIT");
        plan.setDescription("Forfait d'essai gratuit de 30 jours pour découvrir notre plateforme");
        plan.setPrice(BigDecimal.ZERO);
        plan.setCurrency("XAF");
        plan.setDurationDays(30);
        plan.setMaxAgencies(1);
        plan.setMaxVehicles(5);
        plan.setMaxDrivers(2);
        plan.setMaxUsers(3);
        plan.setIsPopular(false);
        plan.setIsCustom(false);
        plan.setSortOrder(1);
        plan.setFeatures(Map.of(
            "geofencing", false,
            "chat", false,
            "advanced_reports", false,
            "api_access", false,
            "priority_support", false,
            "custom_branding", false,
            "multi_language", true,
            "basic_analytics", true
        ));
        return plan;
    }

    private SubscriptionPlan createBasicPlan() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(UUID.randomUUID());
        plan.setName("BASIC");
        plan.setDescription("Forfait de base idéal pour les petites entreprises de location");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setCurrency("XAF");
        plan.setDurationDays(30);
        plan.setMaxAgencies(3);
        plan.setMaxVehicles(20);
        plan.setMaxDrivers(10);
        plan.setMaxUsers(8);
        plan.setIsPopular(true);
        plan.setIsCustom(false);
        plan.setSortOrder(2);
        plan.setFeatures(Map.of(
            "geofencing", false,
            "chat", true,
            "advanced_reports", false,
            "api_access", false,
            "priority_support", false,
            "custom_branding", false,
            "multi_language", true,
            "basic_analytics", true,
            "email_notifications", true
        ));
        return plan;
    }

    private SubscriptionPlan createPremiumPlan() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(UUID.randomUUID());
        plan.setName("PREMIUM");
        plan.setDescription("Forfait premium avec fonctionnalités avancées pour les entreprises en croissance");
        plan.setPrice(new BigDecimal("79.99"));
        plan.setCurrency("XAF");
        plan.setDurationDays(30);
        plan.setMaxAgencies(10);
        plan.setMaxVehicles(100);
        plan.setMaxDrivers(50);
        plan.setMaxUsers(25);
        plan.setIsPopular(true);
        plan.setIsCustom(false);
        plan.setSortOrder(3);
        plan.setFeatures(Map.ofEntries(
            Map.entry("geofencing", true),
            Map.entry("chat", true),
            Map.entry("advanced_reports", true),
            Map.entry("api_access", true),
            Map.entry("priority_support", false),
            Map.entry("custom_branding", true),
            Map.entry("multi_language", true),
            Map.entry("basic_analytics", true),
            Map.entry("advanced_analytics", true),
            Map.entry("email_notifications", true),
            Map.entry("sms_notifications", true),
            Map.entry("integrations", true)
        ));
        return plan;
    }

    private SubscriptionPlan createEnterprisePlan() {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(UUID.randomUUID());
        plan.setName("ENTERPRISE");
        plan.setDescription("Solution complète pour les grandes entreprises avec support dédié");
        plan.setPrice(new BigDecimal("199.99"));
        plan.setCurrency("XAF");
        plan.setDurationDays(30);
        plan.setMaxAgencies(Integer.MAX_VALUE);
        plan.setMaxVehicles(Integer.MAX_VALUE);
        plan.setMaxDrivers(Integer.MAX_VALUE);
        plan.setMaxUsers(Integer.MAX_VALUE);
        plan.setIsPopular(false);
        plan.setIsCustom(false);
        plan.setSortOrder(4);
        plan.setFeatures(Map.ofEntries(
            Map.entry("geofencing", true),
            Map.entry("chat", true),
            Map.entry("advanced_reports", true),
            Map.entry("api_access", true),
            Map.entry("priority_support", true),
            Map.entry("custom_branding", true),
            Map.entry("multi_language", true),
            Map.entry("basic_analytics", true),
            Map.entry("advanced_analytics", true),
            Map.entry("email_notifications", true),
            Map.entry("sms_notifications", true),
            Map.entry("integrations", true),
            Map.entry("white_labeling", true),
            Map.entry("dedicated_support", true),
            Map.entry("custom_features", true)
        ));
        return plan;
    }
}
