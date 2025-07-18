// PATH: src/main/java/inc/yowyob/rental_api_reactive/infrastructure/web/dto/DriverResponse.java

package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.Money;
// import inc.yowyob.rental_api_reactive.application.dto.DriverStatus;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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

    // --- IDENTIFIANTS CLÉS ---
    @JsonProperty("driver_id")
    private UUID driverId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("agency_id")
    private UUID agencyId;

    // --- INFORMATIONS PERSONNELLES (de l'entité User) ---
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


    // --- INFORMATIONS PROFESSIONNELLES (de l'entité Driver) ---
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("license_number")
    private String licenseNumber;

    @JsonProperty("license_type")
    private String licenseType;

    @JsonProperty("license_expiry_date")
    private LocalDate licenseExpiryDate;

    @JsonProperty("experience_years")
    private Integer experienceYears;

    @JsonProperty("rating")
    private Double rating;
    
    // @JsonProperty("status")
    // private DriverStatus status;

    // @JsonProperty("is_available")
    // private Boolean isAvailable;


    // --- INFORMATIONS D'EMPLOYÉ (de l'entité Driver) ---
    @JsonProperty("employee_id")
    private String employeeId; // Matricule

    @JsonProperty("position")
    private String position;

    @JsonProperty("hire_date")
    private LocalDate hireDate;
    
    @JsonProperty("staff_status")
    private String staffStatus;

    @JsonProperty("idCardUrl")
    private String idCardUrl;            // URL or identifier of ID card
  

    @JsonProperty("licenseExpiry")
    @NotNull @Future private LocalDate licenseExpiry;
    
    @JsonProperty("experience")
    @NotNull @Min(0) private Integer experience;

 
    @JsonProperty("driverLicenceUrl")
    private String driverLicenseUrl;     // URL or identifier of license


    @JsonProperty("cni")
    private String cni;


    @JsonProperty("department")
    private String department;


    @JsonProperty("hourlyRate")
    private Money hourlyRate;

    @JsonProperty("workingHours")
    private WorkingHours workingHours;

    // --- CHAMPS CALCULÉS (pour la commodité du frontend) ---
    /**
     * Calcule l'âge actuel du chauffeur. Non stocké en base.
     */
    @JsonProperty("age")
    public Integer getAge() {
        if (this.dateOfBirth == null) {
            return null;
        }
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }
    
    private int age = getAge();

    /**
     * Calcule le nombre d'années de service. Non stocké en base.
     */
    @JsonProperty("yearsOfService")
    public Integer getYearsOfService() {
        if (this.hireDate == null) {
            return null;
        }
        return Period.between(this.hireDate, LocalDate.now()).getYears();
    }
    private int yearsOfService = getYearsOfService();

    
    /**
     * Vérifie si le permis de conduire a expiré. Non stocké en base.
     */
    @JsonProperty("isLicenseExpired")
    public Boolean getIsLicenseExpired() {
        if (this.licenseExpiryDate == null) {
            return null; // ou false si une licence sans date d'expiration est considérée comme valide
        }
        return LocalDate.now().isAfter(this.licenseExpiryDate);
    }
    private boolean isLicenseExpired = getIsLicenseExpired();

    

    // --- AUDIT ---
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    
}
