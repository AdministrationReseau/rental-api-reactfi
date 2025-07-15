package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Map;
import java.util.UUID;

/**
 * DTO pour la création d'une agence
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAgencyRequest {

    @NotNull(message = "Organization ID is required")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @NotBlank(message = "Agency name is required")
    @Size(min = 2, max = 100, message = "Agency name must be between 2 and 100 characters")
    @JsonProperty("name")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @JsonProperty("description")
    private String description;

    // === ADRESSE ===
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @JsonProperty("city")
    private String city;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @JsonProperty("country")
    private String country = "CM";

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @JsonProperty("postal_code")
    private String postalCode;

    @Size(max = 100, message = "Region must not exceed 100 characters")
    @JsonProperty("region")
    private String region;

    // === CONTACT ===
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @JsonProperty("phone")
    private String phone;

    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String email;

    // === GÉOLOCALISATION ===
    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("geofence_zone_id")
    private String geofenceZoneId;

    @PositiveOrZero(message = "Geofence radius must be positive or zero")
    @JsonProperty("geofence_radius")
    private Double geofenceRadius;

    // === GESTIONNAIRE ===
    @JsonProperty("manager_id")
    private UUID managerId;

    // === CONFIGURATION ===
    @JsonProperty("is_24_hours")
    private Boolean is24Hours = false;

    @JsonProperty("timezone")
    private String timezone = "Africa/Douala";

    @JsonProperty("currency")
    private String currency = "XAF";

    @JsonProperty("language")
    private String language = "fr";

    // === HORAIRES DE TRAVAIL ===
    @JsonProperty("working_hours")
    private Map<String, WorkingHoursInfo> workingHours;

    // === PARAMÈTRES BUSINESS ===
    @JsonProperty("business_settings")
    private AgencyBusinessSettings businessSettings;
}
