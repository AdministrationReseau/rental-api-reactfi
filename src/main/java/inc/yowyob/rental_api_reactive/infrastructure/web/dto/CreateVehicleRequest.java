package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.*;
import lombok.*;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour créer un véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleRequest {

    @NotNull(message = "L'ID de la marque est obligatoire")
    @JsonProperty("brandId")
    private UUID brandId;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(min = 2, max = 50, message = "Le modèle doit contenir entre 2 et 50 caractères")
    @JsonProperty("model")
    private String model;

    @NotNull(message = "L'année est obligatoire")
    @Min(value = 1900, message = "L'année doit être supérieure à 1900")
    @Max(value = 2030, message = "L'année ne peut pas être dans un futur lointain")
    @JsonProperty("year")
    private Integer year;

    @NotBlank(message = "La plaque d'immatriculation est obligatoire")
    @Size(min = 2, max = 20, message = "La plaque doit contenir entre 2 et 20 caractères")
    @JsonProperty("licensePlate")
    private String licensePlate;

    @NotNull(message = "Le type de véhicule est obligatoire")
    @JsonProperty("vehicleType")
    private VehicleType vehicleType;

    @JsonProperty("color")
    private String color;

    @JsonProperty("fuelType")
    private FuelType fuelType;

    @JsonProperty("transmissionType")
    private TransmissionType transmissionType;

    @JsonProperty("numberOfSeats")
    private Integer numberOfSeats;

    @JsonProperty("numberOfDoors")
    private Integer numberOfDoors;

    @JsonProperty("engineCapacity")
    private Double engineCapacity;

    @JsonProperty("mileage")
    private Integer mileage;

    @NotNull(message = "L'ID de l'organisation est obligatoire")
    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix journalier doit être positif")
    @JsonProperty("dailyPrice")
    private BigDecimal dailyPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix horaire doit être positif")
    @JsonProperty("hourlyPrice")
    private BigDecimal hourlyPrice;

    @JsonProperty("description")
    private String description;

    @JsonProperty("features")
    private List<String> features;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("lastMaintenanceDate")
    private LocalDateTime lastMaintenanceDate;

    @JsonProperty("nextMaintenanceDate")
    private LocalDateTime nextMaintenanceDate;

    @JsonProperty("maintenanceNotes")
    private String maintenanceNotes;
}
