package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.Permission;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.repository.UserRoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionReactiveService {

    private final UserRoleReactiveRepository userRoleRepository;
    private final RoleReactiveRepository roleRepository;

    /**
     * Récupère toutes les permissions disponibles
     */
    public Flux<PermissionResponse> getAllPermissions() {
        log.debug("Getting all permissions");

        return Flux.fromArray(Permission.values())
            .map(this::mapPermissionToResponse);
    }

    /**
     * Récupère les permissions par ressource
     */
    public Mono<ResourcePermissionsResponse> getPermissionsByResource(String resource) {
        log.debug("Getting permissions for resource: {}", resource);

        Set<PermissionResponse> permissions = Arrays.stream(Permission.values())
            .filter(permission -> permission.getResource().equalsIgnoreCase(resource))
            .map(this::mapPermissionToResponse)
            .collect(Collectors.toSet());

        return Mono.just(ResourcePermissionsResponse.builder()
            .resource(resource)
            .permissions(permissions)
            .totalCount(permissions.size())
            .build());
    }

    /**
     * Récupère toutes les ressources disponibles
     */
    public Flux<String> getAllResources() {
        log.debug("Getting all resources");

        return Flux.fromArray(Permission.values())
            .map(Permission::getResource)
            .distinct();
    }

    /**
     * Récupère les permissions effectives d'un utilisateur
     */
    public Mono<UserPermissionsResponse> getUserPermissions(UUID userId) {
        log.debug("Getting permissions for user: {}", userId);

        return userRoleRepository.findActiveByUserId(userId)
            .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()))
            .filter(Objects::nonNull)
            .map(role -> role.getPermissions())
            .filter(Objects::nonNull)
            .collectList()
            .map(permissionsList -> {
                Set<String> effectivePermissions = permissionsList.stream()
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

                return UserPermissionsResponse.builder()
                    .userId(userId)
                    .permissions(effectivePermissions)
                    .permissionDetails(mapPermissionsToDetails(effectivePermissions))
                    .totalCount(effectivePermissions.size())
                    .build();
            });
    }

    /**
     * Vérifie si un utilisateur a une permission spécifique
     */
    public Mono<Boolean> hasPermission(UUID userId, String permissionCode) {
        log.debug("Checking permission '{}' for user: {}", permissionCode, userId);

        return getUserPermissions(userId)
            .map(userPermissions -> userPermissions.getPermissions().contains(permissionCode))
            .defaultIfEmpty(false);
    }

    /**
     * Vérifie si un utilisateur a toutes les permissions spécifiées
     */
    public Mono<Boolean> hasAllPermissions(UUID userId, Set<String> permissionCodes) {
        log.debug("Checking permissions {} for user: {}", permissionCodes, userId);

        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return Mono.just(true);
        }

        return getUserPermissions(userId)
            .map(userPermissions -> userPermissions.getPermissions().containsAll(permissionCodes))
            .defaultIfEmpty(false);
    }

    /**
     * Vérifie si un utilisateur a au moins une des permissions spécifiées
     */
    public Mono<Boolean> hasAnyPermission(UUID userId, Set<String> permissionCodes) {
        log.debug("Checking any permission {} for user: {}", permissionCodes, userId);

        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return Mono.just(false);
        }

        return getUserPermissions(userId)
            .map(userPermissions -> {
                Set<String> userPerms = userPermissions.getPermissions();
                return permissionCodes.stream().anyMatch(userPerms::contains);
            })
            .defaultIfEmpty(false);
    }

    /**
     * Récupère les permissions d'un rôle
     */
    public Mono<RolePermissionsResponse> getRolePermissions(UUID roleId) {
        log.debug("Getting permissions for role: {}", roleId);

        return roleRepository.findById(roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")))
            .map(role -> RolePermissionsResponse.builder()
                .roleId(roleId)
                .roleName(role.getName())
                .permissions(role.getPermissions())
                .permissionDetails(mapPermissionsToDetails(role.getPermissions()))
                .totalCount(role.getPermissions().size())
                .build());
    }

    /**
     * Compare les permissions entre deux utilisateurs
     */
    public Mono<PermissionComparisonResponse> compareUserPermissions(UUID userId1, UUID userId2) {
        log.debug("Comparing permissions between users: {} and {}", userId1, userId2);

        return Mono.zip(
            getUserPermissions(userId1),
            getUserPermissions(userId2)
        ).map(tuple -> {
            Set<String> user1Perms = tuple.getT1().getPermissions();
            Set<String> user2Perms = tuple.getT2().getPermissions();

            Set<String> commonPermissions = new HashSet<>(user1Perms);
            commonPermissions.retainAll(user2Perms);

            Set<String> user1OnlyPermissions = new HashSet<>(user1Perms);
            user1OnlyPermissions.removeAll(user2Perms);

            Set<String> user2OnlyPermissions = new HashSet<>(user2Perms);
            user2OnlyPermissions.removeAll(user1Perms);

            return PermissionComparisonResponse.builder()
                .userId1(userId1)
                .userId2(userId2)
                .commonPermissions(commonPermissions)
                .user1OnlyPermissions(user1OnlyPermissions)
                .user2OnlyPermissions(user2OnlyPermissions)
                .commonCount(commonPermissions.size())
                .user1OnlyCount(user1OnlyPermissions.size())
                .user2OnlyCount(user2OnlyPermissions.size())
                .build();
        });
    }

    private PermissionResponse mapPermissionToResponse(Permission permission) {
        return PermissionResponse.builder()
            .code(permission.getCode())
            .description(permission.getDescription())
            .resource(permission.getResource())
            .build();
    }

    private Set<PermissionResponse> mapPermissionsToDetails(Set<String> permissionCodes) {
        if (permissionCodes == null) {
            return new HashSet<>();
        }

        return permissionCodes.stream()
            .map(code -> {
                for (Permission permission : Permission.values()) {
                    if (permission.getCode().equals(code)) {
                        return mapPermissionToResponse(permission);
                    }
                }
                return PermissionResponse.builder()
                    .code(code)
                    .description("Unknown permission")
                    .resource("UNKNOWN")
                    .build();
            })
            .collect(Collectors.toSet());
    }
}
