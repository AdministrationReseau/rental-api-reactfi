package inc.yowyob.rental_api_reactive.infrastructure.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class TokenContext {
    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("userType")
    private String userType;

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @JsonProperty("isAgencyBound")
    private Boolean isAgencyBound;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("permissions")
    private java.util.List<String> permissions;
}
