package inc.yowyob.rental_api_reactive.persistence.mapper.vehicle;

// import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.CreateVehicleBrandRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.UpdateVehicleBrandRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.VehicleBrandResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.vehicle.VehicleBrand;

import org.springframework.stereotype.Component;

/**
 * Mapper pour les marques de véhicules
 */
@Component
public class VehicleBrandMapper {

    /**
     * Convertit une entité VehicleBrand en VehicleBrandResponse
     */
    public VehicleBrandResponse toResponse(VehicleBrand vehicleBrand) {
        if (vehicleBrand == null) {
            return null;
        }

        return VehicleBrandResponse.builder()
            .id(vehicleBrand.getId())
            .name(vehicleBrand.getName())
            .logoUrl(vehicleBrand.getLogoUrl())
            .countryOrigin(vehicleBrand.getCountryOrigin())
            .isActive(vehicleBrand.getIsActive())
            .createdAt(vehicleBrand.getCreatedAt())
            .updatedAt(vehicleBrand.getUpdatedAt())
            .build();
    }

    /**
     * Convertit une CreateVehicleBrandRequest en VehicleBrand
     */
    public VehicleBrand toEntity(CreateVehicleBrandRequest request) {
        if (request == null) {
            return null;
        }

        VehicleBrand vehicleBrand = new VehicleBrand();
        vehicleBrand.setName(request.getName());
        vehicleBrand.setLogoUrl(request.getLogoUrl());
        vehicleBrand.setCountryOrigin(request.getCountryOrigin());
        vehicleBrand.prePersist();

        return vehicleBrand;
    }

    /**
     * Met à jour une entité VehicleBrand à partir d'une UpdateVehicleBrandRequest
     */
    public void updateEntity(VehicleBrand vehicleBrand, UpdateVehicleBrandRequest request) {
        if (vehicleBrand == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            vehicleBrand.setName(request.getName());
        }
        if (request.getLogoUrl() != null) {
            vehicleBrand.setLogoUrl(request.getLogoUrl());
        }
        if (request.getCountryOrigin() != null) {
            vehicleBrand.setCountryOrigin(request.getCountryOrigin());
        }
        if (request.getIsActive() != null) {
            vehicleBrand.setIsActive(request.getIsActive());
        }

        vehicleBrand.preUpdate();
    }
}
