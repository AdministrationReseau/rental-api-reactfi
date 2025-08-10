package inc.yowyob.rental_api_reactive.persistence.mapper.booking;

import org.mapstruct.Mapper;

import inc.yowyob.rental_api_reactive.application.dto.booking.BookingDTO;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.booking.BookingResponse;

@Mapper(componentModel = "spring")
public interface BookingResponseMapper {

    BookingResponse toResponse(BookingDTO bookingDto);
}