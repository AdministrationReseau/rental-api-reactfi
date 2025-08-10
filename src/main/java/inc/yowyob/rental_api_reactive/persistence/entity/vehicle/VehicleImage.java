package inc.yowyob.rental_api_reactive.persistence.entity.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.util.ImageType;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant une image de véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("vehicle_images")
public class VehicleImage {
    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotNull(message = "Vehicle ID is required")
    @Column("vehicle_id")
    @JsonProperty("vehicleId")
    private UUID vehicleId;

    @NotBlank(message = "Encrypted URL is required")
    @Column("encrypted_url")
    @JsonProperty("encryptedUrl")
    private String encryptedUrl;

    @NotBlank(message = "Encrypted filename is required")
    @Column("encrypted_filename")
    @JsonProperty("encryptedFilename")
    private String encryptedFilename;

    @NotBlank(message = "Original filename is required")
    @Column("original_filename")
    @JsonProperty("originalFilename")
    private String originalFilename;

    @Column("image_type")
    @JsonProperty("imageType")
    private ImageType imageType;

    @Column("file_size")
    @JsonProperty("fileSize")
    private Long fileSize;

    @Column("mime_type")
    @JsonProperty("mimeType")
    private String mimeType;

    @Column("width")
    @JsonProperty("width")
    private Integer width;

    @Column("height")
    @JsonProperty("height")
    private Integer height;

    @Column("is_primary")
    @JsonProperty("isPrimary")
    private Boolean isPrimary = false;

    @Column("display_order")
    @JsonProperty("displayOrder")
    private Integer displayOrder = 0;

    @Column("is_active")
    @JsonProperty("isActive")
    private Boolean isActive = true;

    @Column("is_deleted")
    @JsonProperty("isDeleted")
    private Boolean isDeleted = false;

    // === AUDIT FIELDS ===
    @Column("created_at")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Column("updated_at")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @Column("created_by")
    @JsonProperty("createdBy")
    private UUID createdBy;

    @Column("updated_by")
    @JsonProperty("updatedBy")
    private UUID updatedBy;

    /**
     * Pre-persist hook
     */
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isPrimary == null) {
            this.isPrimary = false;
        }
        if (this.displayOrder == null) {
            this.displayOrder = 0;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    /**
     * Pre-update hook
     */
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
