package inc.yowyob.rental_api_reactive.application.dto.rental;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RentalPolicyDTO {
    @JsonProperty("cancellation_fee_percentage")
    private BigDecimal cancellationFeePercentage;

    @JsonProperty("late_return_fee_percentage")
    private BigDecimal lateReturnFeePercentage;
}