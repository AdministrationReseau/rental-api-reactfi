package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.SubscriptionPlanReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
@Tag(name = "Subscription Plans", description = "APIs reactives de gestion des forfaits d'abonnement")
public class SubscriptionPlanReactiveController {

    private final SubscriptionPlanReactiveService subscriptionPlanService;

    @Operation(
        summary = "Récupérer tous les forfaits disponibles",
        description = "Retourne la liste de tous les forfaits d'abonnement actifs"
    )
    @GetMapping("/plans")
    public Mono<ApiResponse<List<SubscriptionPlan>>> getAllPlans() {
        log.info("GET /subscription/plans - Fetching all subscription plans");

        return subscriptionPlanService.getAllActivePlans()
            .collectList()
            .map(plans -> ApiResponse.<List<SubscriptionPlan>>builder()
                .success(true)
                .message("Forfaits d'abonnement récupérés avec succès")
                .data(plans)
                .count((long) plans.size())
                .build())
            .doOnSuccess(response -> log.info("Successfully fetched {} subscription plans", response.getData().size()))
            .onErrorReturn(ApiResponse.<List<SubscriptionPlan>>builder()
                .success(false)
                .message("Erreur lors de la récupération des forfaits d'abonnement")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Récupérer un forfait par ID",
        description = "Retourne les détails d'un forfait d'abonnement spécifique"
    )
    @GetMapping("/plans/{planId}")
    public Mono<ApiResponse<SubscriptionPlan>> getPlanById(
        @Parameter(description = "ID du forfait d'abonnement")
        @PathVariable UUID planId
    ) {
        log.info("GET /subscription/plans/{} - Fetching subscription plan", planId);

        return subscriptionPlanService.getPlanById(planId)
            .map(plan -> ApiResponse.<SubscriptionPlan>builder()
                .success(true)
                .message("Forfait d'abonnement trouvé avec succès")
                .data(plan)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<SubscriptionPlan>builder()
                .success(false)
                .message("Forfait d'abonnement non trouvé")
                .data(null)
                .build()))
            .onErrorReturn(ApiResponse.<SubscriptionPlan>builder()
                .success(false)
                .message("Erreur lors de la récupération du forfait d'abonnement")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Récupérer les forfaits populaires",
        description = "Retourne les forfaits marqués comme populaires"
    )
    @GetMapping("/plans/popular")
    public Mono<ApiResponse<List<SubscriptionPlan>>> getPopularPlans() {
        log.info("GET /subscription/plans/popular - Fetching popular subscription plans");

        return subscriptionPlanService.getPopularPlans()
            .collectList()
            .map(plans -> ApiResponse.<List<SubscriptionPlan>>builder()
                .success(true)
                .message("Forfaits populaires récupérés avec succès")
                .data(plans)
                .count((long) plans.size())
                .build())
            .onErrorReturn(ApiResponse.<List<SubscriptionPlan>>builder()
                .success(false)
                .message("Erreur lors de la récupération des forfaits populaires")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Récupérer les forfaits standards",
        description = "Retourne les forfaits non personnalisés"
    )
    @GetMapping("/plans/standard")
    public Mono<ApiResponse<List<SubscriptionPlan>>> getStandardPlans() {
        log.info("GET /subscription/plans/standard - Fetching standard subscription plans");

        return subscriptionPlanService.getStandardPlans()
            .collectList()
            .map(plans -> ApiResponse.<List<SubscriptionPlan>>builder()
                .success(true)
                .message("Forfaits standards récupérés avec succès")
                .data(plans)
                .count((long) plans.size())
                .build())
            .onErrorReturn(ApiResponse.<List<SubscriptionPlan>>builder()
                .success(false)
                .message("Erreur lors de la récupération des forfaits standards")
                .data(null)
                .build());
    }
}
