package inc.yowyob.rental_api_reactive.persistence.mapper;

import org.mapstruct.Mapper;

import inc.yowyob.rental_api_reactive.application.dto.booking.BookingDTO;
import inc.yowyob.rental_api_reactive.infrastructure.web.BookingResponse;

@Mapper(componentModel = "spring")
public interface BookingResponseMapper {

    BookingResponse toResponse(BookingDTO bookingDto);
}