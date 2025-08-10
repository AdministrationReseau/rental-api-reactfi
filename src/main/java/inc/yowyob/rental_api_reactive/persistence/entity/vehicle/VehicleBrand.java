package inc.yowyob.rental_api_reactive.persistence.entity.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant une marque de véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("vehicle_brands")
public class VehicleBrand {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 50, message = "Brand name must be between 2 and 50 characters")
    @Column("name")
    @JsonProperty("name")
    private String name;

    @Column("logo_url")
    @JsonProperty("logoUrl")
    private String logoUrl;

    @Column("country_origin")
    @JsonProperty("countryOrigin")
    private String countryOrigin;

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
