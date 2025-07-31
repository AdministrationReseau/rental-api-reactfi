package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import inc.yowyob.rental_api_reactive.persistence.repository.OrganizationReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class OrganizationReactiveServiceTest {

    @Mock
    private OrganizationReactiveRepository organizationRepository;

    @InjectMocks
    private OrganizationReactiveService organizationService;

    private Organization testOrganization1;
    private Organization testOrganization2;
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

        testOrganization1 = new Organization();
        testOrganization1.setId(organizationId1);
        testOrganization1.setName("YowyoB Transport");
        testOrganization1.setDescription("Entreprise de transport urbain à Douala");
        testOrganization1.setBusinessType("Transport");
        testOrganization1.setRegistrationNumber("CM-DLA-2024-001");
        testOrganization1.setTaxNumber("TAX-001-2024");
        testOrganization1.setOwnerId(ownerId1);
        testOrganization1.setContactEmail("contact@yowyob.transport");
        testOrganization1.setContactPhone("+237123456789");
        testOrganization1.setWebsite("https://transport.yowyob.inc");
        testOrganization1.setAddressLine1("Rue de la République");
        testOrganization1.setAddressLine2("Immeuble Moderne");
        testOrganization1.setCity("Douala");
        testOrganization1.setStateProvince("Littoral");
        testOrganization1.setPostalCode("00237");
        testOrganization1.setCountry("CM");
        testOrganization1.setMaxVehicles(50);
        testOrganization1.setMaxDrivers(25);
        testOrganization1.setMaxAgencies(5);
        testOrganization1.setMaxUsers(20);
        testOrganization1.setCurrency("XAF");
        testOrganization1.setTimezone("Africa/Douala");
        testOrganization1.setDefaultLanguage("fr");
        testOrganization1.setLogoUrl("https://cdn.yowyob.inc/logos/transport.png");
        testOrganization1.setIsVerified(true);
        testOrganization1.setVerificationDate(LocalDateTime.now().minusDays(30));
        testOrganization1.setIsActive(true);
        testOrganization1.setCreatedAt(LocalDateTime.now().minusDays(60));
        testOrganization1.setUpdatedAt(LocalDateTime.now().minusDays(1));


        testOrganization2 = new Organization();
        testOrganization2.setId(organizationId2);
        testOrganization2.setName("Cameroon Logistics");
        testOrganization2.setDescription("Société de logistique et transport de marchandises");
        testOrganization2.setBusinessType("Logistics");
        testOrganization2.setRegistrationNumber("CM-YAO-2024-002");
        testOrganization2.setOwnerId(ownerId2);
        testOrganization2.setContactEmail("info@cameroon-logistics.cm");
        testOrganization2.setContactPhone("+237987654321");
        testOrganization2.setCity("Yaoundé");
        testOrganization2.setCountry("CM");
        testOrganization2.setMaxVehicles(30);
        testOrganization2.setMaxDrivers(15);
        testOrganization2.setMaxAgencies(3);
        testOrganization2.setMaxUsers(10);
        testOrganization2.setCurrency("XAF");
        testOrganization2.setTimezone("Africa/Douala");
        testOrganization2.setDefaultLanguage("fr");
        testOrganization2.setIsVerified(false);
        testOrganization2.setIsActive(true);
        testOrganization2.setCreatedAt(LocalDateTime.now().minusDays(15));
        testOrganization2.setUpdatedAt(LocalDateTime.now().minusDays(2));

    }

    @Test
    void findAll_ShouldReturnAllOrganizations_WhenOrganizationsExist() {
        // Given
        when(organizationRepository.findAll()).thenReturn(Flux.just(testOrganization1, testOrganization2));

        // When & Then
        StepVerifier.create(organizationService.findAll())
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getName()).isEqualTo("YowyoB Transport");
                assertThat(orgResponse.getBusinessType()).isEqualTo("Transport");
                return true;
            })
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getName()).isEqualTo("Cameroon Logistics");
                assertThat(orgResponse.getBusinessType()).isEqualTo("Logistics");
                return true;
            })
            .verifyComplete();

        verify(organizationRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmpty_WhenNoOrganizationsExist() {
        // Given
        when(organizationRepository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(organizationService.findAll())
            .verifyComplete();

        verify(organizationRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnOrganization_WhenOrganizationExists() {
        // Given
        when(organizationRepository.findById(organizationId1)).thenReturn(Mono.just(testOrganization1));

        // When & Then
        StepVerifier.create(organizationService.findById(organizationId1))
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getId()).isEqualTo(organizationId1);
                assertThat(orgResponse.getName()).isEqualTo("YowyoB Transport");
                return true;
            })
            .verifyComplete();

        verify(organizationRepository, times(1)).findById(organizationId1);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenOrganizationNotExists() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(organizationRepository.findById(nonExistentId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(organizationService.findById(nonExistentId))
            .verifyComplete();

        verify(organizationRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void findByOwnerId_ShouldReturnOrganization_WhenOwnerExists() {
        // Given
        when(organizationRepository.findByOwnerId(ownerId1)).thenReturn(Mono.just(testOrganization1));

        // When & Then
        StepVerifier.create(organizationService.findByOwnerId(ownerId1))
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getOwnerId()).isEqualTo(ownerId1);
                assertThat(orgResponse.getName()).isEqualTo("YowyoB Transport");
                return true;
            })
            .verifyComplete();

        verify(organizationRepository, times(1)).findByOwnerId(ownerId1);
    }

    /*@Test
    void findByRegistrationNumber_ShouldReturnOrganization_WhenExists() {
        // Given
        String regNum = "CM-DLA-2024-001";
        when(organizationRepository.findByRegistrationNumber(regNum)).thenReturn(Mono.just(testOrganization1));

        // When & Then
        StepVerifier.create(organizationService.findByRegistrationNumber(regNum))
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getRegistrationNumber()).isEqualTo(regNum);
                assertThat(orgResponse.getName()).isEqualTo("YowyoB Transport");
                return true;
            })
            .verifyComplete();

        verify(organizationRepository, times(1)).findByRegistrationNumber(regNum);
    }*/


    @Test
    void findAllActive_ShouldReturnActiveOrganizations_WhenActiveOrganizationsExist() {
        // Given
        when(organizationRepository.findAllActive()).thenReturn(Flux.just(testOrganization1, testOrganization2));

        // When & Then
        StepVerifier.create(organizationService.findAllActive())
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getIsActive()).isTrue();
                assertThat(orgResponse.getName()).isEqualTo("YowyoB Transport");
                return true;
            })
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getIsActive()).isTrue();
                assertThat(orgResponse.getName()).isEqualTo("Cameroon Logistics");
                return true;
            })
            .verifyComplete();

        verify(organizationRepository, times(1)).findAllActive();
    }

    @Test
    void findAllActive_ShouldReturnEmpty_WhenNoActiveOrganizations() {
        // Given
        when(organizationRepository.findAllActive()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(organizationService.findAllActive())
            .verifyComplete();

        verify(organizationRepository, times(1)).findAllActive();
    }

    @Test
    void existsByName_ShouldReturnTrue_WhenNameExists() {
        // Given
        String existingName = "YowyoB Transport";
        when(organizationRepository.countByName(existingName)).thenReturn(Mono.just(1L));

        // When & Then
        StepVerifier.create(organizationService.existsByName(existingName))
            .expectNext(true)
            .verifyComplete();

        verify(organizationRepository, times(1)).countByName(existingName);
    }

    @Test
    void existsByName_ShouldReturnFalse_WhenNameNotExists() {
        // Given
        String nonExistentName = "Non Existent Organization";
        when(organizationRepository.countByName(nonExistentName)).thenReturn(Mono.just(0L));

        // When & Then
        StepVerifier.create(organizationService.existsByName(nonExistentName))
            .expectNext(false)
            .verifyComplete();

        verify(organizationRepository, times(1)).countByName(nonExistentName);
    }

    @Test
    void save_ShouldReturnSavedOrganization_WhenOrganizationIsValid() {
        // Given
        Organization newOrganization = new Organization();
        newOrganization.setName("New Transport Company");
        newOrganization.setOwnerId(UUID.randomUUID());

        // The service will add an ID, timestamps, etc.
        when(organizationRepository.save(any(Organization.class))).thenReturn(Mono.just(newOrganization));

        // When & Then
        StepVerifier.create(organizationService.save(newOrganization))
            .expectNextMatches(orgResponse -> {
                assertThat(orgResponse.getName()).isEqualTo("New Transport Company");
                return true;
            })
            .verifyComplete();

        verify(organizationRepository, times(1)).save(any(Organization.class));
    }

    @Test
    void deleteById_ShouldCompleteSuccessfully_WhenOrganizationExists() {
        // Given
        when(organizationRepository.deleteById(organizationId1)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(organizationService.deleteById(organizationId1))
            .verifyComplete();

        verify(organizationRepository, times(1)).deleteById(organizationId1);
    }

    @Test
    void save_ShouldSetTimestampsAndId_BeforeSaving() {
        // Given
        Organization newOrganization = new Organization();
        newOrganization.setName("Test Organization");
        newOrganization.setOwnerId(UUID.randomUUID());

        // Prepare an ArgumentCaptor to capture the organization passed to the save method
        ArgumentCaptor<Organization> organizationCaptor = ArgumentCaptor.forClass(Organization.class);

        // When save is called, just return the captured object
        when(organizationRepository.save(organizationCaptor.capture())).thenReturn(Mono.empty());

        // When
        StepVerifier.create(organizationService.save(newOrganization))
            .verifyComplete();

        // Then
        // Verify that the save method on the repository was called once
        verify(organizationRepository, times(1)).save(any(Organization.class));

        // Get the captured organization
        Organization capturedOrg = organizationCaptor.getValue();

        // Assert that the service has set the required fields before saving
        assertThat(capturedOrg.getId()).isNotNull();
        assertThat(capturedOrg.getCreatedAt()).isNotNull();
        assertThat(capturedOrg.getUpdatedAt()).isNotNull();
        assertThat(capturedOrg.getIsActive()).isTrue();
    }
}
