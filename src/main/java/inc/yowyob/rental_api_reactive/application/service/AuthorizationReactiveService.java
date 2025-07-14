package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.RoleType;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.SecurityContext;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserRoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationReactiveService {

    private final UserRoleReactiveRepository userRoleRepository;
    private final RoleReactiveRepository roleRepository;
    private final PermissionReactiveService permissionService;

    /**
     * Vérifie si un utilisateur a une permission spécifique
     */
    public Mono<Boolean> hasPermission(UUID userId, String resource, String action) {
        String permission = resource + "_" + action;
        log.debug("Checking permission {}_{} for user: {}", resource, action, userId);

        return permissionService.hasPermission(userId, permission);
    }

    /**
     * Vérifie si un utilisateur peut accéder à une organisation
     */
    public Mono<Boolean> canAccessOrganization(UUID userId, UUID organizationId) {
        log.debug("Checking organization access for user {} to organization {}", userId, organizationId);

        return userRoleRepository.findActiveByUserId(userId)
            .any(userRole -> organizationId.equals(userRole.getOrganizationId()));
    }

    /**
     * Vérifie si un utilisateur peut accéder à une agence
     */
    public Mono<Boolean> canAccessAgency(UUID userId, UUID agencyId) {
        log.debug("Checking agency access for user {} to agency {}", userId, agencyId);

        return userRoleRepository.findActiveByUserId(userId)
            .any(userRole -> agencyId.equals(userRole.getAgencyId()) || userRole.getAgencyId() == null);
    }

    /**
     * Construit le contexte de sécurité pour un utilisateur
     */
    public Mono<SecurityContext> buildSecurityContext(User user) {
        log.debug("Building security context for user: {}", user.getId());

        return Mono.zip(
            getUserPermissions(user.getId()),
            getUserRoles(user.getId()),
            getUserOrganizations(user.getId())
        ).map(tuple -> SecurityContext.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .organizationId(user.getOrganizationId())
            .permissions(tuple.getT1())
            .roles(tuple.getT2())
            .organizations(tuple.getT3())
            .isSuperAdmin(isSuperAdmin(tuple.getT2()))
            .build());
    }

    /**
     * Vérifie si un utilisateur est super administrateur
     */
    public Mono<Boolean> isSuperAdmin(UUID userId) {
        return getUserRoles(userId)
            .map(this::isSuperAdmin);
    }

    /**
     * Vérifie les permissions hiérarchiques
     */
    public Mono<Boolean> hasHierarchicalPermission(UUID userId, String permission, UUID targetOrganizationId) {
        log.debug("Checking hierarchical permission {} for user {} in organization {}",
            permission, userId, targetOrganizationId);

        return userRoleRepository.findActiveByUserId(userId)
            .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()))
            .filter(Objects::nonNull)
            .any(role -> {
                // Super admin a toutes les permissions
                if (RoleType.SUPER_ADMIN.equals(role.getRoleType())) {
                    return true;
                }

                // Organization owner peut tout faire dans son organisation
                if (RoleType.ORGANIZATION_OWNER.equals(role.getRoleType()) &&
                    targetOrganizationId.equals(role.getOrganizationId())) {
                    return true;
                }

                // Vérifier la permission spécifique
                return role.getPermissions() != null && role.getPermissions().contains(permission);
            });
    }

    private Mono<Set<String>> getUserPermissions(UUID userId) {
        return userRoleRepository.findActiveByUserId(userId)
            .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()))
            .filter(Objects::nonNull)
            .map(role -> role.getPermissions())
            .reduce(new HashSet<>(), (acc, permissions) -> {
                if (permissions != null) {
                    acc.addAll(permissions);
                }
                return acc;
            });
    }

    private Mono<Set<String>> getUserRoles(UUID userId) {
        return userRoleRepository.findActiveByUserId(userId)
            .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()))
            .filter(Objects::nonNull)
            .map(role -> role.getRoleType().name())
            .collect(HashSet::new, Set::add);
    }

    private Mono<Set<UUID>> getUserOrganizations(UUID userId) {
        return userRoleRepository.findActiveByUserId(userId)
            .map(userRole -> userRole.getOrganizationId())
            .collect(HashSet::new, Set::add);
    }

    private boolean isSuperAdmin(Set<String> roles) {
        return roles.contains(RoleType.SUPER_ADMIN.name());
    }
}
