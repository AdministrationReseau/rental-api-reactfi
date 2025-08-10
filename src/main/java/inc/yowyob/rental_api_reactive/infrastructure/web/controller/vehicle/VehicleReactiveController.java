package inc.yowyob.rental_api_reactive.infrastructure.web.controller.vehicle;

import inc.yowyob.rental_api_reactive.application.service.vehicle.VehicleReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.security.UserPrincipal;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.CreateVehicleRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.UpdateVehicleRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.UpdateVehicleStatusRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.VehicleResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.VehicleSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * Contrôleur réactif pour la gestion des véhicules
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicles", description = "API réactive de gestion des véhicules")
public class VehicleReactiveController {

    private final VehicleReactiveService vehicleService;

    @Operation(
        summary = "Créer un véhicule",
        description = "Crée un nouveau véhicule dans le système"
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('VEHICLE', 'WRITE')")
    public Mono<ApiResponse<VehicleResponse>> createVehicle(
        @Valid @RequestBody CreateVehicleRequest createRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("POST /api/v1/vehicles - Creating vehicle: {} {}",
            createRequest.getBrandId(), createRequest.getModel());

        return vehicleService.createVehicle(createRequest, userPrincipal.getId())
            .map(vehicle -> ApiResponse.<VehicleResponse>builder()
                .success(true)
                .message("Véhicule créé avec succès")
                .data(vehicle)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleResponse>builder()
                .success(false)
                .message("Erreur lors de la création du véhicule")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle created successfully"))
            .doOnError(error -> log.error("Failed to create vehicle", error));
    }

    @Operation(
        summary = "Mettre à jour un véhicule",
        description = "Met à jour les informations d'un véhicule existant"
    )
    @PutMapping(value = "/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'UPDATE')")
    public Mono<ApiResponse<VehicleResponse>> updateVehicle(
        @Parameter(description = "ID du véhicule à modifier")
        @PathVariable UUID vehicleId,
        @Valid @RequestBody UpdateVehicleRequest updateRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/vehicles/{} - Updating vehicle", vehicleId);

        return vehicleService.updateVehicle(vehicleId, updateRequest, userPrincipal.getId())
            .map(vehicle -> ApiResponse.<VehicleResponse>builder()
                .success(true)
                .message("Véhicule mis à jour avec succès")
                .data(vehicle)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour du véhicule")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle updated successfully: {}", vehicleId))
            .doOnError(error -> log.error("Failed to update vehicle: {}", vehicleId, error));
    }

    @Operation(
        summary = "Changer le statut d'un véhicule",
        description = "Change le statut d'un véhicule (disponible, loué, maintenance, etc.)"
    )
    @PatchMapping(value = "/{vehicleId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'CHANGE_STATUS')")
    public Mono<ApiResponse<VehicleResponse>> updateVehicleStatus(
        @Parameter(description = "ID du véhicule")
        @PathVariable UUID vehicleId,
        @Valid @RequestBody UpdateVehicleStatusRequest statusRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PATCH /api/v1/vehicles/{}/status - Updating status to: {}",
            vehicleId, statusRequest.getStatus());

        return vehicleService.updateVehicleStatus(vehicleId, statusRequest, userPrincipal.getId())
            .map(vehicle -> ApiResponse.<VehicleResponse>builder()
                .success(true)
                .message("Statut du véhicule mis à jour avec succès")
                .data(vehicle)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour du statut")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle status updated successfully: {}", vehicleId))
            .doOnError(error -> log.error("Failed to update vehicle status: {}", vehicleId, error));
    }

    @Operation(
        summary = "Récupérer un véhicule",
        description = "Récupère les détails d'un véhicule par son ID"
    )
    @GetMapping(value = "/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<VehicleResponse>> getVehicle(
        @Parameter(description = "ID du véhicule à récupérer")
        @PathVariable UUID vehicleId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/vehicles/{} - Fetching vehicle", vehicleId);

        return vehicleService.getVehicleById(vehicleId, userPrincipal.getId())
            .map(vehicle -> ApiResponse.<VehicleResponse>builder()
                .success(true)
                .message("Véhicule récupéré avec succès")
                .data(vehicle)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<VehicleResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération du véhicule")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle fetched successfully: {}", vehicleId))
            .doOnError(error -> log.error("Failed to fetch vehicle: {}", vehicleId, error));
    }

    @Operation(
        summary = "Lister les véhicules",
        description = "Récupère la liste des véhicules avec filtres optionnels"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<Flux<VehicleResponse>>> getAllVehicles(
        @Parameter(description = "ID de l'organisation (optionnel)")
        @RequestParam(required = false) UUID organizationId,
        @Parameter(description = "Uniquement les véhicules actifs")
        @RequestParam(required = false, defaultValue = "true") Boolean activeOnly,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/vehicles - Fetching vehicles for organization: {}, activeOnly: {}",
            organizationId, activeOnly);

        Flux<VehicleResponse> vehicles = vehicleService.getVehiclesByOrganization(
            organizationId, activeOnly, userPrincipal.getId());

        return Mono.just(ApiResponse.<Flux<VehicleResponse>>builder()
                .success(true)
                .message("Véhicules récupérés avec succès")
                .data(vehicles)
                .build())
            .doOnSuccess(response -> log.info("Vehicles fetched successfully"))
            .doOnError(error -> log.error("Failed to fetch vehicles", error));
    }

    @Operation(
        summary = "Lister les véhicules par agence",
        description = "Récupère la liste des véhicules d'une agence spécifique"
    )
    @GetMapping(value = "/agency/{agencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<Flux<VehicleResponse>>> getVehiclesByAgency(
        @Parameter(description = "ID de l'agence")
        @PathVariable UUID agencyId,
        @Parameter(description = "Uniquement les véhicules actifs")
        @RequestParam(required = false, defaultValue = "true") Boolean activeOnly,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/vehicles/agency/{} - Fetching vehicles for agency, activeOnly: {}",
            agencyId, activeOnly);

        Flux<VehicleResponse> vehicles = vehicleService.getVehiclesByAgency(
            agencyId, activeOnly, userPrincipal.getId());

        return Mono.just(ApiResponse.<Flux<VehicleResponse>>builder()
                .success(true)
                .message("Véhicules de l'agence récupérés avec succès")
                .data(vehicles)
                .build())
            .doOnSuccess(response -> log.info("Agency vehicles fetched successfully"))
            .doOnError(error -> log.error("Failed to fetch agency vehicles", error));
    }

    @Operation(
        summary = "Lister les véhicules disponibles",
        description = "Récupère la liste des véhicules disponibles pour location"
    )
    @GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<Flux<VehicleResponse>>> getAvailableVehicles(
        @Parameter(description = "ID de l'organisation (optionnel)")
        @RequestParam(required = false) UUID organizationId,
        @Parameter(description = "ID de l'agence (optionnel)")
        @RequestParam(required = false) UUID agencyId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/vehicles/available - Fetching available vehicles for org: {}, agency: {}",
            organizationId, agencyId);

        Flux<VehicleResponse> vehicles = vehicleService.getAvailableVehicles(
            organizationId, agencyId, userPrincipal.getId());

        return Mono.just(ApiResponse.<Flux<VehicleResponse>>builder()
                .success(true)
                .message("Véhicules disponibles récupérés avec succès")
                .data(vehicles)
                .build())
            .doOnSuccess(response -> log.info("Available vehicles fetched successfully"))
            .doOnError(error -> log.error("Failed to fetch available vehicles", error));
    }

    @Operation(
        summary = "Recherche avancée de véhicules",
        description = "Effectue une recherche avancée de véhicules avec critères multiples"
    )
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<Flux<VehicleResponse>>> searchVehicles(
        @Valid @RequestBody VehicleSearchRequest searchRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("POST /api/v1/vehicles/search - Advanced vehicle search");

        Flux<VehicleResponse> vehicles = vehicleService.searchVehicles(searchRequest, userPrincipal.getId());

        return Mono.just(ApiResponse.<Flux<VehicleResponse>>builder()
                .success(true)
                .message("Recherche de véhicules effectuée avec succès")
                .data(vehicles)
                .build())
            .doOnSuccess(response -> log.info("Vehicle search completed successfully"))
            .doOnError(error -> log.error("Failed to search vehicles", error));
    }

    @Operation(
        summary = "Supprimer un véhicule",
        description = "Supprime un véhicule (suppression logique)"
    )
    @DeleteMapping(value = "/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('VEHICLE', 'DELETE')")
    public Mono<ApiResponse<Void>> deleteVehicle(
        @Parameter(description = "ID du véhicule à supprimer")
        @PathVariable UUID vehicleId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("DELETE /api/v1/vehicles/{} - Deleting vehicle", vehicleId);

        return vehicleService.deleteVehicle(vehicleId, userPrincipal.getId())
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Véhicule supprimé avec succès")
                .build()))
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<Void>builder()
                    .success(false)
                    .message(error.getMessage())
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Accès refusé")
                    .build()))
            .onErrorReturn(ApiResponse.<Void>builder()
                .success(false)
                .message("Erreur lors de la suppression du véhicule")
                .build())
            .doOnSuccess(response -> log.info("Vehicle deleted successfully: {}", vehicleId))
            .doOnError(error -> log.error("Failed to delete vehicle: {}", vehicleId, error));
    }
}
