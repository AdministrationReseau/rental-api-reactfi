package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.PermissionReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Permission Management", description = "APIs de gestion des permissions")
public class PermissionReactiveController {

    private final PermissionReactiveService permissionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister toutes les permissions", description = "Récupère toutes les permissions disponibles dans le système")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<Flux<PermissionResponse>>> getAllPermissions() {
        log.info("GET /api/v1/permissions - Getting all permissions");

        Flux<PermissionResponse> permissions = permissionService.getAllPermissions();

        return Mono.just(ApiResponse.<Flux<PermissionResponse>>builder()
            .success(true)
            .message("Permissions récupérées avec succès")
            .data(permissions)
            .build());
    }

    @GetMapping(value = "/resources", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister les ressources", description = "Récupère toutes les ressources disponibles")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<Flux<String>>> getAllResources() {
        log.info("GET /api/v1/permissions/resources - Getting all resources");

        Flux<String> resources = permissionService.getAllResources();

        return Mono.just(ApiResponse.<Flux<String>>builder()
            .success(true)
            .message("Ressources récupérées avec succès")
            .data(resources)
            .build());
    }

    @GetMapping(value = "/resources/{resource}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les permissions par ressource", description = "Récupère toutes les permissions pour une ressource spécifique")
    @PreAuthorize("hasPermission('ROLE', 'READ')")
    public Mono<ApiResponse<ResourcePermissionsResponse>> getPermissionsByResource(
        @Parameter(description = "Nom de la ressource")
        @PathVariable String resource) {

        log.info("GET /api/v1/permissions/resources/{} - Getting permissions for resource", resource);

        return permissionService.getPermissionsByResource(resource)
            .map(resourcePermissions -> ApiResponse.<ResourcePermissionsResponse>builder()
                .success(true)
                .message("Permissions de la ressource récupérées avec succès")
                .data(resourcePermissions)
                .build());
    }

    @GetMapping(value = "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les permissions d'un utilisateur", description = "Récupère toutes les permissions effectives d'un utilisateur")
    @PreAuthorize("hasPermission('USER', 'READ')")
    public Mono<ApiResponse<UserPermissionsResponse>> getUserPermissions(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId) {

        log.info("GET /api/v1/permissions/users/{} - Getting user permissions", userId);

        return permissionService.getUserPermissions(userId)
            .map(userPermissions -> ApiResponse.<UserPermissionsResponse>builder()
                .success(true)
                .message("Permissions utilisateur récupérées avec succès")
                .data(userPermissions)
                .build());
    }

    @GetMapping(value = "/users/{userId}/check", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier une permission", description = "Vérifie si un utilisateur possède une permission spécifique")
    @PreAuthorize("hasPermission('USER', 'READ')")
    public Mono<ApiResponse<PermissionCheckResponse>> checkPermission(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @Parameter(description = "Code de la permission à vérifier")
        @RequestParam String permission) {

        log.info("GET /api/v1/permissions/users/{}/check - Checking permission: {}", userId, permission);

        return permissionService.hasPermission(userId, permission)
            .map(hasPermission -> {
                PermissionCheckResponse response = PermissionCheckResponse.builder()
                    .userId(userId)
                    .permission(permission)
                    .hasPermission(hasPermission)
                    .build();

                return ApiResponse.<PermissionCheckResponse>builder()
                    .success(true)
                    .message("Vérification de permission effectuée")
                    .data(response)
                    .build();
            });
    }

    @PostMapping(value = "/users/{userId}/check-multiple", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier plusieurs permissions", description = "Vérifie si un utilisateur possède plusieurs permissions")
    @PreAuthorize("hasPermission('USER', 'READ')")
    public Mono<ApiResponse<MultiplePermissionCheckResponse>> checkMultiplePermissions(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @RequestBody CheckMultiplePermissionsRequest request) {

        log.info("POST /api/v1/permissions/users/{}/check-multiple - Checking {} permissions",
            userId, request.getPermissions().size());

        return Mono.zip(
            permissionService.hasAllPermissions(userId, request.getPermissions()),
            permissionService.hasAnyPermission(userId, request.getPermissions())
        ).map(tuple -> {
            MultiplePermissionCheckResponse response = MultiplePermissionCheckResponse.builder()
                .userId(userId)
                .permissions(request.getPermissions())
                .hasAllPermissions(tuple.getT1())
                .hasAnyPermissions(tuple.getT2())
                .build();

            return ApiResponse.<MultiplePermissionCheckResponse>builder()
                .success(true)
                .message("Vérification multiple effectuée")
                .data(response)
                .build();
        });
    }

    @GetMapping(value = "/compare/{userId1}/{userId2}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Comparer les permissions", description = "Compare les permissions entre deux utilisateurs")
    @PreAuthorize("hasPermission('USER', 'READ')")
    public Mono<ApiResponse<PermissionComparisonResponse>> comparePermissions(
        @Parameter(description = "ID du premier utilisateur")
        @PathVariable UUID userId1,
        @Parameter(description = "ID du deuxième utilisateur")
        @PathVariable UUID userId2) {

        log.info("GET /api/v1/permissions/compare/{}/{} - Comparing permissions", userId1, userId2);

        return permissionService.compareUserPermissions(userId1, userId2)
            .map(comparison -> ApiResponse.<PermissionComparisonResponse>builder()
                .success(true)
                .message("Comparaison des permissions effectuée")
                .data(comparison)
                .build());
    }
}
