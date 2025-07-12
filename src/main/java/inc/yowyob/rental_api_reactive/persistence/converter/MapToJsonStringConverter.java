package inc.yowyob.rental_api_reactive.persistence.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

@WritingConverter // Annotation importante pour indiquer une conversion en écriture
@RequiredArgsConstructor
public class MapToJsonStringConverter implements Converter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows // Gère les exceptions de Jackson pour nous
    public String convert(@NonNull Map<String, Object> source) {
        // Convertit la Map en une chaîne de caractères JSON
        return objectMapper.writeValueAsString(source);
    }
}
