package inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import jakarta.validation.constraints.*;

/**
 * DTO pour créer une marque de véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleBrandRequest {

    @NotBlank(message = "Le nom de la marque est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @JsonProperty("name")
    private String name;

    @JsonProperty("logoUrl")
    private String logoUrl;

    @JsonProperty("countryOrigin")
    private String countryOrigin;
}
