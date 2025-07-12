package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.OnboardingSession;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OnboardingSessionReactiveRepository extends ReactiveCassandraRepository<OnboardingSession, UUID> {

    /**
     * Trouve une session par token
     */
    @Query("SELECT * FROM onboarding_sessions WHERE session_token = ?0 ALLOW FILTERING")
    Mono<OnboardingSession> findBySessionToken(String sessionToken);

    /**
     * Trouve les sessions expirées
     */
    @Query("SELECT * FROM onboarding_sessions WHERE expires_at < ?0 ALLOW FILTERING")
    Flux<OnboardingSession> findExpiredSessions(LocalDateTime now);

    /**
     * Trouve les sessions actives (non terminées et non expirées)
     */
    @Query("SELECT * FROM onboarding_sessions WHERE is_completed = false AND expires_at > ?0 ALLOW FILTERING")
    Flux<OnboardingSession> findActiveSessions(LocalDateTime now);

    /**
     * Supprime les sessions expirées
     */
    @Query("DELETE FROM onboarding_sessions WHERE expires_at < ?0")
    Mono<Void> deleteExpiredSessions(LocalDateTime now);
}
