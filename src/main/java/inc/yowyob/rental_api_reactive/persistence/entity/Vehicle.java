package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.FuelType;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.TransmissionType;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.VehicleStatus;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.VehicleType;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entité représentant un véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("vehicles")
public class Vehicle {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    // === INFORMATIONS GÉNÉRALES ===
    @NotNull(message = "Brand ID is required")
    @Column("brand_id")
    @JsonProperty("brandId")
    private UUID brandId;

    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    @Column("model")
    @JsonProperty("model")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be after 1900")
    @Max(value = 2030, message = "Year must not be in the far future")
    @Column("year")
    @JsonProperty("year")
    private Integer year;

    @NotBlank(message = "License plate is required")
    @Size(min = 2, max = 20, message = "License plate must be between 2 and 20 characters")
    @Column("license_plate")
    @JsonProperty("licensePlate")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required")
    @Column("vehicle_type")
    @JsonProperty("vehicleType")
    private VehicleType vehicleType;

    @Column("color")
    @JsonProperty("color")
    private String color;

    // === CARACTÉRISTIQUES TECHNIQUES ===
    @Column("fuel_type")
    @JsonProperty("fuelType")
    private FuelType fuelType;

    @Column("transmission_type")
    @JsonProperty("transmissionType")
    private TransmissionType transmissionType;

    @Column("number_of_seats")
    @JsonProperty("numberOfSeats")
    private Integer numberOfSeats;

    @Column("number_of_doors")
    @JsonProperty("numberOfDoors")
    private Integer numberOfDoors;

    @Column("engine_capacity")
    @JsonProperty("engineCapacity")
    private Double engineCapacity;

    @Column("mileage")
    @JsonProperty("mileage")
    private Integer mileage;

    // === ASSOCIATION ORGANISATION/AGENCE ===
    @NotNull(message = "Organization ID is required")
    @Column("organization_id")
    @JsonProperty("organizationId")
    private UUID organizationId;

    @Column("agency_id")
    @JsonProperty("agencyId")
    private UUID agencyId;

    // === ÉTAT ET STATUT ===
    @NotNull(message = "Vehicle status is required")
    @Column("status")
    @JsonProperty("status")
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column("is_active")
    @JsonProperty("isActive")
    private Boolean isActive = true;

    @Column("is_deleted")
    @JsonProperty("isDeleted")
    private Boolean isDeleted = false;

    // === TARIFICATION ===
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily price must be positive")
    @Column("daily_price")
    @JsonProperty("dailyPrice")
    private BigDecimal dailyPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Hourly price must be positive")
    @Column("hourly_price")
    @JsonProperty("hourlyPrice")
    private BigDecimal hourlyPrice;

    // === MAINTENANCE ===
    @Column("last_maintenance_date")
    @JsonProperty("lastMaintenanceDate")
    private LocalDateTime lastMaintenanceDate;

    @Column("next_maintenance_date")
    @JsonProperty("nextMaintenanceDate")
    private LocalDateTime nextMaintenanceDate;

    @Column("maintenance_notes")
    @JsonProperty("maintenanceNotes")
    private String maintenanceNotes;

    // === DESCRIPTION ET NOTES ===
    @Column("description")
    @JsonProperty("description")
    private String description;

    @Column("features")
    @JsonProperty("features")
    private List<String> features;

    @Column("notes")
    @JsonProperty("notes")
    private String notes;

    // === GÉOLOCALISATION ===
    @Column("current_latitude")
    @JsonProperty("currentLatitude")
    private Double currentLatitude;

    @Column("current_longitude")
    @JsonProperty("currentLongitude")
    private Double currentLongitude;

    @Column("current_address")
    @JsonProperty("currentAddress")
    private String currentAddress;

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
        if (this.status == null) {
            this.status = VehicleStatus.AVAILABLE;
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

    /**
     * Retourne le nom complet du véhicule (Brand Model Year)
     */
    public String getFullName() {
        return String.format("%s %s (%d)",
            this.brandId != null ? this.brandId.toString() : "Unknown",
            this.model,
            this.year);
    }

    /**
     * Vérifie si le véhicule est disponible pour location
     */
    public boolean isAvailableForRental() {
        return this.isActive && !this.isDeleted &&
            this.status == VehicleStatus.AVAILABLE;
    }

    /**
     * Change le statut du véhicule
     */
    public void changeStatus(VehicleStatus newStatus) {
        this.status = newStatus;
        this.preUpdate();
    }
}
