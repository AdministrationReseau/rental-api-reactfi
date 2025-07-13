package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdatePersonnelRequest {
    @JsonProperty("firstName")
    @Size(min = 2, max = 50, message = "Prénom doit être entre 2 et 50 caractères")
    private String firstName;

    @JsonProperty("lastName")
    @Size(min = 2, max = 50, message = "Nom doit être entre 2 et 50 caractères")
    private String lastName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("supervisorId")
    private UUID supervisorId;
}
