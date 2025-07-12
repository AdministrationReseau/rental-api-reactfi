package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserType {
    @JsonProperty("SUPER_ADMIN")
    SUPER_ADMIN,

    @JsonProperty("ORGANIZATION_OWNER")
    ORGANIZATION_OWNER,

    @JsonProperty("AGENCY_MANAGER")
    AGENCY_MANAGER,

    @JsonProperty("RENTAL_AGENT")
    RENTAL_AGENT,

    @JsonProperty("DRIVER")
    DRIVER,

    @JsonProperty("CLIENT")
    CLIENT
}
