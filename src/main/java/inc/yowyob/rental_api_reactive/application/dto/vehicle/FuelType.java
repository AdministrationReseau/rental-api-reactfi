package inc.yowyob.rental_api_reactive.application.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Types de carburant
 */
@Getter
public enum FuelType {
    @JsonProperty("gasoline")
    GASOLINE("gasoline", "Essence"),

    @JsonProperty("diesel")
    DIESEL("diesel", "Diesel"),

    @JsonProperty("electric")
    ELECTRIC("electric", "Électrique"),

    @JsonProperty("hybrid")
    HYBRID("hybrid", "Hybride"),

    @JsonProperty("gas")
    GAS("gas", "Gaz");

    private final String code;
    private final String description;

    FuelType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
