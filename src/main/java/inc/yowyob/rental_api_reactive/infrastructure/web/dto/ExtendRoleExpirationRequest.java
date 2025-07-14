package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtendRoleExpirationRequest {

    @NotNull(message = "New expiration date is required")
    @Future(message = "Expiration date must be in the future")
    @JsonProperty("new_expiration_date")
    private LocalDateTime newExpirationDate;
}
