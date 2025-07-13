package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.UserReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
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
 * Contrôleur réactif pour la gestion des utilisateurs
 * Route de base: /api/v1/users
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs réactives de gestion des utilisateurs")
public class UserReactiveController {

    private final UserReactiveService userService;

    @Operation(
        summary = "Récupérer tous les utilisateurs",
        description = "Retourne la liste de tous les utilisateurs non supprimés"
    )
    @GetMapping
    public Mono<ApiResponse<java.util.List<UserResponse>>> getAllUsers(
        @Parameter(description = "ID de l'organisation (optionnel)")
        @RequestParam(required = false) UUID organizationId,
        @Parameter(description = "Type d'utilisateur (optionnel)")
        @RequestParam(required = false) UserType userType,
        @Parameter(description = "Utilisateurs actifs uniquement")
        @RequestParam(defaultValue = "true") Boolean activeOnly) {

        log.info("GET /users - Fetching users for organization: {}, type: {}, activeOnly: {}",
            organizationId, userType, activeOnly);

        if (organizationId != null) {
            return userService.findByOrganizationId(organizationId)
                .filter(user -> userType == null || userType.equals(user.getUserType()))
                .filter(user -> !activeOnly || user.getIsActive())
                .collectList()
                .map(users -> ApiResponse.<java.util.List<UserResponse>>builder()
                    .success(true)
                    .message("Utilisateurs récupérés avec succès")
                    .data(users)
                    .count((long) users.size())
                    .build());
        } else {
            return userService.findAll()
                .filter(user -> userType == null || userType.equals(user.getUserType()))
                .filter(user -> !activeOnly || user.getIsActive())
                .collectList()
                .map(users -> ApiResponse.<java.util.List<UserResponse>>builder()
                    .success(true)
                    .message("Utilisateurs récupérés avec succès")
                    .data(users)
                    .count((long) users.size())
                    .build());
        }
    }

    @Operation(
        summary = "Récupérer un utilisateur par ID",
        description = "Retourne les détails d'un utilisateur spécifique"
    )
    @GetMapping("/{userId}")
    public Mono<ApiResponse<UserResponse>> getUserById(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId) {

        log.info("GET /users/{} - Fetching user details", userId);

        return userService.findById(userId)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Utilisateur trouvé avec succès")
                .data(user)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<UserResponse>builder()
                .success(false)
                .message("Utilisateur non trouvé")
                .data(null)
                .build()))
            .doOnSuccess(response -> log.info("User details retrieved: {}", userId));
    }

    @Operation(
        summary = "Récupérer un utilisateur par email",
        description = "Retourne les détails d'un utilisateur par son adresse email"
    )
    @GetMapping("/email/{email}")
    public Mono<ApiResponse<UserResponse>> getUserByEmail(
        @Parameter(description = "Email de l'utilisateur")
        @PathVariable String email) {

        log.info("GET /users/email/{} - Fetching user by email", email);

        return userService.findByEmail(email)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Utilisateur trouvé avec succès")
                .data(user)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<UserResponse>builder()
                .success(false)
                .message("Utilisateur non trouvé avec cet email")
                .data(null)
                .build()))
            .doOnSuccess(response -> log.info("User found by email: {}", email));
    }

    @Operation(
        summary = "Mettre à jour le statut d'un utilisateur",
        description = "Active ou désactive un compte utilisateur"
    )
    @PutMapping("/{userId}/status")
    public Mono<ApiResponse<UserResponse>> updateUserStatus(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @Parameter(description = "Nouveau statut")
        @RequestParam Boolean isActive,
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID updatedBy) {

        log.info("PUT /users/{}/status - Updating status to: {}", userId, isActive);

        return userService.updateUserStatus(userId, isActive, updatedBy)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message(isActive ? "Utilisateur activé avec succès" : "Utilisateur désactivé avec succès")
                .data(user)
                .build())
            .doOnSuccess(response -> log.info("User status updated successfully: {}", userId))
            .doOnError(error -> log.error("Failed to update user status: {}", userId, error));
    }

    @Operation(
        summary = "Récupérer le personnel d'une organisation",
        description = "Retourne tous les membres du personnel d'une organisation"
    )
    @GetMapping("/organization/{organizationId}/personnel")
    public Mono<ApiResponse<java.util.List<UserResponse>>> getOrganizationPersonnel(
        @Parameter(description = "ID de l'organisation")
        @PathVariable UUID organizationId) {

        log.info("GET /users/organization/{}/personnel - Fetching personnel", organizationId);

        return userService.findPersonnelByOrganization(organizationId)
            .collectList()
            .map(personnel -> ApiResponse.<java.util.List<UserResponse>>builder()
                .success(true)
                .message("Personnel de l'organisation récupéré avec succès")
                .data(personnel)
                .count((long) personnel.size())
                .build())
            .doOnSuccess(response -> log.info("Retrieved {} personnel for organization: {}",
                response.getData().size(), organizationId));
    }

    @Operation(
        summary = "Récupérer le personnel d'une agence",
        description = "Retourne tous les membres du personnel d'une agence"
    )
    @GetMapping("/agency/{agencyId}/personnel")
    public Mono<ApiResponse<java.util.List<UserResponse>>> getAgencyPersonnel(
        @Parameter(description = "ID de l'agence")
        @PathVariable UUID agencyId) {

        log.info("GET /users/agency/{}/personnel - Fetching personnel", agencyId);

        return userService.findPersonnelByAgency(agencyId)
            .collectList()
            .map(personnel -> ApiResponse.<java.util.List<UserResponse>>builder()
                .success(true)
                .message("Personnel de l'agence récupéré avec succès")
                .data(personnel)
                .count((long) personnel.size())
                .build())
            .doOnSuccess(response -> log.info("Retrieved {} personnel for agency: {}",
                response.getData().size(), agencyId));
    }

    @Operation(
        summary = "Récupérer les utilisateurs par type",
        description = "Retourne tous les utilisateurs d'un type spécifique"
    )
    @GetMapping("/type/{userType}")
    public Mono<ApiResponse<java.util.List<UserResponse>>> getUsersByType(
        @Parameter(description = "Type d'utilisateur")
        @PathVariable UserType userType,
        @Parameter(description = "ID de l'organisation (optionnel)")
        @RequestParam(required = false) UUID organizationId) {

        log.info("GET /users/type/{} - Fetching users by type for organization: {}", userType, organizationId);

        return userService.findByUserType(userType)
            .filter(user -> organizationId == null || organizationId.equals(user.getOrganizationId()))
            .collectList()
            .map(users -> ApiResponse.<java.util.List<UserResponse>>builder()
                .success(true)
                .message("Utilisateurs du type " + userType.getDisplayName() + " récupérés avec succès")
                .data(users)
                .count((long) users.size())
                .build())
            .doOnSuccess(response -> log.info("Retrieved {} users of type: {}",
                response.getData().size(), userType));
    }

    @Operation(
        summary = "Vérifier si un email existe",
        description = "Vérifie si une adresse email est déjà utilisée"
    )
    @GetMapping("/check-email")
    public Mono<ApiResponse<Boolean>> checkEmailExists(
        @Parameter(description = "Adresse email à vérifier")
        @RequestParam String email) {

        log.info("GET /users/check-email - Checking email: {}", email);

        return userService.existsByEmail(email)
            .map(exists -> ApiResponse.<Boolean>builder()
                .success(true)
                .message(exists ? "Email déjà utilisé" : "Email disponible")
                .data(exists)
                .build())
            .doOnSuccess(response -> log.info("Email {} exists: {}", email, response.getData()));
    }

    @Operation(
        summary = "Statistiques des utilisateurs",
        description = "Retourne les statistiques des utilisateurs d'une organisation"
    )
    @GetMapping("/organization/{organizationId}/stats")
    public Mono<ApiResponse<UserStatsResponse>> getUserStats(
        @Parameter(description = "ID de l'organisation")
        @PathVariable UUID organizationId) {

        log.info("GET /users/organization/{}/stats - Getting user statistics", organizationId);

        return userService.getUserStats(organizationId)
            .map(stats -> ApiResponse.<UserStatsResponse>builder()
                .success(true)
                .message("Statistiques des utilisateurs récupérées avec succès")
                .data(stats)
                .build())
            .doOnSuccess(response -> log.info("User statistics retrieved for organization: {}", organizationId));
    }

    @Operation(
        summary = "Rechercher des utilisateurs",
        description = "Recherche des utilisateurs par nom, email ou ID employé"
    )
    @GetMapping("/search")
    public Mono<ApiResponse<java.util.List<UserResponse>>> searchUsers(
        @Parameter(description = "Terme de recherche")
        @RequestParam String query,
        @Parameter(description = "ID de l'organisation (optionnel)")
        @RequestParam(required = false) UUID organizationId,
        @Parameter(description = "Type d'utilisateur (optionnel)")
        @RequestParam(required = false) UserType userType) {

        log.info("GET /users/search - Searching users with query: {} for organization: {}", query, organizationId);

        return userService.findAll()
            .filter(user -> organizationId == null || organizationId.equals(user.getOrganizationId()))
            .filter(user -> userType == null || userType.equals(user.getUserType()))
            .filter(user -> matchesSearchQuery(user, query))
            .collectList()
            .map(users -> ApiResponse.<java.util.List<UserResponse>>builder()
                .success(true)
                .message("Recherche terminée avec succès")
                .data(users)
                .count((long) users.size())
                .build())
            .doOnSuccess(response -> log.info("Search completed, found {} users", response.getData().size()));
    }

    @Operation(
        summary = "Supprimer un utilisateur",
        description = "Supprime définitivement un utilisateur (action irréversible)"
    )
    @DeleteMapping("/{userId}")
    public Mono<ApiResponse<String>> deleteUser(
        @Parameter(description = "ID de l'utilisateur")
        @PathVariable UUID userId,
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID deletedBy) {

        log.info("DELETE /users/{} - Deleting user", userId);

        return userService.deleteById(userId)
            .then(Mono.just(ApiResponse.<String>builder()
                .success(true)
                .message("Utilisateur supprimé avec succès")
                .data("User deleted")
                .build()))
            .doOnSuccess(response -> log.info("User deleted successfully: {}", userId))
            .doOnError(error -> log.error("Failed to delete user: {}", userId, error));
    }

    /**
     * Vérifie si un utilisateur correspond à la requête de recherche
     */
    private boolean matchesSearchQuery(UserResponse user, String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }

        String lowerQuery = query.toLowerCase().trim();

        return (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(lowerQuery)) ||
            (user.getLastName() != null && user.getLastName().toLowerCase().contains(lowerQuery)) ||
            (user.getFullName() != null && user.getFullName().toLowerCase().contains(lowerQuery)) ||
            (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)) ||
            (user.getEmployeeId() != null && user.getEmployeeId().toLowerCase().contains(lowerQuery)) ||
            (user.getDepartment() != null && user.getDepartment().toLowerCase().contains(lowerQuery)) ||
            (user.getPosition() != null && user.getPosition().toLowerCase().contains(lowerQuery));
    }
}
