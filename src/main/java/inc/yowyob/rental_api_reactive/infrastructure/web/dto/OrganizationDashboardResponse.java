package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.UUID;

/**
 * DTO pour le tableau de bord d'organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDashboardResponse {

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("total_agencies")
    private Integer totalAgencies;

    @JsonProperty("active_agencies")
    private Integer activeAgencies;

    @JsonProperty("total_vehicles")
    private Integer totalVehicles;

    @JsonProperty("total_drivers")
    private Integer totalDrivers;

    @JsonProperty("total_users")
    private Integer totalUsers;

    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue;

    @JsonProperty("total_rentals")
    private Integer totalRentals;

    @JsonProperty("agency_utilization")
    private Double agencyUtilization;

    @JsonProperty("vehicle_utilization")
    private Double vehicleUtilization;

    @JsonProperty("subscription_status")
    private String subscriptionStatus;

    @JsonProperty("alerts_count")
    private Integer alertsCount;
}
