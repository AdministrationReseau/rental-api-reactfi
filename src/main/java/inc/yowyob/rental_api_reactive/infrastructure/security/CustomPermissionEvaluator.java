package inc.yowyob.rental_api_reactive.infrastructure.security;

import inc.yowyob.rental_api_reactive.application.service.AuthorizationReactiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final AuthorizationReactiveService authorizationService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }

        UUID userId = getUserId(authentication);
        if (userId == null) {
            return false;
        }

        String permissionStr = permission.toString();
        String[] parts = permissionStr.split("_");

        if (parts.length >= 2) {
            String resource = parts[0];
            String action = parts[1];

            return authorizationService.hasPermission(userId, resource, action).block();
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, null, permission);
    }

    private UUID getUserId(Authentication authentication) {
        try {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                return ((UserPrincipal) principal).getId();
            }
            return UUID.fromString(principal.toString());
        } catch (Exception e) {
            log.warn("Could not extract user ID from authentication", e);
            return null;
        }
    }
}
