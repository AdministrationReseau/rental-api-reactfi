package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePersonnelRequest {
    @JsonProperty("email")
    @NotBlank(message = "Email est requis")
    @Email(message = "Format email invalide")
    private String email;

    @JsonProperty("firstName")
    @NotBlank(message = "Prénom est requis")
    @Size(min = 2, max = 50, message = "Prénom doit être entre 2 et 50 caractères")
    private String firstName;

    @JsonProperty("lastName")
    @NotBlank(message = "Nom est requis")
    @Size(min = 2, max = 50, message = "Nom doit être entre 2 et 50 caractères")
    private String lastName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("userType")
    @NotNull(message = "Type d'utilisateur est requis")
    private UserType userType; // AGENCY_MANAGER, RENTAL_AGENT, DRIVER

    @JsonProperty("organizationId")
    @NotNull(message = "ID organisation est requis")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId; // Optionnel, peut être assigné plus tard

    @JsonProperty("temporaryPassword")
    @NotBlank(message = "Mot de passe temporaire est requis")
    @Size(min = 8, message = "Mot de passe doit faire au moins 8 caractères")
    private String temporaryPassword;

    // Informations employé
    @JsonProperty("employeeId")
    private String employeeId; // ID employé interne à l'organisation

    @JsonProperty("department")
    private String department; // Service/Département

    @JsonProperty("position")
    private String position; // Poste/Fonction

    @JsonProperty("supervisorId")
    private UUID supervisorId; // Superviseur direct
}
