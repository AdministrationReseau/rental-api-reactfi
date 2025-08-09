package inc.yowyob.rental_api_reactive.persistence.mapper.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import inc.yowyob.rental_api_reactive.application.dto.booking.BookingDTO;
import inc.yowyob.rental_api_reactive.application.dto.util.Money;
import inc.yowyob.rental_api_reactive.persistence.entity.Booking;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "withDriver", source = "withDriver")
    @Mapping(target = "totalPrice", source = "totalPrice") // Utiliser la méthode personnalisée
    @Mapping(target = "status", source = "status")
    BookingDTO toDto(Booking booking);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "vehicleId", source = "vehicleId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "withDriver", source = "withDriver")
    @Mapping(target = "totalPrice", source = "totalPrice") // Utiliser la méthode personnalisée
    @Mapping(target = "status", source = "status")
    Booking toEntity(BookingDTO bookingDto);
    
    default Money bigDecimalToMoney(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return new Money(amount, "EUR"); // Ajustez la devise si nécessaire
    }

    default BigDecimal moneyToBigDecimal(Money money) {
        if (money == null || money.getAmount() == null) {
            return null;
        }
        return money.getAmount();
    }
}