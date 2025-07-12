package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.RoleType;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("roles")
public class Role extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
    @Column("name")
    @JsonProperty("name")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column("description")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Organization ID is required")
    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @Column("role_type")
    @JsonProperty("role_type")
    private RoleType roleType = RoleType.CUSTOM;

    @Column("is_system_role")
    @JsonProperty("is_system_role")
    private Boolean isSystemRole = false;

    @Column("is_default_role")
    @JsonProperty("is_default_role")
    private Boolean isDefaultRole = false;

    @Column("priority")
    @JsonProperty("priority")
    private Integer priority = 0;

    @Column("permissions")
    @JsonProperty("permissions")
    private Set<String> permissions = new HashSet<>();

    @Column("color")
    @JsonProperty("color")
    private String color;

    @Column("icon")
    @JsonProperty("icon")
    private String icon;

    @Column("created_by")
    @JsonProperty("created_by")
    private UUID createdBy;

    @Column("updated_by")
    @JsonProperty("updated_by")
    private UUID updatedBy;

    // Constructors
    public Role(String name, String description, UUID organizationId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.organizationId = organizationId;
        prePersist();
    }

    public Role(String name, String description, UUID organizationId, RoleType roleType) {
        this(name, description, organizationId);
        this.roleType = roleType;
        this.isSystemRole = roleType != null && roleType.isSystemRole();
    }

    /**
     * Ajoute une permission au rôle
     */
    public void addPermission(String permissionCode) {
        if (permissionCode != null && !permissionCode.trim().isEmpty()) {
            if (this.permissions == null) {
                this.permissions = new HashSet<>();
            }
            this.permissions.add(permissionCode);
        }
    }

    /**
     * Supprime une permission du rôle
     */
    public void removePermission(String permissionCode) {
        if (this.permissions != null) {
            this.permissions.remove(permissionCode);
        }
    }

    /**
     * Vérifie si le rôle a une permission spécifique
     */
    @JsonProperty("has_permission")
    public boolean hasPermission(String permissionCode) {
        return this.permissions != null && this.permissions.contains(permissionCode);
    }

    /**
     * Vérifie si le rôle peut être modifié
     */
    @JsonProperty("can_be_modified")
    public boolean canBeModified() {
        return !isSystemRole;
    }

    /**
     * Vérifie si le rôle peut être supprimé
     */
    @JsonProperty("can_be_deleted")
    public boolean canBeDeleted() {
        return !isSystemRole && !isDefaultRole;
    }
}
