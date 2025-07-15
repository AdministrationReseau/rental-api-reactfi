package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.AgencyReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.security.UserPrincipal;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
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
 * Contrôleur réactif pour la gestion des agences
 */
@RestController
@RequestMapping("/api/v1/agencies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Agencies", description = "API réactive de gestion des agences")
public class AgencyReactiveController {

    private final AgencyReactiveService agencyService;

    @Operation(
        summary = "Créer une nouvelle agence",
        description = "Crée une nouvelle agence dans une organisation"
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyResponse>> createAgency(
        @Valid @RequestBody CreateAgencyRequest createRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("POST /api/v1/agencies - Creating agency: {} for organization: {}",
            createRequest.getName(), createRequest.getOrganizationId());

        return agencyService.createAgency(createRequest, userPrincipal.getId())
            .map(agency -> ApiResponse.<AgencyResponse>builder()
                .success(true)
                .message("Agence créée avec succès")
                .data(agency)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<AgencyResponse>builder()
                .success(false)
                .message("Erreur lors de la création de l'agence")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir une agence par ID",
        description = "Récupère les détails d'une agence spécifique"
    )
    @GetMapping(value = "/{agencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyResponse>> getAgencyById(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/agencies/{} - Getting agency details", agencyId);

        return agencyService.findById(agencyId)
            .map(agency -> ApiResponse.<AgencyResponse>builder()
                .success(true)
                .message("Agence trouvée")
                .data(agency)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<AgencyResponse>builder()
                .success(false)
                .message("Agence non trouvée")
                .data(null)
                .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message("Accès refusé à cette agence")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<AgencyResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération de l'agence")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Mettre à jour une agence",
        description = "Met à jour les informations d'une agence"
    )
    @PutMapping(value = "/{agencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyResponse>> updateAgency(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        @Valid @RequestBody UpdateAgencyRequest updateRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/agencies/{} - Updating agency", agencyId);

        return agencyService.updateAgency(agencyId, updateRequest, userPrincipal.getId())
            .map(agency -> ApiResponse.<AgencyResponse>builder()
                .success(true)
                .message("Agence mise à jour avec succès")
                .data(agency)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<AgencyResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour de l'agence")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Supprimer une agence",
        description = "Supprime une agence (soft delete)"
    )
    @DeleteMapping(value = "/{agencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Void>> deleteAgency(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("DELETE /api/v1/agencies/{} - Deleting agency", agencyId);

        return agencyService.deleteAgency(agencyId, userPrincipal.getId())
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Agence supprimée avec succès")
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
                .message("Erreur lors de la suppression de l'agence")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les agences d'une organisation",
        description = "Récupère toutes les agences d'une organisation"
    )
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<AgencyResponse>>> getAgenciesByOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID organizationId,
        @RequestParam(defaultValue = "false") boolean activeOnly,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/agencies/organization/{} - Getting agencies, activeOnly: {}",
            organizationId, activeOnly);

        Flux<AgencyResponse> agencies = activeOnly
            ? agencyService.findActiveByOrganizationId(organizationId)
            : agencyService.findByOrganizationId(organizationId);

        return Mono.fromCallable(() -> ApiResponse.<Flux<AgencyResponse>>builder()
                .success(true)
                .message("Agences récupérées avec succès")
                .data(agencies)
                .build())
            .onErrorReturn(ApiResponse.<Flux<AgencyResponse>>builder()
                .success(false)
                .message("Erreur lors de la récupération des agences")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les agences d'un gestionnaire",
        description = "Récupère toutes les agences gérées par un utilisateur"
    )
    @GetMapping(value = "/manager/{managerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<AgencyResponse>>> getAgenciesByManager(
        @Parameter(description = "ID du gestionnaire") @PathVariable UUID managerId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/agencies/manager/{} - Getting managed agencies", managerId);

        Flux<AgencyResponse> agencies = agencyService.findByManagerId(managerId);

        return Mono.fromCallable(() -> ApiResponse.<Flux<AgencyResponse>>builder()
                .success(true)
                .message("Agences du gestionnaire récupérées")
                .data(agencies)
                .build())
            .onErrorReturn(ApiResponse.<Flux<AgencyResponse>>builder()
                .success(false)
                .message("Erreur lors de la récupération des agences")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les agences par localisation",
        description = "Récupère les agences par ville et pays"
    )
    @GetMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<AgencyResponse>>> getAgenciesByLocation(
        @RequestParam String city,
        @RequestParam String country,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/agencies/location - Getting agencies in {} - {}", city, country);

        Flux<AgencyResponse> agencies = agencyService.findByCityAndCountry(city, country);

        return Mono.fromCallable(() -> ApiResponse.<Flux<AgencyResponse>>builder()
                .success(true)
                .message("Agences trouvées pour la localisation")
                .data(agencies)
                .build())
            .onErrorReturn(ApiResponse.<Flux<AgencyResponse>>builder()
                .success(false)
                .message("Erreur lors de la recherche par localisation")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Activer/désactiver une agence",
        description = "Change le statut actif d'une agence"
    )
    @PatchMapping(value = "/{agencyId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyResponse>> toggleAgencyStatus(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        @RequestParam boolean isActive,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PATCH /api/v1/agencies/{}/status - Toggling status to {}", agencyId, isActive);

        return agencyService.toggleAgencyStatus(agencyId, isActive, userPrincipal.getId())
            .map(agency -> ApiResponse.<AgencyResponse>builder()
                .success(true)
                .message("Statut de l'agence modifié avec succès")
                .data(agency)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<AgencyResponse>builder()
                .success(false)
                .message("Erreur lors du changement de statut")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Assigner un gestionnaire à une agence",
        description = "Assigne ou change le gestionnaire d'une agence"
    )
    @PatchMapping(value = "/{agencyId}/manager", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyResponse>> assignManager(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        @RequestParam UUID managerId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PATCH /api/v1/agencies/{}/manager - Assigning manager {}", agencyId, managerId);

        return agencyService.assignManager(agencyId, managerId, userPrincipal.getId())
            .map(agency -> ApiResponse.<AgencyResponse>builder()
                .success(true)
                .message("Gestionnaire assigné avec succès")
                .data(agency)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<AgencyResponse>builder()
                .success(false)
                .message("Erreur lors de l'assignation du gestionnaire")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les statistiques d'une agence",
        description = "Récupère les statistiques détaillées d'une agence"
    )
    @GetMapping(value = "/{agencyId}/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyStatisticsResponse>> getAgencyStatistics(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/agencies/{}/statistics - Getting agency statistics", agencyId);

        return agencyService.getAgencyStatistics(agencyId)
            .map(stats -> ApiResponse.<AgencyStatisticsResponse>builder()
                .success(true)
                .message("Statistiques de l'agence récupérées")
                .data(stats)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<AgencyStatisticsResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<AgencyStatisticsResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<AgencyStatisticsResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération des statistiques")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Mettre à jour les statistiques d'une agence",
        description = "Met à jour les statistiques d'une agence (usage interne)"
    )
    @PutMapping(value = "/{agencyId}/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyResponse>> updateAgencyStatistics(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        @Valid @RequestBody AgencyStatisticsRequest statsRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/agencies/{}/statistics - Updating agency statistics", agencyId);

        return agencyService.updateAgencyStatistics(agencyId, statsRequest)
            .map(agency -> ApiResponse.<AgencyResponse>builder()
                .success(true)
                .message("Statistiques mises à jour avec succès")
                .data(agency)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<AgencyResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<AgencyResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour des statistiques")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Obtenir les agences avec réservation en ligne",
        description = "Récupère les agences qui acceptent les réservations en ligne"
    )
    @GetMapping(value = "/online-booking", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<AgencyResponse>>> getAgenciesWithOnlineBooking() {
        log.info("GET /api/v1/agencies/online-booking - Getting agencies with online booking");

        Flux<AgencyResponse> agencies = agencyService.findAgenciesWithOnlineBooking();

        return Mono.fromCallable(() -> ApiResponse.<Flux<AgencyResponse>>builder()
                .success(true)
                .message("Agences avec réservation en ligne trouvées")
                .data(agencies)
                .build())
            .onErrorReturn(ApiResponse.<Flux<AgencyResponse>>builder()
                .success(false)
                .message("Erreur lors de la recherche")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Rechercher des agences dans une zone géographique",
        description = "Recherche les agences dans une zone géographique définie"
    )
    @GetMapping(value = "/search/bounds", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Flux<AgencyResponse>>> searchAgenciesInBounds(
        @RequestParam double minLat,
        @RequestParam double maxLat,
        @RequestParam double minLng,
        @RequestParam double maxLng) {

        log.info("GET /api/v1/agencies/search/bounds - Searching agencies in bounds: {},{} to {},{}",
            minLat, minLng, maxLat, maxLng);

        Flux<AgencyResponse> agencies = agencyService.findAgenciesInBounds(minLat, maxLat, minLng, maxLng);

        return Mono.fromCallable(() -> ApiResponse.<Flux<AgencyResponse>>builder()
                .success(true)
                .message("Agences trouvées dans la zone")
                .data(agencies)
                .build())
            .onErrorReturn(ApiResponse.<Flux<AgencyResponse>>builder()
                .success(false)
                .message("Erreur lors de la recherche géographique")
                .data(null)
                .build());
    }

    @Operation(
        summary = "Vérifier si une organisation peut créer une agence",
        description = "Vérifie les limites d'abonnement pour la création d'agence"
    )
    @GetMapping(value = "/can-create/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<Boolean>> canCreateAgency(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID organizationId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/agencies/can-create/{} - Checking agency creation limit", organizationId);

        return agencyService.canCreateAgency(organizationId)
            .map(canCreate -> ApiResponse.<Boolean>builder()
                .success(true)
                .message(canCreate ? "Création d'agence autorisée" : "Limite d'agences atteinte")
                .data(canCreate)
                .build())
            .onErrorReturn(ApiResponse.<Boolean>builder()
                .success(false)
                .message("Erreur lors de la vérification")
                .data(false)
                .build());
    }

    @Operation(
        summary = "Compter les agences d'une organisation",
        description = "Retourne le nombre d'agences (total et actives) d'une organisation"
    )
    @GetMapping(value = "/count/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiResponse<AgencyCountResponse>> countAgenciesByOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID organizationId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/agencies/count/{} - Counting agencies", organizationId);

        return agencyService.countByOrganizationId(organizationId)
            .zipWith(agencyService.countActiveByOrganizationId(organizationId))
            .map(tuple -> {
                AgencyCountResponse count = AgencyCountResponse.builder()
                    .organizationId(organizationId)
                    .totalAgencies(tuple.getT1().intValue())
                    .activeAgencies(tuple.getT2().intValue())
                    .inactiveAgencies(tuple.getT1().intValue() - tuple.getT2().intValue())
                    .build();

                return ApiResponse.<AgencyCountResponse>builder()
                    .success(true)
                    .message("Comptage des agences effectué")
                    .data(count)
                    .build();
            })
            .onErrorReturn(ApiResponse.<AgencyCountResponse>builder()
                .success(false)
                .message("Erreur lors du comptage")
                .data(null)
                .build());
    }
}
