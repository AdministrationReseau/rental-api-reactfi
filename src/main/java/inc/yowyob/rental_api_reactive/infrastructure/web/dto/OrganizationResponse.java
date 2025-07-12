package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrganizationResponse {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("business_type")
    private String businessType;

    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("owner_id")
    private UUID ownerId;

    @JsonProperty("contact_email")
    private String contactEmail;

    @JsonProperty("contact_phone")
    private String contactPhone;

    @JsonProperty("website")
    private String website;

    @JsonProperty("full_address")
    private String fullAddress;

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private String country;

    @JsonProperty("max_vehicles")
    private Integer maxVehicles;

    @JsonProperty("max_drivers")
    private Integer maxDrivers;

    @JsonProperty("max_agencies")
    private Integer maxAgencies;

    @JsonProperty("max_users")
    private Integer maxUsers;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("verification_date")
    private LocalDateTime verificationDate;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("is_active")
    private Boolean isActive;
}
