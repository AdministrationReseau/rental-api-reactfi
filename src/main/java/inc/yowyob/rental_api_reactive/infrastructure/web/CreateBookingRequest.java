package inc.yowyob.rental_api_reactive.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.util.Money;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateBookingRequest {
    @NotNull(message = "Vehicle ID is required")
    @JsonProperty("vehicle_id")
    private UUID vehicleId;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("with_driver")
    private boolean withDriver;

    @JsonProperty("total_price")
    private Money totalPrice;
}