package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
        return userRepository.findAll()
            .map(this::mapToUserResponse)
            .doOnNext(user -> log.debug("Found user: {}", user.getEmail()));
    }

    /**
     * Trouve un utilisateur par ID
     */
    public Mono<UserResponse> findById(UUID id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
            .map(this::mapToUserResponse)
            .doOnNext(user -> log.debug("Found user: {}", user.getEmail()));
    }

    /**
     * Trouve un utilisateur par email
     */
    public Mono<UserResponse> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
            .map(this::mapToUserResponse)
            .doOnNext(user -> log.debug("Found user: {}", user.getEmail()));
    }

    /**
     * Trouve les utilisateurs par organisation
     */
    public Flux<UserResponse> findByOrganizationId(UUID organizationId) {
        log.debug("Finding users by organization ID: {}", organizationId);
        return userRepository.findByOrganizationId(organizationId)
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
     * Supprime un utilisateur
     */
    public Mono<Void> deleteById(UUID id) {
        log.debug("Deleting user by ID: {}", id);
        return userRepository.deleteById(id)
            .doOnSuccess(v -> log.info("User deleted successfully: {}", id));
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
        response.setIsEmailVerified(user.getIsEmailVerified());
        response.setIsPhoneVerified(user.getIsPhoneVerified());
        response.setPreferredLanguage(user.getPreferredLanguage());
        response.setTimezone(user.getTimezone());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());
        response.setIsActive(user.getIsActive());
        return response;
    }
}
