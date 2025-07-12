package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("onboarding_sessions")
public class OnboardingSession extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @Column("session_token")
    @JsonProperty("session_token")
    private String sessionToken;

    @Column("current_step")
    @JsonProperty("current_step")
    private Integer currentStep = 1;

    @Column("max_step")
    @JsonProperty("max_step")
    private Integer maxStep = 3;

    @Column("is_completed")
    @JsonProperty("is_completed")
    private Boolean isCompleted = false;

    @Column("expires_at")
    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    // Données des étapes stockées en JSON
    @Column("owner_info")
    @JsonProperty("owner_info")
    private String ownerInfo;

    @Column("organization_info")
    @JsonProperty("organization_info")
    private String organizationInfo;

    @Column("subscription_info")
    @JsonProperty("subscription_info")
    private String subscriptionInfo;

    // Résultats finaux
    @Column("user_id")
    @JsonProperty("user_id")
    private UUID userId;

    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @Column("completed_at")
    @JsonProperty("completed_at")
    private LocalDateTime completedAt;

    // Constructors
    public OnboardingSession() {
        this.id = UUID.randomUUID();
        this.sessionToken = UUID.randomUUID().toString();
        this.expiresAt = LocalDateTime.now().plusHours(24);
        prePersist();
    }

    /**
     * Passe à l'étape suivante
     */
    public void nextStep() {
        if (currentStep < maxStep) {
            currentStep++;
        }
    }

    /**
     * Marque la session comme terminée
     */
    public void complete(UUID organizationId) {
        this.isCompleted = true;
        this.organizationId = organizationId;
        this.completedAt = LocalDateTime.now();
        this.currentStep = maxStep;
    }

    /**
     * Vérifie si la session a expiré
     */
    @JsonProperty("is_expired")
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Vérifie si la session est valide
     */
    @JsonProperty("is_valid")
    public boolean isValid() {
        return !isExpired() && !isCompleted;
    }

    /**
     * Met à jour les informations d'une étape
     */
    public void updateOwnerInfo(String ownerInfo) {
        this.ownerInfo = ownerInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateOrganizationInfo(String organizationInfo) {
        this.organizationInfo = organizationInfo;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSubscriptionInfo(String subscriptionInfo) {
        this.subscriptionInfo = subscriptionInfo;
        this.updatedAt = LocalDateTime.now();
    }
}
