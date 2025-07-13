package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserPreferencesRequest {
    @JsonProperty("preferredLanguage")
    private String preferredLanguage;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("emailNotifications")
    private Boolean emailNotifications;

    @JsonProperty("smsNotifications")
    private Boolean smsNotifications;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("dateFormat")
    private String dateFormat;
}
