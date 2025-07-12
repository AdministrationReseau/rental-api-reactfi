package inc.yowyob.rental_api_reactive.persistence.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

@ReadingConverter // Annotation importante pour indiquer une conversion en lecture
@RequiredArgsConstructor
public class JsonStringToMapConverter implements Converter<String, Map<String, Object>> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public Map<String, Object> convert(@NonNull String source) {
        // Convertit la chaîne JSON en Map.
        // TypeReference est nécessaire pour gérer correctement les types génériques.
        return objectMapper.readValue(source, new TypeReference<>() {});
    }
}
