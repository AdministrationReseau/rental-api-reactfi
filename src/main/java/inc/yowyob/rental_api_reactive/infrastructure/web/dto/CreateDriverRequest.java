package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.Money;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateDriverRequest {

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private UUID userId;

    @NotNull(message = "Organization ID is required")
    @JsonProperty("organization_id")
    private UUID organizationId;

     @NotNull(message = "L'ID de l'agence est requis.")
    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("date_of_birth")
    @NotNull(message = "La date de naissance est requise.")
    private LocalDate dateOfBirth;

    @JsonProperty("license_number")
    @NotBlank(message = "Le numéro de permis est requis.")
    private String licenseNumber;

    @JsonProperty("license_type")
    @NotBlank(message = "Le type de permis est requis.")
    private String licenseType;

    @JsonProperty("licenseExpiry")
    @NotNull @Future private LocalDate licenseExpiry;
    
    @JsonProperty("experience")
    @NotNull @Min(0) private Integer experience;

    @JsonProperty("idCardUrl")
    private String idCardUrl;            // URL or identifier of ID card
    
    @JsonProperty("driverLicenceUrl")
    private String driverLicenseUrl;     // URL or identifier of license

     // Staff info

    @NotNull
    @JsonProperty("userType")
    private UserType userType = UserType.DRIVER;

    @JsonProperty("employeeId")
    @NotBlank private String employeeId;

    @JsonProperty("cni")
    private String cni;

    @JsonProperty("position")
    @NotBlank private String position;

    @JsonProperty("department")
    private String department;

    @JsonProperty("staffStatus")
    @NotNull private String staffStatus;

    @JsonProperty("hourlyRate")
    private Money hourlyRate;

    @JsonProperty("workingHours")
    private WorkingHours workingHours;

    @JsonProperty("hireDate")
    @NotNull private LocalDate hireDate;

}