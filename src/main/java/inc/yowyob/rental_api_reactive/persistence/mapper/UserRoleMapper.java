package inc.yowyob.rental_api_reactive.persistence.mapper;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UserRoleResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {

    public UserRoleResponse toResponse(UserRole userRole) {
        if (userRole == null) {
            return null;
        }

        return UserRoleResponse.builder()
            .id(userRole.getId())
            .userId(userRole.getUserId())
            .roleId(userRole.getRoleId())
            .organizationId(userRole.getOrganizationId())
            .agencyId(userRole.getAgencyId())
            .assignedAt(userRole.getAssignedAt())
            .expiresAt(userRole.getExpiresAt())
            .isActive(userRole.getIsActive())
            .isExpired(userRole.isExpired())
            .assignedBy(userRole.getAssignedBy())
            .revokedAt(userRole.getRevokedAt())
            .revokedBy(userRole.getRevokedBy())
            .build();
    }
}
