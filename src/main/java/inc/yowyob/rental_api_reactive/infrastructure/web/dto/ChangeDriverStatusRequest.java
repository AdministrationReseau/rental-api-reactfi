package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.DriverStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDriverStatusRequest {
    
   @NotNull(message = "Status is required")
    @JsonProperty("status")
    private DriverStatus status;
    
    @JsonProperty("reason")
    private String reason; // Raison du changement de statut (optionnel)
}