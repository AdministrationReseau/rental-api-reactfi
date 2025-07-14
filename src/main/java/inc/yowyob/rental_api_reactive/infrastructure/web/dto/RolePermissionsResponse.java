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
public class RolePermissionsResponse {

    @JsonProperty("role_id")
    private UUID roleId;

    @JsonProperty("role_name")
    private String roleName;

    @JsonProperty("permissions")
    private Set<String> permissions;

    @JsonProperty("permission_details")
    private Set<PermissionResponse> permissionDetails;

    @JsonProperty("total_count")
    private Integer totalCount;
}
