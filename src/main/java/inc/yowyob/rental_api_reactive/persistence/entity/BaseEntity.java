package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class BaseEntity {

    @Column("is_active")
    @JsonProperty("is_active")
    protected Boolean isActive = true;

    @Column("created_at")
    @JsonProperty("created_at")
    protected LocalDateTime createdAt;

    @Column("updated_at")
    @JsonProperty("updated_at")
    protected LocalDateTime updatedAt;

    /**
     * Méthode appelée avant la persistance
     */
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (isActive == null) {
            isActive = true;
        }
    }

    /**
     * Méthode appelée avant la mise à jour
     */
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
