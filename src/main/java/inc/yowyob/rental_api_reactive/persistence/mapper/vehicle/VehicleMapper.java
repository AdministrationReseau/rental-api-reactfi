package inc.yowyob.rental_api_reactive.persistence.mapper.vehicle;

// import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.CreateVehicleRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.UpdateVehicleRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle.VehicleResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.vehicle.Vehicle;

import org.springframework.stereotype.Component;

/**
 * Mapper pour les véhicules
 */
@Component
public class VehicleMapper {

    /**
     * Convertit une entité Vehicle en VehicleResponse
     */
    public VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleResponse.builder()
            .id(vehicle.getId())
            .brandId(vehicle.getBrandId())
            .model(vehicle.getModel())
            .year(vehicle.getYear())
            .licensePlate(vehicle.getLicensePlate())
            .vehicleType(vehicle.getVehicleType())
            .color(vehicle.getColor())
            .fuelType(vehicle.getFuelType())
            .transmissionType(vehicle.getTransmissionType())
            .numberOfSeats(vehicle.getNumberOfSeats())
            .numberOfDoors(vehicle.getNumberOfDoors())
            .engineCapacity(vehicle.getEngineCapacity())
            .mileage(vehicle.getMileage())
            .organizationId(vehicle.getOrganizationId())
            .agencyId(vehicle.getAgencyId())
            .status(vehicle.getStatus())
            .isActive(vehicle.getIsActive())
            .dailyPrice(vehicle.getDailyPrice())
            .hourlyPrice(vehicle.getHourlyPrice())
            .description(vehicle.getDescription())
            .features(vehicle.getFeatures())
            .notes(vehicle.getNotes())
            .lastMaintenanceDate(vehicle.getLastMaintenanceDate())
            .nextMaintenanceDate(vehicle.getNextMaintenanceDate())
            .maintenanceNotes(vehicle.getMaintenanceNotes())
            .currentLatitude(vehicle.getCurrentLatitude())
            .currentLongitude(vehicle.getCurrentLongitude())
            .currentAddress(vehicle.getCurrentAddress())
            .createdAt(vehicle.getCreatedAt())
            .updatedAt(vehicle.getUpdatedAt())
            .fullName(vehicle.getFullName())
            .isAvailableForRental(vehicle.isAvailableForRental())
            .build();
    }

    /**
     * Convertit une CreateVehicleRequest en Vehicle
     */
    public Vehicle toEntity(CreateVehicleRequest request) {
        if (request == null) {
            return null;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setBrandId(request.getBrandId());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setTransmissionType(request.getTransmissionType());
        vehicle.setNumberOfSeats(request.getNumberOfSeats());
        vehicle.setNumberOfDoors(request.getNumberOfDoors());
        vehicle.setEngineCapacity(request.getEngineCapacity());
        vehicle.setMileage(request.getMileage());
        vehicle.setOrganizationId(request.getOrganizationId());
        vehicle.setAgencyId(request.getAgencyId());
        vehicle.setDailyPrice(request.getDailyPrice());
        vehicle.setHourlyPrice(request.getHourlyPrice());
        vehicle.setDescription(request.getDescription());
        vehicle.setFeatures(request.getFeatures());
        vehicle.setNotes(request.getNotes());
        vehicle.setLastMaintenanceDate(request.getLastMaintenanceDate());
        vehicle.setNextMaintenanceDate(request.getNextMaintenanceDate());
        vehicle.setMaintenanceNotes(request.getMaintenanceNotes());
        vehicle.prePersist();

        return vehicle;
    }

    /**
     * Met à jour une entité Vehicle à partir d'une UpdateVehicleRequest
     */
    public void updateEntity(Vehicle vehicle, UpdateVehicleRequest request) {
        if (vehicle == null || request == null) {
            return;
        }

        if (request.getBrandId() != null) {
            vehicle.setBrandId(request.getBrandId());
        }
        if (request.getModel() != null) {
            vehicle.setModel(request.getModel());
        }
        if (request.getYear() != null) {
            vehicle.setYear(request.getYear());
        }
        if (request.getLicensePlate() != null) {
            vehicle.setLicensePlate(request.getLicensePlate());
        }
        if (request.getVehicleType() != null) {
            vehicle.setVehicleType(request.getVehicleType());
        }
        if (request.getColor() != null) {
            vehicle.setColor(request.getColor());
        }
        if (request.getFuelType() != null) {
            vehicle.setFuelType(request.getFuelType());
        }
        if (request.getTransmissionType() != null) {
            vehicle.setTransmissionType(request.getTransmissionType());
        }
        if (request.getNumberOfSeats() != null) {
            vehicle.setNumberOfSeats(request.getNumberOfSeats());
        }
        if (request.getNumberOfDoors() != null) {
            vehicle.setNumberOfDoors(request.getNumberOfDoors());
        }
        if (request.getEngineCapacity() != null) {
            vehicle.setEngineCapacity(request.getEngineCapacity());
        }
        if (request.getMileage() != null) {
            vehicle.setMileage(request.getMileage());
        }
        if (request.getAgencyId() != null) {
            vehicle.setAgencyId(request.getAgencyId());
        }
        if (request.getDailyPrice() != null) {
            vehicle.setDailyPrice(request.getDailyPrice());
        }
        if (request.getHourlyPrice() != null) {
            vehicle.setHourlyPrice(request.getHourlyPrice());
        }
        if (request.getDescription() != null) {
            vehicle.setDescription(request.getDescription());
        }
        if (request.getFeatures() != null) {
            vehicle.setFeatures(request.getFeatures());
        }
        if (request.getNotes() != null) {
            vehicle.setNotes(request.getNotes());
        }
        if (request.getLastMaintenanceDate() != null) {
            vehicle.setLastMaintenanceDate(request.getLastMaintenanceDate());
        }
        if (request.getNextMaintenanceDate() != null) {
            vehicle.setNextMaintenanceDate(request.getNextMaintenanceDate());
        }
        if (request.getMaintenanceNotes() != null) {
            vehicle.setMaintenanceNotes(request.getMaintenanceNotes());
        }
        if (request.getIsActive() != null) {
            vehicle.setIsActive(request.getIsActive());
        }

        vehicle.preUpdate();
    }
}
