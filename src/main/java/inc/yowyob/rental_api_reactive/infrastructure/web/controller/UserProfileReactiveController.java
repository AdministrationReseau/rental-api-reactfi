package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.UserReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * Contrôleur réactif pour la gestion du profil utilisateur
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "APIs réactives de gestion du profil utilisateur")
public class UserProfileReactiveController {

    private final UserReactiveService userService;

    @Operation(
        summary = "Récupérer le profil utilisateur",
        description = "Récupère les informations détaillées du profil de l'utilisateur connecté"
    )
    @GetMapping
    public Mono<ApiResponse<UserResponse>> getProfile(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId) {

        log.info("GET /profile - Getting user profile: {}", userId);

        return userService.findById(userId)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profil utilisateur récupéré avec succès")
                .data(user)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<UserResponse>builder()
                .success(false)
                .message("Utilisateur non trouvé")
                .data(null)
                .build()))
            .doOnSuccess(response -> log.info("User profile retrieved successfully: {}", userId))
            .doOnError(error -> log.error("Failed to get user profile: {}", userId, error));
    }

    @Operation(
        summary = "Mettre à jour le profil utilisateur",
        description = "Met à jour les informations du profil de l'utilisateur connecté"
    )
    @PutMapping
    public Mono<ApiResponse<UserResponse>> updateProfile(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId,
        @Valid @RequestBody UpdateProfileRequest updateRequest) {

        log.info("PUT /profile - Updating user profile: {}", userId);

        return userService.updateProfile(userId, updateRequest)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profil mis à jour avec succès")
                .data(user)
                .build())
            .doOnSuccess(response -> log.info("User profile updated successfully: {}", userId))
            .doOnError(error -> log.error("Failed to update user profile: {}", userId, error));
    }

    @Operation(
        summary = "Uploader une photo de profil",
        description = "Met à jour la photo de profil de l'utilisateur"
    )
    @PostMapping("/avatar")
    public Mono<ApiResponse<String>> uploadAvatar(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId,
        @Parameter(description = "URL de la nouvelle photo de profil")
        @RequestParam String avatarUrl) {

        log.info("POST /profile/avatar - Uploading avatar for user: {}", userId);

        return userService.updateAvatar(userId, avatarUrl)
            .map(updatedUrl -> ApiResponse.<String>builder()
                .success(true)
                .message("Photo de profil mise à jour avec succès")
                .data(updatedUrl)
                .build())
            .doOnSuccess(response -> log.info("Avatar updated successfully for user: {}", userId))
            .doOnError(error -> log.error("Failed to update avatar for user: {}", userId, error));
    }

    @Operation(
        summary = "Supprimer la photo de profil",
        description = "Supprime la photo de profil actuelle de l'utilisateur"
    )
    @DeleteMapping("/avatar")
    public Mono<ApiResponse<String>> deleteAvatar(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId) {

        log.info("DELETE /profile/avatar - Deleting avatar for user: {}", userId);

        return userService.deleteAvatar(userId)
            .then(Mono.just(ApiResponse.<String>builder()
                .success(true)
                .message("Photo de profil supprimée avec succès")
                .data("Avatar deleted")
                .build()))
            .doOnSuccess(response -> log.info("Avatar deleted successfully for user: {}", userId))
            .doOnError(error -> log.error("Failed to delete avatar for user: {}", userId, error));
    }

    @Operation(
        summary = "Mettre à jour les préférences utilisateur",
        description = "Met à jour les préférences de l'utilisateur (langue, fuseau horaire, notifications, etc.)"
    )
    @PutMapping("/preferences")
    public Mono<ApiResponse<UserResponse>> updatePreferences(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId,
        @Valid @RequestBody UserPreferencesRequest preferencesRequest) {

        log.info("PUT /profile/preferences - Updating preferences for user: {}", userId);

        return userService.updatePreferences(userId, preferencesRequest)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Préférences mises à jour avec succès")
                .data(user)
                .build())
            .doOnSuccess(response -> log.info("Preferences updated successfully for user: {}", userId))
            .doOnError(error -> log.error("Failed to update preferences for user: {}", userId, error));
    }

    @Operation(
        summary = "Désactiver le compte utilisateur",
        description = "Désactive le compte de l'utilisateur connecté (soft delete)"
    )
    @PostMapping("/deactivate")
    public Mono<ApiResponse<String>> deactivateAccount(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId,
        @Valid @RequestBody DeactivateAccountRequest deactivateRequest) {

        log.info("POST /profile/deactivate - Deactivating account for user: {}", userId);

        return userService.deactivateAccount(userId, deactivateRequest.getReason())
            .then(Mono.just(ApiResponse.<String>builder()
                .success(true)
                .message("Compte désactivé avec succès")
                .data("Account deactivated")
                .build()))
            .doOnSuccess(response -> log.info("Account deactivated successfully for user: {}", userId))
            .doOnError(error -> log.error("Failed to deactivate account for user: {}", userId, error));
    }

    @Operation(
        summary = "Récupérer les informations employé",
        description = "Récupère les informations spécifiques à l'employé (personnel uniquement)"
    )
    @GetMapping("/employee")
    public Mono<ApiResponse<EmployeeInfoResponse>> getEmployeeInfo(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId) {

        log.info("GET /profile/employee - Getting employee info for user: {}", userId);

        return userService.findById(userId)
            .filter(user -> user.getIsPersonnel())
            .map(user -> {
                EmployeeInfoResponse employeeInfo = new EmployeeInfoResponse();
                employeeInfo.setEmployeeId(user.getEmployeeId());
                employeeInfo.setDepartment(user.getDepartment());
                employeeInfo.setPosition(user.getPosition());
                employeeInfo.setSupervisorId(user.getSupervisorId());
                employeeInfo.setHiredAt(user.getHiredAt());
                employeeInfo.setMustChangePassword(user.getMustChangePassword());
                employeeInfo.setOrganizationId(user.getOrganizationId());
                employeeInfo.setAgencyId(user.getAgencyId());

                return ApiResponse.<EmployeeInfoResponse>builder()
                    .success(true)
                    .message("Informations employé récupérées avec succès")
                    .data(employeeInfo)
                    .build();
            })
            .switchIfEmpty(Mono.just(ApiResponse.<EmployeeInfoResponse>builder()
                .success(false)
                .message("Utilisateur non trouvé ou n'est pas un employé")
                .data(null)
                .build()))
            .doOnSuccess(response -> log.info("Employee info retrieved for user: {}", userId));
    }

    @Operation(
        summary = "Mettre à jour les paramètres de notification",
        description = "Met à jour les préférences de notification de l'utilisateur"
    )
    @PutMapping("/notifications")
    public Mono<ApiResponse<UserResponse>> updateNotificationSettings(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId,
        @Valid @RequestBody NotificationSettingsRequest notificationRequest) {

        log.info("PUT /profile/notifications - Updating notification settings for user: {}", userId);

        UserPreferencesRequest preferencesRequest = new UserPreferencesRequest();
        preferencesRequest.setEmailNotifications(notificationRequest.getEmailNotifications());
        preferencesRequest.setSmsNotifications(notificationRequest.getSmsNotifications());

        return userService.updatePreferences(userId, preferencesRequest)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Paramètres de notification mis à jour avec succès")
                .data(user)
                .build())
            .doOnSuccess(response -> log.info("Notification settings updated for user: {}", userId))
            .doOnError(error -> log.error("Failed to update notification settings for user: {}", userId, error));
    }

    @Operation(
        summary = "Changer la langue de l'interface",
        description = "Change la langue préférée de l'utilisateur"
    )
    @PutMapping("/language")
    public Mono<ApiResponse<UserResponse>> changeLanguage(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId,
        @Parameter(description = "Code de langue (fr, en, es, etc.)")
        @RequestParam String language) {

        log.info("PUT /profile/language - Changing language to {} for user: {}", language, userId);

        UserPreferencesRequest preferencesRequest = new UserPreferencesRequest();
        preferencesRequest.setPreferredLanguage(language);

        return userService.updatePreferences(userId, preferencesRequest)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Langue mise à jour avec succès")
                .data(user)
                .build())
            .doOnSuccess(response -> log.info("Language changed to {} for user: {}", language, userId))
            .doOnError(error -> log.error("Failed to change language for user: {}", userId, error));
    }

    @Operation(
        summary = "Changer le fuseau horaire",
        description = "Met à jour le fuseau horaire de l'utilisateur"
    )
    @PutMapping("/timezone")
    public Mono<ApiResponse<UserResponse>> changeTimezone(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId,
        @Parameter(description = "Fuseau horaire (ex: Africa/Douala)")
        @RequestParam String timezone) {

        log.info("PUT /profile/timezone - Changing timezone to {} for user: {}", timezone, userId);

        UserPreferencesRequest preferencesRequest = new UserPreferencesRequest();
        preferencesRequest.setTimezone(timezone);

        return userService.updatePreferences(userId, preferencesRequest)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Fuseau horaire mis à jour avec succès")
                .data(user)
                .build())
            .doOnSuccess(response -> log.info("Timezone changed to {} for user: {}", timezone, userId))
            .doOnError(error -> log.error("Failed to change timezone for user: {}", userId, error));
    }
}
