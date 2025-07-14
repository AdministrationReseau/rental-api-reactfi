package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePermissionsResponse {

    @JsonProperty("resource")
    private String resource;

    @JsonProperty("permissions")
    private Set<PermissionResponse> permissions;

    @JsonProperty("total_count")
    private Integer totalCount;
}
