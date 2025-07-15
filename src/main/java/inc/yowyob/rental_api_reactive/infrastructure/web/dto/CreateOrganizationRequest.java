package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.OrganizationType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

/**
 * DTO pour la création d'une organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrganizationRequest {

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Organization type is required")
    @JsonProperty("organization_type")
    private OrganizationType organizationType;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Owner ID is required")
    @JsonProperty("owner_id")
    private UUID ownerId;

    // === INFORMATIONS LÉGALES ===
    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("tax_number")
    private String taxNumber;

    @JsonProperty("business_license")
    private String businessLicense;

    // === ADRESSE ===
    @NotBlank(message = "Address is required")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "City is required")
    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country = "CM";

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("region")
    private String region;

    // === CONTACT ===
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @JsonProperty("phone")
    private String phone;

    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String email;

    @JsonProperty("website")
    private String website;

    // === CONFIGURATION ===
    @JsonProperty("currency")
    private String currency = "XAF";

    @JsonProperty("timezone")
    private String timezone = "Africa/Douala";

    @JsonProperty("language")
    private String language = "fr";

    // === BRANDING ===
    @JsonProperty("primary_color")
    private String primaryColor = "#3b82f6";

    @JsonProperty("secondary_color")
    private String secondaryColor = "#1e40af";

    // === POLITIQUES ET PARAMÈTRES ===
    @JsonProperty("policies")
    private OrganizationPolicies policies;

    @JsonProperty("settings")
    private OrganizationSettings settings;
}
