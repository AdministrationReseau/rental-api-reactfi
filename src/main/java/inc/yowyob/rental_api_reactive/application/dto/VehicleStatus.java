package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * États des véhicules
 */
@Getter
public enum VehicleStatus {
    @JsonProperty("available")
    AVAILABLE("available", "Disponible pour location"),

    @JsonProperty("rented")
    RENTED("rented", "Actuellement loué"),

    @JsonProperty("maintenance")
    MAINTENANCE("maintenance", "En maintenance"),

    @JsonProperty("out_of_service")
    OUT_OF_SERVICE("out_of_service", "Hors service"),

    @JsonProperty("in_transit")
    IN_TRANSIT("in_transit", "En transit entre agences");

    private final String code;
    private final String description;

    VehicleStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
