package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import inc.yowyob.rental_api_reactive.persistence.repository.SubscriptionPlanReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanReactiveServiceTest {

    @Mock
    private SubscriptionPlanReactiveRepository subscriptionPlanRepository;

    @InjectMocks
    private SubscriptionPlanReactiveService subscriptionPlanService;

    private SubscriptionPlan testPlan;
    private UUID testPlanId;

    @BeforeEach
    void setUp() {
        testPlanId = UUID.randomUUID();
        testPlan = new SubscriptionPlan();
        testPlan.setId(testPlanId);
        testPlan.setName("BASIC");
        testPlan.setDescription("Plan de base");
        testPlan.setPrice(new BigDecimal("29.99"));
        testPlan.setCurrency("XAF");
        testPlan.setDurationDays(30);
        testPlan.setIsActive(true);
    }

    @Test
    void getAllActivePlans_ShouldReturnAllActivePlans() {
        // Given
        when(subscriptionPlanRepository.findAllActive())
            .thenReturn(Flux.just(testPlan));

        // When & Then
        StepVerifier.create(subscriptionPlanService.getAllActivePlans())
            .expectNext(testPlan)
            .verifyComplete();
    }

    @Test
    void getPlanById_WhenPlanExists_ShouldReturnPlan() {
        // Given
        when(subscriptionPlanRepository.findById(testPlanId))
            .thenReturn(Mono.just(testPlan));

        // When & Then
        StepVerifier.create(subscriptionPlanService.getPlanById(testPlanId))
            .expectNext(testPlan)
            .verifyComplete();
    }

    @Test
    void getPlanById_WhenPlanNotExists_ShouldReturnEmpty() {
        // Given
        when(subscriptionPlanRepository.findById(testPlanId))
            .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(subscriptionPlanService.getPlanById(testPlanId))
            .verifyComplete();
    }

    @Test
    void getPopularPlans_ShouldReturnPopularPlans() {
        // Given
        testPlan.setIsPopular(true);
        when(subscriptionPlanRepository.findPopularPlans())
            .thenReturn(Flux.just(testPlan));

        // When & Then
        StepVerifier.create(subscriptionPlanService.getPopularPlans())
            .expectNext(testPlan)
            .verifyComplete();
    }

    @Test
    void savePlan_ShouldSaveAndReturnPlan() {
        // Given
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class)))
            .thenReturn(Mono.just(testPlan));

        // When & Then
        StepVerifier.create(subscriptionPlanService.savePlan(testPlan))
            .expectNext(testPlan)
            .verifyComplete();
    }

    @Test
    void deactivatePlan_WhenPlanExists_ShouldDeactivateAndReturn() {
        // Given
        when(subscriptionPlanRepository.findById(testPlanId))
            .thenReturn(Mono.just(testPlan));
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class)))
            .thenReturn(Mono.just(testPlan));

        // When & Then
        StepVerifier.create(subscriptionPlanService.deactivatePlan(testPlanId))
            .expectNextMatches(plan -> !plan.getIsActive())
            .verifyComplete();
    }

    @Test
    void deactivatePlan_WhenPlanNotExists_ShouldReturnError() {
        // Given
        when(subscriptionPlanRepository.findById(testPlanId))
            .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(subscriptionPlanService.deactivatePlan(testPlanId))
            .expectError(RuntimeException.class)
            .verify();
    }
}
