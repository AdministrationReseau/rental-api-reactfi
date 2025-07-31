package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.Permission;
import inc.yowyob.rental_api_reactive.persistence.entity.Role;
import inc.yowyob.rental_api_reactive.persistence.entity.UserRole;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.UserRoleReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionReactiveServiceTest {

    @Mock
    private UserRoleReactiveRepository userRoleRepository;

    @Mock
    private RoleReactiveRepository roleRepository;

    @InjectMocks
    private PermissionReactiveService permissionService;

    private UUID userId;
    private UUID roleId;
    private UUID organizationId;
    private UserRole userRole;
    private Role role;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        organizationId = UUID.randomUUID();

        userRole = new UserRole(userId, roleId, organizationId);
        userRole.setIsActive(true);

        role = new Role("Test Role", "Test description", organizationId);
        role.setId(roleId);
        role.setPermissions(Set.of(
            Permission.USER_READ.getCode(),
            Permission.USER_WRITE.getCode(),
            Permission.VEHICLE_READ.getCode()
        ));
    }

    @Test
    void getAllPermissions_ShouldReturnAllPermissions() {
        // When & Then
        StepVerifier.create(permissionService.getAllPermissions())
            .expectNextCount(Permission.values().length)
            .verifyComplete();
    }

    @Test
    void getPermissionsByResource_ShouldReturnResourcePermissions() {
        // When & Then
        StepVerifier.create(permissionService.getPermissionsByResource("USER"))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getResource()).isEqualTo("USER");
                assertThat(response.getPermissions()).isNotEmpty();
                assertThat(response.getTotalCount()).isGreaterThan(0);
            })
            .verifyComplete();
    }

    @Test
    void getUserPermissions_ShouldReturnUserPermissions() {
        // Given
        when(userRoleRepository.findActiveByUserId(userId))
            .thenReturn(Flux.just(userRole));
        when(roleRepository.findById(roleId))
            .thenReturn(Mono.just(role));

        // When & Then
        StepVerifier.create(permissionService.getUserPermissions(userId))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getUserId()).isEqualTo(userId);
                assertThat(response.getPermissions()).contains(
                    Permission.USER_READ.getCode(),
                    Permission.USER_WRITE.getCode(),
                    Permission.VEHICLE_READ.getCode()
                );
                assertThat(response.getTotalCount()).isEqualTo(3);
            })
            .verifyComplete();
    }

    @Test
    void hasPermission_ShouldReturnTrueForValidPermission() {
        // Given
        when(userRoleRepository.findActiveByUserId(userId))
            .thenReturn(Flux.just(userRole));
        when(roleRepository.findById(roleId))
            .thenReturn(Mono.just(role));

        // When & Then
        StepVerifier.create(permissionService.hasPermission(userId, Permission.USER_READ.getCode()))
            .assertNext(hasPermission -> {
                assertThat(hasPermission).isTrue();
            })
            .verifyComplete();
    }

    @Test
    void hasPermission_ShouldReturnFalseForInvalidPermission() {
        // Given
        when(userRoleRepository.findActiveByUserId(userId))
            .thenReturn(Flux.just(userRole));
        when(roleRepository.findById(roleId))
            .thenReturn(Mono.just(role));

        // When & Then
        StepVerifier.create(permissionService.hasPermission(userId, Permission.SYSTEM_ADMIN.getCode()))
            .assertNext(hasPermission -> {
                assertThat(hasPermission).isFalse();
            })
            .verifyComplete();
    }

    @Test
    void hasAllPermissions_ShouldReturnTrueWhenUserHasAllPermissions() {
        // Given
        Set<String> requestedPermissions = Set.of(
            Permission.USER_READ.getCode(),
            Permission.USER_WRITE.getCode()
        );

        when(userRoleRepository.findActiveByUserId(userId))
            .thenReturn(Flux.just(userRole));
        when(roleRepository.findById(roleId))
            .thenReturn(Mono.just(role));

        // When & Then
        StepVerifier.create(permissionService.hasAllPermissions(userId, requestedPermissions))
            .assertNext(hasAllPermissions -> {
                assertThat(hasAllPermissions).isTrue();
            })
            .verifyComplete();
    }

    @Test
    void hasAnyPermission_ShouldReturnTrueWhenUserHasAnyPermission() {
        // Given
        Set<String> requestedPermissions = Set.of(
            Permission.USER_READ.getCode(),
            Permission.SYSTEM_ADMIN.getCode()
        );

        when(userRoleRepository.findActiveByUserId(userId))
            .thenReturn(Flux.just(userRole));
        when(roleRepository.findById(roleId))
            .thenReturn(Mono.just(role));

        // When & Then
        StepVerifier.create(permissionService.hasAnyPermission(userId, requestedPermissions))
            .assertNext(hasAnyPermission -> {
                assertThat(hasAnyPermission).isTrue();
            })
            .verifyComplete();
    }

    @Test
    void compareUserPermissions_ShouldComparePermissionsCorrectly() {
        // Given
        UUID userId2 = UUID.randomUUID();
        UUID roleId2 = UUID.randomUUID();

        UserRole userRole2 = new UserRole(userId2, roleId2, organizationId);
        userRole2.setIsActive(true);

        Role role2 = new Role("Test Role 2", "Test description 2", organizationId);
        role2.setId(roleId2);
        role2.setPermissions(Set.of(
            Permission.USER_READ.getCode(),
            Permission.RENTAL_READ.getCode()
        ));

        when(userRoleRepository.findActiveByUserId(userId))
            .thenReturn(Flux.just(userRole));
        when(roleRepository.findById(roleId))
            .thenReturn(Mono.just(role));

        when(userRoleRepository.findActiveByUserId(userId2))
            .thenReturn(Flux.just(userRole2));
        when(roleRepository.findById(roleId2))
            .thenReturn(Mono.just(role2));

        // When & Then
        StepVerifier.create(permissionService.compareUserPermissions(userId, userId2))
            .assertNext(comparison -> {
                assertThat(comparison).isNotNull();
                assertThat(comparison.getUserId1()).isEqualTo(userId);
                assertThat(comparison.getUserId2()).isEqualTo(userId2);
                assertThat(comparison.getCommonPermissions()).contains(Permission.USER_READ.getCode());
                assertThat(comparison.getUser1OnlyPermissions()).contains(
                    Permission.USER_WRITE.getCode(),
                    Permission.VEHICLE_READ.getCode()
                );
                assertThat(comparison.getUser2OnlyPermissions()).contains(Permission.RENTAL_READ.getCode());
            })
            .verifyComplete();
    }
}
