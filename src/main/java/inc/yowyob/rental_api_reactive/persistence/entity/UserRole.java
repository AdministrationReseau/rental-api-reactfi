package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("user_roles")
public class UserRole {

    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
    @JsonProperty("user_id")
    private UUID userId;

    @PrimaryKeyColumn(name = "role_id", type = PrimaryKeyType.CLUSTERED)
    @JsonProperty("role_id")
    private UUID roleId;

    @NotNull(message = "Organization ID is required")
    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @Column("assigned_at")
    @JsonProperty("assigned_at")
    private LocalDateTime assignedAt;

    @Column("assigned_by")
    @JsonProperty("assigned_by")
    private UUID assignedBy;

    @Column("expires_at")
    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    @Column("is_active")
    @JsonProperty("is_active")
    private Boolean isActive = true;

    // Constructors
    public UserRole(UUID userId, UUID roleId, UUID organizationId, UUID assignedBy) {
        this.userId = userId;
        this.roleId = roleId;
        this.organizationId = organizationId;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si l'assignation est encore valide
     */
    @JsonProperty("is_valid")
    public boolean isValid() {
        return isActive && (expiresAt == null || LocalDateTime.now().isBefore(expiresAt));
    }
}
