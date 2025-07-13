package inc.yowyob.rental_api_reactive.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.yowyob.rental_api_reactive.persistence.entity.OnboardingSession;
import inc.yowyob.rental_api_reactive.persistence.repository.OnboardingSessionReactiveRepository;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OwnerInfoRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OnboardingSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnboardingReactiveServiceTest {

    @Mock
    private OnboardingSessionReactiveRepository onboardingSessionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OnboardingReactiveService onboardingService;

    private OnboardingSession testSession;
    private UUID testSessionId;

    @BeforeEach
    void setUp() {
        testSessionId = UUID.randomUUID();
        testSession = new OnboardingSession();
        testSession.setId(testSessionId);
        testSession.setSessionToken("test-token");
        testSession.setCurrentStep(1);
        testSession.setMaxStep(3);
        testSession.setIsCompleted(false);
        testSession.setExpiresAt(LocalDateTime.now().plusHours(24));
    }

    @Test
    void createOnboardingSession_ShouldCreateAndReturnSession() {
        // Given
        when(onboardingSessionRepository.save(any(OnboardingSession.class)))
            .thenReturn(Mono.just(testSession));

        // When & Then
        StepVerifier.create(onboardingService.createOnboardingSession())
            .expectNextMatches(response ->
                response.getId().equals(testSessionId) &&
                    response.getCurrentStep() == 1 &&
                    !response.getIsCompleted()
            )
            .verifyComplete();
    }

    @Test
    void getOnboardingSession_WhenSessionExists_ShouldReturnSession() {
        // Given
        when(onboardingSessionRepository.findById(testSessionId))
            .thenReturn(Mono.just(testSession));

        // When & Then
        StepVerifier.create(onboardingService.getOnboardingSession(testSessionId))
            .expectNextMatches(response ->
                response.getId().equals(testSessionId) &&
                    response.getSessionToken().equals("test-token")
            )
            .verifyComplete();
    }

    @Test
    void getOnboardingSession_WhenSessionNotExists_ShouldReturnError() {
        // Given
        when(onboardingSessionRepository.findById(testSessionId))
            .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(onboardingService.getOnboardingSession(testSessionId))
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void saveOwnerInfo_WhenSessionValid_ShouldSaveAndReturnUpdatedSession() throws Exception {
        // Given
        OwnerInfoRequest ownerInfo = new OwnerInfoRequest();
        ownerInfo.setFirstName("John");
        ownerInfo.setLastName("Doe");
        ownerInfo.setEmail("john.doe@example.com");

        when(onboardingSessionRepository.findById(testSessionId))
            .thenReturn(Mono.just(testSession));
        when(objectMapper.writeValueAsString(any()))
            .thenReturn("{\"firstName\":\"John\"}");
        when(onboardingSessionRepository.save(any(OnboardingSession.class)))
            .thenReturn(Mono.just(testSession));

        // When & Then
        StepVerifier.create(onboardingService.saveOwnerInfo(testSessionId, ownerInfo))
            .expectNextMatches(response ->
                response.getId().equals(testSessionId)
            )
            .verifyComplete();
    }

    @Test
    void saveOwnerInfo_WhenSessionExpired_ShouldReturnError() {
        // Given
        testSession.setExpiresAt(LocalDateTime.now().minusHours(1)); // Expired
        OwnerInfoRequest ownerInfo = new OwnerInfoRequest();

        when(onboardingSessionRepository.findById(testSessionId))
            .thenReturn(Mono.just(testSession));

        // When & Then
        StepVerifier.create(onboardingService.saveOwnerInfo(testSessionId, ownerInfo))
            .expectError(RuntimeException.class)
            .verify();
    }
}
