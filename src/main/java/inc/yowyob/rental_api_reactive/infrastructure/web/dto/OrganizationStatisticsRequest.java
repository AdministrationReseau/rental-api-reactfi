package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO pour la mise à jour des statistiques d'organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationStatisticsRequest {

    @JsonProperty("resource_counters")
    private ResourceCounters resourceCounters;

    @JsonProperty("financial_stats")
    private FinancialStatistics financialStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResourceCounters {
        @JsonProperty("agencies")
        private Integer agencies;

        @JsonProperty("vehicles")
        private Integer vehicles;

        @JsonProperty("drivers")
        private Integer drivers;

        @JsonProperty("users")
        private Integer users;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FinancialStatistics {
        @JsonProperty("monthly_revenue")
        private Double monthlyRevenue;

        @JsonProperty("yearly_revenue")
        private Double yearlyRevenue;

        @JsonProperty("total_rentals")
        private Integer totalRentals;
    }
}
