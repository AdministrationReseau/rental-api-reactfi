package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO pour la mise à jour des statistiques d'agence
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyStatisticsRequest {

    @JsonProperty("vehicle_stats")
    private VehicleStatistics vehicleStats;

    @JsonProperty("driver_stats")
    private DriverStatistics driverStats;

    @JsonProperty("personnel_count")
    private Integer personnelCount;

    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue;

    @JsonProperty("total_rentals_increment")
    private Integer totalRentalsIncrement;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehicleStatistics {
        @JsonProperty("total")
        private Integer total;

        @JsonProperty("active")
        private Integer active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DriverStatistics {
        @JsonProperty("total")
        private Integer total;

        @JsonProperty("active")
        private Integer active;
    }
}
