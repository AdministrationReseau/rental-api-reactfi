package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.UserType;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveServiceTest {

    @Mock
    private UserReactiveRepository userRepository;

    @InjectMocks
    private UserReactiveService userService;

    private User testUser1;
    private User testUser2;
    private UUID testUserId1;
    private UUID testUserId2;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        testUserId1 = UUID.randomUUID();
        testUserId2 = UUID.randomUUID();
        organizationId = UUID.randomUUID();

        testUser1 = new User();
        testUser1.setId(testUserId1);
        testUser1.setEmail("john.doe@example.com");
        testUser1.setFirstName("John");
        testUser1.setLastName("Doe");
        testUser1.setPhone("+237123456789");
        testUser1.setUserType(UserType.CLIENT);
        testUser1.setOrganizationId(organizationId);
        testUser1.setIsEmailVerified(true);
        testUser1.setIsPhoneVerified(false);
        testUser1.setPreferredLanguage("fr");
        testUser1.setTimezone("Africa/Douala");
        testUser1.setIsActive(true);
        testUser1.setCreatedAt(LocalDateTime.now());

        testUser2 = new User();
        testUser2.setId(testUserId2);
        testUser2.setEmail("jane.smith@example.com");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");
        testUser2.setPhone("+237987654321");
        testUser2.setUserType(UserType.AGENCY_MANAGER);
        testUser2.setOrganizationId(organizationId);
        testUser2.setIsEmailVerified(false);
        testUser2.setIsPhoneVerified(true);
        testUser2.setPreferredLanguage("en");
        testUser2.setTimezone("Africa/Douala");
        testUser2.setIsActive(true);
        testUser2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findAll_ShouldReturnAllUsers_WhenUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.just(testUser1, testUser2));

        // When & Then
        StepVerifier.create(userService.findAll())
            .expectNextMatches(userResponse -> {
                assertThat(userResponse.getEmail()).isEqualTo("john.doe@example.com");
                assertThat(userResponse.getFirstName()).isEqualTo("John");
                assertThat(userResponse.getLastName()).isEqualTo("Doe");
                assertThat(userResponse.getFullName()).isEqualTo("John Doe");
                assertThat(userResponse.getUserType()).isEqualTo(UserType.CLIENT);
                assertThat(userResponse.getIsEmailVerified()).isTrue();
                return true;
            })
            .expectNextMatches(userResponse -> {
                assertThat(userResponse.getEmail()).isEqualTo("jane.smith@example.com");
                assertThat(userResponse.getFirstName()).isEqualTo("Jane");
                assertThat(userResponse.getUserType()).isEqualTo(UserType.AGENCY_MANAGER);
                assertThat(userResponse.getIsPhoneVerified()).isTrue();
                return true;
            })
            .verifyComplete();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmpty_WhenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(userService.findAll())
            .verifyComplete();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(testUserId1)).thenReturn(Mono.just(testUser1));

        // When & Then
        StepVerifier.create(userService.findById(testUserId1))
            .expectNextMatches(userResponse -> {
                assertThat(userResponse.getId()).isEqualTo(testUserId1);
                assertThat(userResponse.getEmail()).isEqualTo("john.doe@example.com");
                assertThat(userResponse.getPhone()).isEqualTo("+237123456789");
                assertThat(userResponse.getOrganizationId()).isEqualTo(organizationId);
                return true;
            })
            .verifyComplete();

        verify(userRepository, times(1)).findById(testUserId1);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userService.findById(nonExistentId))
            .verifyComplete();

        verify(userRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Given
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Mono.just(testUser1));

        // When & Then
        StepVerifier.create(userService.findByEmail(email))
            .expectNextMatches(userResponse -> {
                assertThat(userResponse.getEmail()).isEqualTo(email);
                assertThat(userResponse.getFirstName()).isEqualTo("John");
                return true;
            })
            .verifyComplete();

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userService.findByEmail(nonExistentEmail))
            .verifyComplete();

        verify(userRepository, times(1)).findByEmail(nonExistentEmail);
    }

    @Test
    void findByOrganizationId_ShouldReturnUsersInOrganization_WhenUsersExist() {
        // Given
        when(userRepository.findByOrganizationId(organizationId))
            .thenReturn(Flux.just(testUser1, testUser2));

        // When & Then
        StepVerifier.create(userService.findByOrganizationId(organizationId))
            .expectNextMatches(userResponse -> {
                assertThat(userResponse.getOrganizationId()).isEqualTo(organizationId);
                assertThat(userResponse.getEmail()).isEqualTo("john.doe@example.com");
                return true;
            })
            .expectNextMatches(userResponse -> {
                assertThat(userResponse.getOrganizationId()).isEqualTo(organizationId);
                assertThat(userResponse.getEmail()).isEqualTo("jane.smith@example.com");
                return true;
            })
            .verifyComplete();

        verify(userRepository, times(1)).findByOrganizationId(organizationId);
    }

    @Test
    void findByOrganizationId_ShouldReturnEmpty_WhenNoUsersInOrganization() {
        // Given
        UUID emptyOrganizationId = UUID.randomUUID();
        when(userRepository.findByOrganizationId(emptyOrganizationId))
            .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(userService.findByOrganizationId(emptyOrganizationId))
            .verifyComplete();

        verify(userRepository, times(1)).findByOrganizationId(emptyOrganizationId);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        String existingEmail = "john.doe@example.com";
        when(userRepository.countByEmail(existingEmail)).thenReturn(Mono.just(1L));

        // When & Then
        StepVerifier.create(userService.existsByEmail(existingEmail))
            .expectNext(true)
            .verifyComplete();

        verify(userRepository, times(1)).countByEmail(existingEmail);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailNotExists() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.countByEmail(nonExistentEmail)).thenReturn(Mono.just(0L));

        // When & Then
        StepVerifier.create(userService.existsByEmail(nonExistentEmail))
            .expectNext(false)
            .verifyComplete();

        verify(userRepository, times(1)).countByEmail(nonExistentEmail);
    }

    @Test
    void save_ShouldReturnSavedUser_WhenUserIsValid() {
        // Given
        User newUser = new User();
        newUser.setEmail("new.user@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");
        newUser.setUserType(UserType.CLIENT);

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail("new.user@example.com");
        savedUser.setFirstName("New");
        savedUser.setLastName("User");
        savedUser.setUserType(UserType.CLIENT);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setIsActive(true);

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // When & Then
        StepVerifier.create(userService.save(newUser))
            .expectNextMatches(userResponse -> {
                assertThat(userResponse.getEmail()).isEqualTo("new.user@example.com");
                assertThat(userResponse.getFirstName()).isEqualTo("New");
                assertThat(userResponse.getLastName()).isEqualTo("User");
                assertThat(userResponse.getFullName()).isEqualTo("New User");
                assertThat(userResponse.getUserType()).isEqualTo(UserType.CLIENT);
                assertThat(userResponse.getIsActive()).isTrue();
                return true;
            })
            .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteById_ShouldCompleteSuccessfully_WhenUserExists() {
        // Given
        when(userRepository.deleteById(testUserId1)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userService.deleteById(testUserId1))
            .verifyComplete();

        verify(userRepository, times(1)).deleteById(testUserId1);
    }

    @Test
    void save_ShouldCallPrePersist_BeforeSaving() {
        // Given
        User newUser = new User();
        newUser.setEmail("test@example.com");
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setUserType(UserType.CLIENT);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            // Vérifier que prePersist a été appelé
            assertThat(userToSave.getId()).isNotNull();
            assertThat(userToSave.getCreatedAt()).isNotNull();
            assertThat(userToSave.getUpdatedAt()).isNotNull();
            return Mono.just(userToSave);
        });

        // When & Then
        StepVerifier.create(userService.save(newUser))
            .expectNextCount(1)
            .verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findByEmail_ShouldHandleNullEmail_Gracefully() {
        // Given
        when(userRepository.findByEmail(null)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userService.findByEmail(null))
            .verifyComplete();

        verify(userRepository, times(1)).findByEmail(null);
    }

    @Test
    void existsByEmail_ShouldHandleEmptyEmail_Gracefully() {
        // Given
        String emptyEmail = "";
        when(userRepository.countByEmail(emptyEmail)).thenReturn(Mono.just(0L));

        // When & Then
        StepVerifier.create(userService.existsByEmail(emptyEmail))
            .expectNext(false)
            .verifyComplete();

        verify(userRepository, times(1)).countByEmail(emptyEmail);
    }
}
