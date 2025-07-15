package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.config.AppProperties;
import inc.yowyob.rental_api_reactive.infrastructure.security.jwt.JwtReactiveTokenProvider;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service réactif d'authentification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthReactiveService {

    private final UserReactiveRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtReactiveTokenProvider jwtTokenProvider;
    private final AppProperties appProperties;
    private final PersonnelReactiveService personnelService;

    // Types d'utilisateurs considérés comme personnel
    private static final List<UserType> PERSONNEL_TYPES = Arrays.asList(
        UserType.AGENCY_MANAGER,
        UserType.RENTAL_AGENT
    );

    /**
     * Inscription d'un nouvel utilisateur
     */
    public Mono<AuthResponse> register(RegisterRequest registerRequest) {
        log.info("Registering new user: {}", registerRequest.getEmail());

        return userRepository.countByEmail(registerRequest.getEmail())
            .flatMap(count -> {
                if (count > 0) {
                    return Mono.error(new IllegalArgumentException("Email already exists"));
                }

                // Créer le nouvel utilisateur
                User user = new User();
                user.setId(UUID.randomUUID());
                user.setEmail(registerRequest.getEmail());
                user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                user.setFirstName(registerRequest.getFirstName());
                user.setLastName(registerRequest.getLastName());
                user.setPhone(registerRequest.getPhone());
                user.setUserType(registerRequest.getUserType());
                user.setOrganizationId(registerRequest.getOrganizationId());
                user.setAgencyId(registerRequest.getAgencyId());
                user.setIsActive(true);
                user.setIsEmailVerified(false);
                user.setIsPhoneVerified(false);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                // Générer token de vérification email
                user.setEmailVerificationToken(UUID.randomUUID().toString());

                return userRepository.save(user)
                    .flatMap(savedUser -> generateAuthResponse(savedUser))
                    .doOnSuccess(response -> log.info("User registered successfully: {}", registerRequest.getEmail()));
            });
    }

    /**
     * Connexion d'un utilisateur
     */
    public Mono<AuthResponse> login(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        return userRepository.findByEmail(loginRequest.getEmail())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email or password")))
            .flatMap(user -> {
                // Réinitialiser les tentatives échouées
                user.setLastLoginAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                return userRepository.save(user)
                    .flatMap(savedUser -> {
                        // Vérifier si c'est du personnel
                        if (PERSONNEL_TYPES.contains(savedUser.getUserType())) {
                            return generatePersonnelAuthResponse(savedUser);
                        } else {
                            return generateAuthResponse(savedUser);
                        }
                    })
                    .doOnSuccess(response -> log.info("User logged in successfully: {}", loginRequest.getEmail()));
            });
    }

    /**
     * Génère une réponse d'authentification spécifique pour le personnel
     * avec informations de redirection vers l'agence
     */
    private Mono<AuthResponse> generatePersonnelAuthResponse(User user) {
        return Mono.zip(
            jwtTokenProvider.generateToken(user),
            jwtTokenProvider.generateRefreshToken(user),
            personnelService.getPersonnelAgencyInfo(user.getId())
        ).map(tuple -> {
            String accessToken = tuple.getT1();
            String refreshToken = tuple.getT2();
            AgencyRedirectInfo agencyInfo = tuple.getT3();

            UserResponse userResponse = mapToUserResponse(user);

            AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(appProperties.getJwt().getExpiration() / 1000)
                .user(userResponse)
                .build();

            // Construire l'URL de redirection
            if (agencyInfo.getAgencyId() != null) {
                String redirectUrl = String.format("/dashboard/agencies/%s", agencyInfo.getAgencyId());
                agencyInfo.setRedirectUrl(redirectUrl);
            } else {
                // Personnel non assigné à une agence - redirection vers organisation
                String redirectUrl = String.format("/dashboard/organizations/%s/unassigned-personnel",
                    agencyInfo.getOrganizationId());
                agencyInfo.setRedirectUrl(redirectUrl);
            }

            return authResponse;
        });
    }

    /**
     * Renouvellement du token
     */
    public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshRequest) {
        log.info("Refreshing token");

        return jwtTokenProvider.validateToken(refreshRequest.getRefreshToken())
            .flatMap(isValid -> {
                if (!isValid) {
                    return Mono.error(new IllegalArgumentException("Invalid refresh token"));
                }

                return jwtTokenProvider.getUserIdFromToken(refreshRequest.getRefreshToken())
                    .flatMap(userRepository::findById)
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                    .flatMap(user -> {
                        // Vérifier si c'est du personnel pour la réponse appropriée
                        if (PERSONNEL_TYPES.contains(user.getUserType())) {
                            return generatePersonnelAuthResponse(user);
                        } else {
                            return generateAuthResponse(user);
                        }
                    });
            });
    }

    /**
     * Changement de mot de passe
     */
    public Mono<ApiResponse<String>> changePassword(ChangePasswordRequest changeRequest, UUID userId) {
        log.info("Changing password for user: {}", userId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .flatMap(user -> {
                // Vérifier l'ancien mot de passe
                if (!passwordEncoder.matches(changeRequest.getCurrentPassword(), user.getPassword())) {
                    return Mono.error(new IllegalArgumentException("Current password is incorrect"));
                }

                // Mettre à jour avec le nouveau mot de passe
                user.setPassword(passwordEncoder.encode(changeRequest.getNewPassword()));
                user.setUpdatedAt(LocalDateTime.now());

                return userRepository.save(user)
                    .then(Mono.just(ApiResponse.<String>builder()
                        .success(true)
                        .message("Password changed successfully")
                        .data("Password updated")
                        .build()));
            });
    }

    /**
     * Demande de réinitialisation de mot de passe
     */
    public Mono<ApiResponse<String>> forgotPassword(ForgotPasswordRequest forgotRequest) {
        log.info("Password reset requested for email: {}", forgotRequest.getEmail());

        return userRepository.findByEmail(forgotRequest.getEmail())
            .flatMap(user -> {
                // Générer token de réinitialisation
                String resetToken = UUID.randomUUID().toString();
                user.setPasswordResetToken(resetToken);
                user.setUpdatedAt(LocalDateTime.now());

                return userRepository.save(user)
                    .then(Mono.just(ApiResponse.<String>builder()
                        .success(true)
                        .message("Password reset instructions sent to your email")
                        .data(resetToken) // En production, envoyer par email
                        .build()));
            })
            .switchIfEmpty(Mono.just(ApiResponse.<String>builder()
                .success(true)
                .message("If the email exists, you will receive reset instructions")
                .data(null)
                .build()));
    }

    /**
     * Réinitialisation du mot de passe
     */
    public Mono<ApiResponse<String>> resetPassword(ResetPasswordRequest resetRequest) {
        log.info("Resetting password with token");

        return userRepository.findByPasswordResetToken(resetRequest.getToken())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid or expired reset token")))
            .flatMap(user -> {

                // Mettre à jour le mot de passe
                user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
                user.setPasswordResetToken(null);
                user.setUpdatedAt(LocalDateTime.now());

                return userRepository.save(user)
                    .then(Mono.just(ApiResponse.<String>builder()
                        .success(true)
                        .message("Password reset successfully")
                        .data("Password updated")
                        .build()));
            });
    }

    /**
     * Vérification d'email
     */
    public Mono<ApiResponse<String>> verifyEmail(String token) {
        log.info("Verifying email with token");

        return userRepository.findByEmailVerificationToken(token)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid verification token")))
            .flatMap(user -> {

                // Marquer l'email comme vérifié
                user.setIsEmailVerified(true);
                user.setEmailVerificationToken(null);
                user.setUpdatedAt(LocalDateTime.now());

                return userRepository.save(user)
                    .then(Mono.just(ApiResponse.<String>builder()
                        .success(true)
                        .message("Email verified successfully")
                        .data("Email verified")
                        .build()));
            });
    }

    /**
     * Récupération du profil utilisateur connecté
     */
    public Mono<ApiResponse<UserResponse>> getCurrentUser(UUID userId) {
        log.info("Getting current user profile: {}", userId);

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
            .map(user -> {
                UserResponse userResponse = mapToUserResponse(user);
                return ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("User profile retrieved successfully")
                    .data(userResponse)
                    .build();
            });
    }

    /**
     * Génère une réponse d'authentification avec tokens (utilisateurs classiques)
     */
    private Mono<AuthResponse> generateAuthResponse(User user) {
        return Mono.zip(
            jwtTokenProvider.generateToken(user),
            jwtTokenProvider.generateRefreshToken(user)
        ).map(tuple -> {
            String accessToken = tuple.getT1();
            String refreshToken = tuple.getT2();

            UserResponse userResponse = mapToUserResponse(user);

            AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(appProperties.getJwt().getExpiration() / 1000)
                .user(userResponse)
                .build();

            authResponse.setIsPersonnel(false);
            authResponse.setAgencyRedirectInfo(null);

            return authResponse;
        });
    }

    /**
     * Mappe une entité User vers UserResponse
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .userType(user.getUserType())
            .organizationId(user.getOrganizationId())
            .agencyId(user.getAgencyId())
            .profilePicture(user.getProfilePicture())
            .isEmailVerified(user.getIsEmailVerified())
            .isPhoneVerified(user.getIsPhoneVerified())
            .preferredLanguage(user.getPreferredLanguage())
            .timezone(user.getTimezone())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .isActive(user.getIsActive())
            .address(user.getAddress())
            .city(user.getCity())
            .country(user.getCountry())
            .build();
    }
}
