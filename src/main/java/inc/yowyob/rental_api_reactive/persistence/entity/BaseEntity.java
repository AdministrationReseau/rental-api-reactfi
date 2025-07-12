package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;

import java.time.LocalDateTime;


@Data
public abstract class BaseEntity {

    @Column("created_at")
    @JsonProperty("created_at")
    protected LocalDateTime createdAt;

    @Column("updated_at")
    @JsonProperty("updated_at")
    protected LocalDateTime updatedAt;

    @Column("is_active")
    @JsonProperty("is_active")
    protected Boolean isActive;


    // Méthode pour initialiser les valeurs avant la persistance
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
