package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Paramètres d'organisation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationSettings {

    @JsonProperty("notification_settings")
    private NotificationSettings notificationSettings;

    @JsonProperty("integration_settings")
    private IntegrationSettings integrationSettings;

    @JsonProperty("security_settings")
    private SecuritySettings securitySettings;

    @JsonProperty("business_settings")
    private BusinessSettings businessSettings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationSettings {
        @JsonProperty("email_notifications")
        private Boolean emailNotifications = true;

        @JsonProperty("sms_notifications")
        private Boolean smsNotifications = false;

        @JsonProperty("push_notifications")
        private Boolean pushNotifications = true;

        @JsonProperty("booking_notifications")
        private Boolean bookingNotifications = true;

        @JsonProperty("payment_notifications")
        private Boolean paymentNotifications = true;

        @JsonProperty("maintenance_notifications")
        private Boolean maintenanceNotifications = true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IntegrationSettings {
        @JsonProperty("enable_api_access")
        private Boolean enableApiAccess = false;

        @JsonProperty("webhook_url")
        private String webhookUrl;

        @JsonProperty("payment_gateway")
        private String paymentGateway = "STRIPE";

        @JsonProperty("mapping_service")
        private String mappingService = "GOOGLE_MAPS";

        @JsonProperty("sms_provider")
        private String smsProvider = "TWILIO";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SecuritySettings {
        @JsonProperty("two_factor_auth_required")
        private Boolean twoFactorAuthRequired = false;

        @JsonProperty("password_expiry_days")
        private Integer passwordExpiryDays = 90;

        @JsonProperty("session_timeout_minutes")
        private Integer sessionTimeoutMinutes = 480; // 8 heures

        @JsonProperty("ip_whitelist_enabled")
        private Boolean ipWhitelistEnabled = false;

        @JsonProperty("audit_log_retention_days")
        private Integer auditLogRetentionDays = 365;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessSettings {
        @JsonProperty("operating_hours")
        private String operatingHours = "24/7";

        @JsonProperty("default_late_fee_rate")
        private Double defaultLateFeeRate = 0.1;

        @JsonProperty("damage_assessment_required")
        private Boolean damageAssessmentRequired = true;

        @JsonProperty("require_driver_verification")
        private Boolean requireDriverVerification = true;

        @JsonProperty("auto_insurance_included")
        private Boolean autoInsuranceIncluded = true;
    }
}
