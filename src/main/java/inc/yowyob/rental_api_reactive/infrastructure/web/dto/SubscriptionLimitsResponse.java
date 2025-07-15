package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Réponse pour les limites d'abonnement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionLimitsResponse {
    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("subscription_active")
    private Boolean subscriptionActive;

    @JsonProperty("subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    @JsonProperty("agency_limits")
    private ResourceLimitInfo agencyLimits;

    @JsonProperty("vehicle_limits")
    private ResourceLimitInfo vehicleLimits;

    @JsonProperty("driver_limits")
    private ResourceLimitInfo driverLimits;

    @JsonProperty("user_limits")
    private ResourceLimitInfo userLimits;
}
