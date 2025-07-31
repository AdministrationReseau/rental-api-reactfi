package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.DriverReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ChangeDriverStatusRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.CreateDriverRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.DriverResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UpdateDriverRequest;
import inc.yowyob.rental_api_reactive.application.dto.DriverStatus;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Contrôleur réactif pour la gestion des profils de chauffeurs.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Management", description = "APIs pour la gestion des profils de chauffeurs")
@SecurityRequirement(name = "bearerAuth")
public class DriverReactiveController {

    private final DriverReactiveService driverService;

    // === ENDPOINTS CRUD STANDARDS ===

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Créer un nouveau profil de chauffeur")
    public Mono<Object> createDriver(
        @Valid @RequestBody CreateDriverRequest createRequest,
        @Parameter(hidden = true) @RequestHeader("X-User-Id") String createdBy
    ) {
        log.info("POST /drivers - Creating driver for user ID: {}", createRequest.getUserId());
        return driverService.createDriver(createRequest, UUID.fromString(createdBy))
            .map(driverDto -> ApiResponse.success(driverDto, "Profil de chauffeur créé avec succès.", HttpStatus.CREATED))
            .doOnError(e -> log.error("Error creating driver: {}", e.getMessage()));
    }

    @GetMapping("/{driverId}")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer un chauffeur par son ID")
    public Mono<ApiResponse<DriverResponse>> getDriverById(@PathVariable UUID driverId) {
        log.info("GET /drivers/{} - Fetching driver", driverId);
        return driverService.getDriverById(driverId)
            .map(driverDto -> ApiResponse.success(driverDto, "Chauffeur trouvé avec succès."))
            .switchIfEmpty(Mono.just(ApiResponse.error("Chauffeur non trouvé: " + driverId, HttpStatus.NOT_FOUND)))
            .doOnError(e -> log.error("Error fetching driver {}: {}", driverId, e.getMessage()));
    }

    @PutMapping("/{driverId}")
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Mettre à jour un profil de chauffeur")
    public Mono<ApiResponse<DriverResponse>> updateDriver(
        @PathVariable UUID driverId,
        @Valid @RequestBody UpdateDriverRequest updateRequest,
        @Parameter(hidden = true) @RequestHeader("X-User-Id") String updatedBy
    ) {
        log.info("PUT /drivers/{} - Updating driver", driverId);
        return driverService.updateDriver(driverId, updateRequest, UUID.fromString(updatedBy))
            .map(driverDto -> ApiResponse.success(driverDto, "Profil mis à jour avec succès."))
            .doOnError(e -> log.error("Error updating driver {}: {}", driverId, e.getMessage()));
    }

    @DeleteMapping("/{driverId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('DELETE_DRIVER')")
    @Operation(summary = "Supprimer un profil de chauffeur")
    public Mono<Void> deleteDriver(@PathVariable UUID driverId) {
        log.info("DELETE /drivers/{} - Deleting driver", driverId);
        return driverService.deleteDriver(driverId)
            .doOnError(e -> log.error("Error deleting driver {}: {}", driverId, e.getMessage()));
    }

    // === ENDPOINTS DE GESTION DU STATUT ===

    @PutMapping("/{driverId}/status")
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Changer le statut d'un chauffeur")
    public Mono<ResponseEntity<ApiResponse<DriverResponse>>> changeDriverStatus(
        @PathVariable UUID driverId,
        @Valid @RequestBody ChangeDriverStatusRequest request,
        @RequestHeader("X-User-Id") String updatedBy
    ) {
        log.info("PUT /drivers/{}/status - Changing status to {}", driverId, request.getStatus());
        return driverService.changeDriverStatus(driverId, request, UUID.fromString(updatedBy))
            .map(response -> ResponseEntity.ok(ApiResponse.success(response, "Statut changé avec succès.")))
            .onErrorResume(NoSuchElementException.class, e -> 
                Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Chauffeur non trouvé: " + driverId, HttpStatus.NOT_FOUND))))
            .onErrorResume(IllegalStateException.class, e -> 
                Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Transition de statut invalide: " + e.getMessage(), HttpStatus.BAD_REQUEST))));
    }

    @PutMapping("/{driverId}/on-duty")
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Mettre un chauffeur en service")
    public Mono<ResponseEntity<ApiResponse<DriverResponse>>> setDriverOnDuty(
        @PathVariable UUID driverId,
        @RequestHeader("X-User-Id") String updatedBy
    ) {
        log.info("PUT /drivers/{}/on-duty", driverId);
        return driverService.setDriverOnDuty(driverId, UUID.fromString(updatedBy))
            .map(response -> ResponseEntity.ok(ApiResponse.success(response, "Chauffeur mis en service.")))
            .onErrorResume(this::handleStatusChangeError);
    }

    @PutMapping("/{driverId}/off-duty")
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Mettre un chauffeur hors service")
    public Mono<ResponseEntity<ApiResponse<DriverResponse>>> setDriverOffDuty(
        @PathVariable UUID driverId,
        @RequestHeader("X-User-Id") String updatedBy
    ) {
        log.info("PUT /drivers/{}/off-duty", driverId);
        return driverService.setDriverOffDuty(driverId, UUID.fromString(updatedBy))
            .map(response -> ResponseEntity.ok(ApiResponse.success(response, "Chauffeur mis hors service.")))
            .onErrorResume(this::handleStatusChangeError);
    }

    @PutMapping("/{driverId}/available")
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Rendre un chauffeur disponible")
    public Mono<ResponseEntity<ApiResponse<DriverResponse>>> setDriverAvailable(
        @PathVariable UUID driverId,
        @RequestHeader("X-User-Id") String updatedBy
    ) {
        log.info("PUT /drivers/{}/available", driverId);
        return driverService.setDriverAvailable(driverId, UUID.fromString(updatedBy))
            .map(response -> ResponseEntity.ok(ApiResponse.success(response, "Chauffeur rendu disponible.")))
            .onErrorResume(this::handleStatusChangeError);
    }

    @PutMapping("/{driverId}/on-leave")
    @PreAuthorize("hasAuthority('WRITE_DRIVER')")
    @Operation(summary = "Mettre un chauffeur en congé")
    public Mono<ResponseEntity<ApiResponse<DriverResponse>>> setDriverOnLeave(
        @PathVariable UUID driverId,
        @RequestHeader("X-User-Id") String updatedBy
    ) {
        log.info("PUT /drivers/{}/on-leave", driverId);
        return driverService.setDriverOnLeave(driverId, UUID.fromString(updatedBy))
            .map(response -> ResponseEntity.ok(ApiResponse.success(response, "Chauffeur mis en congé.")))
            .onErrorResume(this::handleStatusChangeError);
    }

    // === ENDPOINTS DE RÉCUPÉRATION PAR ORGANISATION ===

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer tous les chauffeurs d'une organisation")
    public Mono<ApiResponse<List<DriverResponse>>> getDriversByOrganization(
        @PathVariable UUID organizationId,
        @ParameterObject Pageable pageable
    ) {
        log.info("GET /drivers/organization/{}", organizationId);
        return driverService.getAllDriversByOrganization(organizationId, pageable)
            .collectList()
            .map(drivers -> ApiResponse.success(drivers, "Chauffeurs récupérés avec succès."));
    }

    @GetMapping("/organization/{organizationId}/available")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer les chauffeurs disponibles d'une organisation")
    public Mono<ApiResponse<List<DriverResponse>>> getAvailableDriversByOrganization(
        @PathVariable UUID organizationId
    ) {
        log.info("GET /drivers/organization/{}/available", organizationId);
        return driverService.getAvailableDriversByOrganization(organizationId)
            .collectList()
            .map(drivers -> ApiResponse.success(drivers, "Chauffeurs disponibles récupérés."));
    }

    @GetMapping("/organization/{organizationId}/on-duty")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer les chauffeurs en service d'une organisation")
    public Mono<ApiResponse<List<DriverResponse>>> getOnDutyDriversByOrganization(
        @PathVariable UUID organizationId
    ) {
        log.info("GET /drivers/organization/{}/on-duty", organizationId);
        return driverService.getOnDutyDriversByOrganization(organizationId)
            .collectList()
            .map(drivers -> ApiResponse.success(drivers, "Chauffeurs en service récupérés."));
    }

    @GetMapping("/organization/{organizationId}/status/{status}")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer les chauffeurs par statut")
    public Mono<ApiResponse<List<DriverResponse>>> getDriversByStatus(
        @PathVariable UUID organizationId,
        @PathVariable DriverStatus status
    ) {
        log.info("GET /drivers/organization/{}/status/{}", organizationId, status);
        return driverService.getDriversByStatus(status, organizationId)
            .collectList()
            .map(drivers -> ApiResponse.success(drivers, "Chauffeurs avec statut " + status + " récupérés."));
    }

    @GetMapping("/organization/{organizationId}/statistics")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Statistiques des chauffeurs par statut")
    public Mono<ResponseEntity<ApiResponse<Map<DriverStatus, Long>>>> getDriverStatusStatistics(
        @PathVariable UUID organizationId
    ) {
        log.info("GET /drivers/organization/{}/statistics", organizationId);
        return driverService.getDriverStatusStatistics(organizationId)
            .map(stats -> ResponseEntity.ok(ApiResponse.success(stats, "Statistiques récupérées.")));
    }

    // === ENDPOINTS PAR AGENCE ===

    @GetMapping("/agency/{agencyId}")
    @PreAuthorize("hasAuthority('READ_DRIVER')")
    @Operation(summary = "Récupérer les chauffeurs d'une agence")
    public Mono<ApiResponse<List<DriverResponse>>> getDriversByAgency(
        @PathVariable UUID agencyId,
        @ParameterObject Pageable pageable
    ) {
        log.info("GET /drivers/agency/{}", agencyId);
        return driverService.getAllDriversByAgency(agencyId, pageable)
            .collectList()
            .map(drivers -> ApiResponse.success(drivers, "Chauffeurs de l'agence récupérés."));
    }

    // === MÉTHODE UTILITAIRE POUR GESTION D'ERREURS ===

    private Mono<ResponseEntity<ApiResponse<DriverResponse>>> handleStatusChangeError(Throwable error) {
        if (error instanceof NoSuchElementException) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Chauffeur non trouvé", HttpStatus.NOT_FOUND)));
        } else if (error instanceof IllegalStateException) {
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Transition de statut invalide: " + error.getMessage(), HttpStatus.BAD_REQUEST)));
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur interne: " + error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
        }
    }
}