package inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.vehicle.VehicleStatus;
import lombok.*;

import jakarta.validation.constraints.*;

/**
 * DTO pour changer le statut d'un véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVehicleStatusRequest {

    @NotNull(message = "Le nouveau statut est obligatoire")
    @JsonProperty("status")
    private VehicleStatus status;

    @JsonProperty("notes")
    private String notes;
}
