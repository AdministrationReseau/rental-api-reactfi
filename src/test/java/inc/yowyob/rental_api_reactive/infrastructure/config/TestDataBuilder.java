package inc.yowyob.rental_api_reactive.infrastructure.config;

import inc.yowyob.rental_api_reactive.persistence.entity.OnboardingSession;
import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Builder pour créer des données de test
 */
public class TestDataBuilder {

    public static SubscriptionPlan createTestSubscriptionPlan(String name, BigDecimal price) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(UUID.randomUUID());
        plan.setName(name);
        plan.setDescription("Plan de test " + name);
        plan.setPrice(price);
        plan.setCurrency("XAF");
        plan.setDurationDays(30);
        plan.setMaxVehicles(10);
        plan.setMaxDrivers(5);
        plan.setMaxAgencies(2);
        plan.setMaxUsers(8);
        plan.setIsPopular(false);
        plan.setIsCustom(false);
        plan.setSortOrder(1);
        plan.setFeatures(Map.of(
            "geofencing", false,
            "chat", true,
            "advanced_reports", false
        ));
        plan.prePersist();
        return plan;
    }

    public static OnboardingSession createTestOnboardingSession() {
        OnboardingSession session = new OnboardingSession();
        session.setId(UUID.randomUUID());
        session.setSessionToken("test-token-" + UUID.randomUUID());
        session.setCurrentStep(1);
        session.setMaxStep(3);
        session.setIsCompleted(false);
        session.setExpiresAt(LocalDateTime.now().plusHours(24));
        session.prePersist();
        return session;
    }

    public static OwnerInfoRequest createTestOwnerInfoRequest() {
        OwnerInfoRequest request = new OwnerInfoRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("+237123456789");
        request.setIdCardNumber("ID123456789");
        request.setAddress("123 Test Street");
        request.setCity("Yaoundé");
        request.setCountry("Cameroun");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setNationality("Camerounaise");
        request.setProfession("Entrepreneur");
        return request;
    }

    public static OrganizationInfoRequest createTestOrganizationInfoRequest() {
        OrganizationInfoRequest request = new OrganizationInfoRequest();
        request.setOrganizationName("Test Rental Company");
        request.setOrganizationType("ENTERPRISE");
        request.setRegistrationNumber("RC123456");
        request.setTaxNumber("TAX789012");
        request.setAddress("456 Business Avenue");
        request.setCity("Douala");
        request.setCountry("Cameroun");
        request.setDescription("Entreprise de location de véhicules de test");
        request.setAllowsDriverRental(true);
        request.setAllowsDriverlessRental(true);
        request.setRequireDeposit(true);
        request.setDefaultDepositAmount(50000.0);
        request.setCancellationPolicy("Annulation gratuite 24h avant");
        return request;
    }

    public static SubscriptionInfoRequest createTestSubscriptionInfoRequest(UUID planId) {
        SubscriptionInfoRequest request = new SubscriptionInfoRequest();
        request.setSubscriptionPlanId(planId);
        request.setPaymentMethod("CARD");
        request.setPaymentToken("test-payment-token");
        request.setBillingAddress("789 Billing Street");
        request.setBillingCity("Yaoundé");
        request.setBillingCountry("Cameroun");
        request.setAcceptTerms(true);
        request.setNewsletterSubscription(false);
        return request;
    }
}
