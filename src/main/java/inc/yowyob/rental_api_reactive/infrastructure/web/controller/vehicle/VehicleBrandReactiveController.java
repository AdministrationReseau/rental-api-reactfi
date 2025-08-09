package inc.yowyob.rental_api_reactive.infrastructure.web.controller.vehicle;

import inc.yowyob.rental_api_reactive.application.service.vehicle.VehicleBrandReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.security.UserPrincipal;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
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
 * Contrôleur réactif pour la gestion des marques de véhicules
 */
@RestController
@RequestMapping("/api/v1/vehicle-brands")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicle Brands", description = "API réactive de gestion des marques de véhicules")
public class VehicleBrandReactiveController {

    private final VehicleBrandReactiveService vehicleBrandService;

    @Operation(
        summary = "Créer une marque de véhicule",
        description = "Crée une nouvelle marque de véhicule dans le système"
    )
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('VEHICLE', 'WRITE')")
    public Mono<ApiResponse<VehicleBrandResponse>> createVehicleBrand(
        @Valid @RequestBody CreateVehicleBrandRequest createRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("POST /api/v1/vehicle-brands - Creating brand: {}", createRequest.getName());

        return vehicleBrandService.createVehicleBrand(createRequest, userPrincipal.getId())
            .map(brand -> ApiResponse.<VehicleBrandResponse>builder()
                .success(true)
                .message("Marque de véhicule créée avec succès")
                .data(brand)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleBrandResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleBrandResponse>builder()
                .success(false)
                .message("Erreur lors de la création de la marque")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle brand created successfully"))
            .doOnError(error -> log.error("Failed to create vehicle brand", error));
    }

    @Operation(
        summary = "Mettre à jour une marque de véhicule",
        description = "Met à jour les informations d'une marque de véhicule existante"
    )
    @PutMapping(value = "/{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'UPDATE')")
    public Mono<ApiResponse<VehicleBrandResponse>> updateVehicleBrand(
        @Parameter(description = "ID de la marque à modifier")
        @PathVariable UUID brandId,
        @Valid @RequestBody UpdateVehicleBrandRequest updateRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/vehicle-brands/{} - Updating brand", brandId);

        return vehicleBrandService.updateVehicleBrand(brandId, updateRequest, userPrincipal.getId())
            .map(brand -> ApiResponse.<VehicleBrandResponse>builder()
                .success(true)
                .message("Marque de véhicule mise à jour avec succès")
                .data(brand)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleBrandResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleBrandResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour de la marque")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle brand updated successfully: {}", brandId))
            .doOnError(error -> log.error("Failed to update vehicle brand: {}", brandId, error));
    }

    @Operation(
        summary = "Récupérer une marque de véhicule",
        description = "Récupère les détails d'une marque de véhicule par son ID"
    )
    @GetMapping(value = "/{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<VehicleBrandResponse>> getVehicleBrand(
        @Parameter(description = "ID de la marque à récupérer")
        @PathVariable UUID brandId) {

        log.info("GET /api/v1/vehicle-brands/{} - Fetching brand", brandId);

        return vehicleBrandService.getVehicleBrandById(brandId)
            .map(brand -> ApiResponse.<VehicleBrandResponse>builder()
                .success(true)
                .message("Marque de véhicule récupérée avec succès")
                .data(brand)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleBrandResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleBrandResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération de la marque")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle brand fetched successfully: {}", brandId))
            .doOnError(error -> log.error("Failed to fetch vehicle brand: {}", brandId, error));
    }

    @Operation(
        summary = "Lister toutes les marques actives",
        description = "Récupère la liste de toutes les marques de véhicules actives"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<Flux<VehicleBrandResponse>>> getAllActiveBrands() {
        log.info("GET /api/v1/vehicle-brands - Fetching all active brands");

        Flux<VehicleBrandResponse> brands = vehicleBrandService.getAllActiveBrands();

        return Mono.just(ApiResponse.<Flux<VehicleBrandResponse>>builder()
                .success(true)
                .message("Marques de véhicules récupérées avec succès")
                .data(brands)
                .build())
            .doOnSuccess(response -> log.info("All active vehicle brands fetched successfully"))
            .doOnError(error -> log.error("Failed to fetch vehicle brands", error));
    }

    @Operation(
        summary = "Supprimer une marque de véhicule",
        description = "Supprime une marque de véhicule (suppression logique)"
    )
    @DeleteMapping(value = "/{brandId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('VEHICLE', 'DELETE')")
    public Mono<ApiResponse<Void>> deleteVehicleBrand(
        @Parameter(description = "ID de la marque à supprimer")
        @PathVariable UUID brandId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("DELETE /api/v1/vehicle-brands/{} - Deleting brand", brandId);

        return vehicleBrandService.deleteVehicleBrand(brandId, userPrincipal.getId())
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Marque de véhicule supprimée avec succès")
                .build()))
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<Void>builder()
                    .success(false)
                    .message(error.getMessage())
                    .build()))
            .onErrorReturn(ApiResponse.<Void>builder()
                .success(false)
                .message("Erreur lors de la suppression de la marque")
                .build())
            .doOnSuccess(response -> log.info("Vehicle brand deleted successfully: {}", brandId))
            .doOnError(error -> log.error("Failed to delete vehicle brand: {}", brandId, error));
    }
}
