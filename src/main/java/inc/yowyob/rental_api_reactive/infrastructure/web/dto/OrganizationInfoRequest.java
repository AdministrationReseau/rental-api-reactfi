package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * DTO pour les informations de l'organisation (Étape 2)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationInfoRequest {

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @JsonProperty("organization_name")
    private String organizationName;

    @NotBlank(message = "Organization type is required")
    @JsonProperty("organization_type")
    private String organizationType;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("tax_number")
    private String taxNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @JsonProperty("city")
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @JsonProperty("country")
    private String country;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @JsonProperty("description")
    private String description;

    // Politiques de l'organisation
    @JsonProperty("allows_driver_rental")
    private Boolean allowsDriverRental = true;

    @JsonProperty("allows_driverless_rental")
    private Boolean allowsDriverlessRental = true;

    @JsonProperty("require_deposit")
    private Boolean requireDeposit = true;

    @JsonProperty("default_deposit_amount")
    private Double defaultDepositAmount;

    @JsonProperty("cancellation_policy")
    private String cancellationPolicy;
}
