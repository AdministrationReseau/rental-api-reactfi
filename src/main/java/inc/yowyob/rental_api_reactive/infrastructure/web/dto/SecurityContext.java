package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Contexte de sécurité pour l'utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityContext {
    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("user_type")
    private UserType userType;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_email_verified")
    private Boolean isEmailVerified;
    @JsonProperty("permissions")
    private Set<String> permissions;

    @JsonProperty("roles")
    private Set<String> roles;

    @JsonProperty("organizations")
    private Set<UUID> organizations;

    @JsonProperty("is_super_admin")
    private Boolean isSuperAdmin;
}
