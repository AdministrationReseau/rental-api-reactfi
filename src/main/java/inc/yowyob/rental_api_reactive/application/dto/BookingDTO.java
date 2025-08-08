package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import inc.yowyob.rental_api_reactive.application.dto.Money;

@Data
public class BookingDTO {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("vehicle_id")
    private UUID vehicleId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @JsonProperty("with_driver")
    private boolean withDriver;

    @JsonProperty("total_price")
    private Money totalPrice;

    @JsonProperty("status")
    private String status; // Enum à définir (PENDING, CONFIRMED, CANCELLED, etc.)

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}