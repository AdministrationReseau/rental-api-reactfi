package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.Data;

import java.util.UUID;

@Data
public class AgencyRedirectInfo {
    @JsonProperty("personnelId")
    private UUID personnelId;

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @JsonProperty("userType")
    private UserType userType;

    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("requiresPasswordChange")
    private Boolean requiresPasswordChange;

    @JsonProperty("redirectUrl")
    private String redirectUrl; // URL de redirection vers l'agence

    @JsonProperty("agencyName")
    private String agencyName; // Nom de l'agence (à récupérer)

    @JsonProperty("permissions")
    private java.util.List<String> permissions; // Permissions spécifiques au rôle
}
