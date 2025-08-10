package inc.yowyob.rental_api_reactive.infrastructure.web.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.FuelType;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.TransmissionType;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.VehicleStatus;
import inc.yowyob.rental_api_reactive.application.dto.vehicle.VehicleType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de réponse pour un véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("brandId")
    private UUID brandId;

    @JsonProperty("brandName")
    private String brandName;

    @JsonProperty("model")
    private String model;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("licensePlate")
    private String licensePlate;

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

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @JsonProperty("agencyName")
    private String agencyName;

    @JsonProperty("status")
    private VehicleStatus status;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("dailyPrice")
    private BigDecimal dailyPrice;

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

    @JsonProperty("currentLatitude")
    private Double currentLatitude;

    @JsonProperty("currentLongitude")
    private Double currentLongitude;

    @JsonProperty("currentAddress")
    private String currentAddress;

    @JsonProperty("images")
    private List<VehicleImageResponse> images;

    @JsonProperty("primaryImageUrl")
    private String primaryImageUrl;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("isAvailableForRental")
    private Boolean isAvailableForRental;
}
