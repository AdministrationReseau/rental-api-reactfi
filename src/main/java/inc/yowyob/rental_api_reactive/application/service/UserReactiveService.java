package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service réactif pour la gestion des utilisateurs (Mis à jour)
 * Route: src/main/java/inc/yowyob/rental_api_reactive/application/service/UserReactiveService.java
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserReactiveService {

    private final UserReactiveRepository userRepository;

    /**
     * Trouve tous les utilisateurs
     */
    public Flux<UserResponse> findAll() {
        log.debug("Finding all users");
        return userRepository.findNonDeletedUsers()
            .map(this::mapToUserResponse)
            .doOnNext(user -> log.debug("Found user: {}", user.getEmail()));
    }

    /**
     * Trouve un utilisateur par ID
     */
    public Mono<UserResponse> findById(UUID id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
            .filter(user -> !user.getIsDeleted())
            .map(this::mapToUserResponse)
            .doOnNext(user -> log.debug("Found user: {}", user.getEmail()));
    }

    /**
     * Trouve un utilisateur par email
     */
    public Mono<UserResponse> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
            .filter(user -> !user.getIsDeleted())
            .map(this::mapToUserResponse)
            .doOnNext(user -> log.debug("Found user: {}", user.getEmail()));
    }

    /**
     * Trouve les utilisateurs par organisation
     */
    public Flux<UserResponse> findByOrganizationId(UUID organizationId) {
        log.debug("Finding users by organization ID: {}", organizationId);
        return userRepository.findByOrganizationId(organizationId)
            .filter(user -> !user.getIsDeleted())
            .map(this::mapToUserResponse)
            .doOnNext(user -> log.debug("Found user in organization: {}", user.getEmail()));
    }

    /**
     * Vérifie si un email existe
     */
    public Mono<Boolean> existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return userRepository.countByEmail(email)
            .map(count -> count > 0)
            .doOnNext(exists -> log.debug("Email {} exists: {}", email, exists));
    }

    /**
     * Sauvegarde un utilisateur
     */
    public Mono<UserResponse> save(User user) {
        log.debug("Saving user: {}", user.getEmail());
        user.prePersist();
        return userRepository.save(user)
            .map(this::mapToUserResponse)
            .doOnNext(saved -> log.info("User saved successfully: {}", saved.getEmail()));
    }

    /**
     * Met à jour le profil utilisateur
     */
    public Mono<UserResponse> updateProfile(UUID userId, UpdateProfileRequest updateRequest) {
        log.info("Updating user profile: {}", userId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .filter(user -> !user.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User has been deleted")))
            .flatMap(user -> {
                // Mettre à jour les champs modifiables
                if (updateRequest.getFirstName() != null) {
                    user.setFirstName(updateRequest.getFirstName());
                }
                if (updateRequest.getLastName() != null) {
                    user.setLastName(updateRequest.getLastName());
                }
                if (updateRequest.getPhone() != null) {
                    user.setPhone(updateRequest.getPhone());
                }
                if (updateRequest.getAddress() != null) {
                    user.setAddress(updateRequest.getAddress());
                }
                if (updateRequest.getCity() != null) {
                    user.setCity(updateRequest.getCity());
                }
                if (updateRequest.getCountry() != null) {
                    user.setCountry(updateRequest.getCountry());
                }
                if (updateRequest.getProfilePicture() != null) {
                    user.setProfilePicture(updateRequest.getProfilePicture());
                }

                user.preUpdate();
                return userRepository.save(user);
            })
            .map(this::mapToUserResponse)
            .doOnSuccess(response -> log.info("User profile updated successfully: {}", userId));
    }

    /**
     * Met à jour la photo de profil
     */
    public Mono<String> updateAvatar(UUID userId, String avatarUrl) {
        log.info("Updating avatar for user: {}", userId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .filter(user -> !user.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User has been deleted")))
            .flatMap(user -> {
                user.setProfilePicture(avatarUrl);
                user.preUpdate();
                return userRepository.save(user);
            })
            .map(User::getProfilePicture)
            .doOnSuccess(url -> log.info("Avatar updated successfully for user: {}", userId));
    }

    /**
     * Supprime la photo de profil
     */
    public Mono<Void> deleteAvatar(UUID userId) {
        log.info("Deleting avatar for user: {}", userId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .filter(user -> !user.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User has been deleted")))
            .flatMap(user -> {
                user.setProfilePicture(null);
                user.preUpdate();
                return userRepository.save(user);
            })
            .then()
            .doOnSuccess(v -> log.info("Avatar deleted successfully for user: {}", userId));
    }

    /**
     * Met à jour les préférences utilisateur
     */
    public Mono<UserResponse> updatePreferences(UUID userId, UserPreferencesRequest preferencesRequest) {
        log.info("Updating preferences for user: {}", userId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .filter(user -> !user.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User has been deleted")))
            .flatMap(user -> {
                // Mettre à jour les préférences
                if (preferencesRequest.getPreferredLanguage() != null) {
                    user.setPreferredLanguage(preferencesRequest.getPreferredLanguage());
                }
                if (preferencesRequest.getTimezone() != null) {
                    user.setTimezone(preferencesRequest.getTimezone());
                }
                if (preferencesRequest.getEmailNotifications() != null) {
                    user.setEmailNotifications(preferencesRequest.getEmailNotifications());
                }
                if (preferencesRequest.getSmsNotifications() != null) {
                    user.setSmsNotifications(preferencesRequest.getSmsNotifications());
                }
                if (preferencesRequest.getCurrency() != null) {
                    user.setCurrency(preferencesRequest.getCurrency());
                }
                if (preferencesRequest.getDateFormat() != null) {
                    user.setDateFormat(preferencesRequest.getDateFormat());
                }

                user.preUpdate();
                return userRepository.save(user);
            })
            .map(this::mapToUserResponse)
            .doOnSuccess(response -> log.info("Preferences updated successfully for user: {}", userId));
    }

    /**
     * Désactive un compte utilisateur (soft delete)
     */
    public Mono<Void> deactivateAccount(UUID userId, String reason) {
        log.info("Deactivating account for user: {} with reason: {}", userId, reason);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .flatMap(user -> {
                user.markAsDeleted(userId); // Self-deactivation
                // Note: En production, enregistrer la raison dans une table d'audit
                return userRepository.save(user);
            })
            .then()
            .doOnSuccess(v -> log.info("Account deactivated successfully for user: {}", userId));
    }

    /**
     * Active/Désactive un utilisateur
     */
    public Mono<UserResponse> updateUserStatus(UUID userId, Boolean isActive, UUID updatedBy) {
        log.info("Updating user status: {} to {}", userId, isActive);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .flatMap(user -> {
                user.setIsActive(isActive);
                user.setUpdatedBy(updatedBy);
                user.preUpdate();
                return userRepository.save(user);
            })
            .map(this::mapToUserResponse)
            .doOnSuccess(response -> log.info("User status updated successfully: {}", userId));
    }

    /**
     * Supprime un utilisateur par ID
     */
    public Mono<Void> deleteById(UUID id) {
        log.debug("Deleting user by ID: {}", id);
        return userRepository.deleteById(id)
            .doOnSuccess(v -> log.info("User deleted successfully: {}", id));
    }

    /**
     * Trouve les utilisateurs par type
     */
    public Flux<UserResponse> findByUserType(inc.yowyob.rental_api_reactive.application.dto.UserType userType) {
        log.debug("Finding users by type: {}", userType);
        return userRepository.findByUserType(userType)
            .filter(user -> !user.getIsDeleted())
            .map(this::mapToUserResponse);
    }

    /**
     * Trouve le personnel par organisation
     */
    public Flux<UserResponse> findPersonnelByOrganization(UUID organizationId) {
        log.debug("Finding personnel by organization: {}", organizationId);
        return userRepository.findPersonnelByOrganizationId(organizationId)
            .filter(user -> !user.getIsDeleted())
            .map(this::mapToUserResponse);
    }

    /**
     * Trouve le personnel par agence
     */
    public Flux<UserResponse> findPersonnelByAgency(UUID agencyId) {
        log.debug("Finding personnel by agency: {}", agencyId);
        return userRepository.findPersonnelByAgencyId(agencyId)
            .filter(user -> !user.getIsDeleted())
            .map(this::mapToUserResponse);
    }

    /**
     * Statistiques des utilisateurs par organisation
     */
    public Mono<UserStatsResponse> getUserStats(UUID organizationId) {
        log.debug("Getting user stats for organization: {}", organizationId);

        return Mono.zip(
            userRepository.countByOrganizationId(organizationId),
            userRepository.countActiveByOrganizationId(organizationId),
            userRepository.countPersonnelByOrganizationId(organizationId)
        ).map(tuple -> {
            UserStatsResponse stats = new UserStatsResponse();
            stats.setTotalUsers(tuple.getT1());
            stats.setActiveUsers(tuple.getT2());
            stats.setPersonnelCount(tuple.getT3());
            stats.setClientCount(tuple.getT1() - tuple.getT3()); // Approximation
            return stats;
        });
    }

    /**
     * Mappe une entité User vers UserResponse
     */
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setUserType(user.getUserType());
        response.setOrganizationId(user.getOrganizationId());
        response.setAgencyId(user.getAgencyId());
        response.setProfilePicture(user.getProfilePicture());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setCountry(user.getCountry());
        response.setIsEmailVerified(user.getIsEmailVerified());
        response.setIsPhoneVerified(user.getIsPhoneVerified());
        response.setPreferredLanguage(user.getPreferredLanguage());
        response.setTimezone(user.getTimezone());
        response.setCurrency(user.getCurrency());
        response.setDateFormat(user.getDateFormat());
        response.setEmailNotifications(user.getEmailNotifications());
        response.setSmsNotifications(user.getSmsNotifications());
        response.setPushNotifications(user.getPushNotifications());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());
        response.setIsActive(user.getIsActive());

        // Informations employé (si applicable)
        if (user.isPersonnel()) {
            response.setEmployeeId(user.getEmployeeId());
            response.setDepartment(user.getDepartment());
            response.setPosition(user.getPosition());
            response.setSupervisorId(user.getSupervisorId());
            response.setHiredAt(user.getHiredAt());
            response.setMustChangePassword(user.getMustChangePassword());
        }

        return response;
    }
}
