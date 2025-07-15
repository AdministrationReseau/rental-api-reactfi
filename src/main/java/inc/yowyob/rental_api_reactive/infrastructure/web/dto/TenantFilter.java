package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Filtre multi-tenant pour les requêtes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantFilter {
    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("user_type")
    private UserType userType;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("is_global_access")
    private Boolean isGlobalAccess = false;

    @JsonProperty("is_agency_restricted")
    private Boolean isAgencyRestricted = false;
}
