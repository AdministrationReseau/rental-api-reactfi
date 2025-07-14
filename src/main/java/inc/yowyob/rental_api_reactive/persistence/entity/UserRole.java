package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("user_roles")
public class UserRole extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column("user_id")
    @JsonProperty("user_id")
    private UUID userId;

    @NotNull(message = "Role ID is required")
    @Column("role_id")
    @JsonProperty("role_id")
    private UUID roleId;

    @NotNull(message = "Organization ID is required")
    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @Column("agency_id")
    @JsonProperty("agency_id")
    private UUID agencyId;

    @Column("assigned_at")
    @JsonProperty("assigned_at")
    private LocalDateTime assignedAt;

    @Column("expires_at")
    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    @Column("assigned_by")
    @JsonProperty("assigned_by")
    private UUID assignedBy;

    @Column("revoked_at")
    @JsonProperty("revoked_at")
    private LocalDateTime revokedAt;

    @Column("revoked_by")
    @JsonProperty("revoked_by")
    private UUID revokedBy;

    // Constructors
    public UserRole(UUID userId, UUID roleId, UUID organizationId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.roleId = roleId;
        this.organizationId = organizationId;
        this.assignedAt = LocalDateTime.now();
        prePersist();
    }

    public UserRole(UUID userId, UUID roleId, UUID organizationId, UUID agencyId) {
        this(userId, roleId, organizationId);
        this.agencyId = agencyId;
    }

    /**
     * Vérifie si l'assignation est expirée
     */
    @JsonProperty("is_expired")
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Vérifie si l'assignation est active (utilise isActive de BaseEntity)
     */
    @JsonProperty("is_currently_active")
    public boolean isCurrentlyActive() {
        return getIsActive() && !isExpired();
    }

    /**
     * Révoque l'assignation du rôle
     */
    public void revoke(UUID revokedBy) {
        this.setIsActive(false);
        this.revokedAt = LocalDateTime.now();
        this.revokedBy = revokedBy;
        preUpdate();
    }

    /**
     * Active l'assignation du rôle
     */
    public void activate() {
        this.setIsActive(true);
        this.revokedAt = null;
        this.revokedBy = null;
        preUpdate();
    }

    /**
     * Définit une date d'expiration
     */
    public void setExpiration(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        preUpdate();
    }

    /**
     * Vérifie si l'assignation a été révoquée
     */
    @JsonProperty("is_revoked")
    public boolean isRevoked() {
        return revokedAt != null && revokedBy != null;
    }

    /**
     * Vérifie si l'assignation est temporaire (avec date d'expiration)
     */
    @JsonProperty("is_temporary")
    public boolean isTemporary() {
        return expiresAt != null;
    }

    /**
     * Obtient le nombre de jours restants avant expiration
     */
    @JsonProperty("days_until_expiration")
    public Long getDaysUntilExpiration() {
        if (expiresAt == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), expiresAt);
    }

    /**
     * Vérifie si l'assignation expire bientôt (dans les 7 prochains jours)
     */
    @JsonProperty("expires_soon")
    public boolean expiresSoon() {
        if (expiresAt == null) {
            return false;
        }
        Long daysUntilExpiration = getDaysUntilExpiration();
        return daysUntilExpiration != null && daysUntilExpiration <= 7 && daysUntilExpiration > 0;
    }
}
