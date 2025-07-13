package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationSettingsRequest {
    @JsonProperty("emailNotifications")
    private Boolean emailNotifications;

    @JsonProperty("smsNotifications")
    private Boolean smsNotifications;

    @JsonProperty("pushNotifications")
    private Boolean pushNotifications;

    @JsonProperty("marketingEmails")
    private Boolean marketingEmails;

    @JsonProperty("securityAlerts")
    private Boolean securityAlerts;

    @JsonProperty("systemUpdates")
    private Boolean systemUpdates;
}
