package inc.yowyob.rental_api_reactive.application.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Types de véhicules
 */
@Getter
public enum VehicleType {
    @JsonProperty("car")
    CAR("car", "Voiture"),

    @JsonProperty("motorcycle")
    MOTORCYCLE("motorcycle", "Moto"),

    @JsonProperty("truck")
    TRUCK("truck", "Camion"),

    @JsonProperty("van")
    VAN("van", "Fourgonnette"),

    @JsonProperty("bus")
    BUS("bus", "Bus"),

    @JsonProperty("bicycle")
    BICYCLE("bicycle", "Vélo");

    private final String code;
    private final String description;

    VehicleType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
