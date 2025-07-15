package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.OnboardingReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "APIs réactives pour le processus d'inscription et de création d'organisation")
public class OnboardingReactiveController {

    private final OnboardingReactiveService onboardingService;

    @Operation(
        summary = "Démarrer une nouvelle session d'onboarding",
        description = "Crée une nouvelle session et retourne un token de session pour les étapes suivantes."
    )
    @PostMapping("/start")
    public Mono<ApiResponse<OnboardingResponse>> startOnboarding() {
        log.info("POST /onboarding/start - Starting new onboarding session");
        return onboardingService.startOnboarding()
            .map(response -> ApiResponse.<OnboardingResponse>builder()
                .success(true)
                .message("Session d'onboarding démarrée avec succès.")
                .data(response)
                .build())
            .doOnError(error -> log.error("Failed to start onboarding session", error));
    }

    @Operation(
        summary = "Récupérer le statut d'une session d'onboarding",
        description = "Retourne l'état actuel d'une session d'onboarding, y compris l'étape en cours."
    )
    @GetMapping("/{sessionToken}/status")
    public Mono<ApiResponse<OnboardingResponse>> getOnboardingStatus(
        @Parameter(description = "Token de la session d'onboarding") @PathVariable String sessionToken) {

        log.info("GET /onboarding/{}/status - Fetching onboarding status", sessionToken);
        return onboardingService.getOnboardingStatus(sessionToken)
            .map(response -> ApiResponse.<OnboardingResponse>builder()
                .success(true)
                .message("Statut de la session récupéré avec succès.")
                .data(response)
                .build())
            .doOnError(error -> log.error("Failed to get onboarding status for session {}", sessionToken, error));
    }

    @Operation(
        summary = "Étape 1: Sauvegarder les informations du propriétaire",
        description = "Sauvegarde les informations sur le propriétaire du compte (email, mot de passe, etc.)."
    )
    @PutMapping("/{sessionToken}/owner")
    public Mono<ApiResponse<OnboardingResponse>> saveOwnerInfo(
        @Parameter(description = "Token de la session d'onboarding") @PathVariable String sessionToken,
        @Valid @RequestBody OnboardingOwnerRequest ownerRequest) {

        log.info("PUT /onboarding/{}/owner - Saving owner info", sessionToken);
        return onboardingService.saveOwnerInfo(sessionToken, ownerRequest)
            .map(response -> ApiResponse.<OnboardingResponse>builder()
                .success(true)
                .message("Étape 1: Informations du propriétaire sauvegardées.")
                .data(response)
                .build())
            .doOnError(error -> log.error("Error saving owner info for session {}", sessionToken, error));
    }

    @Operation(
        summary = "Étape 2: Sauvegarder les informations de l'organisation",
        description = "Sauvegarde les informations sur l'organisation (nom, type, adresse, etc.)."
    )
    @PutMapping("/{sessionToken}/organization")
    public Mono<ApiResponse<OnboardingResponse>> saveOrganizationInfo(
        @Parameter(description = "Token de la session d'onboarding") @PathVariable String sessionToken,
        @Valid @RequestBody OnboardingOrganizationRequest orgRequest) {

        log.info("PUT /onboarding/{}/organization - Saving organization info", sessionToken);
        return onboardingService.saveOrganizationInfo(sessionToken, orgRequest)
            .map(response -> ApiResponse.<OnboardingResponse>builder()
                .success(true)
                .message("Étape 2: Informations de l'organisation sauvegardées.")
                .data(response)
                .build())
            .doOnError(error -> log.error("Error saving organization info for session {}", sessionToken, error));
    }

    @Operation(
        summary = "Étape 3: Sauvegarder les informations de l'abonnement",
        description = "Sauvegarde le choix du plan d'abonnement et les informations de paiement."
    )
    @PutMapping("/{sessionToken}/subscription")
    public Mono<ApiResponse<OnboardingResponse>> saveSubscriptionInfo(
        @Parameter(description = "Token de la session d'onboarding") @PathVariable String sessionToken,
        @Valid @RequestBody OnboardingSubscriptionRequest subscriptionRequest) {

        log.info("PUT /onboarding/{}/subscription - Saving subscription info", sessionToken);
        return onboardingService.saveSubscriptionInfo(sessionToken, subscriptionRequest)
            .map(response -> ApiResponse.<OnboardingResponse>builder()
                .success(true)
                .message("Étape 3: Informations de l'abonnement sauvegardées.")
                .data(response)
                .build())
            .doOnError(error -> log.error("Error saving subscription info for session {}", sessionToken, error));
    }

    @Operation(
        summary = "Finaliser l'onboarding",
        description = "Valide toutes les informations collectées, crée l'utilisateur, l'organisation, l'abonnement, et clôture la session."
    )
    @PostMapping("/{sessionToken}/complete")
    public Mono<ApiResponse<OnboardingCompletionResponse>> completeOnboarding(
        @Parameter(description = "Token de la session d'onboarding") @PathVariable String sessionToken) {

        log.info("POST /onboarding/{}/complete - Completing onboarding process", sessionToken);
        return onboardingService.completeOnboarding(sessionToken)
            .map(response -> ApiResponse.<OnboardingCompletionResponse>builder()
                .success(true)
                .message(response.getMessage())
                .data(response)
                .build())
            .doOnError(error -> log.error("Failed to complete onboarding for session {}", sessionToken, error));
    }
}
