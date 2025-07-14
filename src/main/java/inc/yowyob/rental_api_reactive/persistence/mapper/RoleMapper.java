package inc.yowyob.rental_api_reactive.persistence.mapper;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.RoleResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponse.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .organizationId(role.getOrganizationId())
            .roleType(role.getRoleType())
            .isSystemRole(role.getIsSystemRole())
            .isDefaultRole(role.getIsDefaultRole())
            .priority(role.getPriority())
            .permissions(role.getPermissions())
            .permissionCount(role.getPermissions() != null ? role.getPermissions().size() : 0)
            .color(role.getColor())
            .icon(role.getIcon())
            .canBeModified(role.canBeModified())
            .canBeDeleted(role.canBeDeleted())
            .createdAt(role.getCreatedAt())
            .updatedAt(role.getUpdatedAt())
            .createdBy(role.getCreatedBy())
            .updatedBy(role.getUpdatedBy())
            .build();
    }
}
