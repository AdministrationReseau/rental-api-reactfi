package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.DriverStatus;
import inc.yowyob.rental_api_reactive.application.dto.Money;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateDriverRequest {

    // === INFORMATIONS PERMIS DE CONDUIRE ===
    
    @JsonProperty("license_number")
    private String licenseNumber;

    @JsonProperty("license_type")
    private String licenseType;

    @JsonProperty("license_expiry_date")
    @Future(message = "License expiry date must be in the future")
    private LocalDate licenseExpiryDate;

    @JsonProperty("experience_years")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experience;

    // === LOCALISATION ET DOCUMENTS ===
    
    @JsonProperty("location")
    private String location;

    @JsonProperty("id_card_url")
    private String idCardUrl;

    @JsonProperty("driver_license_url")
    private String driverLicenseUrl;

    // === VÉHICULES ASSIGNÉS ===
    
    @JsonProperty("assigned_vehicle_ids")
    private List<UUID> assignedVehicleIds;

    // === ASSURANCE ===
    
    @JsonProperty("insurance_provider")
    private String insuranceProvider;

    @JsonProperty("insurance_policy")
    private String insurancePolicy;

    // === INFORMATIONS EMPLOYÉ ===
    
    @JsonProperty("employee_id")
    private String employeeId;

    @JsonProperty("position")
    private String position;

    @JsonProperty("department")
    private String department;

    @JsonProperty("hire_date")
    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    // === INFORMATIONS FINANCIÈRES ===
    
    @JsonProperty("hourly_rate")
    private Money hourlyRate;

    @JsonProperty("working_hours")
    private WorkingHours workingHours;

    // === STATUT (géré séparément par l'endpoint dédié) ===
    
    @JsonProperty("status")
    private DriverStatus status;

    // === ÉVALUATION ===
    
    @JsonProperty("rating")
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating;

    // === NOTES ET COMMENTAIRES ===
    
    @JsonProperty("notes")
    private String notes;
}