package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.util.ImageType;
import lombok.*;

/**
 * DTO pour modifier les métadonnées d'une image de véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVehicleImageRequest {

    @JsonProperty("imageType")
    private ImageType imageType;

    @JsonProperty("isPrimary")
    private Boolean isPrimary;

    @JsonProperty("displayOrder")
    private Integer displayOrder;

    @JsonProperty("isActive")
    private Boolean isActive;
}
