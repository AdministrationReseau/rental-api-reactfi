package inc.yowyob.rental_api_reactive.persistence.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

import inc.yowyob.rental_api_reactive.application.dto.util.Money;

@WritingConverter // Indique que ce convertisseur sert pour l'écriture en BDD
public class MoneyToStringConverter implements Converter<Money, String> {

    @Override
    public String convert(@NonNull Money source) {
        // Transforme l'objet Money en une chaîne de caractères simple
        return source.getAmount().toPlainString() + " " + source.getCurrency();
    }
}