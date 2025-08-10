package inc.yowyob.rental_api_reactive.persistence.entity.booking;

import com.fasterxml.jackson.annotation.JsonProperty;

import inc.yowyob.rental_api_reactive.application.dto.booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("bookings")
public class Booking {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @Column("vehicle_id")
    @JsonProperty("vehicle_id")
    private UUID vehicleId;

    @Column("user_id")
    @JsonProperty("user_id")
    private UUID userId;

    @Column("start_date")
    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @Column("end_date")
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @Column("with_driver")
    @JsonProperty("with_driver")
    private boolean withDriver;

    @Column("total_price")
    @JsonProperty("total_price")
    private BigDecimal totalPrice; // Utiliser BigDecimal pour les montants monétaires

    @Column("status")
    @JsonProperty("status")
    private BookingStatus status;

    @Column("created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Ajoutez les champs pour les politiques si vous les utilisez
    @Column("rental_policy")
    @JsonProperty("rental_policy")
    private String rentalPolicy; // Stockage JSON si nécessaire (utiliser JsonConverter)
}
