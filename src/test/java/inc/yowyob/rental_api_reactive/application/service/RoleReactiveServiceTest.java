package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.Permission;
import inc.yowyob.rental_api_reactive.application.dto.RoleType;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.CreateRoleRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.RoleResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UpdateRoleRequest;
import inc.yowyob.rental_api_reactive.persistence.entity.Role;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.UserRoleReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.mapper.RoleMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleReactiveServiceTest {

    @Mock
    private RoleReactiveRepository roleRepository;

    @Mock
    private UserRoleReactiveRepository userRoleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleReactiveService roleService;

    private UUID organizationId;
    private UUID createdBy;
    private CreateRoleRequest createRequest;
    private Role role;
    private RoleResponse roleResponse;

    @BeforeEach
    void setUp() {
        organizationId = UUID.randomUUID();
        createdBy = UUID.randomUUID();

        createRequest = CreateRoleRequest.builder()
            .name("Test Role")
            .description("Test role description")
            .organizationId(organizationId)
            .roleType(RoleType.CUSTOM)
            .priority(50)
            .permissions(Set.of(Permission.USER_READ.getCode(), Permission.USER_WRITE.getCode()))
            .color("#3b82f6")
            .icon("shield")
            .build();

        role = new Role("Test Role", "Test role description", organizationId);
        role.setId(UUID.randomUUID());
        role.setRoleType(RoleType.CUSTOM);
        role.setPriority(50);
        role.setPermissions(Set.of(Permission.USER_READ.getCode(), Permission.USER_WRITE.getCode()));
        role.setColor("#3b82f6");
        role.setIcon("shield");

        roleResponse = RoleResponse.builder()
            .id(role.getId())
            .name(role.getName())
            .description(role.getDescription())
            .organizationId(role.getOrganizationId())
            .roleType(role.getRoleType())
            .permissions(role.getPermissions())
            .build();
    }

    @Test
    void createRole_ShouldCreateRoleSuccessfully() {
        // Given
        when(roleRepository.countByOrganizationIdAndName(organizationId, "Test Role"))
            .thenReturn(Mono.just(0L));
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.just(role));
        when(roleMapper.toResponse(role)).thenReturn(roleResponse);

        // When & Then
        StepVerifier.create(roleService.createRole(createRequest, createdBy))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getName()).isEqualTo("Test Role");
                assertThat(response.getOrganizationId()).isEqualTo(organizationId);
                assertThat(response.getRoleType()).isEqualTo(RoleType.CUSTOM);
            })
            .verifyComplete();
    }

    @Test
    void createRole_ShouldFailWhenNameExists() {
        // Given
        when(roleRepository.countByOrganizationIdAndName(organizationId, "Test Role"))
            .thenReturn(Mono.just(1L));

        // When & Then
        StepVerifier.create(roleService.createRole(createRequest, createdBy))
            .expectErrorMatches(throwable ->
                throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Role name already exists"))
            .verify();
    }

    @Test
    void updateRole_ShouldUpdateRoleSuccessfully() {
        // Given
        UUID roleId = role.getId();
        UpdateRoleRequest updateRequest = UpdateRoleRequest.builder()
            .name("Updated Role")
            .description("Updated description")
            .build();

        when(roleRepository.findById(roleId)).thenReturn(Mono.just(role));
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.just(role));
        when(roleMapper.toResponse(any(Role.class))).thenReturn(roleResponse);

        // When & Then
        StepVerifier.create(roleService.updateRole(roleId, updateRequest, createdBy))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getId()).isEqualTo(roleId);
            })
            .verifyComplete();
    }

    @Test
    void deleteRole_ShouldDeleteRoleSuccessfully() {
        // Given
        UUID roleId = role.getId();
        role.setIsSystemRole(false);
        role.setIsDefaultRole(false);

        when(roleRepository.findById(roleId)).thenReturn(Mono.just(role));
        when(userRoleRepository.countByRoleId(roleId)).thenReturn(Mono.just(0L));
        when(roleRepository.deleteById(roleId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roleService.deleteRole(roleId))
            .verifyComplete();
    }

    @Test
    void deleteRole_ShouldFailForSystemRole() {
        // Given
        UUID roleId = role.getId();
        role.setIsSystemRole(true);

        when(roleRepository.findById(roleId)).thenReturn(Mono.just(role));

        // When & Then
        StepVerifier.create(roleService.deleteRole(roleId))
            .expectErrorMatches(throwable ->
                throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Cannot delete system role"))
            .verify();
    }

    @Test
    void getRolesByOrganization_ShouldReturnRoles() {
        // Given
        when(roleRepository.findByOrganizationId(organizationId))
            .thenReturn(Flux.just(role));
        when(roleMapper.toResponse(role)).thenReturn(roleResponse);

        // When & Then
        StepVerifier.create(roleService.getRolesByOrganization(organizationId))
            .assertNext(response -> {
                assertThat(response).isNotNull();
                assertThat(response.getOrganizationId()).isEqualTo(organizationId);
            })
            .verifyComplete();
    }

    @Test
    void cloneRole_ShouldCloneRoleSuccessfully() {
        // Given
        UUID roleId = role.getId();
        String newName = "Cloned Role";

        when(roleRepository.findById(roleId)).thenReturn(Mono.just(role));
        when(roleRepository.countByOrganizationIdAndName(organizationId, newName))
            .thenReturn(Mono.just(0L));
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.just(role));
        when(roleMapper.toResponse(any(Role.class))).thenReturn(roleResponse);

        // When & Then
        StepVerifier.create(roleService.cloneRole(roleId, newName, createdBy))
            .assertNext(response -> {
                assertThat(response).isNotNull();
            })
            .verifyComplete();
    }
}
