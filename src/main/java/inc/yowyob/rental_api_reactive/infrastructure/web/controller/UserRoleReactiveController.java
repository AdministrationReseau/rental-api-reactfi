package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.UserRoleReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Role Assignment", description = "APIs d'assignation des rôles aux utilisateurs")
public class UserRoleReactiveController {

    private final UserRoleReactiveService userRoleService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Assigner un rôle", description = "Assigne un rôle à un utilisateur")
    @PreAuthorize("hasPermission('USER', 'MANAGE_ROLES')")
    public Mono<ApiResponse<UserRoleResponse>> assignRole(
        @Valid @RequestBody AssignRoleRequest request,
        @Parameter(description = "ID de l'utilisateur assignant le rôle")
        @RequestHeader("X-User-Id") UUID assignedBy) {

        log.info("POST /api/v1/user-roles - Assigning role {} to user {}",
            request.getRoleId(), request.getUserId());

        return userRoleService.assignRole(request, assignedBy)
            .map(userRole -> ApiResponse.<UserRoleResponse>builder()
                .success(true)
                .message("Rôle assigné avec succès")
                .data(userRole)
                .build())
            .doOnSuccess(response -> log.info("Role assigned: {}", response.getData().getId()))
            .doOnError(error -> log.error("Failed to assign role", error));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Révoquer un rôle", description = "Révoque un rôle spécifique d'un utilisateur")
    @PreAuthorize("hasPermission('USER', 'MANAGE_ROLES')")
    public Mono<ApiResponse<Void>> revokeRole(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId,
        @Parameter(description = "ID de l'utilisateur révoquant le rôle")
        @RequestHeader("X-User-Id") UUID revokedBy) {

        log.info("DELETE /api/v1/user-roles/{}/roles/{} - Revoking role", userId, roleId);

        return userRoleService.revokeRole(userId, roleId, revokedBy)
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Rôle révoqué avec succès")
                .build()))
            .doOnSuccess(response -> log.info("Role revoked for user: {}", userId))
            .doOnError(error -> log.error("Failed to revoke role", error));
    }

    @GetMapping(value = "/{userId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les rôles d'un utilisateur", description = "Récupère tous les rôles actifs d'un utilisateur")
    @PreAuthorize("hasPermission('USER', 'READ')")
    public Mono<ApiResponse<Flux<UserRoleResponse>>> getUserRoles(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId) {

        log.info("GET /api/v1/user-roles/{}/roles - Getting user roles", userId);

        Flux<UserRoleResponse> userRoles = userRoleService.getUserRoles(userId);

        return Mono.just(ApiResponse.<Flux<UserRoleResponse>>builder()
            .success(true)
            .message("Rôles utilisateur récupérés avec succès")
            .data(userRoles)
            .build());
    }

    @GetMapping(value = "/roles/{roleId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les utilisateurs par rôle", description = "Récupère tous les utilisateurs ayant un rôle spécifique")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<Flux<UserRoleResponse>>> getUsersByRole(
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId) {

        log.info("GET /api/v1/user-roles/roles/{}/users - Getting users by role", roleId);

        Flux<UserRoleResponse> userRoles = userRoleService.getUsersByRole(roleId);

        return Mono.just(ApiResponse.<Flux<UserRoleResponse>>builder()
            .success(true)
            .message("Utilisateurs avec le rôle récupérés avec succès")
            .data(userRoles)
            .build());
    }

    @DeleteMapping("/{userId}/roles")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Révoquer tous les rôles", description = "Révoque tous les rôles d'un utilisateur")
    @PreAuthorize("hasPermission('USER', 'MANAGE_ROLES')")
    public Mono<ApiResponse<Void>> revokeAllUserRoles(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @Parameter(description = "ID de l'utilisateur révoquant les rôles")
        @RequestHeader("X-User-Id") UUID revokedBy) {

        log.info("DELETE /api/v1/user-roles/{}/roles - Revoking all roles", userId);

        return userRoleService.revokeAllUserRoles(userId, revokedBy)
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Tous les rôles révoqués avec succès")
                .build()))
            .doOnSuccess(response -> log.info("All roles revoked for user: {}", userId));
    }

    @PutMapping(value = "/{userId}/roles/{roleId}/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Activer un rôle", description = "Active un rôle temporairement désactivé")
    @PreAuthorize("hasPermission('USER', 'MANAGE_ROLES')")
    public Mono<ApiResponse<UserRoleResponse>> activateRole(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId) {

        log.info("PUT /api/v1/user-roles/{}/roles/{}/activate - Activating role", userId, roleId);

        return userRoleService.activateRole(userId, roleId)
            .map(userRole -> ApiResponse.<UserRoleResponse>builder()
                .success(true)
                .message("Rôle activé avec succès")
                .data(userRole)
                .build());
    }

    @PutMapping(value = "/{userId}/roles/{roleId}/extend", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Étendre l'expiration", description = "Étend la date d'expiration d'un rôle")
    @PreAuthorize("hasPermission('USER', 'MANAGE_ROLES')")
    public Mono<ApiResponse<UserRoleResponse>> extendRoleExpiration(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId,
        @Valid @RequestBody ExtendRoleExpirationRequest request) {

        log.info("PUT /api/v1/user-roles/{}/roles/{}/extend - Extending role expiration", userId, roleId);

        return userRoleService.extendRoleExpiration(userId, roleId, request.getNewExpirationDate())
            .map(userRole -> ApiResponse.<UserRoleResponse>builder()
                .success(true)
                .message("Expiration du rôle étendue avec succès")
                .data(userRole)
                .build());
    }
}
