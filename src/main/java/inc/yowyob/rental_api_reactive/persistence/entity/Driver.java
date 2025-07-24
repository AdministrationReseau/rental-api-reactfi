package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.Money;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("drivers")
public class Driver extends BaseEntity {
    
    @PrimaryKeyColumn(name = "driver_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @JsonProperty("driver_id")
    private UUID driverId;

    @NotNull(message = "User ID is required")
    @Column("user_id")
    @JsonProperty("user_id")
    private UUID userId;

    @NotNull(message = "Organization ID is required")
    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @Column("agency_id")
    @JsonProperty("agency_id")
    private UUID agencyId; // Lien vers l'agence (peut être null)

    @Column("date_of_birth")
    @NotNull
    private LocalDate dateOfBirth;

    @Column("license_number")
    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @Column("license_type")
    @NotBlank(message = "License type is required")
    private String licenseType;

    @Column("license_expiry")
    private LocalDate licenseExpiryDate;

    @Column("experience")
    @Min(0)
    private Integer experience; // Années d'expérience

    @Column("location")
    private String location;

    @Column("id_card_url")
    private String idCardUrl;

    @Column("driver_license_url")
    private String driverLicenseUrl;

    @Column("assigned_vehicle_ids")
    private List<UUID> assignedVehicleIds;

    @NotNull(message = "Availability status is required")
    @Column("available")
    @JsonProperty("available")
    private Boolean available = true;

    @Column("rating")
    @DecimalMin(value = "0.0") @DecimalMax(value = "5.0")
    @JsonProperty("rating")
    private Double rating = 0.0;

    @Column("insurance_provider")
    @JsonProperty("insuranceProvider")
    private String insuranceProvider;

    @Column("insurance_policy")
    @JsonProperty("insurancePolicy")
    private String insurancePolicy;

    // === AUDIT ===
    // @Column("created_at")
    // @JsonProperty("createdAt")
    // private LocalDateTime createdAt;

    // @Column("updated_at")
    // @JsonProperty("updatedAt")
    // private LocalDateTime updatedAt;

    @Column("created_by")
    @JsonProperty("createdBy")
    private UUID createdBy;

    @Column("updated_by")
    @JsonProperty("updatedBy")
    private UUID updatedBy;
    // @Column("status")
    // @JsonProperty("status")
    // private DriverStatus status = DriverStatus.AVAILABLE;

    @Column("status_updated_at")
    @JsonProperty("status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @Column("status_updated_by")
    @JsonProperty("status_updated_by")
    private UUID statusUpdatedBy;

   // --- Attributs de Staff ---
    @Column("employee_id") // Nom de la colonne dans Cassandra
    @JsonProperty("employeeId")
    private String employeeId; // ID employé interne à l'organisation

    @Column("department")
    @JsonProperty("department")
    private String department; // Service/Département

    @Column("position")
    @JsonProperty("position")
    private String position; // Poste/Fonction

    @Column("staff_status")
    @JsonProperty("staffStatus")
    private String staffStatus; // Utiliser une enum (ON_SHIFT, ON_LEAVE, etc.)

    @Column("hourly_rate")
    @JsonProperty("hourlyRate")
    @CassandraType(type = CassandraType.Name.TEXT)
    private Money hourlyRate;

    @Column("working_hours")
    @JsonProperty("workingHours")
    @CassandraType(type = CassandraType.Name.TEXT) // On stocke le JSON dans une colonne TEXT
    private WorkingHours workingHours;

    @Column("hire_date")
    @JsonProperty("hireDate")
    private LocalDate hireDate; // Date d'embauche
}