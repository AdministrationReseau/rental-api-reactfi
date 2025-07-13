package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeactivateAccountRequest {
    @JsonProperty("reason")
    @NotBlank(message = "Reason for deactivation is required")
    private String reason;

    @JsonProperty("feedback")
    private String feedback;
}
