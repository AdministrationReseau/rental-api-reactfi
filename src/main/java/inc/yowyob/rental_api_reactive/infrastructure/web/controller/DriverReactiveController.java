package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.DriverReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.CreateDriverRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.DriverResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UpdateDriverRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur réactif pour la gestion des profils de chauffeurs.
 * Un chauffeur est un profil professionnel associé à un utilisateur existant.
 * Route de base: /api/v1/drivers
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Management", description = "APIs pour la gestion des profils de chauffeurs")
@SecurityRequirement(name = "bearerAuth") // Applique la sécurité JWT à tous les endpoints de ce contrôleur
public class DriverReactiveController {

    private final DriverReactiveService driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Créer un nouveau profil de chauffeur", description = "Crée un profil de chauffeur pour un utilisateur existant et le lie à une organisation.")
    public Mono<Object> createDriver(
        @Valid @RequestBody CreateDriverRequest createRequest,
        @Parameter(hidden = true) @RequestHeader("X-User-Id") String createdBy // Récupéré depuis le token JWT par un filtre
    ) {
        log.info("POST /drivers - Attempting to create a driver for user ID: {}", createRequest.getUserId());
        return driverService.createDriver(createRequest, UUID.fromString(createdBy))
            .map(driverDto -> ApiResponse.success(driverDto, "Profil de chauffeur créé avec succès.", HttpStatus.CREATED))
            .doOnError(e -> log.error("Error creating driver profile: {}", e.getMessage()));
    }

    @GetMapping("/{driverId}")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer un chauffeur par son ID", description = "Retourne les informations complètes d'un profil de chauffeur spécifique.")
    public Mono<ApiResponse<DriverResponse>> getDriverById(
        @Parameter(description = "ID unique du profil de chauffeur") @PathVariable UUID driverId
    ) {
        log.info("GET /drivers/{} - Fetching driver profile", driverId);
        return driverService.getDriverById(driverId)
            .map(driverDto -> ApiResponse.success(driverDto, "Chauffeur trouvé avec succès."))
            .switchIfEmpty(Mono.just(ApiResponse.error("Chauffeur non trouvé avec l'ID: " + driverId, HttpStatus.NOT_FOUND)))
            .doOnError(e -> log.error("Error fetching driver profile {}: {}", driverId, e.getMessage()));
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer les chauffeurs d'une organisation", description = "Retourne une liste paginée de tous les chauffeurs associés à une organisation.")
    public Mono<ApiResponse<List<DriverResponse>>> getDriversByOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID organizationId,
        // @Parameter(description = "Statut des chauffeurs à filtrer (optionnel)") @RequestParam(required = false) DriverStatus status,
        @ParameterObject Pageable pageable // SpringDoc recommande @ParameterObject pour Pageable
    ) {
        log.info("GET /drivers/organization/{} - Fetching drivers with status {} and pageable {}", organizationId, pageable);
        return driverService.getAllDriversByOrganization(organizationId, pageable)
            .collectList()
            .map(driverList -> ApiResponse.success(driverList, "Chauffeurs de l'organisation récupérés avec succès."));
    }

    @GetMapping("/agency/{agencyId}")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer les chauffeurs d'une agence", description = "Retourne une liste paginée de tous les chauffeurs associés à une agence.")
    public Mono<ApiResponse<List<DriverResponse>>> getDriversByAgency(
        @Parameter(description = "ID de l'agence") @PathVariable UUID agencyId,
        // @Parameter(description = "Statut des chauffeurs à filtrer (optionnel)") @RequestParam(required = false) DriverStatus status,
        @ParameterObject Pageable pageable
    ) {
        log.info("GET /drivers/agency/{} - Fetching drivers with status {} and pageable {}", agencyId, pageable);
        return driverService.getAllDriversByAgency(agencyId, pageable)
            .collectList()
            .map(driverList -> ApiResponse.success(driverList, "Chauffeurs de l'agence récupérés avec succès."));
    }

    @PutMapping("/{driverId}")
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Mettre à jour un profil de chauffeur", description = "Met à jour les informations d'un profil de chauffeur existant.")
    public Mono<ApiResponse<DriverResponse>> updateDriver(
        @Parameter(description = "ID du profil de chauffeur à mettre à jour") @PathVariable UUID driverId,
        @Valid @RequestBody UpdateDriverRequest updateRequest,
        @Parameter(hidden = true) @RequestHeader("X-User-Id") String updatedBy
    ) {
        log.info("PUT /drivers/{} - Updating driver profile", driverId);
        return driverService.updateDriver(driverId, updateRequest, UUID.fromString(updatedBy))
            .map(driverDto -> ApiResponse.success(driverDto, "Profil de chauffeur mis à jour avec succès."))
            .doOnError(e -> log.error("Error updating driver profile {}: {}", driverId, e.getMessage()));
    }

    @DeleteMapping("/{driverId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('DELETE_DRIVER')")
    @Operation(summary = "Supprimer un profil de chauffeur", description = "Supprime un profil de chauffeur de manière logique ou permanente.")
    public Mono<Void> deleteDriver(
        @Parameter(description = "ID du profil de chauffeur à supprimer") @PathVariable UUID driverId
    ) {
        log.info("DELETE /drivers/{} - Deleting driver profile", driverId);
        return driverService.deleteDriver(driverId)
            .doOnError(e -> log.error("Error deleting driver profile {}: {}", driverId, e.getMessage()));
    }
}