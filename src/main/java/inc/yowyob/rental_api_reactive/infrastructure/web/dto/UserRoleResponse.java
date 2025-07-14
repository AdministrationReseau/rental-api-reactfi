package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("role_id")
    private UUID roleId;

    @JsonProperty("role_name")
    private String roleName;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("assigned_at")
    private LocalDateTime assignedAt;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_expired")
    private Boolean isExpired;

    @JsonProperty("assigned_by")
    private UUID assignedBy;

    @JsonProperty("revoked_at")
    private LocalDateTime revokedAt;

    @JsonProperty("revoked_by")
    private UUID revokedBy;
}
