package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @JsonProperty("refreshToken")
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
