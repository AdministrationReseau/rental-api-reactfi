package inc.yowyob.rental_api_reactive.infrastructure.web.controller.booking;

import inc.yowyob.rental_api_reactive.application.dto.booking.BookingStatus;
import inc.yowyob.rental_api_reactive.application.service.booking.BookingReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.CreateBookingRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.booking.BookingResponse;
import inc.yowyob.rental_api_reactive.persistence.mapper.booking.BookingResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookings", description = "APIs de gestion des réservations")
public class BookingReactiveController {

    private final BookingReactiveService bookingService;
    @Autowired
    private BookingResponseMapper bookingResponseMapper; // Injection du mapper

   @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('RENTAL_WRITE')")
    @Operation(summary = "Créer une nouvelle réservation", description = "Crée une nouvelle réservation")
    public Mono<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest createRequest,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId
    ) {
        log.info("POST /api/v1/bookings - Creating booking for vehicle: {}", createRequest.getVehicleId());

        return bookingService.createBooking(
                createRequest.getVehicleId(),
                UUID.fromString(userId),
                createRequest.getStartDate(),
                createRequest.getEndDate(),
                createRequest.isWithDriver(),
                createRequest.getTotalPrice()
        )
        .map(bookingDto -> {
            // Utiliser le mapper pour créer BookingResponse
            BookingResponse bookingResponse = bookingResponseMapper.toResponse(bookingDto);

            return ApiResponse.<BookingResponse>builder()
                    .success(true)
                    .message("Réservation créée avec succès")
                    .data(bookingResponse)
                    .build();
        })
        .doOnSuccess(response -> log.info("Booking created successfully: {}", response.getData().getId()))
        .doOnError(error -> log.error("Failed to create booking", error));
    }
    
    @GetMapping(value = "/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RENTAL_READ')")
    @Operation(summary = "Obtenir une réservation par ID", description = "Récupère les détails d'une réservation")
    public Mono<ApiResponse<BookingResponse>> getBookingById(
        @Parameter(description = "ID de la réservation") @PathVariable UUID bookingId
    ) {
        log.info("GET /api/v1/bookings/{} - Getting booking details", bookingId);

        return bookingService.getBookingById(bookingId)
            .map(bookingDto -> {
                // Utiliser le mapper pour créer BookingResponse
                BookingResponse bookingResponse = bookingResponseMapper.toResponse(bookingDto);

                return ApiResponse.<BookingResponse>builder()
                    .success(true)
                    .message("Réservation trouvée avec succès")
                    .data(bookingResponse)
                    .build();
            })
            .switchIfEmpty(Mono.just(ApiResponse.<BookingResponse>builder()
                .success(false)
                .message("Réservation non trouvée")
                .data(null)
                .build()))
            .doOnSuccess(response -> log.info("Booking details retrieved: {}", bookingId))
            .doOnError(error -> log.error("Failed to get booking details: {}", bookingId, error));
    }

    @PutMapping("/{bookingId}/confirm")
    @PreAuthorize("hasAuthority('RENTAL_WRITE')")
    @Operation(summary = "Confirmer une réservation", description = "Confirme une réservation")
    public Mono<ApiResponse<BookingResponse>> confirmBooking(
        @Parameter(description = "ID de la réservation") @PathVariable UUID bookingId
    ) {
        log.info("PUT /bookings/{}/confirm - Confirming booking", bookingId);

        return bookingService.confirmBooking(bookingId)
            .map(bookingDto -> {
                // Utiliser le mapper pour créer BookingResponse
                BookingResponse bookingResponse = bookingResponseMapper.toResponse(bookingDto);

                return ApiResponse.<BookingResponse>builder()
                    .success(true)
                    .message("Réservation confirmée avec succès")
                    .data(bookingResponse)
                    .build();
            })
            .switchIfEmpty(Mono.just(ApiResponse.<BookingResponse>builder()
                .success(false)
                .message("Réservation non trouvée")
                .data(null)
                .build()))
            .doOnSuccess(response -> log.info("Booking confirmed with id {}", bookingId))
            .doOnError(error -> log.error("Failed to confirm booking", error));
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('RENTAL_WRITE')")
    @Operation(summary = "Annuler une réservation", description = "Annule une réservation")
    public Mono<ApiResponse<String>> cancelBooking(
        @Parameter(description = "ID de la réservation") @PathVariable UUID bookingId
    ) {
        log.info("DELETE /api/v1/bookings/{} - Cancelling booking", bookingId);

        return bookingService.cancelBooking(bookingId)
            .then(Mono.just(ApiResponse.<String>builder()
                .success(true)
                .message("Réservation annulée avec succès")
                .data("Booking cancelled")
                .build()))
            .onErrorReturn(ApiResponse.<String>builder()
                .success(false)
                .message("Erreur lors de l'annulation")
                .data(null)
                .build())
            .doOnSuccess(response -> log.info("Booking cancelled with id {}", bookingId))
            .doOnError(error -> log.error("Failed to cancel booking: {}", bookingId, error));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RENTAL_READ')")
    @Operation(summary = "Obtenir toutes les réservations", description = "Récupère toutes les réservations")

    public Mono<ApiResponse<Flux<BookingResponse>>> getAllBookings() {
        log.info("GET /api/v1/bookings - Getting all bookings");

        Flux<BookingResponse> bookings =  null; //bookingService.getAllBookings();  // À implémenter dans le service

        return Mono.just(ApiResponse.<Flux<BookingResponse>>builder()
            .success(true)
            .message("Toutes les réservations récupérées")
            .data(bookings)
            .build())
            .doOnSuccess(response -> log.info("All bookings retrieved"));
    }

    // === GET BOOKINGS BY DATE RANGE ===
    @GetMapping(value = "/date-range", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RENTAL_READ')")
    @Operation(summary = "Obtenir les réservations par intervalle de dates", description = "Récupère les réservations dans un intervalle de dates spécifié.")

    public Mono<ApiResponse<Flux<BookingResponse>>> getBookingsByDate(
        @Parameter(description = "Date de début de la réservation") @RequestParam LocalDateTime startDate,
        @Parameter(description = "Date de fin de la réservation") @RequestParam LocalDateTime endDate
    ) {
        log.info("GET /api/v1/bookings/date-range - Getting bookings between {} and {}", startDate, endDate);

       Flux<BookingResponse> bookings =  null;// bookingService.getBookingsByDateRange(startDate, endDate); //À implémenter dans le service

        return Mono.just(ApiResponse.<Flux<BookingResponse>>builder()
            .success(true)
            .message("Réservations récupérées avec succès pour la période spécifiée")
            .data(bookings)
            .build())
            .doOnSuccess(response -> log.info("Bookings by date range retrieved"));
    }

    // === GET BOOKINGS BY STATUS ===
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('RENTAL_READ')")
    @Operation(summary = "Obtenir les réservations par statut", description = "Récupère les réservations selon leur statut.")

    public Mono<ApiResponse<Flux<BookingResponse>>> getBookingsByStatus(
        @Parameter(description = "Statut de la réservation") @PathVariable BookingStatus status
    ) {
        log.info("GET /api/v1/bookings/status/{} - Getting bookings by status", status);

        Flux<BookingResponse> bookings =  null;//bookingService.getBookingsByStatus(status);  //À implémenter dans le service

        return Mono.just(ApiResponse.<Flux<BookingResponse>>builder()
            .success(true)
            .message("Réservations récupérées par statut")
            .data(bookings)
            .build())
            .doOnSuccess(response -> log.info("Bookings by status retrieved"));
    }

    // === REFUSE BOOKING ===
    @PutMapping("/{bookingId}/refuse")
    @PreAuthorize("hasAuthority('RENTAL_WRITE')")
    @Operation(summary = "Refuser une réservation", description = "Refuse une réservation en utilisant son ID.")

    public Mono<ApiResponse<BookingResponse>> refuseBooking(
        @Parameter(description = "ID de la réservation") @PathVariable UUID bookingId
    ) {
        log.info("PUT /bookings/{}/refuse - Refusing booking", bookingId);

        return null;//bookingService.refuseBooking(bookingId) // À implémenter dans le service
             //.map(bookingDto -> ApiResponse.<BookingResponse>builder()...);
    }

    // === EXTEND BOOKING ===
    @PutMapping("/{bookingId}/extend")
    @PreAuthorize("hasAuthority('RENTAL_WRITE')")
    @Operation(summary = "Prolonger une réservation", description = "Prolonge la durée d'une réservation en utilisant son ID.")
    public Mono<ApiResponse<BookingResponse>> extendBooking(
        @Parameter(description = "ID de la réservation") @PathVariable UUID bookingId,
        @Parameter(description = "Nouvelle date de fin") @RequestParam LocalDateTime newEndDate
    ) {
        log.info("PUT /bookings/{}/extend - Extending booking to {}", bookingId, newEndDate);

        return null; //bookingService.extendBooking(bookingId, newEndDate) // À implémenter dans le service
             //.map(bookingDto -> ApiResponse.<BookingResponse>builder()...);
    }
}
