package inc.yowyob.rental_api_reactive.persistence.mapper.vehicle;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.VehicleImageResponse;
import inc.yowyob.rental_api_reactive.persistence.entity.VehicleImage;
import org.springframework.stereotype.Component;

/**
 * Mapper pour les images de véhicules
 */
@Component
public class VehicleImageMapper {

    /**
     * Convertit une entité VehicleImage en VehicleImageResponse
     */
    public VehicleImageResponse toResponse(VehicleImage vehicleImage) {
        if (vehicleImage == null) {
            return null;
        }

        return VehicleImageResponse.builder()
            .id(vehicleImage.getId())
            .vehicleId(vehicleImage.getVehicleId())
            .imageUrl(vehicleImage.getEncryptedUrl()) // Dans une vraie implémentation, déchiffrer ici
            .imageType(vehicleImage.getImageType())
            .fileSize(vehicleImage.getFileSize())
            .mimeType(vehicleImage.getMimeType())
            .width(vehicleImage.getWidth())
            .height(vehicleImage.getHeight())
            .isPrimary(vehicleImage.getIsPrimary())
            .displayOrder(vehicleImage.getDisplayOrder())
            .createdAt(vehicleImage.getCreatedAt())
            .build();
    }

    /**
     * Convertit une VehicleImageResponse en entité VehicleImage (pour les tests)
     */
    public VehicleImage toEntity(VehicleImageResponse response) {
        if (response == null) {
            return null;
        }

        VehicleImage vehicleImage = new VehicleImage();
        vehicleImage.setId(response.getId());
        vehicleImage.setVehicleId(response.getVehicleId());
        vehicleImage.setEncryptedUrl(response.getImageUrl());
        vehicleImage.setImageType(response.getImageType());
        vehicleImage.setFileSize(response.getFileSize());
        vehicleImage.setMimeType(response.getMimeType());
        vehicleImage.setWidth(response.getWidth());
        vehicleImage.setHeight(response.getHeight());
        vehicleImage.setIsPrimary(response.getIsPrimary());
        vehicleImage.setDisplayOrder(response.getDisplayOrder());

        return vehicleImage;
    }
}
