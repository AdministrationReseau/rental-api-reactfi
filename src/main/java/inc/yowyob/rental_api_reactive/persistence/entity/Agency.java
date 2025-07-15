package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entité Agency pour la gestion des agences
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("agencies")
public class Agency extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotNull(message = "Organization ID is required")
    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @NotBlank(message = "Agency name is required")
    @Size(min = 2, max = 100, message = "Agency name must be between 2 and 100 characters")
    @Column("name")
    @JsonProperty("name")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column("description")
    @JsonProperty("description")
    private String description;

    // === ADRESSE ===
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column("address")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column("city")
    @JsonProperty("city")
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column("country")
    @JsonProperty("country")
    private String country = "CM";

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column("postal_code")
    @JsonProperty("postal_code")
    private String postalCode;

    @Size(max = 100, message = "Region must not exceed 100 characters")
    @Column("region")
    @JsonProperty("region")
    private String region;

    // === CONTACT ===
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column("phone")
    @JsonProperty("phone")
    private String phone;

    @Email(message = "Email should be valid")
    @Column("email")
    @JsonProperty("email")
    private String email;

    // === GÉOLOCALISATION ===
    @Column("latitude")
    @JsonProperty("latitude")
    private Double latitude;

    @Column("longitude")
    @JsonProperty("longitude")
    private Double longitude;

    @Column("geofence_zone_id")
    @JsonProperty("geofence_zone_id")
    private String geofenceZoneId;

    @PositiveOrZero(message = "Geofence radius must be positive or zero")
    @Column("geofence_radius")
    @JsonProperty("geofence_radius")
    private Double geofenceRadius;

    // === GESTIONNAIRE ===
    @Column("manager_id")
    @JsonProperty("manager_id")
    private UUID managerId;

    // === CONFIGURATION ===
    @Column("is_24_hours")
    @JsonProperty("is_24_hours")
    private Boolean is24Hours = false;

    @Column("timezone")
    @JsonProperty("timezone")
    private String timezone = "Africa/Douala";

    @Column("currency")
    @JsonProperty("currency")
    private String currency = "XAF";

    @Column("language")
    @JsonProperty("language")
    private String language = "fr";

    // === HORAIRES D'OUVERTURE (stockés en JSON) ===
    @Column("working_hours")
    @JsonProperty("working_hours")
    private String workingHours; // JSON format: {"MONDAY": {"open": "08:00", "close": "18:00"}}

    // === PARAMÈTRES BUSINESS ===
    @Column("allow_online_booking")
    @JsonProperty("allow_online_booking")
    private Boolean allowOnlineBooking = true;

    @Column("require_deposit")
    @JsonProperty("require_deposit")
    private Boolean requireDeposit = true;

    @Column("deposit_percentage")
    @JsonProperty("deposit_percentage")
    private Double depositPercentage = 30.0;

    @Column("min_rental_hours")
    @JsonProperty("min_rental_hours")
    private Integer minRentalHours = 1;

    @Column("max_advance_booking_days")
    @JsonProperty("max_advance_booking_days")
    private Integer maxAdvanceBookingDays = 30;

    // === STATISTIQUES ===
    @Column("total_vehicles")
    @JsonProperty("total_vehicles")
    private Integer totalVehicles = 0;

    @Column("active_vehicles")
    @JsonProperty("active_vehicles")
    private Integer activeVehicles = 0;

    @Column("total_drivers")
    @JsonProperty("total_drivers")
    private Integer totalDrivers = 0;

    @Column("active_drivers")
    @JsonProperty("active_drivers")
    private Integer activeDrivers = 0;

    @Column("total_personnel")
    @JsonProperty("total_personnel")
    private Integer totalPersonnel = 0;

    @Column("total_rentals")
    @JsonProperty("total_rentals")
    private Integer totalRentals = 0;

    @Column("monthly_revenue")
    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue = 0.0;

    // === MÉTADONNÉES ===
    @Column("logo_url")
    @JsonProperty("logo_url")
    private String logoUrl;

    @Column("primary_color")
    @JsonProperty("primary_color")
    private String primaryColor = "#3b82f6";

    @Column("secondary_color")
    @JsonProperty("secondary_color")
    private String secondaryColor = "#1e40af";

    // Constructors
    public Agency(UUID organizationId, String name, String address, String city) {
        this.id = UUID.randomUUID();
        this.organizationId = organizationId;
        this.name = name;
        this.address = address;
        this.city = city;
        prePersist();
    }

    // === MÉTHODES UTILITAIRES ===

    /**
     * Obtient l'adresse complète
     */
    @JsonProperty("full_address")
    public String getFullAddress() {
        StringBuilder address = new StringBuilder(this.address);
        if (city != null) address.append(", ").append(city);
        if (region != null) address.append(", ").append(region);
        if (country != null) address.append(", ").append(country);
        return address.toString();
    }

    /**
     * Vérifie si l'agence est ouverte actuellement
     */
    @JsonProperty("is_currently_open")
    public boolean isCurrentlyOpen() {
        if (is24Hours) return true;

        // Logique d'horaires à implémenter selon les working_hours
        LocalTime now = LocalTime.now();
        // TODO: Implémenter la vérification des horaires selon le jour
        return true;
    }

    /**
     * Vérifie si l'agence a une géolocalisation définie
     */
    @JsonProperty("has_location")
    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    /**
     * Vérifie si l'agence a un géofencing configuré
     */
    @JsonProperty("has_geofencing")
    public boolean hasGeofencing() {
        return geofenceZoneId != null || (hasLocation() && geofenceRadius != null && geofenceRadius > 0);
    }

    /**
     * Calcule le taux d'occupation des véhicules
     */
    @JsonProperty("vehicle_utilization_rate")
    public double getVehicleUtilizationRate() {
        if (totalVehicles == null || totalVehicles == 0) return 0.0;
        return (activeVehicles != null ? activeVehicles : 0) * 100.0 / totalVehicles;
    }

    /**
     * Calcule le taux d'activité des chauffeurs
     */
    @JsonProperty("driver_activity_rate")
    public double getDriverActivityRate() {
        if (totalDrivers == null || totalDrivers == 0) return 0.0;
        return (activeDrivers != null ? activeDrivers : 0) * 100.0 / totalDrivers;
    }

    /**
     * Met à jour les statistiques des véhicules
     */
    public void updateVehicleStats(int total, int active) {
        this.totalVehicles = total;
        this.activeVehicles = active;
        preUpdate();
    }

    /**
     * Met à jour les statistiques des chauffeurs
     */
    public void updateDriverStats(int total, int active) {
        this.totalDrivers = total;
        this.activeDrivers = active;
        preUpdate();
    }

    /**
     * Met à jour le nombre de personnel
     */
    public void updatePersonnelCount(int count) {
        this.totalPersonnel = count;
        preUpdate();
    }

    /**
     * Incrémente le nombre de locations
     */
    public void incrementRentals() {
        this.totalRentals = (totalRentals != null ? totalRentals : 0) + 1;
        preUpdate();
    }

    /**
     * Met à jour le chiffre d'affaires mensuel
     */
    public void updateMonthlyRevenue(double revenue) {
        this.monthlyRevenue = revenue;
        preUpdate();
    }
}
