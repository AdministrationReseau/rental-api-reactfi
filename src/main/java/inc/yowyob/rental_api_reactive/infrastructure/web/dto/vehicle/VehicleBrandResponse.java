package inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour une marque de véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBrandResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("logoUrl")
    private String logoUrl;

    @JsonProperty("countryOrigin")
    private String countryOrigin;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
}
