package inc.yowyob.rental_api_reactive.application.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
public enum BookingStatus {
    @JsonProperty("PENDING")
    PENDING,
    @JsonProperty("CONFIRMED")
    CONFIRMED,
    @JsonProperty("CANCELLED")
    CANCELLED,
    @JsonProperty("COMPLETED")
    COMPLETED,
    @JsonProperty("IN_PROGRESS")
    IN_PROGRESS
}