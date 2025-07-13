package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO pour les informations de souscription (Étape 3)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionInfoRequest {

    @NotNull(message = "Subscription plan ID is required")
    @JsonProperty("subscription_plan_id")
    private UUID subscriptionPlanId;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_token")
    private String paymentToken;

    @JsonProperty("billing_address")
    private String billingAddress;

    @JsonProperty("billing_city")
    private String billingCity;

    @JsonProperty("billing_country")
    private String billingCountry;

    @JsonProperty("accept_terms")
    private Boolean acceptTerms = false;

    @JsonProperty("newsletter_subscription")
    private Boolean newsletterSubscription = false;
}
