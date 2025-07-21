package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Types de transmission
 */
@Getter
public enum TransmissionType {
    @JsonProperty("manual")
    MANUAL("manual", "Manuelle"),

    @JsonProperty("automatic")
    AUTOMATIC("automatic", "Automatique"),

    @JsonProperty("semi_automatic")
    SEMI_AUTOMATIC("semi_automatic", "Semi-automatique");

    private final String code;
    private final String description;

    TransmissionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
