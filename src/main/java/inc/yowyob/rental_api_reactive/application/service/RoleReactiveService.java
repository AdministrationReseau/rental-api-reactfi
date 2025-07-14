package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.Permission;
import inc.yowyob.rental_api_reactive.application.dto.RoleType;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.Role;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.UserRoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleReactiveService {

    private final RoleReactiveRepository roleRepository;
    private final UserRoleReactiveRepository userRoleRepository;
    private final RoleMapper roleMapper;

    /**
     * Crée un nouveau rôle
     */
    public Mono<RoleResponse> createRole(CreateRoleRequest request, UUID createdBy) {
        log.info("Creating role: {} for organization: {}", request.getName(), request.getOrganizationId());

        return roleRepository.countByOrganizationIdAndName(request.getOrganizationId(), request.getName())
            .flatMap(count -> {
                if (count > 0) {
                    return Mono.error(new IllegalArgumentException("Role name already exists in organization"));
                }
                return createNewRole(request, createdBy);
            });
    }

    private Mono<RoleResponse> createNewRole(CreateRoleRequest request, UUID createdBy) {
        return validatePermissions(request.getPermissions())
            .then(Mono.fromCallable(() -> {
                Role role = new Role(
                    request.getName(),
                    request.getDescription(),
                    request.getOrganizationId()
                );

                role.setRoleType(request.getRoleType() != null ? request.getRoleType() : RoleType.CUSTOM);
                role.setPriority(request.getPriority() != null ? request.getPriority() : 0);
                role.setPermissions(request.getPermissions());
                role.setColor(request.getColor());
                role.setIcon(request.getIcon());
                role.setCreatedBy(createdBy);
                role.setUpdatedBy(createdBy);

                return role;
            }))
            .flatMap(roleRepository::save)
            .map(roleMapper::toResponse)
            .doOnSuccess(role -> log.info("Role created: {}", role.getId()))
            .doOnError(error -> log.error("Failed to create role: {}", request.getName(), error));
    }

    /**
     * Récupère les permissions d'un rôle
     */
    public Mono<RolePermissionsResponse> getRolePermissions(UUID roleId) {
        log.debug("Getting permissions for role: {}", roleId);

        return roleRepository.findById(roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")))
            .map(role -> {
                Set<PermissionResponse> permissionDetails = role.getPermissions().stream()
                    .map(permCode -> {
                        for (Permission permission : Permission.values()) {
                            if (permission.getCode().equals(permCode)) {
                                return PermissionResponse.builder()
                                    .code(permission.getCode())
                                    .description(permission.getDescription())
                                    .resource(permission.getResource())
                                    .build();
                            }
                        }
                        return PermissionResponse.builder()
                            .code(permCode)
                            .description("Unknown permission")
                            .resource("UNKNOWN")
                            .build();
                    })
                    .collect(Collectors.toSet());

                return RolePermissionsResponse.builder()
                    .roleId(roleId)
                    .roleName(role.getName())
                    .permissions(role.getPermissions())
                    .permissionDetails(permissionDetails)
                    .totalCount(role.getPermissions().size())
                    .build();
            });
    }

    /**
     * Met à jour un rôle existant
     */
    public Mono<RoleResponse> updateRole(UUID roleId, UpdateRoleRequest request, UUID updatedBy) {
        log.info("Updating role: {}", roleId);

        return roleRepository.findById(roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")))
            .flatMap(role -> {
                if (Boolean.TRUE.equals(role.getIsSystemRole())) {
                    return Mono.error(new IllegalArgumentException("Cannot modify system role"));
                }
                return updateRoleEntity(role, request, updatedBy);
            });
    }

    private Mono<RoleResponse> updateRoleEntity(Role role, UpdateRoleRequest request, UUID updatedBy) {
        return validatePermissions(request.getPermissions())
            .then(Mono.fromCallable(() -> {
                if (request.getName() != null) role.setName(request.getName());
                if (request.getDescription() != null) role.setDescription(request.getDescription());
                if (request.getPermissions() != null) role.setPermissions(request.getPermissions());
                if (request.getPriority() != null) role.setPriority(request.getPriority());
                if (request.getColor() != null) role.setColor(request.getColor());
                if (request.getIcon() != null) role.setIcon(request.getIcon());

                role.setUpdatedBy(updatedBy);
                role.preUpdate();

                return role;
            }))
            .flatMap(roleRepository::save)
            .map(roleMapper::toResponse);
    }

    /**
     * Supprime un rôle
     */
    public Mono<Void> deleteRole(UUID roleId) {
        log.info("Deleting role: {}", roleId);

        return roleRepository.findById(roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")))
            .flatMap(role -> {
                if (Boolean.TRUE.equals(role.getIsSystemRole())) {
                    return Mono.error(new IllegalArgumentException("Cannot delete system role"));
                }
                if (Boolean.TRUE.equals(role.getIsDefaultRole())) {
                    return Mono.error(new IllegalArgumentException("Cannot delete default role"));
                }

                // Vérifier qu'aucun utilisateur n'a ce rôle
                return userRoleRepository.countByRoleId(roleId)
                    .flatMap(count -> {
                        if (count > 0) {
                            return Mono.error(new IllegalArgumentException("Cannot delete role assigned to users"));
                        }
                        return roleRepository.deleteById(roleId);
                    });
            });
    }

    /**
     * Récupère tous les rôles d'une organisation
     */
    public Flux<RoleResponse> getRolesByOrganization(UUID organizationId) {
        log.debug("Getting roles for organization: {}", organizationId);

        return roleRepository.findByOrganizationId(organizationId)
            .map(roleMapper::toResponse);
    }

    /**
     * Récupère un rôle par son ID
     */
    public Mono<RoleResponse> getRoleById(UUID roleId) {
        log.debug("Getting role: {}", roleId);

        return roleRepository.findById(roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")))
            .map(roleMapper::toResponse);
    }

    /**
     * Récupère les rôles par défaut d'une organisation
     */
    public Flux<RoleResponse> getDefaultRoles(UUID organizationId) {
        log.debug("Getting default roles for organization: {}", organizationId);

        return roleRepository.findDefaultRolesByOrganizationId(organizationId)
            .map(roleMapper::toResponse);
    }

    /**
     * Récupère les rôles système
     */
    public Flux<RoleResponse> getSystemRoles() {
        log.debug("Getting system roles");

        return roleRepository.findSystemRoles()
            .map(roleMapper::toResponse);
    }

    /**
     * Valide les permissions
     */
    private Mono<Void> validatePermissions(Set<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return Mono.empty();
        }

        Set<String> validPermissions = Arrays.stream(Permission.values())
            .map(Permission::getCode)
            .collect(Collectors.toSet());

        for (String permission : permissions) {
            if (!validPermissions.contains(permission)) {
                return Mono.error(new IllegalArgumentException("Invalid permission: " + permission));
            }
        }

        return Mono.empty();
    }

    /**
     * Clone un rôle avec un nouveau nom
     */
    public Mono<RoleResponse> cloneRole(UUID roleId, String newName, UUID createdBy) {
        log.info("Cloning role: {} with new name: {}", roleId, newName);

        return roleRepository.findById(roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found")))
            .flatMap(originalRole -> {
                // Vérifier que le nouveau nom n'existe pas
                return roleRepository.countByOrganizationIdAndName(originalRole.getOrganizationId(), newName)
                    .flatMap(count -> {
                        if (count > 0) {
                            return Mono.error(new IllegalArgumentException("Role name already exists"));
                        }

                        Role clonedRole = new Role(
                            newName,
                            "Clone of " + originalRole.getName(),
                            originalRole.getOrganizationId()
                        );

                        clonedRole.setRoleType(RoleType.CUSTOM);
                        clonedRole.setPermissions(originalRole.getPermissions());
                        clonedRole.setPriority(originalRole.getPriority());
                        clonedRole.setColor(originalRole.getColor());
                        clonedRole.setIcon(originalRole.getIcon());
                        clonedRole.setCreatedBy(createdBy);
                        clonedRole.setUpdatedBy(createdBy);

                        return roleRepository.save(clonedRole);
                    });
            })
            .map(roleMapper::toResponse);
    }
}
