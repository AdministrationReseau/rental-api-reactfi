package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;
import java.util.UUID;

/**
 * Validation de changement d'abonnement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionChangeValidation {
    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("new_plan_id")
    private UUID newPlanId;

    @JsonProperty("is_upgrade")
    private Boolean isUpgrade;

    @JsonProperty("can_change")
    private Boolean canChange;

    @JsonProperty("agency_limit_sufficient")
    private Boolean agencyLimitSufficient;

    @JsonProperty("vehicle_limit_sufficient")
    private Boolean vehicleLimitSufficient;

    @JsonProperty("driver_limit_sufficient")
    private Boolean driverLimitSufficient;

    @JsonProperty("user_limit_sufficient")
    private Boolean userLimitSufficient;

    @JsonProperty("blocking_reasons")
    private List<String> blockingReasons;
}
