package inc.yowyob.rental_api_reactive.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import inc.yowyob.rental_api_reactive.application.dto.WorkingHours; // Assurez-vous que le chemin est correct
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

@ReadingConverter
public class JsonToWorkingHoursConverter implements Converter<String, WorkingHours> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public JsonToWorkingHoursConverter() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public WorkingHours convert(@NonNull String source) {
        try {
            return objectMapper.readValue(source, WorkingHours.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to WorkingHours", e);
        }
    }
}