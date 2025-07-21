package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.*;
import inc.yowyob.rental_api_reactive.persistence.repository.*;
import inc.yowyob.rental_api_reactive.persistence.mapper.VehicleMapper;
import inc.yowyob.rental_api_reactive.application.dto.VehicleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service réactif pour la gestion des véhicules
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleReactiveService {

    private final VehicleReactiveRepository vehicleRepository;
    private final VehicleBrandReactiveRepository vehicleBrandRepository;
    private final VehicleImageReactiveRepository vehicleImageRepository;
    private final OrganizationReactiveRepository organizationRepository;
    private final AgencyReactiveRepository agencyRepository;
    private final VehicleMapper vehicleMapper;
    private final MultiTenantReactiveService multiTenantService;
    private final SubscriptionValidationReactiveService subscriptionValidationService;

    /**
     * Crée un nouveau véhicule
     */
    public Mono<VehicleResponse> createVehicle(CreateVehicleRequest createRequest, UUID createdBy) {
        log.info("Creating vehicle: {} {} for organization: {}",
            createRequest.getBrandId(), createRequest.getModel(), createRequest.getOrganizationId());

        return validateVehicleCreation(createRequest)
            .then(createVehicleFromRequest(createRequest, createdBy))
            .flatMap(vehicleRepository::save)
            .flatMap(this::enrichVehicleResponse)
            .doOnSuccess(response -> log.info("Vehicle created successfully: {}", response.getId()))
            .doOnError(error -> log.error("Error creating vehicle: {}", error.getMessage()));
    }

    /**
     * Met à jour un véhicule
     */
    public Mono<VehicleResponse> updateVehicle(UUID vehicleId, UpdateVehicleRequest updateRequest, UUID updatedBy) {
        log.info("Updating vehicle: {}", vehicleId);

        return vehicleRepository.findById(vehicleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule non trouvé")))
            .filter(vehicle -> !vehicle.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule supprimé")))
            .flatMap(existingVehicle -> multiTenantService.validateOrganizationAccess(existingVehicle.getOrganizationId(), updatedBy)
                .then(Mono.just(existingVehicle)))
            .flatMap(existingVehicle -> {
                if (updateRequest.getLicensePlate() != null &&
                    !updateRequest.getLicensePlate().equals(existingVehicle.getLicensePlate())) {
                    return validateLicensePlate(updateRequest.getLicensePlate())
                        .then(Mono.just(existingVehicle));
                }
                return Mono.just(existingVehicle);
            })
            .flatMap(existingVehicle -> {
                updateVehicleFromRequest(existingVehicle, updateRequest, updatedBy);
                return vehicleRepository.save(existingVehicle);
            })
            .flatMap(this::enrichVehicleResponse)
            .doOnSuccess(response -> log.info("Vehicle updated successfully: {}", response.getId()));
    }

    /**
     * Change le statut d'un véhicule
     */
    public Mono<VehicleResponse> updateVehicleStatus(UUID vehicleId, UpdateVehicleStatusRequest statusRequest, UUID updatedBy) {
        log.info("Updating vehicle status: {} to {}", vehicleId, statusRequest.getStatus());

        return vehicleRepository.findById(vehicleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule non trouvé")))
            .filter(vehicle -> !vehicle.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule supprimé")))
            .flatMap(vehicle -> multiTenantService.validateOrganizationAccess(vehicle.getOrganizationId(), updatedBy)
                .then(Mono.just(vehicle)))
            .flatMap(vehicle -> {
                vehicle.changeStatus(statusRequest.getStatus());
                if (statusRequest.getNotes() != null) {
                    vehicle.setNotes(statusRequest.getNotes());
                }
                vehicle.setUpdatedBy(updatedBy);
                return vehicleRepository.save(vehicle);
            })
            .flatMap(this::enrichVehicleResponse)
            .doOnSuccess(response -> log.info("Vehicle status updated successfully: {}", response.getId()));
    }

    /**
     * Récupère un véhicule par ID
     */
    public Mono<VehicleResponse> getVehicleById(UUID vehicleId, UUID requestingUserId) {
        log.debug("Fetching vehicle: {}", vehicleId);

        return vehicleRepository.findById(vehicleId)
            .filter(vehicle -> !vehicle.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule non trouvé")))
            .flatMap(vehicle -> multiTenantService.validateOrganizationAccess(vehicle.getOrganizationId(), requestingUserId)
                .then(Mono.just(vehicle)))
            .flatMap(this::enrichVehicleResponse);
    }

    /**
     * Récupère les véhicules par organisation
     */
    public Flux<VehicleResponse> getVehiclesByOrganization(UUID organizationId, Boolean activeOnly, UUID requestingUserId) {
        log.debug("Fetching vehicles for organization: {}, activeOnly: {}", organizationId, activeOnly);

        return multiTenantService.validateOrganizationAccess(organizationId, requestingUserId)
            .thenMany(activeOnly != null && activeOnly
                ? vehicleRepository.findActiveByOrganizationId(organizationId)
                : vehicleRepository.findByOrganizationId(organizationId))
            .flatMap(this::enrichVehicleResponse);
    }

    /**
     * Récupère les véhicules par agence
     */
    public Flux<VehicleResponse> getVehiclesByAgency(UUID agencyId, Boolean activeOnly, UUID requestingUserId) {
        log.debug("Fetching vehicles for agency: {}, activeOnly: {}", agencyId, activeOnly);

        return agencyRepository.findById(agencyId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée")))
            .flatMap(agency -> multiTenantService.validateOrganizationAccess(agency.getOrganizationId(), requestingUserId)
                .then(Mono.just(agency)))
            .thenMany(activeOnly != null && activeOnly
                ? vehicleRepository.findActiveByAgencyId(agencyId)
                : vehicleRepository.findByAgencyId(agencyId))
            .flatMap(this::enrichVehicleResponse);
    }

    /**
     * Récupère les véhicules disponibles
     */
    public Flux<VehicleResponse> getAvailableVehicles(UUID organizationId, UUID agencyId, UUID requestingUserId) {
        log.debug("Fetching available vehicles for organization: {}, agency: {}", organizationId, agencyId);

        Mono<Void> accessValidation = organizationId != null
            ? multiTenantService.validateOrganizationAccess(organizationId, requestingUserId)
            : Mono.empty();

        return accessValidation
            .thenMany(agencyId != null
                ? vehicleRepository.findAvailableByAgencyId(agencyId)
                : organizationId != null
                ? vehicleRepository.findAvailableByOrganizationId(organizationId)
                : vehicleRepository.findByStatus(VehicleStatus.AVAILABLE))
            .flatMap(this::enrichVehicleResponse);
    }

    /**
     * Recherche avancée de véhicules
     */
    public Flux<VehicleResponse> searchVehicles(VehicleSearchRequest searchRequest, UUID requestingUserId) {
        log.debug("Advanced vehicle search with criteria: {}", searchRequest);

        return multiTenantService.validateOrganizationAccess(searchRequest.getOrganizationId(), requestingUserId)
            .thenMany(performAdvancedSearch(searchRequest))
            .flatMap(this::enrichVehicleResponse);
    }

    /**
     * Supprime un véhicule (suppression logique)
     */
    public Mono<Void> deleteVehicle(UUID vehicleId, UUID deletedBy) {
        log.info("Deleting vehicle: {}", vehicleId);

        return vehicleRepository.findById(vehicleId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule non trouvé")))
            .filter(vehicle -> !vehicle.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule déjà supprimé")))
            .flatMap(vehicle -> multiTenantService.validateOrganizationAccess(vehicle.getOrganizationId(), deletedBy)
                .then(Mono.just(vehicle)))
            .flatMap(vehicle -> {
                vehicle.setIsDeleted(true);
                vehicle.setIsActive(false);
                vehicle.setUpdatedBy(deletedBy);
                vehicle.preUpdate();
                return vehicleRepository.save(vehicle);
            })
            .then()
            .doOnSuccess(v -> log.info("Vehicle deleted successfully: {}", vehicleId));
    }

    /**
     * Valide la création d'un véhicule
     */
    private Mono<Void> validateVehicleCreation(CreateVehicleRequest createRequest) {
        return Mono.when(
            validateBrandExists(createRequest.getBrandId()),
            validateLicensePlate(createRequest.getLicensePlate()),
            validateOrganizationExists(createRequest.getOrganizationId()),
            validateAgencyIfProvided(createRequest.getAgencyId(), createRequest.getOrganizationId())
        );
    }

    /**
     * Valide qu'une marque existe
     */
    private Mono<Void> validateBrandExists(UUID brandId) {
        return vehicleBrandRepository.findById(brandId)
            .filter(brand -> brand.getIsActive() && !brand.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Marque de véhicule non trouvée ou inactive")))
            .then();
    }

    /**
     * Valide l'unicité de la plaque d'immatriculation
     */
    private Mono<Void> validateLicensePlate(String licensePlate) {
        return vehicleRepository.existsByLicensePlate(licensePlate)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException("Un véhicule avec cette plaque d'immatriculation existe déjà"));
                }
                return Mono.empty();
            });
    }

    /**
     * Valide qu'une organisation existe
     */
    private Mono<Void> validateOrganizationExists(UUID organizationId) {
        return organizationRepository.findById(organizationId)
            .filter(org -> org.getIsActive())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée ou inactive")))
            .then();
    }

    /**
     * Valide une agence si fournie
     */
    private Mono<Void> validateAgencyIfProvided(UUID agencyId, UUID organizationId) {
        if (agencyId == null) {
            return Mono.empty();
        }

        return agencyRepository.findById(agencyId)
            .filter(agency -> agency.getIsActive())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Agence non trouvée ou inactive")))
            .filter(agency -> agency.getOrganizationId().equals(organizationId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("L'agence n'appartient pas à cette organisation")))
            .then();
    }

    /**
     * Effectue une recherche avancée
     */
    private Flux<Vehicle> performAdvancedSearch(VehicleSearchRequest searchRequest) {
        // Implémentation simplifiée - dans un vrai projet, utiliser Elasticsearch ou des requêtes CQL plus complexes
        Flux<Vehicle> results = vehicleRepository.findByOrganizationId(searchRequest.getOrganizationId());

        if (searchRequest.getAgencyId() != null) {
            results = results.filter(vehicle -> searchRequest.getAgencyId().equals(vehicle.getAgencyId()));
        }

        if (searchRequest.getBrandIds() != null && !searchRequest.getBrandIds().isEmpty()) {
            results = results.filter(vehicle -> searchRequest.getBrandIds().contains(vehicle.getBrandId()));
        }

        if (searchRequest.getVehicleTypes() != null && !searchRequest.getVehicleTypes().isEmpty()) {
            results = results.filter(vehicle -> searchRequest.getVehicleTypes().contains(vehicle.getVehicleType()));
        }

        if (searchRequest.getStatuses() != null && !searchRequest.getStatuses().isEmpty()) {
            results = results.filter(vehicle -> searchRequest.getStatuses().contains(vehicle.getStatus()));
        }

        if (Boolean.TRUE.equals(searchRequest.getAvailableOnly())) {
            results = results.filter(Vehicle::isAvailableForRental);
        }

        if (Boolean.TRUE.equals(searchRequest.getIsActive())) {
            results = results.filter(vehicle -> vehicle.getIsActive() && !vehicle.getIsDeleted());
        }

        return results;
    }

    /**
     * Enrichit la réponse véhicule avec les données liées
     */
    private Mono<VehicleResponse> enrichVehicleResponse(Vehicle vehicle) {
        return Mono.zip(
            getBrandName(vehicle.getBrandId()),
            getAgencyName(vehicle.getAgencyId()),
            getVehicleImages(vehicle.getId())
        ).map(tuple -> {
            String brandName = tuple.getT1();
            String agencyName = tuple.getT2();
            java.util.List<VehicleImageResponse> images = tuple.getT3();

            VehicleResponse response = vehicleMapper.toResponse(vehicle);
            response.setBrandName(brandName);
            response.setAgencyName(agencyName);
            response.setImages(images);
            response.setFullName(String.format("%s %s (%d)", brandName, vehicle.getModel(), vehicle.getYear()));
            response.setIsAvailableForRental(vehicle.isAvailableForRental());

            // URL de l'image principale
            images.stream()
                .filter(VehicleImageResponse::getIsPrimary)
                .findFirst()
                .ifPresent(primaryImage -> response.setPrimaryImageUrl(primaryImage.getImageUrl()));

            return response;
        });
    }

    /**
     * Récupère le nom de la marque
     */
    private Mono<String> getBrandName(UUID brandId) {
        if (brandId == null) return Mono.just("");

        return vehicleBrandRepository.findById(brandId)
            .map(VehicleBrand::getName)
            .defaultIfEmpty("Marque inconnue");
    }

    /**
     * Récupère le nom de l'agence
     */
    private Mono<String> getAgencyName(UUID agencyId) {
        if (agencyId == null) return Mono.just("");

        return agencyRepository.findById(agencyId)
            .map(Agency::getName)
            .defaultIfEmpty("");
    }

    /**
     * Récupère les images du véhicule
     */
    private Mono<java.util.List<VehicleImageResponse>> getVehicleImages(UUID vehicleId) {
        return vehicleImageRepository.findActiveByVehicleId(vehicleId)
            .map(this::mapImageToResponse)
            .collectList();
    }

    /**
     * Mappe une image vers sa réponse
     */
    private VehicleImageResponse mapImageToResponse(VehicleImage image) {
        return VehicleImageResponse.builder()
            .id(image.getId())
            .vehicleId(image.getVehicleId())
            .imageUrl(image.getEncryptedUrl()) // Dans un vrai projet, déchiffrer ici
            .imageType(image.getImageType())
            .fileSize(image.getFileSize())
            .mimeType(image.getMimeType())
            .width(image.getWidth())
            .height(image.getHeight())
            .isPrimary(image.getIsPrimary())
            .displayOrder(image.getDisplayOrder())
            .createdAt(image.getCreatedAt())
            .build();
    }

    /**
     * Crée un véhicule à partir de la requête
     */
    private Mono<Vehicle> createVehicleFromRequest(CreateVehicleRequest createRequest, UUID createdBy) {
        return Mono.fromCallable(() -> {
            Vehicle vehicle = new Vehicle();
            vehicle.setBrandId(createRequest.getBrandId());
            vehicle.setModel(createRequest.getModel());
            vehicle.setYear(createRequest.getYear());
            vehicle.setLicensePlate(createRequest.getLicensePlate());
            vehicle.setVehicleType(createRequest.getVehicleType());
            vehicle.setColor(createRequest.getColor());
            vehicle.setFuelType(createRequest.getFuelType());
            vehicle.setTransmissionType(createRequest.getTransmissionType());
            vehicle.setNumberOfSeats(createRequest.getNumberOfSeats());
            vehicle.setNumberOfDoors(createRequest.getNumberOfDoors());
            vehicle.setEngineCapacity(createRequest.getEngineCapacity());
            vehicle.setMileage(createRequest.getMileage());
            vehicle.setOrganizationId(createRequest.getOrganizationId());
            vehicle.setAgencyId(createRequest.getAgencyId());
            vehicle.setDailyPrice(createRequest.getDailyPrice());
            vehicle.setHourlyPrice(createRequest.getHourlyPrice());
            vehicle.setDescription(createRequest.getDescription());
            vehicle.setFeatures(createRequest.getFeatures());
            vehicle.setNotes(createRequest.getNotes());
            vehicle.setLastMaintenanceDate(createRequest.getLastMaintenanceDate());
            vehicle.setNextMaintenanceDate(createRequest.getNextMaintenanceDate());
            vehicle.setMaintenanceNotes(createRequest.getMaintenanceNotes());
            vehicle.setCreatedBy(createdBy);
            vehicle.setUpdatedBy(createdBy);
            vehicle.prePersist();
            return vehicle;
        });
    }

    /**
     * Met à jour un véhicule à partir de la requête
     */
    private void updateVehicleFromRequest(Vehicle existingVehicle, UpdateVehicleRequest updateRequest, UUID updatedBy) {
        if (updateRequest.getBrandId() != null) {
            existingVehicle.setBrandId(updateRequest.getBrandId());
        }
        if (updateRequest.getModel() != null) {
            existingVehicle.setModel(updateRequest.getModel());
        }
        if (updateRequest.getYear() != null) {
            existingVehicle.setYear(updateRequest.getYear());
        }
        if (updateRequest.getLicensePlate() != null) {
            existingVehicle.setLicensePlate(updateRequest.getLicensePlate());
        }
        if (updateRequest.getVehicleType() != null) {
            existingVehicle.setVehicleType(updateRequest.getVehicleType());
        }
        if (updateRequest.getColor() != null) {
            existingVehicle.setColor(updateRequest.getColor());
        }
        if (updateRequest.getFuelType() != null) {
            existingVehicle.setFuelType(updateRequest.getFuelType());
        }
        if (updateRequest.getTransmissionType() != null) {
            existingVehicle.setTransmissionType(updateRequest.getTransmissionType());
        }
        if (updateRequest.getNumberOfSeats() != null) {
            existingVehicle.setNumberOfSeats(updateRequest.getNumberOfSeats());
        }
        if (updateRequest.getNumberOfDoors() != null) {
            existingVehicle.setNumberOfDoors(updateRequest.getNumberOfDoors());
        }
        if (updateRequest.getEngineCapacity() != null) {
            existingVehicle.setEngineCapacity(updateRequest.getEngineCapacity());
        }
        if (updateRequest.getMileage() != null) {
            existingVehicle.setMileage(updateRequest.getMileage());
        }
        if (updateRequest.getAgencyId() != null) {
            existingVehicle.setAgencyId(updateRequest.getAgencyId());
        }
        if (updateRequest.getDailyPrice() != null) {
            existingVehicle.setDailyPrice(updateRequest.getDailyPrice());
        }
        if (updateRequest.getHourlyPrice() != null) {
            existingVehicle.setHourlyPrice(updateRequest.getHourlyPrice());
        }
        if (updateRequest.getDescription() != null) {
            existingVehicle.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getFeatures() != null) {
            existingVehicle.setFeatures(updateRequest.getFeatures());
        }
        if (updateRequest.getNotes() != null) {
            existingVehicle.setNotes(updateRequest.getNotes());
        }
        if (updateRequest.getLastMaintenanceDate() != null) {
            existingVehicle.setLastMaintenanceDate(updateRequest.getLastMaintenanceDate());
        }
        if (updateRequest.getNextMaintenanceDate() != null) {
            existingVehicle.setNextMaintenanceDate(updateRequest.getNextMaintenanceDate());
        }
        if (updateRequest.getMaintenanceNotes() != null) {
            existingVehicle.setMaintenanceNotes(updateRequest.getMaintenanceNotes());
        }
        if (updateRequest.getIsActive() != null) {
            existingVehicle.setIsActive(updateRequest.getIsActive());
        }
        existingVehicle.setUpdatedBy(updatedBy);
        existingVehicle.preUpdate();
    }
}
