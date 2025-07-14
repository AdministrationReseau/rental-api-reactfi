package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UserPermissionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheReactiveService {

    private final PermissionReactiveService permissionService;
    private final ConcurrentMap<UUID, CacheEntry> permissionCache = new ConcurrentHashMap<>();
    private static final Duration CACHE_DURATION = Duration.ofMinutes(15);

    /**
     * Récupère les permissions d'un utilisateur avec cache
     */
    public Mono<UserPermissionsResponse> getUserPermissions(UUID userId) {
        log.debug("Getting cached permissions for user: {}", userId);

        CacheEntry entry = permissionCache.get(userId);

        if (entry != null && !entry.isExpired()) {
            log.debug("Returning cached permissions for user: {}", userId);
            return Mono.just(entry.getPermissions());
        }

        return permissionService.getUserPermissions(userId)
            .doOnNext(permissions -> {
                CacheEntry newEntry = new CacheEntry(permissions, LocalDateTime.now().plus(CACHE_DURATION));
                permissionCache.put(userId, newEntry);
                log.debug("Cached permissions for user: {}", userId);
            });
    }

    /**
     * Invalide le cache des permissions d'un utilisateur
     */
    public void evictUserPermissions(UUID userId) {
        log.debug("Evicting permissions cache for user: {}", userId);
        permissionCache.remove(userId);
    }

    /**
     * Invalide tout le cache des permissions
     */
    public void evictAllPermissions() {
        log.debug("Evicting all permissions cache");
        permissionCache.clear();
    }

    /**
     * Vérifie si un utilisateur a une permission avec cache
     */
    public Mono<Boolean> hasPermission(UUID userId, String permissionCode) {
        return getUserPermissions(userId)
            .map(permissions -> permissions.getPermissions().contains(permissionCode));
    }

    /**
     * Nettoyage automatique des entrées expirées
     */
    public void cleanupExpiredEntries() {
        log.debug("Cleaning up expired permission cache entries");
        permissionCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private static class CacheEntry {
        private final UserPermissionsResponse permissions;
        private final LocalDateTime expiresAt;

        public CacheEntry(UserPermissionsResponse permissions, LocalDateTime expiresAt) {
            this.permissions = permissions;
            this.expiresAt = expiresAt;
        }

        public UserPermissionsResponse getPermissions() {
            return permissions;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}
