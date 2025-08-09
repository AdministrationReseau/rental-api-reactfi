package inc.yowyob.rental_api_reactive.application.service.vehicle;

import inc.yowyob.rental_api_reactive.application.dto.util.ImageType;
import inc.yowyob.rental_api_reactive.application.service.organization.MultiTenantReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.VehicleImage;
import inc.yowyob.rental_api_reactive.persistence.mapper.vehicle.VehicleImageMapper;
import inc.yowyob.rental_api_reactive.persistence.repository.vehicle.VehicleImageReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.vehicle.VehicleReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service réactif pour la gestion des images de véhicules
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleImageReactiveService {

    private final VehicleImageReactiveRepository vehicleImageRepository;
    private final VehicleReactiveRepository vehicleRepository;
    private final VehicleImageService vehicleImageService;
    private final VehicleImageMapper vehicleImageMapper;
    private final MultiTenantReactiveService multiTenantService;

    /**
     * Upload des images pour un véhicule
     */
    public Flux<VehicleImageResponse> uploadVehicleImages(UUID vehicleId, Flux<FilePart> files,
                                                          String imageType, Boolean isPrimary, UUID uploadedBy) {
        log.info("Uploading images for vehicle: {}", vehicleId);

        return validateVehicleAccess(vehicleId, uploadedBy)
            .thenMany(files)
            .flatMap(filePart -> processImageUpload(vehicleId, filePart, imageType, isPrimary, uploadedBy))
            .flatMap(vehicleImageRepository::save)
            .flatMap(this::enrichImageResponse)
            .doOnNext(image -> log.info("Image uploaded successfully: {}", image.getId()))
            .doOnError(error -> log.error("Error uploading images for vehicle: {}", vehicleId, error));
    }

    /**
     * Récupère toutes les images d'un véhicule
     */
    public Flux<VehicleImageResponse> getVehicleImages(UUID vehicleId, UUID requestingUserId) {
        log.debug("Fetching images for vehicle: {}", vehicleId);

        return validateVehicleAccess(vehicleId, requestingUserId)
            .thenMany(vehicleImageRepository.findActiveByVehicleId(vehicleId))
            .flatMap(this::enrichImageResponse);
    }

    /**
     * Récupère une image spécifique
     */
    public Mono<VehicleImageResponse> getVehicleImage(UUID vehicleId, UUID imageId, UUID requestingUserId) {
        log.debug("Fetching image: {} for vehicle: {}", imageId, vehicleId);

        return validateVehicleAccess(vehicleId, requestingUserId)
            .then(vehicleImageRepository.findById(imageId))
            .filter(image -> !image.getIsDeleted() && image.getVehicleId().equals(vehicleId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Image non trouvée")))
            .flatMap(this::enrichImageResponse);
    }

    /**
     * Définit une image comme principale
     */
    public Mono<VehicleImageResponse> setPrimaryImage(UUID vehicleId, UUID imageId, UUID updatedBy) {
        log.info("Setting primary image: {} for vehicle: {}", imageId, vehicleId);

        return validateVehicleAccess(vehicleId, updatedBy)
            .then(vehicleImageRepository.findById(imageId))
            .filter(image -> !image.getIsDeleted() && image.getVehicleId().equals(vehicleId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Image non trouvée")))
            .flatMap(targetImage -> {
                // D'abord, retirer le statut principal de toutes les autres images
                return vehicleImageRepository.findAllPrimaryByVehicleId(vehicleId)
                    .filter(image -> !image.getId().equals(imageId))
                    .flatMap(image -> {
                        image.setIsPrimary(false);
                        image.setUpdatedBy(updatedBy);
                        image.preUpdate();
                        return vehicleImageRepository.save(image);
                    })
                    .then(Mono.just(targetImage));
            })
            .flatMap(targetImage -> {
                // Ensuite, définir l'image cible comme principale
                targetImage.setIsPrimary(true);
                targetImage.setUpdatedBy(updatedBy);
                targetImage.preUpdate();
                return vehicleImageRepository.save(targetImage);
            })
            .flatMap(this::enrichImageResponse)
            .doOnSuccess(image -> log.info("Primary image set successfully: {}", imageId));
    }

    /**
     * Met à jour les métadonnées d'une image
     */
    public Mono<VehicleImageResponse> updateVehicleImage(UUID vehicleId, UUID imageId,
                                                         UpdateVehicleImageRequest updateRequest, UUID updatedBy) {
        log.info("Updating image: {} for vehicle: {}", imageId, vehicleId);

        return validateVehicleAccess(vehicleId, updatedBy)
            .then(vehicleImageRepository.findById(imageId))
            .filter(image -> !image.getIsDeleted() && image.getVehicleId().equals(vehicleId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Image non trouvée")))
            .flatMap(existingImage -> {
                updateImageFromRequest(existingImage, updateRequest, updatedBy);
                return vehicleImageRepository.save(existingImage);
            })
            .flatMap(this::enrichImageResponse)
            .doOnSuccess(image -> log.info("Image updated successfully: {}", imageId));
    }

    /**
     * Supprime une image
     */
    public Mono<Void> deleteVehicleImage(UUID vehicleId, UUID imageId, UUID deletedBy) {
        log.info("Deleting image: {} for vehicle: {}", imageId, vehicleId);

        return validateVehicleAccess(vehicleId, deletedBy)
            .then(vehicleImageRepository.findById(imageId))
            .filter(image -> !image.getIsDeleted() && image.getVehicleId().equals(vehicleId))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Image non trouvée")))
            .flatMap(image -> {
                // Supprimer le fichier physique
                return vehicleImageService.deleteFile(image.getEncryptedUrl())
                    .then(Mono.just(image));
            })
            .flatMap(image -> {
                // Suppression logique de l'enregistrement
                image.setIsDeleted(true);
                image.setIsActive(false);
                image.setUpdatedBy(deletedBy);
                image.preUpdate();
                return vehicleImageRepository.save(image);
            })
            .then()
            .doOnSuccess(v -> log.info("Image deleted successfully: {}", imageId));
    }

    /**
     * Valide l'accès au véhicule
     */
    private Mono<Void> validateVehicleAccess(UUID vehicleId, UUID userId) {
        return vehicleRepository.findById(vehicleId)
            .filter(vehicle -> !vehicle.getIsDeleted())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Véhicule non trouvé")))
            .flatMap(vehicle -> multiTenantService.validateOrganizationAccess(vehicle.getOrganizationId(), userId))
            .then();
    }

    /**
     * Traite l'upload d'une image
     */
    private Mono<VehicleImage> processImageUpload(UUID vehicleId, FilePart filePart,
                                                  String imageType, Boolean isPrimary, UUID uploadedBy) {
        return filePart.content()
            .collectList()
            .flatMap(dataBuffers -> {
                // Convertir les DataBuffer en byte array
                byte[] fileData = convertDataBuffersToByteArray(dataBuffers);

                return vehicleImageService.saveFile(fileData, filePart.filename(), "vehicles", vehicleId)
                    .flatMap(uploadResult -> createImageEntity(vehicleId, uploadResult, imageType, isPrimary, uploadedBy));
            });
    }

    /**
     * Crée une entité image à partir du résultat d'upload
     */
    private Mono<VehicleImage> createImageEntity(UUID vehicleId, VehicleImageService.FileUploadResult uploadResult,
                                                 String imageType, Boolean isPrimary, UUID uploadedBy) {
        return Mono.fromCallable(() -> {
            VehicleImage image = new VehicleImage();
            image.setVehicleId(vehicleId);
            image.setEncryptedUrl(uploadResult.getEncryptedUrl());
            image.setEncryptedFilename(uploadResult.getEncryptedFilename());
            image.setOriginalFilename(uploadResult.getOriginalFilename());
            image.setFileSize(uploadResult.getFileSize());

            // Déterminer le type d'image
            if (imageType != null) {
                try {
                    image.setImageType(ImageType.valueOf(imageType.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    image.setImageType(ImageType.MAIN);
                }
            } else {
                image.setImageType(ImageType.MAIN);
            }

            // Déterminer si c'est l'image principale
            image.setIsPrimary(isPrimary != null ? isPrimary : false);

            // Déterminer le type MIME
            String filename = uploadResult.getOriginalFilename().toLowerCase();
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                image.setMimeType("image/jpeg");
            } else if (filename.endsWith(".png")) {
                image.setMimeType("image/png");
            } else if (filename.endsWith(".webp")) {
                image.setMimeType("image/webp");
            } else {
                image.setMimeType("application/octet-stream");
            }

            image.setCreatedBy(uploadedBy);
            image.setUpdatedBy(uploadedBy);
            image.prePersist();

            return image;
        });
    }

    /**
     * Enrichit la réponse image avec l'URL déchiffrée
     */
    private Mono<VehicleImageResponse> enrichImageResponse(VehicleImage image) {
        return vehicleImageService.decryptUrl(image.getEncryptedUrl())
            .map(decryptedUrl -> {
                VehicleImageResponse response = vehicleImageMapper.toResponse(image);
                response.setImageUrl(decryptedUrl); // URL déchiffrée pour l'affichage
                return response;
            })
            .onErrorReturn(vehicleImageMapper.toResponse(image)); // En cas d'erreur de déchiffrement, retourner sans URL
    }

    /**
     * Met à jour une image à partir de la requête
     */
    private void updateImageFromRequest(VehicleImage existingImage, UpdateVehicleImageRequest updateRequest, UUID updatedBy) {
        if (updateRequest.getImageType() != null) {
            existingImage.setImageType(updateRequest.getImageType());
        }
        if (updateRequest.getIsPrimary() != null) {
            existingImage.setIsPrimary(updateRequest.getIsPrimary());
        }
        if (updateRequest.getDisplayOrder() != null) {
            existingImage.setDisplayOrder(updateRequest.getDisplayOrder());
        }
        if (updateRequest.getIsActive() != null) {
            existingImage.setIsActive(updateRequest.getIsActive());
        }

        existingImage.setUpdatedBy(updatedBy);
        existingImage.preUpdate();
    }

    /**
     * Convertit les DataBuffer en byte array
     */
    private byte[] convertDataBuffersToByteArray(java.util.List<org.springframework.core.io.buffer.DataBuffer> dataBuffers) {
        int totalLength = dataBuffers.stream().mapToInt(org.springframework.core.io.buffer.DataBuffer::readableByteCount).sum();
        byte[] result = new byte[totalLength];
        int offset = 0;

        for (org.springframework.core.io.buffer.DataBuffer dataBuffer : dataBuffers) {
            int length = dataBuffer.readableByteCount();
            dataBuffer.read(result, offset, length);
            offset += length;
            org.springframework.core.io.buffer.DataBufferUtils.release(dataBuffer);
        }

        return result;
    }
}
