package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.RoleReactiveService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Role Management", description = "APIs de gestion des rôles")
public class RoleReactiveController {

    private final RoleReactiveService roleService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un nouveau rôle", description = "Crée un nouveau rôle personnalisé dans une organisation")
    @PreAuthorize("hasPermission('ROLE', 'WRITE')")
    public Mono<ApiResponse<RoleResponse>> createRole(
        @Valid @RequestBody CreateRoleRequest request,
        @Parameter(description = "ID de l'utilisateur créateur")
        @RequestHeader("X-User-Id") UUID createdBy) {

        log.info("POST /api/v1/roles - Creating role: {}", request.getName());

        return roleService.createRole(request, createdBy)
            .map(role -> ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Rôle créé avec succès")
                .data(role)
                .build())
            .doOnSuccess(response -> log.info("Role created: {}", response.getData().getId()))
            .doOnError(error -> log.error("Failed to create role: {}", request.getName(), error));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister les rôles", description = "Récupère tous les rôles d'une organisation")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<Flux<RoleResponse>>> getRoles(
        @Parameter(description = "ID de l'organisation")
        @RequestParam UUID organizationId) {

        log.info("GET /api/v1/roles - Getting roles for organization: {}", organizationId);

        Flux<RoleResponse> roles = roleService.getRolesByOrganization(organizationId);

        return Mono.just(ApiResponse.<Flux<RoleResponse>>builder()
            .success(true)
            .message("Rôles récupérés avec succès")
            .data(roles)
            .build());
    }

    @GetMapping(value = "/{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un rôle", description = "Récupère les détails d'un rôle spécifique")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<RoleResponse>> getRole(
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId) {

        log.info("GET /api/v1/roles/{} - Getting role details", roleId);

        return roleService.getRoleById(roleId)
            .map(role -> ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Rôle récupéré avec succès")
                .data(role)
                .build())
            .doOnSuccess(response -> log.info("Role retrieved: {}", roleId))
            .doOnError(error -> log.error("Failed to get role: {}", roleId, error));
    }

    @PutMapping(value = "/{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modifier un rôle", description = "Met à jour les informations d'un rôle existant")
    @PreAuthorize("hasPermission('ROLE', 'UPDATE')")
    public Mono<ApiResponse<RoleResponse>> updateRole(
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId,
        @Valid @RequestBody UpdateRoleRequest request,
        @Parameter(description = "ID de l'utilisateur modificateur")
        @RequestHeader("X-User-Id") UUID updatedBy) {

        log.info("PUT /api/v1/roles/{} - Updating role", roleId);

        return roleService.updateRole(roleId, request, updatedBy)
            .map(role -> ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Rôle modifié avec succès")
                .data(role)
                .build())
            .doOnSuccess(response -> log.info("Role updated: {}", roleId))
            .doOnError(error -> log.error("Failed to update role: {}", roleId, error));
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un rôle", description = "Supprime un rôle personnalisé")
    @PreAuthorize("hasPermission('ROLE', 'DELETE')")
    public Mono<ApiResponse<Void>> deleteRole(
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId) {

        log.info("DELETE /api/v1/roles/{} - Deleting role", roleId);

        return roleService.deleteRole(roleId)
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Rôle supprimé avec succès")
                .build()))
            .doOnSuccess(response -> log.info("Role deleted: {}", roleId))
            .doOnError(error -> log.error("Failed to delete role: {}", roleId, error));
    }

    @GetMapping(value = "/{roleId}/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les permissions d'un rôle", description = "Récupère toutes les permissions associées à un rôle")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<RolePermissionsResponse>> getRolePermissions(
        @Parameter(description = "ID du rôle")
        @PathVariable UUID roleId) {

        log.info("GET /api/v1/roles/{}/permissions - Getting role permissions", roleId);

        return roleService.getRolePermissions(roleId)
            .map(permissions -> ApiResponse.<RolePermissionsResponse>builder()
                .success(true)
                .message("Permissions du rôle récupérées avec succès")
                .data(permissions)
                .build());
    }

    @PostMapping(value = "/{roleId}/clone", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cloner un rôle", description = "Crée une copie d'un rôle existant avec un nouveau nom")
    @PreAuthorize("hasPermission('ROLE', 'WRITE')")
    public Mono<ApiResponse<RoleResponse>> cloneRole(
        @Parameter(description = "ID du rôle à cloner")
        @PathVariable UUID roleId,
        @Valid @RequestBody CloneRoleRequest request,
        @Parameter(description = "ID de l'utilisateur créateur")
        @RequestHeader("X-User-Id") UUID createdBy) {

        log.info("POST /api/v1/roles/{}/clone - Cloning role with name: {}", roleId, request.getNewName());

        return roleService.cloneRole(roleId, request.getNewName(), createdBy)
            .map(role -> ApiResponse.<RoleResponse>builder()
                .success(true)
                .message("Rôle cloné avec succès")
                .data(role)
                .build())
            .doOnSuccess(response -> log.info("Role cloned: {}", response.getData().getId()));
    }

    @GetMapping(value = "/system", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les rôles système", description = "Récupère tous les rôles prédéfinis du système")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<Flux<RoleResponse>>> getSystemRoles() {
        log.info("GET /api/v1/roles/system - Getting system roles");

        Flux<RoleResponse> systemRoles = roleService.getSystemRoles();

        return Mono.just(ApiResponse.<Flux<RoleResponse>>builder()
            .success(true)
            .message("Rôles système récupérés avec succès")
            .data(systemRoles)
            .build());
    }

    @GetMapping(value = "/defaults", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les rôles par défaut", description = "Récupère les rôles par défaut d'une organisation")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<Flux<RoleResponse>>> getDefaultRoles(
        @Parameter(description = "ID de l'organisation")
        @RequestParam UUID organizationId) {

        log.info("GET /api/v1/roles/defaults - Getting default roles for organization: {}", organizationId);

        Flux<RoleResponse> defaultRoles = roleService.getDefaultRoles(organizationId);

        return Mono.just(ApiResponse.<Flux<RoleResponse>>builder()
            .success(true)
            .message("Rôles par défaut récupérés avec succès")
            .data(defaultRoles)
            .build());
    }
}
