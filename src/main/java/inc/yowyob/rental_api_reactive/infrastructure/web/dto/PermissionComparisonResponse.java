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
public class PermissionComparisonResponse {

    @JsonProperty("user_id_1")
    private UUID userId1;

    @JsonProperty("user_id_2")
    private UUID userId2;

    @JsonProperty("common_permissions")
    private Set<String> commonPermissions;

    @JsonProperty("user_1_only_permissions")
    private Set<String> user1OnlyPermissions;

    @JsonProperty("user_2_only_permissions")
    private Set<String> user2OnlyPermissions;

    @JsonProperty("common_count")
    private Integer commonCount;

    @JsonProperty("user_1_only_count")
    private Integer user1OnlyCount;

    @JsonProperty("user_2_only_count")
    private Integer user2OnlyCount;
}
