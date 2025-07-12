package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("organizations")
public class Organization extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 100, message = "Organization name must be between 2 and 100 characters")
    @Column("name")
    @JsonProperty("name")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column("description")
    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Business type is required")
    @Column("business_type")
    @JsonProperty("business_type")
    private String businessType;

    @Column("registration_number")
    @JsonProperty("registration_number")
    private String registrationNumber;

    @Column("tax_number")
    @JsonProperty("tax_number")
    private String taxNumber;

    @NotNull(message = "Owner ID is required")
    @Column("owner_id")
    @JsonProperty("owner_id")
    private UUID ownerId;

    // Informations de contact
    @Email(message = "Email should be valid")
    @Column("contact_email")
    @JsonProperty("contact_email")
    private String contactEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column("contact_phone")
    @JsonProperty("contact_phone")
    private String contactPhone;

    @Column("website")
    @JsonProperty("website")
    private String website;

    // Adresse
    @Column("address_line1")
    @JsonProperty("address_line1")
    private String addressLine1;

    @Column("address_line2")
    @JsonProperty("address_line2")
    private String addressLine2;

    @Column("city")
    @JsonProperty("city")
    private String city;

    @Column("state_province")
    @JsonProperty("state_province")
    private String stateProvince;

    @Column("postal_code")
    @JsonProperty("postal_code")
    private String postalCode;

    @Column("country")
    @JsonProperty("country")
    private String country = "CM";

    // Limites d'abonnement
    @Column("max_vehicles")
    @JsonProperty("max_vehicles")
    private Integer maxVehicles = 10;

    @Column("max_drivers")
    @JsonProperty("max_drivers")
    private Integer maxDrivers = 5;

    @Column("max_agencies")
    @JsonProperty("max_agencies")
    private Integer maxAgencies = 1;

    @Column("max_users")
    @JsonProperty("max_users")
    private Integer maxUsers = 10;

    // Paramètres
    @Column("currency")
    @JsonProperty("currency")
    private String currency = "XAF";

    @Column("timezone")
    @JsonProperty("timezone")
    private String timezone = "Africa/Douala";

    @Column("default_language")
    @JsonProperty("default_language")
    private String defaultLanguage = "fr";

    // Métadonnées
    @Column("logo_url")
    @JsonProperty("logo_url")
    private String logoUrl;

    @Column("is_verified")
    @JsonProperty("is_verified")
    private Boolean isVerified = false;

    @Column("verification_date")
    @JsonProperty("verification_date")
    private LocalDateTime verificationDate;

    @Column("subscription_id")
    @JsonProperty("subscription_id")
    private UUID subscriptionId;

    // Constructors
    public Organization(String name, String businessType, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.businessType = businessType;
        this.ownerId = ownerId;
        prePersist();
    }

    /**
     * Vérifie si l'organisation peut ajouter plus de véhicules
     */
    @JsonProperty("can_add_vehicles")
    public boolean canAddVehicles() {
        // Cette logique sera implémentée avec le comptage réel des véhicules
        return true;
    }

    /**
     * Vérifie si l'organisation peut ajouter plus de chauffeurs
     */
    @JsonProperty("can_add_drivers")
    public boolean canAddDrivers() {
        // Cette logique sera implémentée avec le comptage réel des chauffeurs
        return true;
    }

    /**
     * Obtient l'adresse complète
     */
    @JsonProperty("full_address")
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null) address.append(addressLine1);
        if (addressLine2 != null) address.append(", ").append(addressLine2);
        if (city != null) address.append(", ").append(city);
        if (stateProvince != null) address.append(", ").append(stateProvince);
        if (postalCode != null) address.append(" ").append(postalCode);
        if (country != null) address.append(", ").append(country);
        return address.toString();
    }
}
