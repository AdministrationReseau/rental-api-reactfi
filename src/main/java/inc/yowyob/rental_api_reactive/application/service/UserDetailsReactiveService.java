package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Service réactif pour le chargement des détails utilisateur
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsReactiveService implements ReactiveUserDetailsService {

    private final UserReactiveRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        log.debug("Loading user by email: {}", email);

        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with email: " + email)))
            .map(this::createUserDetails)
            .doOnNext(userDetails -> log.debug("User loaded successfully: {}", email))
            .doOnError(error -> log.error("Failed to load user: {}", email, error));
    }

    /**
     * Crée un objet UserDetails à partir d'un utilisateur
     */
    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .disabled(!user.getIsActive())
            .accountExpired(false)
            .accountLocked(user.getLockedUntil() != null && user.getLockedUntil().isAfter(java.time.LocalDateTime.now()))
            .credentialsExpired(false)
            .authorities(Collections.emptyList()) // À implémenter selon les rôles
            .build();
    }
}
