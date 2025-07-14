package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityContext {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("permissions")
    private Set<String> permissions;

    @JsonProperty("roles")
    private Set<String> roles;

    @JsonProperty("organizations")
    private Set<UUID> organizations;

    @JsonProperty("is_super_admin")
    private Boolean isSuperAdmin;
}
