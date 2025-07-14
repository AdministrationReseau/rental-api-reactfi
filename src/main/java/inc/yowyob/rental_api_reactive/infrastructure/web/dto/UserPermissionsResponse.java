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
public class UserPermissionsResponse {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("permissions")
    private Set<String> permissions;

    @JsonProperty("permission_details")
    private Set<PermissionResponse> permissionDetails;

    @JsonProperty("total_count")
    private Integer totalCount;
}
