package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.OrganizationReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.security.UserPrincipal;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.application.dto.OrganizationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Contrôleur réactif pour la gestion des organisations
 */
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Organizations", description = "API réactive de gestion des organisations")
public class OrganizationReactiveController {

    private final OrganizationReactiveService organizationService;

    @Operation(
        summary = "Créer une nouvelle organisation",
        description = "Crée une nouvelle organisation dans le système"
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationResponse>> createOrganization(
        @Valid @RequestBody CreateOrganizationRequest createRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("POST /api/v1/organizations - Creating organization: {}", createRequest.getName());

        return organizationService.createOrganization(createRequest, userPrincipal.getId())
            .map(organization -> ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Organisation créée avec succès")
                .data(organization)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Erreur lors de la création de l'organisation")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir toutes les organisations",
        description = "Récupère la liste des organisations selon les permissions de l'utilisateur"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<OrganizationResponse>>> getAllOrganizations(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations - Getting organizations for user: {}", userPrincipal.getId());

        Flux<OrganizationResponse> organizations = organizationService.findAll(userPrincipal.getId());

        return Mono.fromCallable(() -> ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(true)
                .message("Organisations récupérées avec succès")
                .data(organizations)
                .build())
            .onErrorReturn(ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(false)
                .message("Erreur lors de la récupération des organisations")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir une organisation par ID",
        description = "Récupère une organisation spécifique par son ID"
    )
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationResponse>> getOrganizationById(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/{} - Getting organization by ID", id);

        return organizationService.findById(id, userPrincipal.getId())
            .map(org -> ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Organisation trouvée")
                .data(org)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Organisation non trouvée")
                .data(null)
                .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message("Accès refusé à cette organisation")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération de l'organisation")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Mettre à jour une organisation",
        description = "Met à jour les informations d'une organisation"
    )
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationResponse>> updateOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @Valid @RequestBody UpdateOrganizationRequest updateRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/organizations/{} - Updating organization", id);

        return organizationService.updateOrganization(id, updateRequest, userPrincipal.getId())
            .map(organization -> ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Organisation mise à jour avec succès")
                .data(organization)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour de l'organisation")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Supprimer une organisation",
        description = "Supprime une organisation (soft delete)"
    )
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Void>> deleteOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("DELETE /api/v1/organizations/{} - Deleting organization", id);

        return organizationService.deleteById(id, userPrincipal.getId())
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Organisation supprimée avec succès")
                .data(null)
                .build()))
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<Void>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<Void>builder()
                .success(false)
                .message("Erreur lors de la suppression de l'organisation")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les organisations actives",
        description = "Récupère toutes les organisations actives"
    )
    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<OrganizationResponse>>> getActiveOrganizations(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/active - Getting active organizations");

        Flux<OrganizationResponse> organizations = organizationService.findAllActive(userPrincipal.getId());

        return Mono.fromCallable(() -> ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(true)
                .message("Organisations actives récupérées")
                .data(organizations)
                .build())
            .onErrorReturn(ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(false)
                .message("Erreur lors de la récupération des organisations actives")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Vérifier l'existence d'un nom d'organisation",
        description = "Vérifie si un nom d'organisation est déjà utilisé"
    )
    @GetMapping(value = "/exists/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Boolean>> checkOrganizationNameExists(
        @Parameter(description = "Nom à vérifier") @PathVariable String name) {

        log.info("GET /api/v1/organizations/exists/{} - Checking if organization name exists", name);

        return organizationService.existsByName(name)
            .map(exists -> ApiResponse.<Boolean>builder()
                .success(true)
                .message(exists ? "Nom existe" : "Nom disponible")
                .data(exists)
                .build())
            .onErrorReturn(ApiResponse.<Boolean>builder()
                .success(false)
                .message("Erreur lors de la vérification du nom d'organisation")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Activer/désactiver une organisation",
        description = "Change le statut actif d'une organisation"
    )
    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationResponse>> toggleOrganizationStatus(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @RequestParam boolean isActive,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PATCH /api/v1/organizations/{}/status - Toggling status to {}", id, isActive);

        return organizationService.toggleOrganizationStatus(id, isActive, userPrincipal.getId())
            .map(organization -> ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Statut de l'organisation modifié avec succès")
                .data(organization)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Erreur lors du changement de statut")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Vérifier une organisation",
        description = "Marque une organisation comme vérifiée (Super Admin uniquement)"
    )
    @PatchMapping(value = "/{id}/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationResponse>> verifyOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PATCH /api/v1/organizations/{}/verify - Verifying organization", id);

        return organizationService.verifyOrganization(id, userPrincipal.getId())
            .map(organization -> ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Organisation vérifiée avec succès")
                .data(organization)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Erreur lors de la vérification")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les organisations par type",
        description = "Récupère les organisations d'un type spécifique"
    )
    @GetMapping(value = "/type/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<OrganizationResponse>>> getOrganizationsByType(
        @Parameter(description = "Type d'organisation") @PathVariable OrganizationType type,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/type/{} - Getting organizations by type", type);

        Flux<OrganizationResponse> organizations = organizationService.findByType(type, userPrincipal.getId());

        return Mono.fromCallable(() -> ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(true)
                .message("Organisations trouvées pour le type " + type.getDisplayName())
                .data(organizations)
                .build())
            .onErrorReturn(ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(false)
                .message("Erreur lors de la recherche par type")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les organisations par localisation",
        description = "Récupère les organisations par ville et pays"
    )
    @GetMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<OrganizationResponse>>> getOrganizationsByLocation(
        @RequestParam String city,
        @RequestParam String country,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/location - Getting organizations in {} - {}", city, country);

        Flux<OrganizationResponse> organizations = organizationService.findByCityAndCountry(city, country, userPrincipal.getId());

        return Mono.fromCallable(() -> ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(true)
                .message("Organisations trouvées pour la localisation")
                .data(organizations)
                .build())
            .onErrorReturn(ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(false)
                .message("Erreur lors de la recherche par localisation")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les statistiques d'une organisation",
        description = "Récupère les statistiques détaillées d'une organisation"
    )
    @GetMapping(value = "/{id}/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationStatisticsResponse>> getOrganizationStatistics(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/{}/statistics - Getting organization statistics", id);

        return organizationService.getOrganizationStatistics(id, userPrincipal.getId())
            .map(stats -> ApiResponse.<OrganizationStatisticsResponse>builder()
                .success(true)
                .message("Statistiques de l'organisation récupérées")
                .data(stats)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<OrganizationStatisticsResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<OrganizationStatisticsResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationStatisticsResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération des statistiques")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Mettre à jour les statistiques d'une organisation",
        description = "Met à jour les statistiques d'une organisation (usage interne)"
    )
    @PutMapping(value = "/{id}/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationResponse>> updateOrganizationStatistics(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @Valid @RequestBody OrganizationStatisticsRequest statsRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/organizations/{}/statistics - Updating organization statistics", id);

        return organizationService.updateOrganizationStatistics(id, statsRequest)
            .map(organization -> ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Statistiques mises à jour avec succès")
                .data(organization)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour des statistiques")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Mettre à jour l'abonnement d'une organisation",
        description = "Met à jour le plan d'abonnement d'une organisation"
    )
    @PutMapping(value = "/{id}/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationResponse>> updateSubscription(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @Valid @RequestBody UpdateSubscriptionRequest subscriptionRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/organizations/{}/subscription - Updating subscription", id);

        return organizationService.updateSubscription(id, subscriptionRequest, userPrincipal.getId())
            .map(organization -> ApiResponse.<OrganizationResponse>builder()
                .success(true)
                .message("Abonnement mis à jour avec succès")
                .data(organization)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<OrganizationResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<OrganizationResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour de l'abonnement")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les organisations nécessitant une attention",
        description = "Récupère les organisations avec des alertes (abonnement expirant, non vérifiées, etc.)"
    )
    @GetMapping(value = "/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<OrganizationResponse>>> getOrganizationsNeedingAttention(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/alerts - Getting organizations needing attention");

        Flux<OrganizationResponse> organizations = organizationService.findOrganizationsNeedingAttention(userPrincipal.getId());

        return Mono.fromCallable(() -> ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(true)
                .message("Organisations nécessitant une attention trouvées")
                .data(organizations)
                .build())
            .onErrorReturn(ApiResponse.<Flux<OrganizationResponse>>builder()
                .success(false)
                .message("Erreur lors de la recherche d'alertes")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les agences d'une organisation",
        description = "Récupère toutes les agences d'une organisation"
    )
    @GetMapping(value = "/{id}/agencies", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<AgencyResponse>>> getOrganizationAgencies(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @RequestParam(defaultValue = "false") boolean activeOnly,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/{}/agencies - Getting organization agencies, activeOnly: {}", id, activeOnly);

        // Cette méthode sera déléguée au service des agences
        // Pour l'instant, nous retournons une réponse placeholder
        return Mono.fromCallable(() -> ApiResponse.<Flux<AgencyResponse>>builder()
            .success(true)
            .message("Redirection vers /api/v1/agencies/organization/" + id)
            .data(null)
            .build());
    }

    @Operation(
        summary = "Obtenir les statistiques générales",
        description = "Récupère les statistiques générales d'une organisation (tableau de bord)"
    )
    @GetMapping(value = "/{id}/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<OrganizationDashboardResponse>> getOrganizationDashboard(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/organizations/{}/stats - Getting organization dashboard", id);

        return organizationService.getOrganizationStatistics(id, userPrincipal.getId())
            .map(stats -> {
                // Convertir les statistiques en format dashboard
                OrganizationDashboardResponse dashboard = OrganizationDashboardResponse.builder()
                    .organizationId(stats.getOrganizationId())
                    .totalAgencies(stats.getTotalAgencies())
                    .activeAgencies(stats.getActiveAgencies())
                    .totalVehicles(stats.getTotalVehicles())
                    .totalDrivers(stats.getTotalDrivers())
                    .totalUsers(stats.getTotalUsers())
                    .monthlyRevenue(stats.getMonthlyRevenue())
                    .totalRentals(stats.getTotalRentals())
                    .agencyUtilization(stats.getAgencyUsagePercentage())
                    .vehicleUtilization(stats.getVehicleUsagePercentage())
                    .subscriptionStatus(stats.getIsSubscriptionActive() ? "ACTIVE" : "EXPIRED")
                    .alertsCount(calculateAlertsCount(stats))
                    .build();

                return ApiResponse.<OrganizationDashboardResponse>builder()
                    .success(true)
                    .message("Tableau de bord récupéré")
                    .data(dashboard)
                    .build();
            })
            .onErrorReturn(ApiResponse.<OrganizationDashboardResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération du tableau de bord")
                .data(null)
                .build());
    }

    // === MÉTHODES PRIVÉES ===

    /**
     * Calcule le nombre d'alertes pour une organisation
     */
    private int calculateAlertsCount(OrganizationStatisticsResponse stats) {
        int alerts = 0;

        // Abonnement expirant ou expiré
        if (!stats.getIsSubscriptionActive()) {
            alerts++;
        }

        // Utilisation proche de la limite (> 80%)
        if (stats.getAgencyUsagePercentage() > 80 ||
            stats.getVehicleUsagePercentage() > 80) {
            alerts++;
        }

        // Pas d'activité récente (plus de 30 jours)
        if (stats.getLastActivityAt() != null &&
            stats.getLastActivityAt().isBefore(java.time.LocalDateTime.now().minusDays(30))) {
            alerts++;
        }

        return alerts;
    }
}
