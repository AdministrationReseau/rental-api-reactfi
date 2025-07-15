package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import inc.yowyob.rental_api_reactive.persistence.repository.OrganizationReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.AgencyReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.mapper.OrganizationMapper;
import inc.yowyob.rental_api_reactive.application.dto.OrganizationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service réactif pour la gestion des organisations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationReactiveService {

    private final OrganizationReactiveRepository organizationRepository;
    private final AgencyReactiveRepository agencyRepository;
    private final OrganizationMapper organizationMapper;
    private final MultiTenantReactiveService multiTenantService;
    private final SubscriptionValidationReactiveService subscriptionValidationService;

    /**
     * Crée une nouvelle organisation
     */
    public Mono<OrganizationResponse> createOrganization(CreateOrganizationRequest createRequest, UUID createdBy) {
        log.info("Creating organization: {} for owner: {}", createRequest.getName(), createRequest.getOwnerId());

        return validateOrganizationCreation(createRequest)
            .then(createOrganizationFromRequest(createRequest, createdBy))
            .flatMap(organizationRepository::save)
            .map(organizationMapper::toResponse)
            .doOnSuccess(response -> log.info("Organization created successfully: {}", response.getId()))
            .doOnError(error -> log.error("Error creating organization: {}", error.getMessage()));
    }

    /**
     * Met à jour une organisation
     */
    public Mono<OrganizationResponse> updateOrganization(UUID organizationId, UpdateOrganizationRequest updateRequest, UUID updatedBy) {
        log.info("Updating organization: {}", organizationId);

        return multiTenantService.validateOrganizationAccess(organizationId, updatedBy)
            .then(organizationRepository.findById(organizationId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                updateOrganizationFromRequest(organization, updateRequest, updatedBy);
                return organizationRepository.save(organization);
            })
            .map(organizationMapper::toResponse)
            .doOnSuccess(response -> log.info("Organization updated successfully: {}", response.getId()))
            .doOnError(error -> log.error("Error updating organization {}: {}", organizationId, error.getMessage()));
    }

    /**
     * Trouve toutes les organisations (avec filtrage multi-tenant)
     */
    public Flux<OrganizationResponse> findAll(UUID requesterId) {
        log.debug("Finding all organizations for user: {}", requesterId);

        return multiTenantService.getFilterOrganizationId(requesterId)
            .flatMapMany(orgFilter -> {
                if (orgFilter == null) {
                    // Super admin voit toutes les organisations
                    return organizationRepository.findAll();
                } else {
                    // Utilisateur normal voit seulement son organisation
                    return organizationRepository.findById(orgFilter).flux();
                }
            })
            .map(organizationMapper::toResponse)
            .doOnNext(org -> log.debug("Found organization: {}", org.getName()));
    }

    /**
     * Trouve une organisation par ID
     */
    public Mono<OrganizationResponse> findById(UUID id, UUID requesterId) {
        log.debug("Finding organization by ID: {} for user: {}", id, requesterId);

        return multiTenantService.validateOrganizationAccess(id, requesterId)
            .then(organizationRepository.findById(id))
            .map(organizationMapper::toResponse)
            .doOnNext(org -> log.debug("Found organization: {}", org.getName()));
    }

    /**
     * Trouve les organisations actives
     */
    public Flux<OrganizationResponse> findAllActive(UUID requesterId) {
        log.debug("Finding active organizations for user: {}", requesterId);

        return multiTenantService.getFilterOrganizationId(requesterId)
            .flatMapMany(orgFilter -> {
                if (orgFilter == null) {
                    return organizationRepository.findAllActive();
                } else {
                    return organizationRepository.findById(orgFilter)
                        .filter(org -> org.getIsActive())
                        .flux();
                }
            })
            .map(organizationMapper::toResponse)
            .doOnNext(org -> log.debug("Found active organization: {}", org.getName()));
    }

    /**
     * Vérifie si un nom d'organisation existe
     */
    public Mono<Boolean> existsByName(String name) {
        return organizationRepository.existsByName(name);
    }

    /**
     * Supprime une organisation (soft delete)
     */
    public Mono<Void> deleteById(UUID organizationId, UUID deletedBy) {
        log.info("Deleting organization: {}", organizationId);

        return multiTenantService.validateOrganizationAccess(organizationId, deletedBy)
            .then(organizationRepository.findById(organizationId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> validateOrganizationDeletion(organizationId)
                .then(Mono.fromRunnable(() -> {
                    organization.setIsActive(false);
                }))
                .then(organizationRepository.save(organization))
                .then())
            .doOnSuccess(v -> log.info("Organization deleted successfully: {}", organizationId))
            .doOnError(error -> log.error("Error deleting organization {}: {}", organizationId, error.getMessage()));
    }

    /**
     * Active/désactive une organisation
     */
    public Mono<OrganizationResponse> toggleOrganizationStatus(UUID organizationId, boolean isActive, UUID updatedBy) {
        log.info("Toggling organization status: {} to {}", organizationId, isActive);

        return multiTenantService.validateOrganizationAccess(organizationId, updatedBy)
            .then(organizationRepository.findById(organizationId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                organization.setIsActive(isActive);
                organization.preUpdate();
                return organizationRepository.save(organization);
            })
            .map(organizationMapper::toResponse)
            .doOnSuccess(response -> log.info("Organization status toggled: {} - {}",
                response.getId(), response.getIsActive()));
    }

    /**
     * Vérifie une organisation
     */
    public Mono<OrganizationResponse> verifyOrganization(UUID organizationId, UUID verifiedBy) {
        log.info("Verifying organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                organization.verify(verifiedBy);
                return organizationRepository.save(organization);
            })
            .map(organizationMapper::toResponse)
            .doOnSuccess(response -> log.info("Organization verified: {}", response.getId()));
    }

    /**
     * Met à jour l'abonnement d'une organisation
     */
    public Mono<OrganizationResponse> updateSubscription(UUID organizationId, UpdateSubscriptionRequest request, UUID updatedBy) {
        log.info("Updating subscription for organization: {}", organizationId);

        return multiTenantService.validateOrganizationAccess(organizationId, updatedBy)
            .then(organizationRepository.findById(organizationId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                organization.updateSubscription(
                    request.getSubscriptionPlanId(),
                    request.getExpiresAt(),
                    request.getAutoRenew()
                );
                return organizationRepository.save(organization);
            })
            .map(organizationMapper::toResponse)
            .doOnSuccess(response -> log.info("Subscription updated for organization: {}", response.getId()));
    }

    /**
     * Met à jour les statistiques d'une organisation
     */
    public Mono<OrganizationResponse> updateOrganizationStatistics(UUID organizationId, OrganizationStatisticsRequest statsRequest) {
        log.debug("Updating statistics for organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                if (statsRequest.getResourceCounters() != null) {
                    organization.updateResourceCounters(
                        statsRequest.getResourceCounters().getAgencies(),
                        statsRequest.getResourceCounters().getVehicles(),
                        statsRequest.getResourceCounters().getDrivers(),
                        statsRequest.getResourceCounters().getUsers()
                    );
                }

                if (statsRequest.getFinancialStats() != null) {
                    organization.updateFinancialStats(
                        statsRequest.getFinancialStats().getMonthlyRevenue(),
                        statsRequest.getFinancialStats().getYearlyRevenue(),
                        statsRequest.getFinancialStats().getTotalRentals()
                    );
                }

                return organizationRepository.save(organization);
            })
            .map(organizationMapper::toResponse)
            .doOnSuccess(response -> log.debug("Statistics updated for organization: {}", response.getId()));
    }

    /**
     * Trouve les organisations par type
     */
    public Flux<OrganizationResponse> findByType(OrganizationType type, UUID requesterId) {
        return multiTenantService.getFilterOrganizationId(requesterId)
            .flatMapMany(orgFilter -> {
                if (orgFilter == null) {
                    return organizationRepository.findByOrganizationType(type);
                } else {
                    return organizationRepository.findById(orgFilter)
                        .filter(org -> org.getOrganizationType() == type)
                        .flux();
                }
            })
            .map(organizationMapper::toResponse);
    }

    /**
     * Trouve les organisations par ville et pays
     */
    public Flux<OrganizationResponse> findByCityAndCountry(String city, String country, UUID requesterId) {
        return multiTenantService.getFilterOrganizationId(requesterId)
            .flatMapMany(orgFilter -> {
                if (orgFilter == null) {
                    return organizationRepository.findByCityAndCountry(city, country);
                } else {
                    return organizationRepository.findById(orgFilter)
                        .filter(org -> city.equals(org.getCity()) && country.equals(org.getCountry()))
                        .flux();
                }
            })
            .map(organizationMapper::toResponse);
    }

    /**
     * Obtient les statistiques complètes d'une organisation
     */
    public Mono<OrganizationStatisticsResponse> getOrganizationStatistics(UUID organizationId, UUID requesterId) {
        return multiTenantService.validateOrganizationAccess(organizationId, requesterId)
            .then(organizationRepository.findById(organizationId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(this::buildOrganizationStatistics);
    }

    /**
     * Trouve les organisations nécessitant une attention
     */
    public Flux<OrganizationResponse> findOrganizationsNeedingAttention(UUID requesterId) {
        return multiTenantService.getFilterOrganizationId(requesterId)
            .flatMapMany(orgFilter -> {
                if (orgFilter == null) {
                    return organizationRepository.findOrganizationsNeedingAttention();
                } else {
                    return organizationRepository.findById(orgFilter)
                        .filter(org -> !org.getIsVerified() || org.isSubscriptionExpiringSoon())
                        .flux();
                }
            })
            .map(organizationMapper::toResponse);
    }

    // === MÉTHODES PRIVÉES ===

    /**
     * Valide la création d'une organisation
     */
    private Mono<Void> validateOrganizationCreation(CreateOrganizationRequest request) {
        return organizationRepository.existsByName(request.getName())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException("Une organisation avec ce nom existe déjà"));
                }
                return Mono.empty();
            })
            .then(validateRegistrationNumber(request.getRegistrationNumber()))
            .then(validateTaxNumber(request.getTaxNumber()));
    }

    /**
     * Valide le numéro d'enregistrement
     */
    private Mono<Void> validateRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null) return Mono.empty();

        return organizationRepository.existsByRegistrationNumber(registrationNumber)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException("Ce numéro d'enregistrement existe déjà"));
                }
                return Mono.empty();
            });
    }

    /**
     * Valide le numéro fiscal
     */
    private Mono<Void> validateTaxNumber(String taxNumber) {
        if (taxNumber == null) return Mono.empty();

        return organizationRepository.existsByTaxNumber(taxNumber)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException("Ce numéro fiscal existe déjà"));
                }
                return Mono.empty();
            });
    }

    /**
     * Crée une organisation à partir de la requête
     */
    private Mono<Organization> createOrganizationFromRequest(CreateOrganizationRequest request, UUID createdBy) {
        Organization organization = new Organization(
            request.getName(),
            request.getOrganizationType(),
            request.getOwnerId()
        );

        // Informations de base
        organization.setDescription(request.getDescription());

        // Informations légales
        organization.setRegistrationNumber(request.getRegistrationNumber());
        organization.setTaxNumber(request.getTaxNumber());
        organization.setBusinessLicense(request.getBusinessLicense());

        // Adresse
        organization.setAddress(request.getAddress());
        organization.setCity(request.getCity());
        organization.setCountry(request.getCountry() != null ? request.getCountry() : "CM");
        organization.setPostalCode(request.getPostalCode());
        organization.setRegion(request.getRegion());

        // Contact
        organization.setPhone(request.getPhone());
        organization.setEmail(request.getEmail());
        organization.setWebsite(request.getWebsite());

        // Configuration
        organization.setCurrency(request.getCurrency() != null ? request.getCurrency() : "XAF");
        organization.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Africa/Douala");
        organization.setLanguage(request.getLanguage() != null ? request.getLanguage() : "fr");

        // Limites selon le type d'organisation
        OrganizationType.OrganizationLimits limits = request.getOrganizationType().getDefaultLimits();
        organization.setMaxAgencies(limits.getMaxAgencies());
        organization.setMaxVehicles(limits.getMaxVehicles());
        organization.setMaxDrivers(limits.getMaxDrivers());
        organization.setMaxUsers(limits.getMaxUsers());

        // Branding
        organization.setPrimaryColor(request.getPrimaryColor());
        organization.setSecondaryColor(request.getSecondaryColor());

        // Politiques et paramètres (à stocker en JSON)
        if (request.getPolicies() != null) {
            organization.setPolicies(convertToJson(request.getPolicies()));
        }
        if (request.getSettings() != null) {
            organization.setSettings(convertToJson(request.getSettings()));
        }

        return Mono.just(organization);
    }

    /**
     * Met à jour une organisation à partir de la requête
     */
    private void updateOrganizationFromRequest(Organization organization, UpdateOrganizationRequest request, UUID updatedBy) {
        if (request.getName() != null) organization.setName(request.getName());
        if (request.getDescription() != null) organization.setDescription(request.getDescription());
        if (request.getOrganizationType() != null) organization.setOrganizationType(request.getOrganizationType());

        // Informations légales
        if (request.getRegistrationNumber() != null) organization.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getTaxNumber() != null) organization.setTaxNumber(request.getTaxNumber());
        if (request.getBusinessLicense() != null) organization.setBusinessLicense(request.getBusinessLicense());

        // Adresse
        if (request.getAddress() != null) organization.setAddress(request.getAddress());
        if (request.getCity() != null) organization.setCity(request.getCity());
        if (request.getCountry() != null) organization.setCountry(request.getCountry());
        if (request.getPostalCode() != null) organization.setPostalCode(request.getPostalCode());
        if (request.getRegion() != null) organization.setRegion(request.getRegion());

        // Contact
        if (request.getPhone() != null) organization.setPhone(request.getPhone());
        if (request.getEmail() != null) organization.setEmail(request.getEmail());
        if (request.getWebsite() != null) organization.setWebsite(request.getWebsite());

        // Configuration
        if (request.getCurrency() != null) organization.setCurrency(request.getCurrency());
        if (request.getTimezone() != null) organization.setTimezone(request.getTimezone());
        if (request.getLanguage() != null) organization.setLanguage(request.getLanguage());

        // Branding
        if (request.getPrimaryColor() != null) organization.setPrimaryColor(request.getPrimaryColor());
        if (request.getSecondaryColor() != null) organization.setSecondaryColor(request.getSecondaryColor());

        // Politiques et paramètres
        if (request.getPolicies() != null) {
            organization.setPolicies(convertToJson(request.getPolicies()));
        }
        if (request.getSettings() != null) {
            organization.setSettings(convertToJson(request.getSettings()));
        }

        organization.preUpdate();
    }

    /**
     * Valide la suppression d'une organisation
     */
    private Mono<Void> validateOrganizationDeletion(UUID organizationId) {
        return agencyRepository.countActiveByOrganizationId(organizationId)
            .flatMap(activeAgencies -> {
                if (activeAgencies > 0) {
                    return Mono.error(new IllegalArgumentException(
                        "Impossible de supprimer une organisation avec des agences actives"));
                }
                return Mono.empty();
            });
    }

    /**
     * Construit les statistiques complètes d'une organisation
     */
    private Mono<OrganizationStatisticsResponse> buildOrganizationStatistics(Organization organization) {
        return agencyRepository.countByOrganizationId(organization.getId())
            .zipWith(agencyRepository.countActiveByOrganizationId(organization.getId()))
            .map(tuple -> OrganizationStatisticsResponse.builder()
                .organizationId(organization.getId())
                .totalAgencies(tuple.getT1().intValue())
                .activeAgencies(tuple.getT2().intValue())
                .maxAgencies(organization.getMaxAgencies())
                .totalVehicles(organization.getCurrentVehicles())
                .maxVehicles(organization.getMaxVehicles())
                .totalDrivers(organization.getCurrentDrivers())
                .maxDrivers(organization.getMaxDrivers())
                .totalUsers(organization.getCurrentUsers())
                .maxUsers(organization.getMaxUsers())
                .monthlyRevenue(organization.getMonthlyRevenue())
                .yearlyRevenue(organization.getYearlyRevenue())
                .totalRentals(organization.getTotalRentals())
                .agencyUsagePercentage(organization.getAgencyUsagePercentage())
                .vehicleUsagePercentage(organization.getVehicleUsagePercentage())
                .driverUsagePercentage(organization.getDriverUsagePercentage())
                .userUsagePercentage(organization.getUserUsagePercentage())
                .isSubscriptionActive(organization.isSubscriptionActive())
                .subscriptionExpiresAt(organization.getSubscriptionExpiresAt())
                .lastActivityAt(organization.getLastActivityAt())
                .build());
    }

    /**
     * Convertit un objet en JSON
     */
    private String convertToJson(Object object) {
        // TODO: Implémenter la conversion JSON avec ObjectMapper
        return "{}"; // Placeholder
    }
}
