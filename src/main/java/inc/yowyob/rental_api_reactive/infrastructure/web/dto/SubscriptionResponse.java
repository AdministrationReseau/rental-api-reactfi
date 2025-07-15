package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Réponse abonnement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("subscription_plan_id")
    private UUID subscriptionPlanId;

    @JsonProperty("status")
    private inc.yowyob.rental_api_reactive.application.dto.SubscriptionStatus status;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("amount")
    private java.math.BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("auto_renew")
    private Boolean autoRenew;

    @JsonProperty("is_active")
    private Boolean isActive;
}
