package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Paramètres business d'une agence
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyBusinessSettings {

    @JsonProperty("allow_online_booking")
    private Boolean allowOnlineBooking = true;

    @JsonProperty("require_deposit")
    private Boolean requireDeposit = true;

    @JsonProperty("deposit_percentage")
    @DecimalMin(value = "0.0", message = "Deposit percentage must be positive")
    @DecimalMax(value = "100.0", message = "Deposit percentage cannot exceed 100%")
    private Double depositPercentage = 30.0;

    @JsonProperty("min_rental_hours")
    @Min(value = 1, message = "Minimum rental hours must be at least 1")
    private Integer minRentalHours = 1;

    @JsonProperty("max_advance_booking_days")
    @Min(value = 1, message = "Maximum advance booking days must be at least 1")
    private Integer maxAdvanceBookingDays = 30;

    @JsonProperty("auto_confirm_booking")
    private Boolean autoConfirmBooking = false;

    @JsonProperty("require_driver_license")
    private Boolean requireDriverLicense = true;

    @JsonProperty("min_age_requirement")
    @Min(value = 18, message = "Minimum age must be at least 18")
    private Integer minAgeRequirement = 21;

    @JsonProperty("allow_international_license")
    private Boolean allowInternationalLicense = true;

    @JsonProperty("require_credit_card")
    private Boolean requireCreditCard = false;

    @JsonProperty("cancellation_deadline_hours")
    @Min(value = 1, message = "Cancellation deadline must be at least 1 hour")
    private Integer cancellationDeadlineHours = 24;

    @JsonProperty("late_return_penalty_rate")
    @DecimalMin(value = "0.0", message = "Penalty rate must be positive")
    private Double lateReturnPenaltyRate = 0.1; // 10% par heure de retard
}
