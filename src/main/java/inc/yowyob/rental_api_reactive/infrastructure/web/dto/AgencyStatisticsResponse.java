package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour les statistiques d'agence
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyStatisticsResponse {

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("total_vehicles")
    private Integer totalVehicles;

    @JsonProperty("active_vehicles")
    private Integer activeVehicles;

    @JsonProperty("total_drivers")
    private Integer totalDrivers;

    @JsonProperty("active_drivers")
    private Integer activeDrivers;

    @JsonProperty("total_personnel")
    private Integer totalPersonnel;

    @JsonProperty("total_rentals")
    private Integer totalRentals;

    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue;

    @JsonProperty("vehicle_utilization_rate")
    private Double vehicleUtilizationRate;

    @JsonProperty("driver_activity_rate")
    private Double driverActivityRate;

    @JsonProperty("last_updated")
    private LocalDateTime lastUpdated;
}
