package inc.yowyob.rental_api_reactive.application.service.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.time.LocalDateTime;

import inc.yowyob.rental_api_reactive.application.dto.booking.BookingDTO;
import inc.yowyob.rental_api_reactive.application.dto.booking.BookingStatus;
import inc.yowyob.rental_api_reactive.application.dto.util.Money;
import inc.yowyob.rental_api_reactive.persistence.entity.booking.Booking;
import inc.yowyob.rental_api_reactive.persistence.mapper.booking.BookingMapper;
import inc.yowyob.rental_api_reactive.persistence.repository.booking.BookingReactiveRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingReactiveService {

    private final BookingReactiveRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public Mono<BookingDTO> createBooking(UUID vehicleId, UUID userId, LocalDateTime startDate, LocalDateTime endDate, boolean withDriver, Money totalPrice) {
        log.info("Creating booking for vehicle {}, user {}, from {} to {} with driver {}",
            vehicleId, userId, startDate, endDate, withDriver);

        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setVehicleId(vehicleId);
        booking.setUserId(userId);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setWithDriver(withDriver);
        booking.setTotalPrice(totalPrice.getAmount());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());


        return bookingRepository.save(booking)
            .map(bookingMapper::toDto)
            .doOnSuccess(createdBooking ->
                log.info("Booking created with id {}", createdBooking.getId()));
    }

    public Mono<BookingDTO> getBookingById(UUID bookingId) {
        log.info("Getting booking with id {}", bookingId);
        return bookingRepository.findById(bookingId)
            .map(bookingMapper::toDto)
            .doOnSuccess(booking -> log.info("Found booking for id {}", bookingId));
    }

    public Mono<BookingDTO> confirmBooking(UUID bookingId) {
       log.info("Confirming booking with id {}", bookingId);
       return bookingRepository.findById(bookingId)
           .switchIfEmpty(Mono.error(new IllegalArgumentException("Booking not found")))
           .flatMap(booking -> {
               if (booking.getStatus() != BookingStatus.PENDING) {
                   return Mono.error(new IllegalStateException("Cannot confirm booking in status " + booking.getStatus()));
               }
               booking.setStatus(BookingStatus.CONFIRMED);
               return bookingRepository.save(booking).map(bookingMapper::toDto);
           })
           .doOnSuccess(confirmedBooking -> log.info("Booking confirmed with id {}", bookingId));
    }

    public Mono<Void> cancelBooking(UUID bookingId) {
       log.info("Cancelling booking with id {}", bookingId);
       return bookingRepository.findById(bookingId)
           .switchIfEmpty(Mono.error(new IllegalArgumentException("Booking not found")))
           .flatMap(booking -> {
               if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
                   return Mono.error(new IllegalStateException("Cannot cancel booking in status " + booking.getStatus()));
               }
               booking.setStatus(BookingStatus.CANCELLED);
               return bookingRepository.save(booking).then();
           })
           .doOnSuccess(unused -> log.info("Booking cancelled with id {}", bookingId));
    }
}