package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloneRoleRequest {

    @NotBlank(message = "New role name is required")
    @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
    @JsonProperty("new_name")
    private String newName;
}
