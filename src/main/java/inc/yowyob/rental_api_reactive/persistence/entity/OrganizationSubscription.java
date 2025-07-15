package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.SubscriptionStatus;
import inc.yowyob.rental_api_reactive.application.dto.PaymentMethod;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("organization_subscriptions")
public class OrganizationSubscription extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotNull(message = "Organization ID is required")
    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @NotNull(message = "Subscription plan ID is required")
    @Column("subscription_plan_id")
    @JsonProperty("subscription_plan_id")
    private UUID subscriptionPlanId;

    @NotNull(message = "Status is required")
    @Column("status")
    @JsonProperty("status")
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @NotNull(message = "Start date is required")
    @Column("start_date")
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Column("end_date")
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @Column("auto_renew")
    @JsonProperty("auto_renew")
    private Boolean autoRenew = true;

    @Column("payment_method")
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @Column("payment_reference")
    @JsonProperty("payment_reference")
    private String paymentReference;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be greater than or equal to 0")
    @Column("amount")
    @JsonProperty("amount")
    private BigDecimal amount;

    @Column("currency")
    @JsonProperty("currency")
    private String currency = "XAF";

    @Column("next_billing_date")
    @JsonProperty("next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column("cancelled_at")
    @JsonProperty("cancelled_at")
    private LocalDateTime cancelledAt;

    @Column("cancelled_by")
    @JsonProperty("cancelled_by")
    private UUID cancelledBy;

    @Column("cancellation_reason")
    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    // Constructors
    public OrganizationSubscription(UUID organizationId, UUID subscriptionPlanId, BigDecimal amount, PaymentMethod paymentMethod) {
        this.id = UUID.randomUUID();
        this.organizationId = organizationId;
        this.subscriptionPlanId = subscriptionPlanId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        prePersist();
    }

    /**
     * Vérifie si l'abonnement est actif
     */
    @JsonProperty("is_active")
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE &&
            LocalDateTime.now().isBefore(endDate);
    }

    /**
     * Vérifie si l'abonnement a expiré
     */
    @JsonProperty("is_expired")
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    /**
     * Calcule les jours restants
     */
    @JsonProperty("days_remaining")
    public long getDaysRemaining() {
        if (isExpired()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
    }
}
