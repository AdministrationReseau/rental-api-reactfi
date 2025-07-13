package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import inc.yowyob.rental_api_reactive.persistence.repository.OrganizationReactiveRepository;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OrganizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationReactiveService {

    private final OrganizationReactiveRepository organizationRepository;

    /**
     * Trouve toutes les organisations
     */
    public Flux<OrganizationResponse> findAll() {
        log.debug("Finding all organizations");
        return organizationRepository.findAll()
            .map(this::mapToOrganizationResponse)
            .doOnNext(org -> log.debug("Found organization: {}", org.getName()));
    }

    /**
     * Trouve une organisation par ID
     */
    public Mono<OrganizationResponse> findById(UUID id) {
        log.debug("Finding organization by ID: {}", id);
        return organizationRepository.findById(id)
            .map(this::mapToOrganizationResponse)
            .doOnNext(org -> log.debug("Found organization: {}", org.getName()));
    }

    /**
     * Trouve les organisations actives
     */
    public Flux<OrganizationResponse> findAllActive() {
        log.debug("Finding all active organizations");
        return organizationRepository.findAllActive()
            .map(this::mapToOrganizationResponse)
            .doOnNext(org -> log.debug("Found active organization: {}", org.getName()));
    }

    /**
     * Vérifie si un nom d'organisation existe
     */
    public Mono<Boolean> existsByName(String name) {
        log.debug("Checking if organization name exists: {}", name);
        return organizationRepository.countByName(name)
            .map(count -> count > 0)
            .doOnNext(exists -> log.debug("Organization name {} exists: {}", name, exists));
    }

    /**
     * Sauvegarde une organisation
     */
    public Mono<OrganizationResponse> save(Organization organization) {
        log.debug("Saving organization: {}", organization.getName());
        organization.prePersist();
        return organizationRepository.save(organization)
            .map(this::mapToOrganizationResponse)
            .doOnNext(saved -> log.info("Organization saved successfully: {}", saved.getName()));
    }

    /**
     * Supprime une organisation
     */
    public Mono<Void> deleteById(UUID id) {
        log.debug("Deleting organization by ID: {}", id);
        return organizationRepository.deleteById(id)
            .doOnSuccess(v -> log.info("Organization deleted successfully: {}", id));
    }

    /**
     * Mappe une entité Organization vers OrganizationResponse
     */
    private OrganizationResponse mapToOrganizationResponse(Organization organization) {
        OrganizationResponse response = new OrganizationResponse();
        response.setId(organization.getId());
        response.setName(organization.getName());
        response.setDescription(organization.getDescription());
        response.setBusinessType(organization.getBusinessType());
        response.setRegistrationNumber(organization.getRegistrationNumber());
        response.setOwnerId(organization.getOwnerId());
        response.setContactEmail(organization.getContactEmail());
        response.setContactPhone(organization.getContactPhone());
        response.setWebsite(organization.getWebsite());
        response.setFullAddress(organization.getFullAddress());
        response.setCity(organization.getCity());
        response.setCountry(organization.getCountry());
        response.setMaxVehicles(organization.getMaxVehicles());
        response.setMaxDrivers(organization.getMaxDrivers());
        response.setMaxAgencies(organization.getMaxAgencies());
        response.setMaxUsers(organization.getMaxUsers());
        response.setCurrency(organization.getCurrency());
        response.setTimezone(organization.getTimezone());
        response.setLogoUrl(organization.getLogoUrl());
        response.setIsVerified(organization.getIsVerified());
        response.setVerificationDate(organization.getVerificationDate());
        response.setCreatedAt(organization.getCreatedAt());
        response.setIsActive(organization.getIsActive());
        return response;
    }
}
