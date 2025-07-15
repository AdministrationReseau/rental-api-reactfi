package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.SecurityContext;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.TenantFilter;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.AgencyReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.OrganizationReactiveRepository;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service réactif pour la gestion multi-tenant
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiTenantReactiveService {

    private final UserReactiveRepository userRepository;
    private final AgencyReactiveRepository agencyRepository;
    private final OrganizationReactiveRepository organizationRepository;
    private final AuthorizationReactiveService authorizationService;

    /**
     * Valide l'accès d'un utilisateur à une organisation
     */
    public Mono<Void> validateOrganizationAccess(UUID organizationId, UUID userId) {
        log.debug("Validating organization access for user {} to organization {}", userId, organizationId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .flatMap(user -> {
                // Super admin a accès à tout
                if (user.getUserType() == UserType.SUPER_ADMIN) {
                    return Mono.empty();
                }

                // Vérifier que l'utilisateur appartient à l'organisation
                if (!organizationId.equals(user.getOrganizationId())) {
                    return Mono.error(new SecurityException("Accès refusé à cette organisation"));
                }

                return Mono.empty();
            })
            .then()
            .doOnSuccess(v -> log.debug("Organization access validated for user {}", userId))
            .doOnError(error -> log.warn("Organization access denied for user {}: {}", userId, error.getMessage()));
    }

    /**
     * Valide l'accès d'un utilisateur à une agence
     */
    public Mono<Void> validateAgencyAccess(UUID agencyId, UUID userId) {
        log.debug("Validating agency access for user {} to agency {}", userId, agencyId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .flatMap(user -> {
                // Super admin a accès à tout
                if (user.getUserType() == UserType.SUPER_ADMIN) {
                    return Mono.empty();
                }

                return agencyRepository.findById(agencyId)
                    .switchIfEmpty(Mono.error(new SecurityException("Agence non trouvée")))
                    .flatMap(agency -> {
                        // Propriétaire d'organisation a accès à toutes ses agences
                        if (user.getUserType() == UserType.ORGANIZATION_OWNER &&
                            agency.getOrganizationId().equals(user.getOrganizationId())) {
                            return Mono.empty();
                        }

                        // Manager d'agence a accès à son agence
                        if (user.getUserType() == UserType.AGENCY_MANAGER &&
                            agencyId.equals(user.getAgencyId())) {
                            return Mono.empty();
                        }

                        // Agent de location a accès à son agence
                        if (user.getUserType() == UserType.RENTAL_AGENT &&
                            agencyId.equals(user.getAgencyId())) {
                            return Mono.empty();
                        }

                        // Vérifier que l'utilisateur appartient à la même organisation que l'agence
                        if (!agency.getOrganizationId().equals(user.getOrganizationId())) {
                            return Mono.error(new SecurityException("Accès refusé à cette agence"));
                        }

                        return Mono.empty();
                    });
            })
            .then()
            .doOnSuccess(v -> log.debug("Agency access validated for user {}", userId))
            .doOnError(error -> log.warn("Agency access denied for user {}: {}", userId, error.getMessage()));
    }

    /**
     * Valide l'accès d'un utilisateur à un autre utilisateur (pour modification)
     */
    public Mono<Void> validateUserAccess(UUID targetUserId, UUID requesterId) {
        log.debug("Validating user access from {} to {}", requesterId, targetUserId);

        return userRepository.findById(requesterId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur demandeur non trouvé")))
            .zipWith(userRepository.findById(targetUserId)
                .switchIfEmpty(Mono.error(new SecurityException("Utilisateur cible non trouvé"))))
            .flatMap(tuple -> {
                User requester = tuple.getT1();
                User target = tuple.getT2();

                // Super admin a accès à tout
                if (requester.getUserType() == UserType.SUPER_ADMIN) {
                    return Mono.empty();
                }

                // Un utilisateur peut toujours se modifier lui-même
                if (requesterId.equals(targetUserId)) {
                    return Mono.empty();
                }

                // Propriétaire d'organisation peut modifier les utilisateurs de son organisation
                if (requester.getUserType() == UserType.ORGANIZATION_OWNER &&
                    requester.getOrganizationId().equals(target.getOrganizationId())) {
                    return Mono.empty();
                }

                // Manager d'agence peut modifier les utilisateurs de son agence
                if (requester.getUserType() == UserType.AGENCY_MANAGER &&
                    requester.getAgencyId() != null &&
                    requester.getAgencyId().equals(target.getAgencyId())) {
                    return Mono.empty();
                }

                // Vérifier que les utilisateurs appartiennent à la même organisation
                if (!requester.getOrganizationId().equals(target.getOrganizationId())) {
                    return Mono.error(new SecurityException("Accès refusé à cet utilisateur"));
                }

                return Mono.error(new SecurityException("Permissions insuffisantes"));
            })
            .then()
            .doOnSuccess(v -> log.debug("User access validated from {} to {}", requesterId, targetUserId))
            .doOnError(error -> log.warn("User access denied from {} to {}: {}",
                requesterId, targetUserId, error.getMessage()));
    }

    /**
     * Obtient l'organisation d'un utilisateur
     */
    public Mono<UUID> getUserOrganizationId(UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .map(User::getOrganizationId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur sans organisation")));
    }

    /**
     * Obtient l'agence d'un utilisateur
     */
    public Mono<UUID> getUserAgencyId(UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .map(User::getAgencyId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur sans agence")));
    }

    /**
     * Vérifie si un utilisateur peut créer une agence
     */
    public Mono<Boolean> canCreateAgency(UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .map(user -> user.getUserType() == UserType.SUPER_ADMIN ||
                user.getUserType() == UserType.ORGANIZATION_OWNER);
    }

    /**
     * Vérifie si un utilisateur peut modifier une organisation
     */
    public Mono<Boolean> canModifyOrganization(UUID organizationId, UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .map(user -> {
                if (user.getUserType() == UserType.SUPER_ADMIN) {
                    return true;
                }

                return user.getUserType() == UserType.ORGANIZATION_OWNER &&
                    organizationId.equals(user.getOrganizationId());
            });
    }

    /**
     * Vérifie si un utilisateur peut supprimer une agence
     */
    public Mono<Boolean> canDeleteAgency(UUID agencyId, UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .flatMap(user -> {
                if (user.getUserType() == UserType.SUPER_ADMIN) {
                    return Mono.just(true);
                }

                if (user.getUserType() != UserType.ORGANIZATION_OWNER) {
                    return Mono.just(false);
                }

                return agencyRepository.findById(agencyId)
                    .switchIfEmpty(Mono.error(new SecurityException("Agence non trouvée")))
                    .map(agency -> agency.getOrganizationId().equals(user.getOrganizationId()));
            });
    }

    /**
     * Filtre les données selon l'organisation de l'utilisateur
     */
    public Mono<UUID> getFilterOrganizationId(UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .map(user -> {
                // Super admin peut voir toutes les organisations (retourne null pour pas de filtre)
                if (user.getUserType() == UserType.SUPER_ADMIN) {
                    return null;
                }

                return user.getOrganizationId();
            });
    }

    /**
     * Vérifie si un utilisateur a les permissions pour une action spécifique
     */
    public Mono<Boolean> hasPermission(UUID userId, String resource, String action) {
        log.debug("Checking permission {}_{} for user {}", resource, action, userId);

        return authorizationService.hasPermission(userId, resource, action)
            .doOnNext(hasPermission -> log.debug("User {} has permission {}_{}: {}",
                userId, resource, action, hasPermission));
    }

    /**
     * Vérifie l'isolation des données entre organisations
     */
    public Mono<Void> validateDataIsolation(UUID resourceOrganizationId, UUID userOrganizationId) {
        if (resourceOrganizationId == null || userOrganizationId == null) {
            return Mono.error(new SecurityException("IDs d'organisation manquants"));
        }

        if (!resourceOrganizationId.equals(userOrganizationId)) {
            return Mono.error(new SecurityException("Violation de l'isolation des données"));
        }

        return Mono.empty();
    }

    /**
     * Obtient le contexte de sécurité pour un utilisateur
     */
    public Mono<SecurityContext> getSecurityContext(UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .map(user -> SecurityContext.builder()
                .userId(user.getId())
                .userType(user.getUserType())
                .organizationId(user.getOrganizationId())
                .agencyId(user.getAgencyId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .build());
    }

    /**
     * Applique les filtres multi-tenant pour les requêtes
     */
    public Mono<TenantFilter> getTenantFilter(UUID userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new SecurityException("Utilisateur non trouvé")))
            .map(user -> {
                TenantFilter.TenantFilterBuilder builder = TenantFilter.builder()
                    .userId(user.getId())
                    .userType(user.getUserType());

                // Super admin voit tout
                if (user.getUserType() == UserType.SUPER_ADMIN) {
                    builder.isGlobalAccess(true);
                } else {
                    builder.organizationId(user.getOrganizationId());

                    // Si l'utilisateur est lié à une agence spécifique
                    if (user.getAgencyId() != null &&
                        (user.getUserType() == UserType.AGENCY_MANAGER ||
                            user.getUserType() == UserType.RENTAL_AGENT)) {
                        builder.agencyId(user.getAgencyId());
                        builder.isAgencyRestricted(true);
                    }
                }

                return builder.build();
            });
    }
}
