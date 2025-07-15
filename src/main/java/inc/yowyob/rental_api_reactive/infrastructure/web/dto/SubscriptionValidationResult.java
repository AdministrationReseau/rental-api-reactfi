package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.UUID;

/**
 * Résultat de validation d'abonnement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionValidationResult {
    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("subscription_active")
    private Boolean subscriptionActive;

    @JsonProperty("can_create_agency")
    private Boolean canCreateAgency;

    @JsonProperty("can_add_vehicle")
    private Boolean canAddVehicle;

    @JsonProperty("can_add_driver")
    private Boolean canAddDriver;

    @JsonProperty("can_add_user")
    private Boolean canAddUser;

    @JsonProperty("agency_usage_percentage")
    private Double agencyUsagePercentage;

    @JsonProperty("vehicle_usage_percentage")
    private Double vehicleUsagePercentage;

    @JsonProperty("driver_usage_percentage")
    private Double driverUsagePercentage;

    @JsonProperty("user_usage_percentage")
    private Double userUsagePercentage;

    @JsonProperty("has_limit_warnings")
    private Boolean hasLimitWarnings;

    @JsonProperty("subscription_expiring_soon")
    private Boolean subscriptionExpiringSoon;
}
