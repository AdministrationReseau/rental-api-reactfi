package inc.yowyob.rental_api_reactive.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.yowyob.rental_api_reactive.persistence.entity.OnboardingSession;
import inc.yowyob.rental_api_reactive.persistence.repository.OnboardingSessionReactiveRepository;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
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
public class OnboardingReactiveService {

    private final OnboardingSessionReactiveRepository onboardingSessionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Crée une nouvelle session d'onboarding
     */
    public Mono<OnboardingSessionResponse> createOnboardingSession() {
        log.info("Creating new onboarding session");

        OnboardingSession session = new OnboardingSession();

        return onboardingSessionRepository.save(session)
            .map(this::mapToResponse)
            .doOnSuccess(response -> log.info("Created onboarding session: {}", response.getId()))
            .doOnError(error -> log.error("Error creating onboarding session", error));
    }

    /**
     * Récupère une session d'onboarding par ID
     */
    public Mono<OnboardingSessionResponse> getOnboardingSession(UUID sessionId) {
        log.debug("Fetching onboarding session: {}", sessionId);

        return onboardingSessionRepository.findById(sessionId)
            .map(this::mapToResponse)
            .doOnNext(response -> log.debug("Found session: {}", sessionId))
            .switchIfEmpty(Mono.error(new RuntimeException("Session not found: " + sessionId)));
    }

    /**
     * Récupère une session par token
     */
    public Mono<OnboardingSessionResponse> getSessionByToken(String sessionToken) {
        log.debug("Fetching onboarding session by token: {}", sessionToken);

        return onboardingSessionRepository.findBySessionToken(sessionToken)
            .map(this::mapToResponse)
            .doOnNext(response -> log.debug("Found session by token"))
            .switchIfEmpty(Mono.error(new RuntimeException("Session not found for token")));
    }

    /**
     * Sauvegarde les informations du propriétaire (Étape 1)
     */
    public Mono<OnboardingSessionResponse> saveOwnerInfo(UUID sessionId, OwnerInfoRequest ownerInfo) {
        log.info("Saving owner info for session: {}", sessionId);

        return onboardingSessionRepository.findById(sessionId)
            .switchIfEmpty(Mono.error(new RuntimeException("Session not found: " + sessionId)))
            .flatMap(session -> {
                if (!session.isValid()) {
                    return Mono.error(new RuntimeException("Session is not valid"));
                }

                try {
                    String ownerInfoJson = objectMapper.writeValueAsString(ownerInfo);
                    session.updateOwnerInfo(ownerInfoJson);
                    session.nextStep();

                    return onboardingSessionRepository.save(session);
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error serializing owner info", e));
                }
            })
            .map(this::mapToResponse)
            .doOnSuccess(response -> log.info("Owner info saved for session: {}", sessionId))
            .doOnError(error -> log.error("Error saving owner info for session: {}", sessionId, error));
    }

    /**
     * Sauvegarde les informations de l'organisation (Étape 2)
     */
    public Mono<OnboardingSessionResponse> saveOrganizationInfo(UUID sessionId, OrganizationInfoRequest organizationInfo) {
        log.info("Saving organization info for session: {}", sessionId);

        return onboardingSessionRepository.findById(sessionId)
            .switchIfEmpty(Mono.error(new RuntimeException("Session not found: " + sessionId)))
            .flatMap(session -> {
                if (!session.isValid()) {
                    return Mono.error(new RuntimeException("Session is not valid"));
                }

                if (session.getCurrentStep() < 2) {
                    return Mono.error(new RuntimeException("Must complete step 1 first"));
                }

                try {
                    String organizationInfoJson = objectMapper.writeValueAsString(organizationInfo);
                    session.updateOrganizationInfo(organizationInfoJson);
                    session.nextStep();

                    return onboardingSessionRepository.save(session);
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error serializing organization info", e));
                }
            })
            .map(this::mapToResponse)
            .doOnSuccess(response -> log.info("Organization info saved for session: {}", sessionId))
            .doOnError(error -> log.error("Error saving organization info for session: {}", sessionId, error));
    }

    /**
     * Finalise le processus d'onboarding (Étape 3)
     */
    public Mono<OnboardingCompletedResponse> completeOnboarding(UUID sessionId, SubscriptionInfoRequest subscriptionInfo) {
        log.info("Completing onboarding for session: {}", sessionId);

        return onboardingSessionRepository.findById(sessionId)
            .switchIfEmpty(Mono.error(new RuntimeException("Session not found: " + sessionId)))
            .flatMap(session -> {
                if (!session.isValid()) {
                    return Mono.error(new RuntimeException("Session is not valid"));
                }

                if (session.getCurrentStep() < 3) {
                    return Mono.error(new RuntimeException("Must complete previous steps first"));
                }

                try {
                    String subscriptionInfoJson = objectMapper.writeValueAsString(subscriptionInfo);
                    session.updateSubscriptionInfo(subscriptionInfoJson);

                    // Marquer comme terminé avec un organizationId temporaire
                    UUID organizationId = UUID.randomUUID();
                    session.complete(organizationId);

                    return onboardingSessionRepository.save(session)
                        .map(savedSession -> OnboardingCompletedResponse.builder()
                            .sessionId(savedSession.getId())
                            .organizationId(organizationId)
                            .message("Onboarding completed successfully")
                            .build());
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Error serializing subscription info", e));
                }
            })
            .doOnSuccess(response -> log.info("Onboarding completed for session: {}", sessionId))
            .doOnError(error -> log.error("Error completing onboarding for session: {}", sessionId, error));
    }

    /**
     * Nettoie les sessions expirées
     */
    public Mono<Long> cleanupExpiredSessions() {
        log.info("Cleaning up expired sessions");

        return onboardingSessionRepository.findExpiredSessions(LocalDateTime.now())
            .flatMap(session -> {
                session.setIsCompleted(true);
                return onboardingSessionRepository.save(session);
            })
            .count()
            .doOnSuccess(count -> log.info("Cleaned up {} expired sessions", count))
            .doOnError(error -> log.error("Error cleaning up expired sessions", error));
    }

    /**
     * Récupère les sessions actives
     */
    public Flux<OnboardingSessionResponse> getActiveSessions() {
        log.debug("Fetching active onboarding sessions");

        return onboardingSessionRepository.findActiveSessions(LocalDateTime.now())
            .map(this::mapToResponse)
            .doOnNext(session -> log.debug("Found active session: {}", session.getId()))
            .doOnComplete(() -> log.info("Successfully fetched active sessions"));
    }

    /**
     * Mappe une entité vers une réponse
     */
    private OnboardingSessionResponse mapToResponse(OnboardingSession session) {
        return OnboardingSessionResponse.builder()
            .id(session.getId())
            .sessionToken(session.getSessionToken())
            .currentStep(session.getCurrentStep())
            .maxStep(session.getMaxStep())
            .isCompleted(session.getIsCompleted())
            .expiresAt(session.getExpiresAt())
            .ownerInfoCompleted(session.getOwnerInfo() != null)
            .organizationInfoCompleted(session.getOrganizationInfo() != null)
            .subscriptionInfoCompleted(session.getSubscriptionInfo() != null)
            .organizationId(session.getOrganizationId())
            .completedAt(session.getCompletedAt())
            .createdAt(session.getCreatedAt())
            .updatedAt(session.getUpdatedAt())
            .build();
    }
}
