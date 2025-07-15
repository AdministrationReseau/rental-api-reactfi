package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.OrganizationType;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO pour la mise à jour d'une organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrganizationRequest {

    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("organization_type")
    private OrganizationType organizationType;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @JsonProperty("description")
    private String description;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("tax_number")
    private String taxNumber;

    @JsonProperty("business_license")
    private String businessLicense;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("region")
    private String region;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @JsonProperty("phone")
    private String phone;

    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String email;

    @JsonProperty("website")
    private String website;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("language")
    private String language;

    @JsonProperty("primary_color")
    private String primaryColor;

    @JsonProperty("secondary_color")
    private String secondaryColor;

    @JsonProperty("policies")
    private OrganizationPolicies policies;

    @JsonProperty("settings")
    private OrganizationSettings settings;
}
