package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.OrganizationType;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité Organization
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("organizations")
public class Organization extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    // === INFORMATIONS DE BASE ===
    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @Column("name")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Organization type is required")
    @Column("organization_type")
    @JsonProperty("organization_type")
    private OrganizationType organizationType;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column("description")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Owner ID is required")
    @Column("owner_id")
    @JsonProperty("owner_id")
    private UUID ownerId;

    // === INFORMATIONS LÉGALES ===
    @Column("registration_number")
    @JsonProperty("registration_number")
    private String registrationNumber;

    @Column("tax_number")
    @JsonProperty("tax_number")
    private String taxNumber;

    @Column("business_license")
    @JsonProperty("business_license")
    private String businessLicense;

    // === ADRESSE ===
    @NotBlank(message = "Address is required")
    @Column("address")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "City is required")
    @Column("city")
    @JsonProperty("city")
    private String city;

    @NotBlank(message = "Country is required")
    @Column("country")
    @JsonProperty("country")
    private String country = "CM";

    @Column("postal_code")
    @JsonProperty("postal_code")
    private String postalCode;

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

    @Column("website")
    @JsonProperty("website")
    private String website;

    // === VÉRIFICATION ===
    @Column("is_verified")
    @JsonProperty("is_verified")
    private Boolean isVerified = false;

    @Column("verification_date")
    @JsonProperty("verification_date")
    private LocalDateTime verificationDate;

    @Column("verified_by")
    @JsonProperty("verified_by")
    private UUID verifiedBy;

    // === LIMITES D'ABONNEMENT ===
    @Column("max_agencies")
    @JsonProperty("max_agencies")
    private Integer maxAgencies = 1;

    @Column("max_vehicles")
    @JsonProperty("max_vehicles")
    private Integer maxVehicles = 10;

    @Column("max_drivers")
    @JsonProperty("max_drivers")
    private Integer maxDrivers = 5;

    @Column("max_users")
    @JsonProperty("max_users")
    private Integer maxUsers = 10;

    // === COMPTEURS ACTUELS ===
    @Column("current_agencies")
    @JsonProperty("current_agencies")
    private Integer currentAgencies = 0;

    @Column("current_vehicles")
    @JsonProperty("current_vehicles")
    private Integer currentVehicles = 0;

    @Column("current_drivers")
    @JsonProperty("current_drivers")
    private Integer currentDrivers = 0;

    @Column("current_users")
    @JsonProperty("current_users")
    private Integer currentUsers = 0;

    // === CONFIGURATION ===
    @Column("currency")
    @JsonProperty("currency")
    private String currency = "XAF";

    @Column("timezone")
    @JsonProperty("timezone")
    private String timezone = "Africa/Douala";

    @Column("language")
    @JsonProperty("language")
    private String language = "fr";

    // === BRANDING ===
    @Column("logo_url")
    @JsonProperty("logo_url")
    private String logoUrl;

    @Column("primary_color")
    @JsonProperty("primary_color")
    private String primaryColor = "#3b82f6";

    @Column("secondary_color")
    @JsonProperty("secondary_color")
    private String secondaryColor = "#1e40af";

    // === POLITIQUES (stockées en JSON) ===
    @Column("policies")
    @JsonProperty("policies")
    private String policies; // JSON des politiques d'organisation

    // === PARAMÈTRES (stockés en JSON) ===
    @Column("settings")
    @JsonProperty("settings")
    private String settings; // JSON des paramètres d'organisation

    // === ABONNEMENT ===
    @Column("subscription_plan_id")
    @JsonProperty("subscription_plan_id")
    private UUID subscriptionPlanId;

    @Column("subscription_expires_at")
    @JsonProperty("subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    @Column("subscription_auto_renew")
    @JsonProperty("subscription_auto_renew")
    private Boolean subscriptionAutoRenew = true;

    // === STATISTIQUES ===
    @Column("total_rentals")
    @JsonProperty("total_rentals")
    private Integer totalRentals = 0;

    @Column("monthly_revenue")
    @JsonProperty("monthly_revenue")
    private Double monthlyRevenue = 0.0;

    @Column("yearly_revenue")
    @JsonProperty("yearly_revenue")
    private Double yearlyRevenue = 0.0;

    @Column("last_activity_at")
    @JsonProperty("last_activity_at")
    private LocalDateTime lastActivityAt;

    // Constructors
    public Organization(String name, OrganizationType type, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.organizationType = type;
        this.ownerId = ownerId;
        this.lastActivityAt = LocalDateTime.now();
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
     * Vérifie si l'organisation peut créer une nouvelle agence
     */
    @JsonProperty("can_create_agency")
    public boolean canCreateAgency() {
        return currentAgencies != null && maxAgencies != null && currentAgencies < maxAgencies;
    }

    /**
     * Vérifie si l'organisation peut ajouter un véhicule
     */
    @JsonProperty("can_add_vehicle")
    public boolean canAddVehicle() {
        return currentVehicles != null && maxVehicles != null && currentVehicles < maxVehicles;
    }

    /**
     * Vérifie si l'organisation peut ajouter un chauffeur
     */
    @JsonProperty("can_add_driver")
    public boolean canAddDriver() {
        return currentDrivers != null && maxDrivers != null && currentDrivers < maxDrivers;
    }

    /**
     * Vérifie si l'organisation peut ajouter un utilisateur
     */
    @JsonProperty("can_add_user")
    public boolean canAddUser() {
        return currentUsers != null && maxUsers != null && currentUsers < maxUsers;
    }

    /**
     * Calcul du pourcentage d'utilisation des agences
     */
    @JsonProperty("agency_usage_percentage")
    public double getAgencyUsagePercentage() {
        if (maxAgencies == null || maxAgencies == 0) return 0.0;
        return (currentAgencies != null ? currentAgencies : 0) * 100.0 / maxAgencies;
    }

    /**
     * Calcul du pourcentage d'utilisation des véhicules
     */
    @JsonProperty("vehicle_usage_percentage")
    public double getVehicleUsagePercentage() {
        if (maxVehicles == null || maxVehicles == 0) return 0.0;
        return (currentVehicles != null ? currentVehicles : 0) * 100.0 / maxVehicles;
    }

    /**
     * Calcul du pourcentage d'utilisation des chauffeurs
     */
    @JsonProperty("driver_usage_percentage")
    public double getDriverUsagePercentage() {
        if (maxDrivers == null || maxDrivers == 0) return 0.0;
        return (currentDrivers != null ? currentDrivers : 0) * 100.0 / maxDrivers;
    }

    /**
     * Calcul du pourcentage d'utilisation des utilisateurs
     */
    @JsonProperty("user_usage_percentage")
    public double getUserUsagePercentage() {
        if (maxUsers == null || maxUsers == 0) return 0.0;
        return (currentUsers != null ? currentUsers : 0) * 100.0 / maxUsers;
    }

    /**
     * Vérifie si l'abonnement est actif
     */
    @JsonProperty("is_subscription_active")
    public boolean isSubscriptionActive() {
        return subscriptionExpiresAt != null && subscriptionExpiresAt.isAfter(LocalDateTime.now());
    }

    /**
     * Vérifie si l'abonnement expire bientôt (dans les 30 jours)
     */
    @JsonProperty("is_subscription_expiring_soon")
    public boolean isSubscriptionExpiringSoon() {
        return subscriptionExpiresAt != null &&
            subscriptionExpiresAt.isAfter(LocalDateTime.now()) &&
            subscriptionExpiresAt.isBefore(LocalDateTime.now().plusDays(30));
    }

    /**
     * Met à jour l'activité de l'organisation
     */
    public void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
        preUpdate();
    }

    /**
     * Incrémente le compteur d'agences
     */
    public void incrementAgencies() {
        this.currentAgencies = (currentAgencies != null ? currentAgencies : 0) + 1;
        updateActivity();
    }

    /**
     * Décrémente le compteur d'agences
     */
    public void decrementAgencies() {
        this.currentAgencies = Math.max(0, (currentAgencies != null ? currentAgencies : 0) - 1);
        updateActivity();
    }

    /**
     * Met à jour les compteurs de ressources
     */
    public void updateResourceCounters(int agencies, int vehicles, int drivers, int users) {
        this.currentAgencies = agencies;
        this.currentVehicles = vehicles;
        this.currentDrivers = drivers;
        this.currentUsers = users;
        updateActivity();
    }

    /**
     * Met à jour les statistiques financières
     */
    public void updateFinancialStats(double monthlyRevenue, double yearlyRevenue, int totalRentals) {
        this.monthlyRevenue = monthlyRevenue;
        this.yearlyRevenue = yearlyRevenue;
        this.totalRentals = totalRentals;
        updateActivity();
    }

    /**
     * Vérifie l'organisation
     */
    public void verify(UUID verifiedBy) {
        this.isVerified = true;
        this.verificationDate = LocalDateTime.now();
        this.verifiedBy = verifiedBy;
        preUpdate();
    }

    /**
     * Met à jour l'abonnement
     */
    public void updateSubscription(UUID planId, LocalDateTime expiresAt, boolean autoRenew) {
        this.subscriptionPlanId = planId;
        this.subscriptionExpiresAt = expiresAt;
        this.subscriptionAutoRenew = autoRenew;
        preUpdate();
    }
}
