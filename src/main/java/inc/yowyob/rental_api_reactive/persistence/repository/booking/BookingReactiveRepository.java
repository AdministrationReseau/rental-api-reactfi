package inc.yowyob.rental_api_reactive.persistence.repository.booking;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import inc.yowyob.rental_api_reactive.persistence.entity.booking.Booking;
import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BookingReactiveRepository extends ReactiveCassandraRepository<Booking, UUID> {

    @Query("SELECT * FROM bookings WHERE user_id = ?0")
    Flux<Booking> findByUserId(UUID userId);

    @Query("SELECT * FROM bookings WHERE vehicle_id = ?0")
    Flux<Booking> findByVehicleId(UUID vehicleId);
}