package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.Agency;
import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import inc.yowyob.rental_api_reactive.persistence.repository.AgencyReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.OrganizationReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.mapper.AgencyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service réactif pour la gestion des agences
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyReactiveService {

    private final AgencyReactiveRepository agencyRepository;
    private final OrganizationReactiveRepository organizationRepository;
    private final AgencyMapper agencyMapper;
    private final MultiTenantReactiveService multiTenantService;
    private final SubscriptionValidationReactiveService subscriptionValidationService;

    /**
     * Crée une nouvelle agence
     */
    public Mono<AgencyResponse> createAgency(CreateAgencyRequest createRequest, UUID createdBy) {
        log.info("Creating agency: {} for organization: {}",
            createRequest.getName(), createRequest.getOrganizationId());

        return validateAgencyCreation(createRequest.getOrganizationId())
            .then(checkAgencyNameUniqueness(createRequest.getOrganizationId(), createRequest.getName()))
            .then(organizationRepository.findById(createRequest.getOrganizationId()))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                Agency agency = createAgencyFromRequest(createRequest, createdBy);
                return agencyRepository.save(agency)
                    .flatMap(savedAgency -> {
                        // Incrémenter le compteur d'agences de l'organisation
                        organization.incrementAgencies();
                        return organizationRepository.save(organization)
                            .then(Mono.just(savedAgency));
                    });
            })
            .map(agencyMapper::toResponse)
            .doOnSuccess(response -> log.info("Agency created successfully: {}", response.getId()))
            .doOnError(error -> log.error("Error creating agency: {}", error.getMessage()));
    }

    /**
     * Met à jour une agence
     */
    public Mono<AgencyResponse> updateAgency(UUID agencyId, UpdateAgencyRequest updateRequest, UUID updatedBy) {
        log.info("Updating agency: {}", agencyId);

        return multiTenantService.validateAgencyAccess(agencyId, updatedBy)
            .then(agencyRepository.findById(agencyId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée")))
            .flatMap(agency -> {
                updateAgencyFromRequest(agency, updateRequest, updatedBy);
                return agencyRepository.save(agency);
            })
            .map(agencyMapper::toResponse)
            .doOnSuccess(response -> log.info("Agency updated successfully: {}", response.getId()))
            .doOnError(error -> log.error("Error updating agency {}: {}", agencyId, error.getMessage()));
    }

    /**
     * Trouve une agence par ID
     */
    public Mono<AgencyResponse> findById(UUID agencyId) {
        log.debug("Finding agency by ID: {}", agencyId);

        return agencyRepository.findById(agencyId)
            .map(agencyMapper::toResponse)
            .doOnNext(agency -> log.debug("Found agency: {}", agency.getName()));
    }

    /**
     * Trouve toutes les agences d'une organisation
     */
    public Flux<AgencyResponse> findByOrganizationId(UUID organizationId) {
        log.debug("Finding agencies for organization: {}", organizationId);

        return agencyRepository.findByOrganizationId(organizationId)
            .map(agencyMapper::toResponse)
            .doOnNext(agency -> log.debug("Found agency: {}", agency.getName()));
    }

    /**
     * Trouve les agences actives d'une organisation
     */
    public Flux<AgencyResponse> findActiveByOrganizationId(UUID organizationId) {
        log.debug("Finding active agencies for organization: {}", organizationId);

        return agencyRepository.findActiveByOrganizationId(organizationId)
            .map(agencyMapper::toResponse)
            .doOnNext(agency -> log.debug("Found active agency: {}", agency.getName()));
    }

    /**
     * Trouve les agences par gestionnaire
     */
    public Flux<AgencyResponse> findByManagerId(UUID managerId) {
        log.debug("Finding agencies for manager: {}", managerId);

        return agencyRepository.findByManagerId(managerId)
            .map(agencyMapper::toResponse)
            .doOnNext(agency -> log.debug("Found agency managed by {}: {}", managerId, agency.getName()));
    }

    /**
     * Trouve les agences par ville et pays
     */
    public Flux<AgencyResponse> findByCityAndCountry(String city, String country) {
        log.debug("Finding agencies in {} - {}", city, country);

        return agencyRepository.findByCityAndCountry(city, country)
            .map(agencyMapper::toResponse)
            .doOnNext(agency -> log.debug("Found agency in {}: {}", city, agency.getName()));
    }

    /**
     * Supprime une agence (soft delete)
     */
    public Mono<Void> deleteAgency(UUID agencyId, UUID deletedBy) {
        log.info("Deleting agency: {}", agencyId);

        return multiTenantService.validateAgencyAccess(agencyId, deletedBy)
            .then(agencyRepository.findById(agencyId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée")))
            .flatMap(agency -> {
                // Vérifier qu'aucun véhicule ou chauffeur n'est actif
                return validateAgencyDeletion(agencyId)
                    .then(organizationRepository.findById(agency.getOrganizationId()))
                    .flatMap(organization -> {
                        // Soft delete de l'agence
                        agency.setIsActive(false);

                        // Décrémenter le compteur d'agences de l'organisation
                        organization.decrementAgencies();

                        return agencyRepository.save(agency)
                            .then(organizationRepository.save(organization))
                            .then();
                    });
            })
            .doOnSuccess(v -> log.info("Agency deleted successfully: {}", agencyId))
            .doOnError(error -> log.error("Error deleting agency {}: {}", agencyId, error.getMessage()));
    }

    /**
     * Active/désactive une agence
     */
    public Mono<AgencyResponse> toggleAgencyStatus(UUID agencyId, boolean isActive, UUID updatedBy) {
        log.info("Toggling agency status: {} to {}", agencyId, isActive);

        return multiTenantService.validateAgencyAccess(agencyId, updatedBy)
            .then(agencyRepository.findById(agencyId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée")))
            .flatMap(agency -> {
                agency.setIsActive(isActive);
                agency.preUpdate();
                return agencyRepository.save(agency);
            })
            .map(agencyMapper::toResponse)
            .doOnSuccess(response -> log.info("Agency status toggled: {} - {}",
                response.getId(), response.getIsActive()));
    }

    /**
     * Assigne un gestionnaire à une agence
     */
    public Mono<AgencyResponse> assignManager(UUID agencyId, UUID managerId, UUID assignedBy) {
        log.info("Assigning manager {} to agency {}", managerId, agencyId);

        return multiTenantService.validateAgencyAccess(agencyId, assignedBy)
            .then(multiTenantService.validateUserAccess(managerId, assignedBy))
            .then(agencyRepository.findById(agencyId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée")))
            .flatMap(agency -> {
                agency.setManagerId(managerId);
                agency.preUpdate();
                return agencyRepository.save(agency);
            })
            .map(agencyMapper::toResponse)
            .doOnSuccess(response -> log.info("Manager assigned to agency: {}", response.getId()));
    }

    /**
     * Met à jour les statistiques d'une agence
     */
    public Mono<AgencyResponse> updateAgencyStatistics(UUID agencyId, AgencyStatisticsRequest statsRequest) {
        log.debug("Updating statistics for agency: {}", agencyId);

        return agencyRepository.findById(agencyId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée")))
            .flatMap(agency -> {
                if (statsRequest.getVehicleStats() != null) {
                    agency.updateVehicleStats(
                        statsRequest.getVehicleStats().getTotal(),
                        statsRequest.getVehicleStats().getActive()
                    );
                }

                if (statsRequest.getDriverStats() != null) {
                    agency.updateDriverStats(
                        statsRequest.getDriverStats().getTotal(),
                        statsRequest.getDriverStats().getActive()
                    );
                }

                if (statsRequest.getPersonnelCount() != null) {
                    agency.updatePersonnelCount(statsRequest.getPersonnelCount());
                }

                if (statsRequest.getMonthlyRevenue() != null) {
                    agency.updateMonthlyRevenue(statsRequest.getMonthlyRevenue());
                }

                return agencyRepository.save(agency);
            })
            .map(agencyMapper::toResponse)
            .doOnSuccess(response -> log.debug("Statistics updated for agency: {}", response.getId()));
    }

    /**
     * Compte les agences d'une organisation
     */
    public Mono<Long> countByOrganizationId(UUID organizationId) {
        return agencyRepository.countByOrganizationId(organizationId);
    }

    /**
     * Compte les agences actives d'une organisation
     */
    public Mono<Long> countActiveByOrganizationId(UUID organizationId) {
        return agencyRepository.countActiveByOrganizationId(organizationId);
    }

    /**
     * Vérifie si une organisation peut créer une nouvelle agence
     */
    public Mono<Boolean> canCreateAgency(UUID organizationId) {
        return subscriptionValidationService.validateAgencyCreationLimit(organizationId);
    }

    /**
     * Trouve les agences avec réservation en ligne
     */
    public Flux<AgencyResponse> findAgenciesWithOnlineBooking() {
        return agencyRepository.findAgenciesWithOnlineBooking()
            .map(agencyMapper::toResponse);
    }

    /**
     * Trouve les agences dans une zone géographique
     */
    public Flux<AgencyResponse> findAgenciesInBounds(double minLat, double maxLat, double minLng, double maxLng) {
        return agencyRepository.findAgenciesInBounds(minLat, maxLat, minLng, maxLng)
            .map(agencyMapper::toResponse);
    }

    /**
     * Obtient les statistiques d'une agence
     */
    public Mono<AgencyStatisticsResponse> getAgencyStatistics(UUID agencyId) {
        return agencyRepository.findById(agencyId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée")))
            .map(this::buildAgencyStatistics);
    }

    // === MÉTHODES PRIVÉES ===

    /**
     * Valide la création d'une agence
     */
    private Mono<Void> validateAgencyCreation(UUID organizationId) {
        return subscriptionValidationService.validateAgencyCreationLimit(organizationId)
            .flatMap(canCreate -> {
                if (!canCreate) {
                    return Mono.error(new IllegalArgumentException(
                        "Limite d'agences atteinte pour cette organisation"));
                }
                return Mono.empty();
            });
    }

    /**
     * Vérifie l'unicité du nom d'agence dans l'organisation
     */
    private Mono<Void> checkAgencyNameUniqueness(UUID organizationId, String name) {
        return agencyRepository.countByOrganizationIdAndName(organizationId, name)
            .flatMap(count -> {
                if (count > 0) {
                    return Mono.error(new IllegalArgumentException(
                        "Une agence avec ce nom existe déjà dans cette organisation"));
                }
                return Mono.empty();
            });
    }

    /**
     * Crée une agence à partir de la requête
     */
    private Agency createAgencyFromRequest(CreateAgencyRequest request, UUID createdBy) {
        Agency agency = new Agency(
            request.getOrganizationId(),
            request.getName(),
            request.getAddress(),
            request.getCity()
        );

        // Informations de base
        agency.setDescription(request.getDescription());
        agency.setCountry(request.getCountry() != null ? request.getCountry() : "CM");
        agency.setPostalCode(request.getPostalCode());
        agency.setRegion(request.getRegion());
        agency.setPhone(request.getPhone());
        agency.setEmail(request.getEmail());

        // Géolocalisation
        if (request.getLatitude() != null && request.getLongitude() != null) {
            agency.setLatitude(request.getLatitude());
            agency.setLongitude(request.getLongitude());
        }

        // Géofencing
        agency.setGeofenceZoneId(request.getGeofenceZoneId());
        agency.setGeofenceRadius(request.getGeofenceRadius());

        // Configuration
        agency.setIs24Hours(request.getIs24Hours() != null ? request.getIs24Hours() : false);
        agency.setManagerId(request.getManagerId());
        agency.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Africa/Douala");
        agency.setCurrency(request.getCurrency() != null ? request.getCurrency() : "XAF");
        agency.setLanguage(request.getLanguage() != null ? request.getLanguage() : "fr");

        // Paramètres business
        if (request.getBusinessSettings() != null) {
            agency.setAllowOnlineBooking(request.getBusinessSettings().getAllowOnlineBooking());
            agency.setRequireDeposit(request.getBusinessSettings().getRequireDeposit());
            agency.setDepositPercentage(request.getBusinessSettings().getDepositPercentage());
            agency.setMinRentalHours(request.getBusinessSettings().getMinRentalHours());
            agency.setMaxAdvanceBookingDays(request.getBusinessSettings().getMaxAdvanceBookingDays());
        }

        // Horaires de travail (à stocker en JSON)
        if (request.getWorkingHours() != null) {
            agency.setWorkingHours(convertWorkingHoursToJson(request.getWorkingHours()));
        }

        return agency;
    }

    /**
     * Met à jour une agence à partir de la requête
     */
    private void updateAgencyFromRequest(Agency agency, UpdateAgencyRequest request, UUID updatedBy) {
        if (request.getName() != null) agency.setName(request.getName());
        if (request.getDescription() != null) agency.setDescription(request.getDescription());
        if (request.getAddress() != null) agency.setAddress(request.getAddress());
        if (request.getCity() != null) agency.setCity(request.getCity());
        if (request.getCountry() != null) agency.setCountry(request.getCountry());
        if (request.getPostalCode() != null) agency.setPostalCode(request.getPostalCode());
        if (request.getRegion() != null) agency.setRegion(request.getRegion());
        if (request.getPhone() != null) agency.setPhone(request.getPhone());
        if (request.getEmail() != null) agency.setEmail(request.getEmail());

        // Géolocalisation
        if (request.getLatitude() != null) agency.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) agency.setLongitude(request.getLongitude());

        // Configuration
        if (request.getIs24Hours() != null) agency.setIs24Hours(request.getIs24Hours());
        if (request.getManagerId() != null) agency.setManagerId(request.getManagerId());
        if (request.getTimezone() != null) agency.setTimezone(request.getTimezone());

        // Paramètres business
        if (request.getBusinessSettings() != null) {
            if (request.getBusinessSettings().getAllowOnlineBooking() != null) {
                agency.setAllowOnlineBooking(request.getBusinessSettings().getAllowOnlineBooking());
            }
            if (request.getBusinessSettings().getRequireDeposit() != null) {
                agency.setRequireDeposit(request.getBusinessSettings().getRequireDeposit());
            }
            if (request.getBusinessSettings().getDepositPercentage() != null) {
                agency.setDepositPercentage(request.getBusinessSettings().getDepositPercentage());
            }
        }

        // Horaires de travail
        if (request.getWorkingHours() != null) {
            agency.setWorkingHours(convertWorkingHoursToJson(request.getWorkingHours()));
        }

        agency.preUpdate();
    }

    /**
     * Valide la suppression d'une agence
     */
    private Mono<Void> validateAgencyDeletion(UUID agencyId) {
        // Vérifier qu'il n'y a pas de véhicules actifs
        // Vérifier qu'il n'y a pas de chauffeurs actifs
        // Vérifier qu'il n'y a pas de locations en cours
        // TODO: Implémenter les vérifications avec les autres services
        return Mono.empty();
    }

    /**
     * Construit les statistiques d'une agence
     */
    private AgencyStatisticsResponse buildAgencyStatistics(Agency agency) {
        return AgencyStatisticsResponse.builder()
            .agencyId(agency.getId())
            .totalVehicles(agency.getTotalVehicles())
            .activeVehicles(agency.getActiveVehicles())
            .totalDrivers(agency.getTotalDrivers())
            .activeDrivers(agency.getActiveDrivers())
            .totalPersonnel(agency.getTotalPersonnel())
            .totalRentals(agency.getTotalRentals())
            .monthlyRevenue(agency.getMonthlyRevenue())
            .vehicleUtilizationRate(agency.getVehicleUtilizationRate())
            .driverActivityRate(agency.getDriverActivityRate())
            .lastUpdated(agency.getUpdatedAt())
            .build();
    }

    /**
     * Convertit les horaires de travail en JSON
     */
    private String convertWorkingHoursToJson(Object workingHours) {
        // TODO: Implémenter la conversion JSON
        // Utiliser ObjectMapper pour convertir en JSON
        return "{}"; // Placeholder
    }
}
