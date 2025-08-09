package inc.yowyob.rental_api_reactive.infrastructure.web.controller.vehicle;

import inc.yowyob.rental_api_reactive.application.service.vehicle.VehicleImageReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.security.UserPrincipal;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * Contrôleur réactif pour la gestion des images de véhicules
 */
@RestController
@RequestMapping("/api/v1/vehicles/{vehicleId}/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicle Images", description = "API réactive de gestion des images de véhicules")
public class VehicleImageReactiveController {

    private final VehicleImageReactiveService vehicleImageService;

    @Operation(
        summary = "Uploader des images de véhicule",
        description = "Upload une ou plusieurs images pour un véhicule"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission('VEHICLE', 'MANAGE_IMAGES')")
    public Mono<ApiResponse<Flux<VehicleImageResponse>>> uploadVehicleImages(
        @Parameter(description = "ID du véhicule")
        @PathVariable UUID vehicleId,
        @Parameter(description = "Fichiers images à uploader")
        @RequestPart("files") Flux<FilePart> files,
        @Parameter(description = "Type d'image (optionnel)")
        @RequestParam(required = false) String imageType,
        @Parameter(description = "Définir comme image principale")
        @RequestParam(required = false, defaultValue = "false") Boolean isPrimary,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("POST /api/v1/vehicles/{}/images - Uploading images", vehicleId);

        Flux<VehicleImageResponse> uploadedImages = vehicleImageService.uploadVehicleImages(
            vehicleId, files, imageType, isPrimary, userPrincipal.getId());

        return Mono.just(ApiResponse.<Flux<VehicleImageResponse>>builder()
                .success(true)
                .message("Images uploadées avec succès")
                .data(uploadedImages)
                .build())
            .doOnSuccess(response -> log.info("Vehicle images uploaded successfully for vehicle: {}", vehicleId))
            .doOnError(error -> log.error("Failed to upload vehicle images: {}", vehicleId, error));
    }

    @Operation(
        summary = "Lister les images d'un véhicule",
        description = "Récupère toutes les images d'un véhicule"
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<Flux<VehicleImageResponse>>> getVehicleImages(
        @Parameter(description = "ID du véhicule")
        @PathVariable UUID vehicleId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/vehicles/{}/images - Fetching vehicle images", vehicleId);

        Flux<VehicleImageResponse> images = vehicleImageService.getVehicleImages(vehicleId, userPrincipal.getId());

        return Mono.just(ApiResponse.<Flux<VehicleImageResponse>>builder()
                .success(true)
                .message("Images du véhicule récupérées avec succès")
                .data(images)
                .build())
            .doOnSuccess(response -> log.info("Vehicle images fetched successfully for vehicle: {}", vehicleId))
            .doOnError(error -> log.error("Failed to fetch vehicle images: {}", vehicleId, error));
    }

    @Operation(
        summary = "Récupérer une image spécifique",
        description = "Récupère les détails d'une image spécifique"
    )
    @GetMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'READ')")
    public Mono<ApiResponse<VehicleImageResponse>> getVehicleImage(
        @Parameter(description = "ID du véhicule")
        @PathVariable UUID vehicleId,
        @Parameter(description = "ID de l'image")
        @PathVariable UUID imageId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("GET /api/v1/vehicles/{}/images/{} - Fetching specific vehicle image", vehicleId, imageId);

        return vehicleImageService.getVehicleImage(vehicleId, imageId, userPrincipal.getId())
            .map(image -> ApiResponse.<VehicleImageResponse>builder()
                .success(true)
                .message("Image du véhicule récupérée avec succès")
                .data(image)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleImageResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<VehicleImageResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleImageResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération de l'image")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle image fetched successfully: {}", imageId))
            .doOnError(error -> log.error("Failed to fetch vehicle image: {}", imageId, error));
    }

    @Operation(
        summary = "Définir une image comme principale",
        description = "Définit une image spécifique comme image principale du véhicule"
    )
    @PatchMapping(value = "/{imageId}/primary", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'MANAGE_IMAGES')")
    public Mono<ApiResponse<VehicleImageResponse>> setPrimaryImage(
        @Parameter(description = "ID du véhicule")
        @PathVariable UUID vehicleId,
        @Parameter(description = "ID de l'image")
        @PathVariable UUID imageId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PATCH /api/v1/vehicles/{}/images/{}/primary - Setting as primary image", vehicleId, imageId);

        return vehicleImageService.setPrimaryImage(vehicleId, imageId, userPrincipal.getId())
            .map(image -> ApiResponse.<VehicleImageResponse>builder()
                .success(true)
                .message("Image principale définie avec succès")
                .data(image)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleImageResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<VehicleImageResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleImageResponse>builder()
                .success(false)
                .message("Erreur lors de la définition de l'image principale")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Primary image set successfully: {}", imageId))
            .doOnError(error -> log.error("Failed to set primary image: {}", imageId, error));
    }

    @Operation(
        summary = "Mettre à jour les informations d'une image",
        description = "Met à jour les métadonnées d'une image (type, ordre d'affichage, etc.)"
    )
    @PutMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission('VEHICLE', 'MANAGE_IMAGES')")
    public Mono<ApiResponse<VehicleImageResponse>> updateVehicleImage(
        @Parameter(description = "ID du véhicule")
        @PathVariable UUID vehicleId,
        @Parameter(description = "ID de l'image")
        @PathVariable UUID imageId,
        @Valid @RequestBody UpdateVehicleImageRequest updateRequest,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("PUT /api/v1/vehicles/{}/images/{} - Updating image metadata", vehicleId, imageId);

        return vehicleImageService.updateVehicleImage(vehicleId, imageId, updateRequest, userPrincipal.getId())
            .map(image -> ApiResponse.<VehicleImageResponse>builder()
                .success(true)
                .message("Informations de l'image mises à jour avec succès")
                .data(image)
                .build())
            .onErrorResume(IllegalArgumentException.class, error ->
                Mono.just(ApiResponse.<VehicleImageResponse>builder()
                    .success(false)
                    .message(error.getMessage())
                    .data(null)
                    .build()))
            .onErrorResume(SecurityException.class, error ->
                Mono.just(ApiResponse.<VehicleImageResponse>builder()
                    .success(false)
                    .message("Accès refusé")
                    .data(null)
                    .build()))
            .onErrorReturn(ApiResponse.<VehicleImageResponse>builder()
                .success(false)
                .message("Erreur lors de la mise à jour de l'image")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Vehicle image updated successfully: {}", imageId))
            .doOnError(error -> log.error("Failed to update vehicle image: {}", imageId, error));
    }

    @Operation(
        summary = "Supprimer une image",
        description = "Supprime une image de véhicule"
    )
    @DeleteMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasPermission('VEHICLE', 'MANAGE_IMAGES')")
    public Mono<ApiResponse<Void>> deleteVehicleImage(
        @Parameter(description = "ID du véhicule")
        @PathVariable UUID vehicleId,
        @Parameter(description = "ID de l'image")
        @PathVariable UUID imageId,
        @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("DELETE /api/v1/vehicles/{}/images/{} - Deleting vehicle image", vehicleId, imageId);

        return vehicleImageService.deleteVehicleImage(vehicleId, imageId, userPrincipal.getId())
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Image supprimée avec succès")
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
                .message("Erreur lors de la suppression de l'image")
                .build())
            .doOnSuccess(response -> log.info("Vehicle image deleted successfully: {}", imageId))
            .doOnError(error -> log.error("Failed to delete vehicle image: {}", imageId, error));
    }
}
