package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Informations sur les horaires de travail
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkingHoursInfo {

    @JsonProperty("is_open")
    private Boolean isOpen = true;

    @JsonProperty("open_time")
    private String openTime; // Format HH:mm

    @JsonProperty("close_time")
    private String closeTime; // Format HH:mm

    @JsonProperty("break_start")
    private String breakStart; // Format HH:mm (optionnel)

    @JsonProperty("break_end")
    private String breakEnd; // Format HH:mm (optionnel)
}
