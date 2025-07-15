package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Réponse d'onboarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingResponse {
    @JsonProperty("session_token")
    private String sessionToken;

    @JsonProperty("current_step")
    private Integer currentStep;

    @JsonProperty("max_step")
    private Integer maxStep;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    @JsonProperty("has_owner_info")
    private Boolean hasOwnerInfo;

    @JsonProperty("has_organization_info")
    private Boolean hasOrganizationInfo;

    @JsonProperty("has_subscription_info")
    private Boolean hasSubscriptionInfo;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
