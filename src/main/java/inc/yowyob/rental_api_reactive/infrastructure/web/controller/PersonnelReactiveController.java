package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.PersonnelReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * Contrôleur réactif pour la gestion du personnel
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/personnel")
@RequiredArgsConstructor
@Tag(name = "Personnel Management", description = "APIs réactives de gestion du personnel d'organisation")
public class PersonnelReactiveController {

    private final PersonnelReactiveService personnelService;

    @Operation(
        summary = "Créer un nouveau membre du personnel",
        description = "Crée un nouveau utilisateur de type personnel (AGENCY_MANAGER, RENTAL_AGENT, DRIVER) et l'affecte à une agence"
    )
    @PostMapping
    public Mono<ApiResponse<PersonnelResponse>> createPersonnel(
        @Valid @RequestBody CreatePersonnelRequest createRequest,
        @Parameter(description = "ID de l'utilisateur connecté (ORGANIZATION_OWNER)")
        @RequestHeader("X-User-Id") UUID createdBy) {

        log.info("POST /personnel - Creating personnel: {} for organization: {}",
            createRequest.getEmail(), createRequest.getOrganizationId());

        return personnelService.createPersonnel(createRequest, createdBy)
            .map(personnel -> ApiResponse.<PersonnelResponse>builder()
                .success(true)
                .message("Personnel créé avec succès")
                .data(personnel)
                .build())
            .doOnSuccess(response -> log.info("Personnel created successfully: {}", createRequest.getEmail()))
            .doOnError(error -> log.error("Failed to create personnel: {}", createRequest.getEmail(), error));
    }

    @Operation(
        summary = "Récupérer tous les membres du personnel",
        description = "Retourne la liste du personnel d'une organisation avec filtres optionnels"
    )
    @GetMapping
    public Mono<ApiResponse<java.util.List<PersonnelResponse>>> getAllPersonnel(
        @Parameter(description = "ID de l'organisation")
        @RequestParam UUID organizationId,
        @Parameter(description = "ID de l'agence (optionnel)")
        @RequestParam(required = false) UUID agencyId,
        @Parameter(description = "Type d'utilisateur (optionnel)")
        @RequestParam(required = false) UserType userType,
        @Parameter(description = "Statut actif (optionnel)")
        @RequestParam(required = false) Boolean isActive) {

        log.info("GET /personnel - Fetching personnel for organization: {}, agency: {}",
            organizationId, agencyId);

        return personnelService.getPersonnelByFilters(organizationId, agencyId, userType, isActive)
            .collectList()
            .map(personnel -> ApiResponse.<java.util.List<PersonnelResponse>>builder()
                .success(true)
                .message("Personnel récupéré avec succès")
                .data(personnel)
                .count((long) personnel.size())
                .build())
            .doOnSuccess(response -> log.info("Retrieved {} personnel members", response.getData().size()));
    }

    @Operation(
        summary = "Récupérer un membre du personnel par ID",
        description = "Retourne les détails d'un membre du personnel spécifique"
    )
    @GetMapping("/{personnelId}")
    public Mono<ApiResponse<PersonnelResponse>> getPersonnelById(
        @Parameter(description = "ID du personnel")
        @PathVariable UUID personnelId) {

        log.info("GET /personnel/{} - Fetching personnel details", personnelId);

        return personnelService.getPersonnelById(personnelId)
            .map(personnel -> ApiResponse.<PersonnelResponse>builder()
                .success(true)
                .message("Personnel trouvé avec succès")
                .data(personnel)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<PersonnelResponse>builder()
                .success(false)
                .message("Personnel non trouvé")
                .data(null)
                .build()))
            .doOnSuccess(response -> log.info("Personnel details retrieved: {}", personnelId));
    }

    @Operation(
        summary = "Mettre à jour un membre du personnel",
        description = "Met à jour les informations d'un membre du personnel"
    )
    @PutMapping("/{personnelId}")
    public Mono<ApiResponse<PersonnelResponse>> updatePersonnel(
        @Parameter(description = "ID du personnel")
        @PathVariable UUID personnelId,
        @Valid @RequestBody UpdatePersonnelRequest updateRequest,
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID updatedBy) {

        log.info("PUT /personnel/{} - Updating personnel", personnelId);

        return personnelService.updatePersonnel(personnelId, updateRequest, updatedBy)
            .map(personnel -> ApiResponse.<PersonnelResponse>builder()
                .success(true)
                .message("Personnel mis à jour avec succès")
                .data(personnel)
                .build())
            .doOnSuccess(response -> log.info("Personnel updated successfully: {}", personnelId))
            .doOnError(error -> log.error("Failed to update personnel: {}", personnelId, error));
    }

    @Operation(
        summary = "Assigner du personnel à une agence",
        description = "Assigne ou réassigne un membre du personnel à une agence"
    )
    @PutMapping("/{personnelId}/agency")
    public Mono<ApiResponse<PersonnelResponse>> assignToAgency(
        @Parameter(description = "ID du personnel")
        @PathVariable UUID personnelId,
        @Parameter(description = "ID de la nouvelle agence")
        @RequestParam UUID agencyId,
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID assignedBy) {

        log.info("PUT /personnel/{}/agency - Assigning to agency: {}", personnelId, agencyId);

        return personnelService.assignToAgency(personnelId, agencyId, assignedBy)
            .map(personnel -> ApiResponse.<PersonnelResponse>builder()
                .success(true)
                .message("Personnel assigné à l'agence avec succès")
                .data(personnel)
                .build())
            .doOnSuccess(response -> log.info("Personnel assigned to agency successfully: {}", personnelId))
            .doOnError(error -> log.error("Failed to assign personnel to agency: {}", personnelId, error));
    }

    @Operation(
        summary = "Activer/Désactiver un membre du personnel",
        description = "Active ou désactive un compte personnel"
    )
    @PutMapping("/{personnelId}/status")
    public Mono<ApiResponse<PersonnelResponse>> updateStatus(
        @Parameter(description = "ID du personnel")
        @PathVariable UUID personnelId,
        @Parameter(description = "Nouveau statut")
        @RequestParam Boolean isActive,
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID updatedBy) {

        log.info("PUT /personnel/{}/status - Updating status to: {}", personnelId, isActive);

        return personnelService.updateStatus(personnelId, isActive, updatedBy)
            .map(personnel -> ApiResponse.<PersonnelResponse>builder()
                .success(true)
                .message(isActive ? "Personnel activé avec succès" : "Personnel désactivé avec succès")
                .data(personnel)
                .build())
            .doOnSuccess(response -> log.info("Personnel status updated successfully: {}", personnelId))
            .doOnError(error -> log.error("Failed to update personnel status: {}", personnelId, error));
    }

    @Operation(
        summary = "Supprimer un membre du personnel",
        description = "Supprime définitivement un compte personnel (action irréversible)"
    )
    @DeleteMapping("/{personnelId}")
    public Mono<ApiResponse<String>> deletePersonnel(
        @Parameter(description = "ID du personnel")
        @PathVariable UUID personnelId,
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID deletedBy) {

        log.info("DELETE /personnel/{} - Deleting personnel", personnelId);

        return personnelService.deletePersonnel(personnelId, deletedBy)
            .then(Mono.just(ApiResponse.<String>builder()
                .success(true)
                .message("Personnel supprimé avec succès")
                .data("Personnel deleted")
                .build()))
            .doOnSuccess(response -> log.info("Personnel deleted successfully: {}", personnelId))
            .doOnError(error -> log.error("Failed to delete personnel: {}", personnelId, error));
    }

    @Operation(
        summary = "Récupérer le personnel par agence",
        description = "Retourne tous les membres du personnel d'une agence spécifique"
    )
    @GetMapping("/agency/{agencyId}")
    public Mono<ApiResponse<java.util.List<PersonnelResponse>>> getPersonnelByAgency(
        @Parameter(description = "ID de l'agence")
        @PathVariable UUID agencyId,
        @Parameter(description = "Type d'utilisateur (optionnel)")
        @RequestParam(required = false) UserType userType) {

        log.info("GET /personnel/agency/{} - Fetching personnel for agency", agencyId);

        return personnelService.getPersonnelByAgency(agencyId, userType)
            .collectList()
            .map(personnel -> ApiResponse.<java.util.List<PersonnelResponse>>builder()
                .success(true)
                .message("Personnel de l'agence récupéré avec succès")
                .data(personnel)
                .count((long) personnel.size())
                .build())
            .doOnSuccess(response -> log.info("Retrieved {} personnel for agency: {}",
                response.getData().size(), agencyId));
    }
}
