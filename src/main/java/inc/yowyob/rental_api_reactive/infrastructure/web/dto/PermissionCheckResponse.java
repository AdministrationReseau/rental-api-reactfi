package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCheckResponse {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("permission")
    private String permission;

    @JsonProperty("has_permission")
    private Boolean hasPermission;
}
