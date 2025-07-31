package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.DriverStatus;
import inc.yowyob.rental_api_reactive.application.dto.Money;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de réponse complet représentant un chauffeur.
 * Il combine les informations personnelles de l'entité User avec les informations 
 * professionnelles de l'entité Driver pour fournir une vue unifiée et pratique à l'API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    // === IDENTIFIANTS ===
    
    @JsonProperty("driver_id")
    private UUID driverId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("agency_id")
    private UUID agencyId;

    // === INFORMATIONS UTILISATEUR (depuis User) ===
    
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("user_type")
    private UserType userType;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("age")
    private Integer age; // Calculé par le mapper

    // === INFORMATIONS PERMIS ===
    
    @JsonProperty("license_number")
    private String licenseNumber;

    @JsonProperty("license_type")
    private String licenseType;

    @JsonProperty("license_expiry_date")
    private LocalDate licenseExpiryDate;

    @JsonProperty("is_license_expired")
    private Boolean isLicenseExpired; // Calculé par le mapper

    @JsonProperty("experience_years")
    private Integer experienceYears;

    // === LOCALISATION ET DOCUMENTS ===
    
    @JsonProperty("location")
    private String location;

    @JsonProperty("id_card_url")
    private String idCardUrl;

    @JsonProperty("driver_license_url")
    private String driverLicenseUrl;

    @JsonProperty("cni")
    private String cni;

    // === VÉHICULES ===
    
    @JsonProperty("assigned_vehicle_ids")
    private List<UUID> assignedVehicleIds;

    @JsonProperty("assigned_vehicle_count")
    private Integer assignedVehicleCount; // Calculé par le mapper

    @JsonProperty("has_assigned_vehicles")
    private Boolean hasAssignedVehicles; // Calculé par le mapper

    // === ÉVALUATION ===
    
    @JsonProperty("rating")
    private Double rating;

    // === ASSURANCE ===
    
    @JsonProperty("insurance_provider")
    private String insuranceProvider;

    @JsonProperty("insurance_policy")
    private String insurancePolicy;

    // === STATUT DU CHAUFFEUR ===
    
    @JsonProperty("status")
    private DriverStatus status;

    @JsonProperty("status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @JsonProperty("status_updated_by")
    private UUID statusUpdatedBy;

    // Champs dérivés pour le statut (calculés par le mapper)
    @JsonProperty("is_available")
    private Boolean isAvailable;

    @JsonProperty("is_on_duty")
    private Boolean isOnDuty;

    @JsonProperty("is_off_duty")
    private Boolean isOffDuty;

    @JsonProperty("is_on_leave")
    private Boolean isOnLeave;

    @JsonProperty("can_be_assigned")
    private Boolean canBeAssigned;

    @JsonProperty("is_work_ready")
    private Boolean isWorkReady;

    // === INFORMATIONS EMPLOYÉ ===
    
    @JsonProperty("employee_id")
    private String employeeId;

    @JsonProperty("position")
    private String position;

    @JsonProperty("department")
    private String department;

    @JsonProperty("hire_date")
    private LocalDate hireDate;

    @JsonProperty("years_of_service")
    private Integer yearsOfService; // Calculé par le mapper

    // === INFORMATIONS FINANCIÈRES ===
    
    @JsonProperty("hourly_rate")
    private Money hourlyRate;

    @JsonProperty("working_hours")
    private WorkingHours workingHours;

    // === AUDIT TRAIL ===
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private UUID createdBy;

    @JsonProperty("updated_by")
    private UUID updatedBy;
}