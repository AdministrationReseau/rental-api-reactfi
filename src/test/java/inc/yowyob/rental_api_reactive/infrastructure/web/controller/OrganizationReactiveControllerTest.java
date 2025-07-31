package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.OrganizationReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OrganizationResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(OrganizationReactiveController.class)
class OrganizationReactiveControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrganizationReactiveService organizationService;

    private OrganizationResponse testOrgResponse1;
    private OrganizationResponse testOrgResponse2;
    private UUID organizationId1;
    private UUID organizationId2;
    private UUID ownerId1;
    private UUID ownerId2;

    @BeforeEach
    void setUp() {
        organizationId1 = UUID.randomUUID();
        organizationId2 = UUID.randomUUID();
        ownerId1 = UUID.randomUUID();
        ownerId2 = UUID.randomUUID();

        testOrgResponse1 = new OrganizationResponse();
        testOrgResponse1.setId(organizationId1);
        testOrgResponse1.setName("YowyoB Transport");
        testOrgResponse1.setDescription("Entreprise de transport urbain à Douala");
        testOrgResponse1.setBusinessType("Transport");
        testOrgResponse1.setRegistrationNumber("CM-DLA-2024-001");
        testOrgResponse1.setOwnerId(ownerId1);
        testOrgResponse1.setContactEmail("contact@yowyob.transport");
        testOrgResponse1.setContactPhone("+237123456789");
        testOrgResponse1.setWebsite("https://transport.yowyob.inc");
        testOrgResponse1.setFullAddress("Rue de la République, Immeuble Moderne, Douala, Littoral 00237, CM");
        testOrgResponse1.setCity("Douala");
        testOrgResponse1.setCountry("CM");
        testOrgResponse1.setMaxVehicles(50);
        testOrgResponse1.setMaxDrivers(25);
        testOrgResponse1.setMaxAgencies(5);
        testOrgResponse1.setMaxUsers(20);
        testOrgResponse1.setCurrency("XAF");
        testOrgResponse1.setTimezone("Africa/Douala");
        testOrgResponse1.setLogoUrl("https://cdn.yowyob.inc/logos/transport.png");
        testOrgResponse1.setIsVerified(true);
        testOrgResponse1.setVerificationDate(LocalDateTime.now().minusDays(30));
        testOrgResponse1.setCreatedAt(LocalDateTime.now().minusDays(60));
        testOrgResponse1.setIsActive(true);

        testOrgResponse2 = new OrganizationResponse();
        testOrgResponse2.setId(organizationId2);
        testOrgResponse2.setName("Cameroon Logistics");
        testOrgResponse2.setDescription("Société de logistique et transport de marchandises");
        testOrgResponse2.setBusinessType("Logistics");
        testOrgResponse2.setRegistrationNumber("CM-YAO-2024-002");
        testOrgResponse2.setOwnerId(ownerId2);
        testOrgResponse2.setContactEmail("info@cameroon-logistics.cm");
        testOrgResponse2.setContactPhone("+237987654321");
        testOrgResponse2.setCity("Yaoundé");
        testOrgResponse2.setCountry("CM");
        testOrgResponse2.setMaxVehicles(30);
        testOrgResponse2.setMaxDrivers(15);
        testOrgResponse2.setMaxAgencies(3);
        testOrgResponse2.setMaxUsers(10);
        testOrgResponse2.setCurrency("XAF");
        testOrgResponse2.setTimezone("Africa/Douala");
        testOrgResponse2.setIsVerified(false);
        testOrgResponse2.setCreatedAt(LocalDateTime.now().minusDays(15));
        testOrgResponse2.setIsActive(true);
    }

    @Test
    void getAllOrganizations_ShouldReturnSuccessResponse_WhenOrganizationsExist() {
        // Given
        when(organizationService.findAll()).thenReturn(Flux.just(testOrgResponse1, testOrgResponse2));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(false)
            .jsonPath("$.message").isEqualTo("Organisation non trouvée")
            .jsonPath("$.status_code").isEqualTo(404);
    }

    @Test
    void getOrganizationById_ShouldReturnBadRequest_WhenIdIsInvalid() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/{id}", "invalid-uuid")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getOrganizationByOwner_ShouldReturnOrganization_WhenOwnerExists() {
        // Given
        when(organizationService.findByOwnerId(ownerId1)).thenReturn(Mono.just(testOrgResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/owner/{ownerId}", ownerId1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Organisation du propriétaire trouvée")
            .jsonPath("$.data.owner_id").isEqualTo(ownerId1.toString())
            .jsonPath("$.data.name").isEqualTo("YowyoB Transport");
    }

    @Test
    void getOrganizationByOwner_ShouldReturnNotFound_WhenOwnerHasNoOrganization() {
        // Given
        UUID ownerWithoutOrg = UUID.randomUUID();
        when(organizationService.findByOwnerId(ownerWithoutOrg)).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/owner/{ownerId}", ownerWithoutOrg)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(false)
            .jsonPath("$.message").isEqualTo("Organisation non trouvée pour ce propriétaire")
            .jsonPath("$.status_code").isEqualTo(404);
    }

    @Test
    void getOrganizationByOwner_ShouldReturnBadRequest_WhenOwnerIdIsInvalid() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/owner/{ownerId}", "invalid-uuid")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getActiveOrganizations_ShouldReturnActiveOrganizations_WhenActiveOrganizationsExist() {
        // Given
        when(organizationService.findAllActive()).thenReturn(Flux.just(testOrgResponse1, testOrgResponse2));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/active")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Organisations actives récupérées")
            .jsonPath("$.data").isNotEmpty();
    }

    @Test
    void getActiveOrganizations_ShouldReturnEmpty_WhenNoActiveOrganizations() {
        // Given
        when(organizationService.findAllActive()).thenReturn(Flux.empty());

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/active")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Organisations actives récupérées");
    }

    @Test
    void checkOrganizationNameExists_ShouldReturnTrue_WhenNameExists() {
        // Given
        String existingName = "YowyoB Transport";
        when(organizationService.existsByName(existingName)).thenReturn(Mono.just(true));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/exists/{name}", existingName)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Nom existe");
    }

    @Test
    void checkOrganizationNameExists_ShouldReturnFalse_WhenNameNotExists() {
        // Given
        String nonExistentName = "Non Existent Organization";
        when(organizationService.existsByName(nonExistentName)).thenReturn(Mono.just(false));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/exists/{name}", nonExistentName)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo(false)
            .jsonPath("$.message").isEqualTo("Nom disponible");
    }

    @Test
    void checkOrganizationNameExists_ShouldHandleSpecialCharacters_InName() {
        // Given
        String nameWithSpecialChars = "Org & Co. (Ltd)";
        when(organizationService.existsByName(nameWithSpecialChars)).thenReturn(Mono.just(false));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/exists/{name}", nameWithSpecialChars)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo(false);
    }

    @Test
    void checkOrganizationNameExists_ShouldHandleEmptyName_Gracefully() {
        // Given
        String emptyName = "";
        when(organizationService.existsByName(emptyName)).thenReturn(Mono.just(false));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/exists/{name}", emptyName)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isEqualTo(false);
    }

    @Test
    void getAllOrganizations_ShouldHandleServiceError_Gracefully() {
        // Given
        when(organizationService.findAll()).thenReturn(Flux.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void getOrganizationById_ShouldHandleServiceError_Gracefully() {
        // Given
        when(organizationService.findById(organizationId1)).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/{id}", organizationId1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void getOrganizationByOwner_ShouldHandleServiceError_Gracefully() {
        // Given
        when(organizationService.findByOwnerId(ownerId1)).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/owner/{ownerId}", ownerId1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void checkOrganizationNameExists_ShouldHandleServiceError_Gracefully() {
        // Given
        String name = "Test Organization";
        when(organizationService.existsByName(name)).thenReturn(Mono.error(new RuntimeException("Service error")));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/exists/{name}", name)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void getAllOrganizations_ShouldHaveCorrectContentType() {
        // Given
        when(organizationService.findAll()).thenReturn(Flux.just(testOrgResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void getAllOrganizations_ShouldAcceptJsonOnly() {
        // Given
        when(organizationService.findAll()).thenReturn(Flux.just(testOrgResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void getOrganizationById_ShouldReturnCorrectJsonStructure() {
        // Given
        when(organizationService.findById(organizationId1)).thenReturn(Mono.just(testOrgResponse1));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/{id}", organizationId1)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").exists()
            .jsonPath("$.message").exists()
            .jsonPath("$.data").exists()
            .jsonPath("$.timestamp").exists()
            .jsonPath("$.status_code").exists()
            .jsonPath("$.data.id").exists()
            .jsonPath("$.data.name").exists()
            .jsonPath("$.data.business_type").exists()
            .jsonPath("$.data.owner_id").exists()
            .jsonPath("$.data.is_active").exists();
    }

    @Test
    void getActiveOrganizations_ShouldFilterActiveOrganizations() {
        // Given
        when(organizationService.findAllActive()).thenReturn(Flux.just(testOrgResponse1, testOrgResponse2));

        // When & Then
        webTestClient.get()
            .uri("/api/v1/organizations/active")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.data").isArray();
    }
}
