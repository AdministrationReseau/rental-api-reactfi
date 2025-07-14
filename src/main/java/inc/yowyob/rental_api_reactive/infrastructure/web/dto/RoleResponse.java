package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("role_type")
    private RoleType roleType;

    @JsonProperty("is_system_role")
    private Boolean isSystemRole;

    @JsonProperty("is_default_role")
    private Boolean isDefaultRole;

    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("permissions")
    private Set<String> permissions;

    @JsonProperty("permission_count")
    private Integer permissionCount;

    @JsonProperty("user_count")
    private Integer userCount;

    @JsonProperty("color")
    private String color;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("can_be_modified")
    private Boolean canBeModified;

    @JsonProperty("can_be_deleted")
    private Boolean canBeDeleted;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private UUID createdBy;

    @JsonProperty("updated_by")
    private UUID updatedBy;
}
