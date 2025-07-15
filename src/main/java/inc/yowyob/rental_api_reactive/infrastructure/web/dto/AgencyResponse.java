package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO de réponse pour une agence
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("name")
    private String name;

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

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("full_address")
    private String fullAddress;

    @JsonProperty("geofence_zone_id")
    private String geofenceZoneId;

    @JsonProperty("geofence_radius")
    private Double geofenceRadius;

    @JsonProperty("has_location")
    private Boolean hasLocation;

    @JsonProperty("has_geofencing")
    private Boolean hasGeofencing;

    @JsonProperty("manager_id")
    private UUID managerId;

    @JsonProperty("is_24_hours")
    private Boolean is24Hours;

    @JsonProperty("is_currently_open")
    private Boolean isCurrentlyOpen;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("language")
    private String language;

    @JsonProperty("working_hours")
    private Map<String, WorkingHoursInfo> workingHours;

    @JsonProperty("allow_online_booking")
    private Boolean allowOnlineBooking;

    @JsonProperty("require_deposit")
    private Boolean requireDeposit;

    @JsonProperty("deposit_percentage")
    private Double depositPercentage;

    @JsonProperty("min_rental_hours")
    private Integer minRentalHours;

    @JsonProperty("max_advance_booking_days")
    private Integer maxAdvanceBookingDays;

    @JsonProperty("total_vehicles")
    private Integer totalVehicles;

    @JsonProperty("active_vehicles")
    private Integer activeVehicles;

    @JsonProperty("total_drivers")
    private Integer totalDrivers;

    @JsonProperty("active_drivers")
    private Integer activeDrivers;

    @JsonProperty("total_personnel")
    private Integer totalPersonnel;

    @JsonProperty("total_rentals")
    private Integer totalRentals;

    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue;

    @JsonProperty("vehicle_utilization_rate")
    private Double vehicleUtilizationRate;

    @JsonProperty("driver_activity_rate")
    private Double driverActivityRate;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("primary_color")
    private String primaryColor;

    @JsonProperty("secondary_color")
    private String secondaryColor;

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
