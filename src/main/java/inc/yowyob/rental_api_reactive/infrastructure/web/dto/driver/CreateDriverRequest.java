package inc.yowyob.rental_api_reactive.infrastructure.web.dto.driver;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.driver.DriverStatus;
import inc.yowyob.rental_api_reactive.application.dto.util.Money;
import inc.yowyob.rental_api_reactive.application.dto.util.WorkingHours;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateDriverRequest {

    // === INFORMATIONS OBLIGATOIRES ===
    
    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private UUID userId;

    @NotNull(message = "Organization ID is required")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @NotNull(message = "Agency ID is required")
    @JsonProperty("agency_id")
    private UUID agencyId;

    // === INFORMATIONS PERMIS DE CONDUIRE ===
    
    @JsonProperty("license_number")
    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @JsonProperty("license_type")
    @NotBlank(message = "License type is required")
    private String licenseType;

    @JsonProperty("license_expiry_date")
    @NotNull(message = "License expiry date is required")
    @Future(message = "License expiry date must be in the future")
    private LocalDate licenseExpiryDate;
    
    // === INFORMATIONS EXPÉRIENCE ===
    
    @JsonProperty("experience_years")
    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experience;

    // === DOCUMENTS ===
    
    @JsonProperty("id_card_url")
    private String idCardUrl;
    
    @JsonProperty("driver_license_url")
    private String driverLicenseUrl;

    // === INFORMATIONS EMPLOYÉ ===
    
    @JsonProperty("employee_id")
    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @JsonProperty("cni")
    private String cni; // Carte Nationale d'Identité

    @JsonProperty("position")
    @NotBlank(message = "Position is required")
    private String position;

    @JsonProperty("department")
    private String department;

    @JsonProperty("hire_date")
    @NotNull(message = "Hire date is required")
    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    // === INFORMATIONS FINANCIÈRES ===
    
    @JsonProperty("hourly_rate")
    private Money hourlyRate;

    @JsonProperty("working_hours")
    private WorkingHours workingHours;

    // === STATUT ===
    
    @JsonProperty("status")
    private DriverStatus status = DriverStatus.OFF_DUTY; // Valeur par défaut

    // === INFORMATIONS ADDITIONNELLES ===
    
    @JsonProperty("rating")
    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    private Double rating;

    @JsonProperty("notes")
    private String notes; // Notes additionnelles sur le chauffeur
}