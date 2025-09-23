package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.dto.UserType;
import inc.yowyob.rental_api_reactive.application.service.UserReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebFluxTest(UserReactiveController.class)
class UserReactiveControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserReactiveService userService;

    private UserResponse testUserResponse1;
    private UserResponse testUserResponse2;
    private UUID testUserId1;
    private UUID testUserId2;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        testUserId1 = UUID.randomUUID();
        testUserId2 = UUID.randomUUID();
        organizationId = UUID.randomUUID();

        testUserResponse1 = new UserResponse();
        testUserResponse1.setId(testUserId1);
        testUserResponse1.setEmail("john.doe@example.com");
        testUserResponse1.setFirstName("John");
        testUserResponse1.setLastName("Doe");
        testUserResponse1.setFullName("John Doe");
        testUserResponse1.setPhone("+237123456789");
        testUserResponse1.setUserType(UserType.CLIENT);
        testUserResponse1.setOrganizationId(organizationId);
        testUserResponse1.setProfilePicture("https://cdn.example.com/profiles/john.jpg");
        testUserResponse1.setIsEmailVerified(true);
        testUserResponse1.setIsPhoneVerified(false);
        testUserResponse1.setPreferredLanguage("fr");
        testUserResponse1.setTimezone("Africa/Douala");
        testUserResponse1.setLastLoginAt(LocalDateTime.now().minusHours(2));
        testUserResponse1.setCreatedAt(LocalDateTime.now().minusDays(30));
        testUserResponse1.setIsActive(true);

        testUserResponse2 = new UserResponse();
        testUserResponse2.setId(testUserId2);
        testUserResponse2.setEmail("jane.smith@example.com");
        testUserResponse2.setFirstName("Jane");
        testUserResponse2.setLastName("Smith");
        testUserResponse2.setFullName("Jane Smith");
        testUserResponse2.setPhone("+237987654321");
        testUserResponse2.setUserType(UserType.AGENCY_MANAGER);
        testUserResponse2.setOrganizationId(organizationId);
        testUserResponse2.setIsEmailVerified(false);
        testUserResponse2.setIsPhoneVerified(true);
        testUserResponse2.setPreferredLanguage("en");
        testUserResponse2.setTimezone("Africa/Douala");
        testUserResponse2.setLastLoginAt(LocalDateTime.now().minusMinutes(30));
        testUserResponse2.setCreatedAt(LocalDateTime.now().minusDays(15));
        testUserResponse2.setIsActive(true);
    }

    @Test
    void getAllUsers_ShouldReturnSuccessResponse_WhenUsersExist() {
        // Given
        when(userService.findAll()).thenReturn(Flux.just(testUserResponse1, testUserResponse2));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Utilisateurs récupérés avec succès")
            .jsonPath("$.data").isNotEmpty()
            .jsonPath("$.timestamp").isNotEmpty()
            .jsonPath("$.status_code").isEqualTo(200);
    }

    @Test
    void getAllUsers_ShouldReturnSuccessResponse_WhenNoUsersExist() {
        // Given
        when(userService.findAll()).thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Utilisateurs récupérés avec succès");
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userService.findById(testUserId1)).thenReturn(Mono.just(testUserResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/{id}", testUserId1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Utilisateur trouvé")
            .jsonPath("$.data.id").isEqualTo(testUserId1.toString())
            .jsonPath("$.data.email").isEqualTo("john.doe@example.com")
            .jsonPath("$.data.first_name").isEqualTo("John")
            .jsonPath("$.data.last_name").isEqualTo("Doe")
            .jsonPath("$.data.full_name").isEqualTo("John Doe")
            .jsonPath("$.data.phone").isEqualTo("+237123456789")
            .jsonPath("$.data.user_type").isEqualTo("CLIENT")
            .jsonPath("$.data.organization_id").isEqualTo(organizationId.toString())
            .jsonPath("$.data.is_email_verified").isEqualTo(true)
            .jsonPath("$.data.is_phone_verified").isEqualTo(false)
            .jsonPath("$.data.preferred_language").isEqualTo("fr")
            .jsonPath("$.data.timezone").isEqualTo("Africa/Douala")
            .jsonPath("$.data.is_active").isEqualTo(true);
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userService.findById(nonExistentId)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/{id}", nonExistentId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(false)
            .jsonPath("$.message").isEqualTo("Utilisateur non trouvé")
            .jsonPath("$.status_code").isEqualTo(404);
    }

    @Test
    void getUserById_ShouldReturnBadRequest_WhenIdIsInvalid() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/{id}", "invalid-uuid")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() {
        // Given
        String email = "john.doe@example.com";
        when(userService.findByEmail(email)).thenReturn(Mono.just(testUserResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/email/{email}", email)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Utilisateur trouvé")
            .jsonPath("$.data.email").isEqualTo(email)
            .jsonPath("$.data.first_name").isEqualTo("John");
    }

    @Test
    void getUserByEmail_ShouldReturnNotFound_WhenEmailNotExists() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userService.findByEmail(nonExistentEmail)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/email/{email}", nonExistentEmail)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(false)
            .jsonPath("$.message").isEqualTo("Utilisateur non trouvé")
            .jsonPath("$.status_code").isEqualTo(404);
    }

    @Test
    void getUserByEmail_ShouldHandleSpecialCharacters_InEmail() {
        // Given
        String emailWithSpecialChars = "test+special@example-domain.co.uk";
        when(userService.findByEmail(emailWithSpecialChars)).thenReturn(Mono.just(testUserResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/email/{email}", emailWithSpecialChars)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true);
    }

    @Test
    void getUsersByOrganization_ShouldReturnUsers_WhenUsersExistInOrganization() {
        // Given
        when(userService.findByOrganizationId(organizationId))
            .thenReturn(Flux.just(testUserResponse1, testUserResponse2));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/organization/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Utilisateurs de l'organisation récupérés")
            .jsonPath("$.data").isNotEmpty();
    }

    @Test
    void getUsersByOrganization_ShouldReturnEmpty_WhenNoUsersInOrganization() {
        // Given
        UUID emptyOrganizationId = UUID.randomUUID();
        when(userService.findByOrganizationId(emptyOrganizationId)).thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/organization/{organizationId}", emptyOrganizationId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Utilisateurs de l'organisation récupérés");
    }

    @Test
    void getUsersByOrganization_ShouldReturnBadRequest_WhenOrganizationIdIsInvalid() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/organization/{organizationId}", "invalid-uuid")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void checkEmailExists_ShouldReturnTrue_WhenEmailExists() {
        // Given
        String existingEmail = "john.doe@example.com";
        when(userService.existsByEmail(existingEmail)).thenReturn(Mono.just(true));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/exists/{email}", existingEmail)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Email existe");
    }

    @Test
    void checkEmailExists_ShouldReturnFalse_WhenEmailNotExists() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(userService.existsByEmail(nonExistentEmail)).thenReturn(Mono.just(false));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/exists/{email}", nonExistentEmail)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo(false)
            .jsonPath("$.message").isEqualTo("Email disponible");
    }

    @Test
    void checkEmailExists_ShouldHandleInvalidEmail_Gracefully() {
        // Given
        String invalidEmail = "invalid-email-format";
        when(userService.existsByEmail(invalidEmail)).thenReturn(Mono.just(false));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/exists/{email}", invalidEmail)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo(false);
    }

    @Test
    void getAllUsers_ShouldHandleServiceError_Gracefully() {
        // Given
        when(userService.findAll()).thenReturn(Flux.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void getUserById_ShouldHandleServiceError_Gracefully() {
        // Given
        when(userService.findById(testUserId1)).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/{id}", testUserId1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void checkEmailExists_ShouldHandleServiceError_Gracefully() {
        // Given
        String email = "test@example.com";
        when(userService.existsByEmail(email)).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users/exists/{email}", email)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void getAllUsers_ShouldHaveCorrectContentType() {
        // Given
        when(userService.findAll()).thenReturn(Flux.just(testUserResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void getAllUsers_ShouldAcceptJsonOnly() {
        // Given
        when(userService.findAll()).thenReturn(Flux.just(testUserResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/users")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().is5xxServerError();
    }
}
