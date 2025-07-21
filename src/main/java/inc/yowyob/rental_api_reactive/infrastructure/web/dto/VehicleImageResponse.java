package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.ImageType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour une image de véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleImageResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("vehicleId")
    private UUID vehicleId;

    @JsonProperty("imageUrl")
    private String imageUrl; // URL déchiffrée pour l'affichage

    @JsonProperty("imageType")
    private ImageType imageType;

    @JsonProperty("fileSize")
    private Long fileSize;

    @JsonProperty("mimeType")
    private String mimeType;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("isPrimary")
    private Boolean isPrimary;

    @JsonProperty("displayOrder")
    private Integer displayOrder;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
