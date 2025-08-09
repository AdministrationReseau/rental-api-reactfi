package inc.yowyob.rental_api_reactive.application.dto.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Types d'images
 */
@Getter
public enum ImageType {
    @JsonProperty("main")
    MAIN("main", "Image principale"),

    @JsonProperty("exterior")
    EXTERIOR("exterior", "Vue extérieure"),

    @JsonProperty("interior")
    INTERIOR("interior", "Vue intérieure"),

    @JsonProperty("engine")
    ENGINE("engine", "Moteur"),

    @JsonProperty("document")
    DOCUMENT("document", "Document officiel");

    private final String code;
    private final String description;

    ImageType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
