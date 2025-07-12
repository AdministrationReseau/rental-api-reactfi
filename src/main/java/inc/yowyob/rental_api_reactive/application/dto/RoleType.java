package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum RoleType {
    @JsonProperty("SYSTEM")
    SYSTEM(true, "Rôle système non modifiable"),

    @JsonProperty("PREDEFINED")
    PREDEFINED(false, "Rôle prédéfini modifiable"),

    @JsonProperty("CUSTOM")
    CUSTOM(false, "Rôle personnalisé");

    @JsonProperty("is_system_role")
    private final boolean isSystemRole;

    @JsonProperty("description")
    private final String description;

    RoleType(boolean isSystemRole, String description) {
        this.isSystemRole = isSystemRole;
        this.description = description;
    }
}
