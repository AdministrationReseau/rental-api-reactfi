package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Politiques d'organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationPolicies {

    @JsonProperty("rental_policy")
    private RentalPolicy rentalPolicy;

    @JsonProperty("cancellation_policy")
    private CancellationPolicy cancellationPolicy;

    @JsonProperty("privacy_policy")
    private PrivacyPolicy privacyPolicy;

    @JsonProperty("terms_of_service")
    private TermsOfService termsOfService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RentalPolicy {
        @JsonProperty("min_rental_duration_hours")
        private Integer minRentalDurationHours = 1;

        @JsonProperty("max_rental_duration_days")
        private Integer maxRentalDurationDays = 30;

        @JsonProperty("require_deposit")
        private Boolean requireDeposit = true;

        @JsonProperty("default_deposit_percentage")
        private Double defaultDepositPercentage = 30.0;

        @JsonProperty("allow_one_way_rentals")
        private Boolean allowOneWayRentals = false;

        @JsonProperty("fuel_policy")
        private String fuelPolicy = "FULL_TO_FULL";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CancellationPolicy {
        @JsonProperty("free_cancellation_hours")
        private Integer freeCancellationHours = 24;

        @JsonProperty("cancellation_fee_percentage")
        private Double cancellationFeePercentage = 0.0;

        @JsonProperty("no_show_penalty_percentage")
        private Double noShowPenaltyPercentage = 100.0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrivacyPolicy {
        @JsonProperty("data_retention_days")
        private Integer dataRetentionDays = 2555; // 7 ans

        @JsonProperty("allow_marketing_emails")
        private Boolean allowMarketingEmails = true;

        @JsonProperty("share_data_with_partners")
        private Boolean shareDataWithPartners = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TermsOfService {
        @JsonProperty("version")
        private String version = "1.0";

        @JsonProperty("last_updated")
        private LocalDateTime lastUpdated;

        @JsonProperty("acceptance_required")
        private Boolean acceptanceRequired = true;
    }
}
