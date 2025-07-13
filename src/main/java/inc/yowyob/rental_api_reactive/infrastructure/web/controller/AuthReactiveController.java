package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.AuthReactiveService;
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
 * Contrôleur réactif d'authentification
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs réactives d'authentification et de gestion des utilisateurs")
public class AuthReactiveController {

    private final AuthReactiveService authService;

    @Operation(
        summary = "Inscription d'un nouvel utilisateur",
        description = "Crée un nouveau compte utilisateur avec validation email"
    )
    @PostMapping("/register")
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("POST /auth/register - Registering user: {}", registerRequest.getEmail());

        return authService.register(registerRequest)
            .doOnSuccess(response -> log.info("User registered successfully: {}", registerRequest.getEmail()))
            .doOnError(error -> log.error("Registration failed for: {}", registerRequest.getEmail(), error));
    }

    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur et retourne les tokens JWT"
    )
    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("POST /auth/login - Login attempt: {}", loginRequest.getEmail());

        return authService.login(loginRequest)
            .doOnSuccess(response -> log.info("User logged in successfully: {}", loginRequest.getEmail()))
            .doOnError(error -> log.error("Login failed for: {}", loginRequest.getEmail(), error));
    }

    @Operation(
        summary = "Renouvellement du token d'accès",
        description = "Génère un nouveau token d'accès à partir du refresh token"
    )
    @PostMapping("/refresh")
    public Mono<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        log.info("POST /auth/refresh - Refreshing token");

        return authService.refreshToken(refreshRequest)
            .doOnSuccess(response -> log.info("Token refreshed successfully"))
            .doOnError(error -> log.error("Token refresh failed", error));
    }

    @Operation(
        summary = "Déconnexion",
        description = "Déconnecte l'utilisateur (côté client principalement)"
    )
    @PostMapping("/logout")
    public Mono<ApiResponse<String>> logout() {
        log.info("POST /auth/logout - User logout");

        // Dans un système JWT stateless, la déconnexion se fait côté client
        // En supprimant les tokens du stockage local
        return Mono.just(ApiResponse.<String>builder()
            .success(true)
            .message("Logged out successfully")
            .data("Please remove tokens from client storage")
            .build());
    }

    @Operation(
        summary = "Changement de mot de passe",
        description = "Change le mot de passe de l'utilisateur connecté"
    )
    @PostMapping("/change-password")
    public Mono<ApiResponse<String>> changePassword(
        @Valid @RequestBody ChangePasswordRequest changeRequest,
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId) {

        log.info("POST /auth/change-password - Changing password for user: {}", userId);

        return authService.changePassword(changeRequest, userId)
            .doOnSuccess(response -> log.info("Password changed successfully for user: {}", userId))
            .doOnError(error -> log.error("Password change failed for user: {}", userId, error));
    }

    @Operation(
        summary = "Demande de réinitialisation de mot de passe",
        description = "Envoie un email avec les instructions de réinitialisation"
    )
    @PostMapping("/forgot-password")
    public Mono<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotRequest) {
        log.info("POST /auth/forgot-password - Password reset requested for: {}", forgotRequest.getEmail());

        return authService.forgotPassword(forgotRequest)
            .doOnSuccess(response -> log.info("Password reset instructions sent for: {}", forgotRequest.getEmail()))
            .doOnError(error -> log.error("Forgot password failed for: {}", forgotRequest.getEmail(), error));
    }

    @Operation(
        summary = "Réinitialisation du mot de passe",
        description = "Réinitialise le mot de passe avec le token reçu par email"
    )
    @PostMapping("/reset-password")
    public Mono<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetRequest) {
        log.info("POST /auth/reset-password - Resetting password with token");

        return authService.resetPassword(resetRequest)
            .doOnSuccess(response -> log.info("Password reset successfully"))
            .doOnError(error -> log.error("Password reset failed", error));
    }

    @Operation(
        summary = "Vérification d'email",
        description = "Vérifie l'adresse email avec le token reçu"
    )
    @PostMapping("/verify-email")
    public Mono<ApiResponse<String>> verifyEmail(
        @Parameter(description = "Token de vérification email")
        @RequestParam String token) {

        log.info("POST /auth/verify-email - Verifying email with token");

        return authService.verifyEmail(token)
            .doOnSuccess(response -> log.info("Email verified successfully"))
            .doOnError(error -> log.error("Email verification failed", error));
    }

    @Operation(
        summary = "Profil utilisateur connecté",
        description = "Récupère les informations de l'utilisateur connecté"
    )
    @GetMapping("/me")
    public Mono<ApiResponse<UserResponse>> getCurrentUser(
        @Parameter(description = "ID de l'utilisateur connecté")
        @RequestHeader("X-User-Id") UUID userId) {

        log.info("GET /auth/me - Getting current user profile: {}", userId);

        return authService.getCurrentUser(userId)
            .doOnSuccess(response -> log.info("User profile retrieved for: {}", userId))
            .doOnError(error -> log.error("Failed to get user profile for: {}", userId, error));
    }
}
