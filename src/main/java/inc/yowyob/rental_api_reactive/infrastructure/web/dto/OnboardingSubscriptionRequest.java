package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.PaymentMethod;
import lombok.*;
import java.util.UUID;

/**
 * Requête d'informations abonnement pour l'onboarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingSubscriptionRequest {
    @JsonProperty("subscription_plan_id")
    private UUID subscriptionPlanId;

    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("payment_reference")
    private String paymentReference;

    @JsonProperty("auto_renew")
    private Boolean autoRenew;
}
