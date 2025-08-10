package inc.yowyob.rental_api_reactive.persistence.repository.vehicle;

import inc.yowyob.rental_api_reactive.application.dto.util.ImageType;
import inc.yowyob.rental_api_reactive.persistence.entity.vehicle.VehicleImage;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository réactif pour les images de véhicules
 */
@Repository
public interface VehicleImageReactiveRepository extends ReactiveCassandraRepository<VehicleImage, UUID> {

    /**
     * Trouve toutes les images actives d'un véhicule
     */
    @Query("SELECT * FROM vehicle_images WHERE vehicle_id = ?0 AND is_active = true AND is_deleted = false ORDER BY display_order ASC ALLOW FILTERING")
    Flux<VehicleImage> findActiveByVehicleId(UUID vehicleId);

    /**
     * Trouve toutes les images d'un véhicule (y compris inactives)
     */
    @Query("SELECT * FROM vehicle_images WHERE vehicle_id = ?0 AND is_deleted = false ORDER BY display_order ASC ALLOW FILTERING")
    Flux<VehicleImage> findByVehicleId(UUID vehicleId);

    /**
     * Trouve l'image principale d'un véhicule
     */
    @Query("SELECT * FROM vehicle_images WHERE vehicle_id = ?0 AND is_primary = true AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Mono<VehicleImage> findPrimaryByVehicleId(UUID vehicleId);

    /**
     * Trouve les images par type
     */
    @Query("SELECT * FROM vehicle_images WHERE vehicle_id = ?0 AND image_type = ?1 AND is_active = true AND is_deleted = false ORDER BY display_order ASC ALLOW FILTERING")
    Flux<VehicleImage> findByVehicleIdAndImageType(UUID vehicleId, ImageType imageType);

    /**
     * Trouve les images par nom de fichier chiffré
     */
    @Query("SELECT * FROM vehicle_images WHERE encrypted_filename = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<VehicleImage> findByEncryptedFilename(String encryptedFilename);

    /**
     * Compte les images d'un véhicule
     */
    @Query("SELECT COUNT(*) FROM vehicle_images WHERE vehicle_id = ?0 AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countByVehicleId(UUID vehicleId);

    /**
     * Compte les images actives d'un véhicule
     */
    @Query("SELECT COUNT(*) FROM vehicle_images WHERE vehicle_id = ?0 AND is_active = true AND is_deleted = false ALLOW FILTERING")
    Mono<Long> countActiveByVehicleId(UUID vehicleId);

    /**
     * Trouve les images créées récemment
     */
    @Query("SELECT * FROM vehicle_images WHERE created_at >= ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<VehicleImage> findRecentlyCreated(LocalDateTime since);

    /**
     * Trouve toutes les images principales pour détecter les conflits
     */
    @Query("SELECT * FROM vehicle_images WHERE vehicle_id = ?0 AND is_primary = true AND is_deleted = false ALLOW FILTERING")
    Flux<VehicleImage> findAllPrimaryByVehicleId(UUID vehicleId);

    /**
     * Trouve les images par taille de fichier (pour nettoyage)
     */
    @Query("SELECT * FROM vehicle_images WHERE file_size > ?0 AND is_deleted = false ALLOW FILTERING")
    Flux<VehicleImage> findByFileSizeGreaterThan(Long fileSize);
}
