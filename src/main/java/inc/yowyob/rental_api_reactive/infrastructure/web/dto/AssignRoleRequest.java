package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleRequest {

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private UUID userId;

    @NotNull(message = "Role ID is required")
    @JsonProperty("role_id")
    private UUID roleId;

    @NotNull(message = "Organization ID is required")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;
}
