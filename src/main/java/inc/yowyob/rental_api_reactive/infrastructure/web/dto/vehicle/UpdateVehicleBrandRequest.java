package inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import jakarta.validation.constraints.*;

/**
 * DTO pour modifier une marque de véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVehicleBrandRequest {

    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @JsonProperty("name")
    private String name;

    @JsonProperty("logoUrl")
    private String logoUrl;

    @JsonProperty("countryOrigin")
    private String countryOrigin;

    @JsonProperty("isActive")
    private Boolean isActive;
}
