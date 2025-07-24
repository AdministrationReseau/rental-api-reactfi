package inc.yowyob.rental_api_reactive.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHours {
    @JsonProperty("monday")
    private TimeSlot monday;

    @JsonProperty("tuesday")
    private TimeSlot tuesday;

    @JsonProperty("wednesday")
    private TimeSlot wednesday;

    @JsonProperty("thursday")
    private TimeSlot thursday;

    @JsonProperty("friday")
    private TimeSlot friday;

    @JsonProperty("saturday")
    private TimeSlot saturday;

    @JsonProperty("sunday")
    private TimeSlot sunday;
}