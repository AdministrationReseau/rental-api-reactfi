package inc.yowyob.rental_api_reactive.application.service.vehicle;

// import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.CreateVehicleBrandRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.UpdateVehicleBrandRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.VehicleBrandResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.VehicleBrand;
import inc.yowyob.rental_api_reactive.persistence.mapper.vehicle.VehicleBrandMapper;
import inc.yowyob.rental_api_reactive.persistence.repository.vehicle.VehicleBrandReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service réactif pour la gestion des marques de véhicules
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleBrandReactiveService {

    private final VehicleBrandReactiveRepository vehicleBrandRepository;
    private final VehicleBrandMapper vehicleBrandMapper;

    /**
     * Crée une nouvelle marque de véhicule
     */
    public Mono<VehicleBrandResponse> createVehicleBrand(CreateVehicleBrandRequest createRequest, UUID createdBy) {
        log.info("Creating vehicle brand: {}", createRequest.getName());

        return validateBrandName(createRequest.getName())
            .then(createBrandFromRequest(createRequest, createdBy))
            .flatMap(vehicleBrandRepository::save)
            .map(vehicleBrandMapper::toResponse)
            .doOnSuccess(response -> log.info("Vehicle brand created successfully: {}", response.getId()))
            .doOnError(error -> log.error("Error creating vehicle brand: {}", error.getMessage()));
    }

    /**
     * Met à jour une marque de véhicule
     */
    public Mono<VehicleBrandResponse> updateVehicleBrand(UUID brandId, UpdateVehicleBrandRequest updateRequest, UUID updatedBy) {
        log.info("Updating vehicle brand: {}", brandId);

        return vehicleBrandRepository.findById(brandId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Marque de véhicule non trouvée")))
            .filter(brand -> !brand.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Marque de véhicule supprimée")))
            .flatMap(existingBrand -> {
                if (updateRequest.getName() != null && !updateRequest.getName().equals(existingBrand.getName())) {
                    return validateBrandName(updateRequest.getName())
                        .then(Mono.just(existingBrand));
                }
                return Mono.just(existingBrand);
            })
            .flatMap(existingBrand -> {
                updateBrandFromRequest(existingBrand, updateRequest, updatedBy);
                return vehicleBrandRepository.save(existingBrand);
            })
            .map(vehicleBrandMapper::toResponse)
            .doOnSuccess(response -> log.info("Vehicle brand updated successfully: {}", response.getId()));
    }

    /**
     * Récupère une marque par ID
     */
    public Mono<VehicleBrandResponse> getVehicleBrandById(UUID brandId) {
        log.debug("Fetching vehicle brand: {}", brandId);

        return vehicleBrandRepository.findById(brandId)
            .filter(brand -> !brand.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Marque de véhicule non trouvée")))
            .map(vehicleBrandMapper::toResponse);
    }

    /**
     * Récupère toutes les marques actives
     */
    public Flux<VehicleBrandResponse> getAllActiveBrands() {
        log.debug("Fetching all active vehicle brands");

        return vehicleBrandRepository.findAllActive()
            .map(vehicleBrandMapper::toResponse);
    }

    /**
     * Supprime une marque (suppression logique)
     */
    public Mono<Void> deleteVehicleBrand(UUID brandId, UUID deletedBy) {
        log.info("Deleting vehicle brand: {}", brandId);

        return vehicleBrandRepository.findById(brandId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Marque de véhicule non trouvée")))
            .filter(brand -> !brand.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Marque de véhicule déjà supprimée")))
            .flatMap(brand -> {
                brand.setIsDeleted(true);
                brand.setIsActive(false);
                brand.setUpdatedBy(deletedBy);
                brand.preUpdate();
                return vehicleBrandRepository.save(brand);
            })
            .then()
            .doOnSuccess(v -> log.info("Vehicle brand deleted successfully: {}", brandId));
    }

    /**
     * Valide l'unicité du nom de marque
     */
    private Mono<Void> validateBrandName(String name) {
        return vehicleBrandRepository.existsByName(name)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException("Une marque avec ce nom existe déjà"));
                }
                return Mono.empty();
            });
    }

    /**
     * Crée une entité marque à partir de la requête
     */
    private Mono<VehicleBrand> createBrandFromRequest(CreateVehicleBrandRequest createRequest, UUID createdBy) {
        return Mono.fromCallable(() -> {
            VehicleBrand brand = new VehicleBrand();
            brand.setName(createRequest.getName());
            brand.setLogoUrl(createRequest.getLogoUrl());
            brand.setCountryOrigin(createRequest.getCountryOrigin());
            brand.setCreatedBy(createdBy);
            brand.setUpdatedBy(createdBy);
            brand.prePersist();
            return brand;
        });
    }

    /**
     * Met à jour une entité marque à partir de la requête
     */
    private void updateBrandFromRequest(VehicleBrand existingBrand, UpdateVehicleBrandRequest updateRequest, UUID updatedBy) {
        if (updateRequest.getName() != null) {
            existingBrand.setName(updateRequest.getName());
        }
        if (updateRequest.getLogoUrl() != null) {
            existingBrand.setLogoUrl(updateRequest.getLogoUrl());
        }
        if (updateRequest.getCountryOrigin() != null) {
            existingBrand.setCountryOrigin(updateRequest.getCountryOrigin());
        }
        if (updateRequest.getIsActive() != null) {
            existingBrand.setIsActive(updateRequest.getIsActive());
        }
        existingBrand.setUpdatedBy(updatedBy);
        existingBrand.preUpdate();
    }
}
