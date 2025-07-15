package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Réponse de finalisation d'onboarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingCompletionResponse {
    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("organization")
    private OrganizationResponse organization;

    @JsonProperty("subscription")
    private SubscriptionResponse subscription;

    @JsonProperty("next_steps")
    private String nextSteps;
}
