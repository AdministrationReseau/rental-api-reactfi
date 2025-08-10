package inc.yowyob.rental_api_reactive.infrastructure.web.dto.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.organization.OrganizationResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.user.UserResponse;
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
