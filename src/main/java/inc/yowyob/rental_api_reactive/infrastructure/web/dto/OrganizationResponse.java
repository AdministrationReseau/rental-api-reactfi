package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.OrganizationType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour une organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("organization_type")
    private OrganizationType organizationType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("owner_id")
    private UUID ownerId;

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

    @JsonProperty("full_address")
    private String fullAddress;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

    @JsonProperty("website")
    private String website;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("verification_date")
    private LocalDateTime verificationDate;

    @JsonProperty("verified_by")
    private UUID verifiedBy;

    @JsonProperty("max_agencies")
    private Integer maxAgencies;

    @JsonProperty("max_vehicles")
    private Integer maxVehicles;

    @JsonProperty("max_drivers")
    private Integer maxDrivers;

    @JsonProperty("max_users")
    private Integer maxUsers;

    @JsonProperty("current_agencies")
    private Integer currentAgencies;

    @JsonProperty("current_vehicles")
    private Integer currentVehicles;

    @JsonProperty("current_drivers")
    private Integer currentDrivers;

    @JsonProperty("current_users")
    private Integer currentUsers;

    @JsonProperty("can_create_agency")
    private Boolean canCreateAgency;

    @JsonProperty("can_add_vehicle")
    private Boolean canAddVehicle;

    @JsonProperty("can_add_driver")
    private Boolean canAddDriver;

    @JsonProperty("can_add_user")
    private Boolean canAddUser;

    @JsonProperty("agency_usage_percentage")
    private Double agencyUsagePercentage;

    @JsonProperty("vehicle_usage_percentage")
    private Double vehicleUsagePercentage;

    @JsonProperty("driver_usage_percentage")
    private Double driverUsagePercentage;

    @JsonProperty("user_usage_percentage")
    private Double userUsagePercentage;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("language")
    private String language;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("primary_color")
    private String primaryColor;

    @JsonProperty("secondary_color")
    private String secondaryColor;

    @JsonProperty("subscription_plan_id")
    private UUID subscriptionPlanId;

    @JsonProperty("subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    @JsonProperty("subscription_auto_renew")
    private Boolean subscriptionAutoRenew;

    @JsonProperty("is_subscription_active")
    private Boolean isSubscriptionActive;

    @JsonProperty("is_subscription_expiring_soon")
    private Boolean isSubscriptionExpiringSoon;

    @JsonProperty("total_rentals")
    private Integer totalRentals;

    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue;

    @JsonProperty("yearly_revenue")
    private Double yearlyRevenue;

    @JsonProperty("last_activity_at")
    private LocalDateTime lastActivityAt;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private UUID createdBy;

    @JsonProperty("updated_by")
    private UUID updatedBy;
}
