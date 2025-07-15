package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour la mise à jour d'abonnement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubscriptionRequest {

    @NotNull(message = "Subscription plan ID is required")
    @JsonProperty("subscription_plan_id")
    private UUID subscriptionPlanId;

    @NotNull(message = "Expiration date is required")
    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    @JsonProperty("auto_renew")
    private Boolean autoRenew = true;
}
