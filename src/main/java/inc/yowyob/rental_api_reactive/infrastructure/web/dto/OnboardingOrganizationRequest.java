package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Requête d'informations organisation pour l'onboarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingOrganizationRequest {
    @JsonProperty("name")
    private String name;

    @JsonProperty("organization_type")
    private inc.yowyob.rental_api_reactive.application.dto.OrganizationType organizationType;

    @JsonProperty("description")
    private String description;

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

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("website")
    private String website;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("tax_number")
    private String taxNumber;

    @JsonProperty("business_license")
    private String businessLicense;

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
}
