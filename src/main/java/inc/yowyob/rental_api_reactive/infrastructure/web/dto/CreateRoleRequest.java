package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
    @JsonProperty("name")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Organization ID is required")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("role_type")
    private RoleType roleType;

    @Min(value = 0, message = "Priority must be positive")
    @Max(value = 100, message = "Priority must not exceed 100")
    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("permissions")
    private Set<String> permissions;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex color")
    @JsonProperty("color")
    private String color;

    @Size(max = 50, message = "Icon name must not exceed 50 characters")
    @JsonProperty("icon")
    private String icon;
}
