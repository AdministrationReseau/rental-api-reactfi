package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("subscription_plans")
public class SubscriptionPlan extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotBlank(message = "Plan name is required")
    @Size(min = 2, max = 100, message = "Plan name must be between 2 and 100 characters")
    @Column("name")
    @JsonProperty("name")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column("description")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column("price")
    @JsonProperty("price")
    private BigDecimal price;

    @NotBlank(message = "Currency is required")
    @Column("currency")
    @JsonProperty("currency")
    private String currency = "XAF";

    @NotNull(message = "Duration in days is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    @Column("duration_days")
    @JsonProperty("duration_days")
    private Integer durationDays;

    // Limites du plan
    @Column("max_vehicles")
    @JsonProperty("max_vehicles")
    private Integer maxVehicles;

    @Column("max_drivers")
    @JsonProperty("max_drivers")
    private Integer maxDrivers;

    @Column("max_agencies")
    @JsonProperty("max_agencies")
    private Integer maxAgencies;

    @Column("max_users")
    @JsonProperty("max_users")
    private Integer maxUsers;

    // Fonctionnalités
    @Column("features")
    @JsonProperty("features")
    private Map<String, Object> features;

    @Column("is_popular")
    @JsonProperty("is_popular")
    private Boolean isPopular = false;

    @Column("is_custom")
    @JsonProperty("is_custom")
    private Boolean isCustom = false;

    @Column("sort_order")
    @JsonProperty("sort_order")
    private Integer sortOrder = 0;

    // Constructors
    public SubscriptionPlan(String name, BigDecimal price, String currency, Integer durationDays) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.durationDays = durationDays;
        prePersist();
    }

    /**
     * Vérifie si le plan est gratuit
     */
    @JsonProperty("is_free")
    public boolean isFree() {
        return price.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Calcule le prix par jour
     */
    @JsonProperty("price_per_day")
    public BigDecimal getPricePerDay() {
        return price.divide(BigDecimal.valueOf(durationDays), 2, BigDecimal.ROUND_HALF_UP);
    }
}
