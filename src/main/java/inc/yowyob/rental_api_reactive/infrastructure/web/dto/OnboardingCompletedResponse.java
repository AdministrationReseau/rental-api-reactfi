package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour la finalisation d'onboarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingCompletedResponse {

    @JsonProperty("session_id")
    private UUID sessionId;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("completed_at")
    private LocalDateTime completedAt;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
