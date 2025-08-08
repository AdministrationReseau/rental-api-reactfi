package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.service.UserRoleReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.AssignRoleRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UserRoleResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.Role;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.entity.UserRole;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.UserRoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.mapper.UserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleReactiveServiceTest {

    @Mock
    private UserRoleReactiveRepository userRoleRepository;

    @Mock
    private RoleReactiveRepository roleRepository;

    @Mock
    private UserReactiveRepository userRepository;

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserRoleReactiveService userRoleService;

    private UUID userId;
    private UUID roleId;
    private UUID organizationId;
    private UUID assignedBy;
    private AssignRoleRequest assignRequest;
    private User user;
    private Role role;
    private UserRole userRole;
    private UserRoleResponse userRoleResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        organizationId = UUID.randomUUID();
        assignedBy = UUID.randomUUID();

        assignRequest = AssignRoleRequest.builder()
            .userId(userId)
            .roleId(roleId)
            .organizationId(organizationId)
            .expiresAt(LocalDateTime.now().plusMonths(6))
            .build();

        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        role = new Role("Test Role", "Test description", organizationId);
        role.setId(roleId);

        userRole = new UserRole(userId, roleId, organizationId);
        userRole.setId(UUID.randomUUID());
        userRole.setAssignedBy(assignedBy);

        userRoleResponse = UserRoleResponse.builder()
            .id(userRole.getId())
            .userId(userId)
            .roleId(roleId)
            .organizationId(organizationId)
            .isActive(true)
            .build();
    }

    @Test
    void assignRole_ShouldAssignRoleSuccessfully() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(roleRepository.findById(roleId)).thenReturn(Mono.just(role));
        when(userRoleRepository.findByUserIdAndRoleId(userId, roleId))
            .thenReturn(Mono.empty());
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(Mono.just(userRole));
        when(userRoleMapper.toResponse(userRole)).thenReturn(userRoleResponse);

        // When & Then
        StepVerifier.create(userRoleService.assignRole(assignRequest, assignedBy))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getUserId()).isEqualTo(userId);
                assertThat(response.getRoleId()).isEqualTo(roleId);
                assertThat(response.getOrganizationId()).isEqualTo(organizationId);
            })
            .verifyComplete();
    }

    @Test
    void assignRole_ShouldFailWhenUserNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userRoleService.assignRole(assignRequest, assignedBy))
            .expectErrorMatches(throwable ->
                throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("User not found"))
            .verify();
    }

    @Test
    void assignRole_ShouldFailWhenRoleNotFound() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(roleRepository.findById(roleId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userRoleService.assignRole(assignRequest, assignedBy))
            .expectErrorMatches(throwable ->
                throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Role not found"))
            .verify();
    }

    @Test
    void assignRole_ShouldFailWhenRoleAlreadyAssigned() {
        // Given
        userRole.setIsActive(true);
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(roleRepository.findById(roleId)).thenReturn(Mono.just(role));
        when(userRoleRepository.findByUserIdAndRoleId(userId, roleId))
            .thenReturn(Mono.just(userRole));

        // When & Then
        StepVerifier.create(userRoleService.assignRole(assignRequest, assignedBy))
            .expectErrorMatches(throwable ->
                throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("User already has this role"))
            .verify();
    }

    @Test
    void revokeRole_ShouldRevokeRoleSuccessfully() {
        // Given
        when(userRoleRepository.findByUserIdAndRoleId(userId, roleId))
            .thenReturn(Mono.just(userRole));
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(Mono.just(userRole));

        // When & Then
        StepVerifier.create(userRoleService.revokeRole(userId, roleId, assignedBy))
            .verifyComplete();
    }

    @Test
    void getUserRoles_ShouldReturnUserRoles() {
        // Given
        when(userRoleRepository.findActiveByUserId(userId))
            .thenReturn(Flux.just(userRole));
        when(userRoleMapper.toResponse(userRole)).thenReturn(userRoleResponse);

        // When & Then
        StepVerifier.create(userRoleService.getUserRoles(userId))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getUserId()).isEqualTo(userId);
            })
            .verifyComplete();
    }

    @Test
    void activateRole_ShouldActivateRoleSuccessfully() {
        // Given
        userRole.setIsActive(false);
        when(userRoleRepository.findByUserIdAndRoleId(userId, roleId))
            .thenReturn(Mono.just(userRole));
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(Mono.just(userRole));
        when(userRoleMapper.toResponse(any(UserRole.class))).thenReturn(userRoleResponse);

        // When & Then
        StepVerifier.create(userRoleService.activateRole(userId, roleId))
            .assertNext(response -> {
                assertThat(response).isNotNull();
            })
            .verifyComplete();
    }
}
