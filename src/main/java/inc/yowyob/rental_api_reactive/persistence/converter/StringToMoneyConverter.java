package inc.yowyob.rental_api_reactive.persistence.converter;

import inc.yowyob.rental_api_reactive.application.dto.Money;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import java.math.BigDecimal;

@ReadingConverter // Indique que ce convertisseur sert pour la lecture depuis la BDD
public class StringToMoneyConverter implements Converter<String, Money> {

    @Override
    public Money convert(@NonNull String source) {
        // Transforme la chaîne de caractères en objet Money
        String[] parts = source.split(" ");
        if (parts.length == 2) {
            BigDecimal amount = new BigDecimal(parts[0]);
            String currency = parts[1];
            return new Money(amount, currency);
        }
        throw new IllegalArgumentException("Invalid format for Money: " + source);
    }
}