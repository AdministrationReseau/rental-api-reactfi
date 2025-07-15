package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour les statistiques d'organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationStatisticsResponse {

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("total_agencies")
    private Integer totalAgencies;

    @JsonProperty("active_agencies")
    private Integer activeAgencies;

    @JsonProperty("max_agencies")
    private Integer maxAgencies;

    @JsonProperty("total_vehicles")
    private Integer totalVehicles;

    @JsonProperty("max_vehicles")
    private Integer maxVehicles;

    @JsonProperty("total_drivers")
    private Integer totalDrivers;

    @JsonProperty("max_drivers")
    private Integer maxDrivers;

    @JsonProperty("total_users")
    private Integer totalUsers;

    @JsonProperty("max_users")
    private Integer maxUsers;

    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue;

    @JsonProperty("yearly_revenue")
    private Double yearlyRevenue;

    @JsonProperty("total_rentals")
    private Integer totalRentals;

    @JsonProperty("agency_usage_percentage")
    private Double agencyUsagePercentage;

    @JsonProperty("vehicle_usage_percentage")
    private Double vehicleUsagePercentage;

    @JsonProperty("driver_usage_percentage")
    private Double driverUsagePercentage;

    @JsonProperty("user_usage_percentage")
    private Double userUsagePercentage;

    @JsonProperty("is_subscription_active")
    private Boolean isSubscriptionActive;

    @JsonProperty("subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    @JsonProperty("last_activity_at")
    private LocalDateTime lastActivityAt;
}
