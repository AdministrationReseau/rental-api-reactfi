package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.OnboardingReactiveService;
import inc.yowyob.rental_api_reactive.application.service.SubscriptionPlanReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "APIs reactives pour le processus d'inscription d'organisation")
public class OnboardingReactiveController {

    private final OnboardingReactiveService onboardingService;
    private final SubscriptionPlanReactiveService subscriptionPlanService;

    @Operation(
        summary = "Récupérer les forfaits disponibles pour l'onboarding",
        description = "Retourne la liste des forfaits d'abonnement disponibles pour l'inscription"
    )
    @GetMapping("/plans")
    public Mono<ApiResponse<Flux<SubscriptionPlan>>> getAvailablePlans() {
        log.info("GET /onboarding/plans - Fetching available subscription plans");

        return Mono.fromCallable(() -> subscriptionPlanService.getAllActivePlans())
            .map(plans -> ApiResponse.<Flux<SubscriptionPlan>>builder()
                .success(true)
                .message("Forfaits d'abonnement disponibles récupérés avec succès")
                .data(plans)
                .build())
            .doOnSuccess(response -> log.info("Successfully fetched subscription plans for onboarding"))
            .onErrorReturn(ApiResponse.<Flux<SubscriptionPlan>>builder()
                .success(false)
                .message("Erreur lors de la récupération des forfaits d'abonnement")
                .build());
    }

    @Operation(
        summary = "Créer une session d'onboarding",
        description = "Démarre un nouveau processus d'inscription pour un futur propriétaire"
    )
    @PostMapping("/session")
    public Mono<ApiResponse<OnboardingSessionResponse>> createSession() {
        log.info("POST /onboarding/session - Creating new onboarding session");

        return onboardingService.createOnboardingSession()
            .map(session -> ApiResponse.<OnboardingSessionResponse>builder()
                .success(true)
                .message("Session d'onboarding créée avec succès")
                .data(session)
                .build())
            .doOnSuccess(response -> log.info("Successfully created onboarding session"))
            .onErrorReturn(ApiResponse.<OnboardingSessionResponse>builder()
                .success(false)
                .message("Erreur lors de la création de la session d'onboarding")
                .build());
    }

    @Operation(
        summary = "Récupérer une session d'onboarding",
        description = "Retourne les détails d'une session d'onboarding spécifique"
    )
    @GetMapping("/session/{sessionId}")
    public Mono<ApiResponse<OnboardingSessionResponse>> getSession(
        @Parameter(description = "ID de la session d'onboarding")
        @PathVariable UUID sessionId
    ) {
        log.info("GET /onboarding/session/{} - Fetching onboarding session", sessionId);

        return onboardingService.getOnboardingSession(sessionId)
            .map(session -> ApiResponse.<OnboardingSessionResponse>builder()
                .success(true)
                .message("Session d'onboarding récupérée avec succès")
                .data(session)
                .build())
            .onErrorReturn(ApiResponse.<OnboardingSessionResponse>builder()
                .success(false)
                .message("Session d'onboarding non trouvée")
                .build());
    }

    @Operation(
        summary = "Récupérer une session par token",
        description = "Retourne les détails d'une session d'onboarding par son token"
    )
    @GetMapping("/session/token/{sessionToken}")
    public Mono<ApiResponse<OnboardingSessionResponse>> getSessionByToken(
        @Parameter(description = "Token de la session d'onboarding")
        @PathVariable String sessionToken
    ) {
        log.info("GET /onboarding/session/token/{} - Fetching onboarding session by token", sessionToken);

        return onboardingService.getSessionByToken(sessionToken)
            .map(session -> ApiResponse.<OnboardingSessionResponse>builder()
                .success(true)
                .message("Session d'onboarding récupérée avec succès")
                .data(session)
                .build())
            .onErrorReturn(ApiResponse.<OnboardingSessionResponse>builder()
                .success(false)
                .message("Session d'onboarding non trouvée pour ce token")
                .build());
    }

    @Operation(
        summary = "Sauvegarder les informations du propriétaire (Étape 1)",
        description = "Sauvegarde les informations personnelles du futur propriétaire"
    )
    @PutMapping("/session/{sessionId}/owner-info")
    public Mono<ApiResponse<OnboardingSessionResponse>> saveOwnerInfo(
        @Parameter(description = "ID de la session d'onboarding")
        @PathVariable UUID sessionId,
        @Parameter(description = "Informations du futur propriétaire")
        @Valid @RequestBody OwnerInfoRequest ownerInfo
    ) {
        log.info("PUT /onboarding/session/{}/owner-info - Saving owner information", sessionId);

        return onboardingService.saveOwnerInfo(sessionId, ownerInfo)
            .map(session -> ApiResponse.<OnboardingSessionResponse>builder()
                .success(true)
                .message("Informations du propriétaire sauvegardées avec succès")
                .data(session)
                .build())
            .onErrorReturn(ApiResponse.<OnboardingSessionResponse>builder()
                .success(false)
                .message("Erreur lors de la sauvegarde des informations du propriétaire")
                .build());
    }

    @Operation(
        summary = "Sauvegarder les informations de l'organisation (Étape 2)",
        description = "Sauvegarde les informations et politiques de l'organisation"
    )
    @PutMapping("/session/{sessionId}/organization-info")
    public Mono<ApiResponse<OnboardingSessionResponse>> saveOrganizationInfo(
        @Parameter(description = "ID de la session d'onboarding")
        @PathVariable UUID sessionId,
        @Parameter(description = "Informations de l'organisation")
        @Valid @RequestBody OrganizationInfoRequest organizationInfo
    ) {
        log.info("PUT /onboarding/session/{}/organization-info - Saving organization information", sessionId);

        return onboardingService.saveOrganizationInfo(sessionId, organizationInfo)
            .map(session -> ApiResponse.<OnboardingSessionResponse>builder()
                .success(true)
                .message("Informations de l'organisation sauvegardées avec succès")
                .data(session)
                .build())
            .onErrorReturn(ApiResponse.<OnboardingSessionResponse>builder()
                .success(false)
                .message("Erreur lors de la sauvegarde des informations de l'organisation")
                .build());
    }

    @Operation(
        summary = "Finaliser le processus d'onboarding (Étape 3)",
        description = "Traite le paiement et finalise la création du compte et de l'organisation"
    )
    @PostMapping("/session/{sessionId}/complete")
    public Mono<ApiResponse<OnboardingCompletedResponse>> completeOnboarding(
        @Parameter(description = "ID de la session d'onboarding")
        @PathVariable UUID sessionId,
        @Parameter(description = "Informations de souscription et paiement")
        @Valid @RequestBody SubscriptionInfoRequest subscriptionInfo
    ) {
        log.info("POST /onboarding/session/{}/complete - Completing onboarding", sessionId);

        return onboardingService.completeOnboarding(sessionId, subscriptionInfo)
            .map(result -> ApiResponse.<OnboardingCompletedResponse>builder()
                .success(true)
                .message("Processus d'inscription terminé avec succès")
                .data(result)
                .build())
            .onErrorReturn(ApiResponse.<OnboardingCompletedResponse>builder()
                .success(false)
                .message("Erreur lors de la finalisation du processus d'inscription")
                .build());
    }

    @Operation(
        summary = "Obtenir les sessions actives",
        description = "Retourne toutes les sessions d'onboarding actuellement en cours"
    )
    @GetMapping("/sessions/active")
    public Mono<ApiResponse<Flux<OnboardingSessionResponse>>> getActiveSessions() {
        log.info("GET /onboarding/sessions/active - Fetching active onboarding sessions");

        return Mono.fromCallable(() -> onboardingService.getActiveSessions())
            .map(sessions -> ApiResponse.<Flux<OnboardingSessionResponse>>builder()
                .success(true)
                .message("Sessions actives récupérées avec succès")
                .data(sessions)
                .build())
            .onErrorReturn(ApiResponse.<Flux<OnboardingSessionResponse>>builder()
                .success(false)
                .message("Erreur lors de la récupération des sessions actives")
                .build());
    }

    @Operation(
        summary = "Nettoyer les sessions expirées",
        description = "Met à jour le statut des sessions expirées (tâche administrative)"
    )
    @PostMapping("/sessions/cleanup")
    public Mono<ApiResponse<String>> cleanupExpiredSessions() {
        log.info("POST /onboarding/sessions/cleanup - Cleaning up expired sessions");

        return onboardingService.cleanupExpiredSessions()
            .map(count -> ApiResponse.<String>builder()
                .success(true)
                .message("Nettoyage effectué avec succès")
                .data(count + " sessions expirées traitées")
                .build())
            .onErrorReturn(ApiResponse.<String>builder()
                .success(false)
                .message("Erreur lors du nettoyage des sessions expirées")
                .build());
    }
}
