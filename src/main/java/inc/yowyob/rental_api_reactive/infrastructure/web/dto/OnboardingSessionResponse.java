package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour les sessions d'onboarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingSessionResponse {

    @JsonProperty("id")
    private UUID id;

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

    @JsonProperty("owner_info_completed")
    private Boolean ownerInfoCompleted;

    @JsonProperty("organization_info_completed")
    private Boolean organizationInfoCompleted;

    @JsonProperty("subscription_info_completed")
    private Boolean subscriptionInfoCompleted;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("completed_at")
    private LocalDateTime completedAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("completion_percentage")
    public Double getCompletionPercentage() {
        int completed = 0;
        if (Boolean.TRUE.equals(ownerInfoCompleted)) completed++;
        if (Boolean.TRUE.equals(organizationInfoCompleted)) completed++;
        if (Boolean.TRUE.equals(subscriptionInfoCompleted)) completed++;

        return (double) completed / maxStep * 100.0;
    }

    @JsonProperty("is_expired")
    public Boolean getIsExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @JsonProperty("is_valid")
    public Boolean getIsValid() {
        return !getIsExpired() && !isCompleted;
    }
}
