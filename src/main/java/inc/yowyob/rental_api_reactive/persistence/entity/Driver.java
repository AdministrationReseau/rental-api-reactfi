package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.DriverStatus;
import inc.yowyob.rental_api_reactive.application.dto.Money;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours;
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
public class Driver {
    
    // === CLÉS ET IDENTIFIANTS ===
    
    @PrimaryKeyColumn(name = "driver_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID driverId;

    @Column("user_id")
    private UUID userId;

    @Column("organization_id")
    private UUID organizationId;

    @Column("agency_id")
    private UUID agencyId;

    // === INFORMATIONS PERMIS DE CONDUIRE ===
    
    @Column("license_number")
    private String licenseNumber;

    @Column("license_type")
    private String licenseType;

    @Column("license_expiry_date")
    private LocalDate licenseExpiryDate;

    @Column("experience_years")
    private Integer experience; // Années d'expérience

    // === DOCUMENTS ===
    
    @Column("id_card_url")
    private String idCardUrl;

    @Column("driver_license_url")
    private String driverLicenseUrl;

    // === INFORMATIONS OPÉRATIONNELLES ===
    
    @Column("location")
    private String location;

    @Column("assigned_vehicle_ids")
    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.UUID)
    private List<UUID> assignedVehicleIds;

    @Column("rating")
    private Double rating = 0.0;

    // === ASSURANCE ===
    
    @Column("insurance_provider")
    private String insuranceProvider;

    @Column("insurance_policy")
    private String insurancePolicy;

    // === STATUT DU CHAUFFEUR ===
    
    @Column("status")
    private DriverStatus status = DriverStatus.OFF_DUTY;

    @Column("status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @Column("status_updated_by")
    private UUID statusUpdatedBy;

    // === INFORMATIONS EMPLOYÉ ===
    
    @Column("employee_id")
    private String employeeId;

    @Column("department")
    private String department;

    @Column("position")
    private String position;

    @Column("hire_date")
    private LocalDate hireDate;

    @Column("date_of_birth")
    private LocalDate dateOfBirth;


    // === INFORMATIONS FINANCIÈRES ===
    
    @Column("hourly_rate")
    @CassandraType(type = CassandraType.Name.TEXT)
    private Money hourlyRate;

    @Column("working_hours")
    @CassandraType(type = CassandraType.Name.TEXT)
    private WorkingHours workingHours;

    // === AUDIT TRAIL ===
    
    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("created_by")
    private UUID createdBy;

    @Column("updated_by")
    private UUID updatedBy;

    // === MÉTHODES UTILITAIRES ===
    
    /**
     * Vérifie si le chauffeur est disponible pour une mission
     */
    public boolean isAvailable() {
        return this.status == DriverStatus.AVAILABLE;
    }

    /**
     * Vérifie si le chauffeur est en service
     */
    public boolean isOnDuty() {
        return this.status == DriverStatus.ON_DUTY;
    }

    /**
     * Vérifie si le chauffeur est hors service
     */
    public boolean isOffDuty() {
        return this.status == DriverStatus.OFF_DUTY;
    }

    /**
     * Vérifie si le chauffeur est en congé
     */
    public boolean isOnLeave() {
        return this.status == DriverStatus.ON_LEAVE;
    }

    /**
     * Vérifie si le chauffeur peut être assigné à une mission
     */
    public boolean canBeAssigned() {
        return this.status != null && this.status.isAssignable();
    }

    /**
     * Vérifie si le chauffeur est prêt à travailler
     */
    public boolean isWorkReady() {
        return this.status != null && this.status.isWorkReady();
    }

    /**
     * Vérifie si le permis de conduire est expiré
     */
    public boolean isLicenseExpired() {
        return licenseExpiryDate != null && licenseExpiryDate.isBefore(LocalDate.now());
    }

    /**
     * Calcule le nombre de véhicules assignés
     */
    public int getAssignedVehicleCount() {
        return assignedVehicleIds != null ? assignedVehicleIds.size() : 0;
    }

    /**
     * Vérifie si le chauffeur a des véhicules assignés
     */
    public boolean hasAssignedVehicles() {
        return assignedVehicleIds != null && !assignedVehicleIds.isEmpty();
    }
}