package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.Money;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateDriverRequest {
     // Staff info
    @JsonProperty("position")
    private String position;

    @JsonProperty("department")
    private String department;

    @JsonProperty("staffStatus")
    private String staffStatus;

    @JsonProperty("hourlyRate")
    private Money hourlyRate;

    @JsonProperty("workingHours")
    private WorkingHours workingHours;

    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth; // On peut autoriser la correction de la date de naissance
    // Driver info
    @JsonProperty("licenseNumber")
    private String licenseNumber;

    @JsonProperty("licenseType")
    private String licenseType;

    @JsonProperty("licenseExpiry")
    private LocalDate licenseExpiry;

    @JsonProperty("experience")
    private Integer experience;

    /**
     * La localisation actuelle du chauffeur (ex: "Douala, Bonapriso").
     */
    @JsonProperty("location")
    private String location;

    @JsonProperty("idCardUrl")
    private String idCardUrl;

    @JsonProperty("driverLicenseUrl")
    private String driverLicenseUrl;
     @JsonProperty("profileUrl")
    private String profileUrl;      // La photo de profile du driver

    /**
     * Liste des IDs de véhicules actuellement assignés.
     */
    @JsonProperty("assignedVehicleIds")
    private List<UUID> assignedVehicleIds;

    /**
     * Disponibilité du chauffeur pour de nouvelles courses.
     */
    @JsonProperty("available")
    private Boolean available;

    /**
     * Statut opérationnel du chauffeur (disponible, en course, hors service...).
    //  */
    // @JsonProperty("status")
    // private DriverStatus status;
}