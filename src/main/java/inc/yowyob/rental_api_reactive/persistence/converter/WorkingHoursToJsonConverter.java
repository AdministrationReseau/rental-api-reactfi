package inc.yowyob.rental_api_reactive.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours; // Assurez-vous que le chemin est correct
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

@WritingConverter
public class WorkingHoursToJsonConverter implements Converter<WorkingHours, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(@NonNull WorkingHours source) {
        try {
            return objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting WorkingHours to JSON", e);
        }
    }
}
