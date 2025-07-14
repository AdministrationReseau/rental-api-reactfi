package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.UserRole;
import inc.yowyob.rental_api_reactive.persistence.repository.UserRoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleReactiveService {

    private final UserRoleReactiveRepository userRoleRepository;
    private final RoleReactiveRepository roleRepository;
    private final UserReactiveRepository userRepository;
    private final UserRoleMapper userRoleMapper;

    /**
     * Assigne un rôle à un utilisateur
     */
    public Mono<UserRoleResponse> assignRole(AssignRoleRequest request, UUID assignedBy) {
        log.info("Assigning role {} to user {} in organization {}",
            request.getRoleId(), request.getUserId(), request.getOrganizationId());

        return validateAssignmentRequest(request)
            .then(checkExistingAssignment(request))
            .then(createUserRole(request, assignedBy))
            .map(userRoleMapper::toResponse)
            .doOnSuccess(userRole -> log.info("Role assigned successfully: {}", userRole.getId()))
            .doOnError(error -> log.error("Failed to assign role", error));
    }

    private Mono<Void> validateAssignmentRequest(AssignRoleRequest request) {
        return userRepository.findById(request.getUserId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .then(roleRepository.findById(request.getRoleId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Role not found"))))
            .then();
    }

    private Mono<Void> checkExistingAssignment(AssignRoleRequest request) {
        return userRoleRepository.findByUserIdAndRoleId(request.getUserId(), request.getRoleId())
            .filter(userRole -> userRole.getIsActive())
            .flatMap(existingRole -> Mono.error(new IllegalArgumentException("User already has this role")))
            .then();
    }

    private Mono<UserRole> createUserRole(AssignRoleRequest request, UUID assignedBy) {
        return Mono.fromCallable(() -> {
            UserRole userRole = new UserRole(
                request.getUserId(),
                request.getRoleId(),
                request.getOrganizationId(),
                request.getAgencyId()
            );

            userRole.setAssignedBy(assignedBy);
            if (request.getExpiresAt() != null) {
                userRole.setExpiresAt(request.getExpiresAt());
            }

            return userRole;
        }).flatMap(userRoleRepository::save);
    }

    /**
     * Révoque un rôle d'un utilisateur
     */
    public Mono<Void> revokeRole(UUID userId, UUID roleId, UUID revokedBy) {
        log.info("Revoking role {} from user {}", roleId, userId);

        return userRoleRepository.findByUserIdAndRoleId(userId, roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User role assignment not found")))
            .flatMap(userRole -> {
                userRole.revoke(revokedBy);
                return userRoleRepository.save(userRole);
            })
            .then()
            .doOnSuccess(v -> log.info("Role revoked successfully"))
            .doOnError(error -> log.error("Failed to revoke role", error));
    }

    /**
     * Récupère tous les rôles d'un utilisateur
     */
    public Flux<UserRoleResponse> getUserRoles(UUID userId) {
        log.debug("Getting roles for user: {}", userId);

        return userRoleRepository.findActiveByUserId(userId)
            .map(userRoleMapper::toResponse);
    }

    /**
     * Récupère tous les utilisateurs ayant un rôle spécifique
     */
    public Flux<UserRoleResponse> getUsersByRole(UUID roleId) {
        log.debug("Getting users with role: {}", roleId);

        return userRoleRepository.findByRoleId(roleId)
            .filter(userRole -> userRole.getIsActive())
            .map(userRoleMapper::toResponse);
    }

    /**
     * Supprime tous les rôles d'un utilisateur
     */
    public Mono<Void> revokeAllUserRoles(UUID userId, UUID revokedBy) {
        log.info("Revoking all roles from user: {}", userId);

        return userRoleRepository.findActiveByUserId(userId)
            .flatMap(userRole -> {
                userRole.revoke(revokedBy);
                return userRoleRepository.save(userRole);
            })
            .then()
            .doOnSuccess(v -> log.info("All roles revoked for user: {}", userId));
    }

    /**
     * Active un rôle temporairement désactivé
     */
    public Mono<UserRoleResponse> activateRole(UUID userId, UUID roleId) {
        log.info("Activating role {} for user {}", roleId, userId);

        return userRoleRepository.findByUserIdAndRoleId(userId, roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User role assignment not found")))
            .flatMap(userRole -> {
                userRole.activate();
                return userRoleRepository.save(userRole);
            })
            .map(userRoleMapper::toResponse);
    }

    /**
     * Étend l'expiration d'un rôle
     */
    public Mono<UserRoleResponse> extendRoleExpiration(UUID userId, UUID roleId, LocalDateTime newExpirationDate) {
        log.info("Extending role {} expiration for user {} to {}", roleId, userId, newExpirationDate);

        return userRoleRepository.findByUserIdAndRoleId(userId, roleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User role assignment not found")))
            .flatMap(userRole -> {
                userRole.setExpiration(newExpirationDate);
                return userRoleRepository.save(userRole);
            })
            .map(userRoleMapper::toResponse);
    }
}
