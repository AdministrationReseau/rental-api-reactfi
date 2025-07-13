package inc.yowyob.rental_api_reactive.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration des propriétés de l'application
 * Route: src/main/java/inc/yowyob/rental_api_reactive/infrastructure/config/AppProperties.java
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @JsonProperty("jwt")
    private Jwt jwt = new Jwt();

    @JsonProperty("cors")
    private Cors cors = new Cors();

    @JsonProperty("security")
    private Security security = new Security();

    @Data
    public static class Jwt {
        @JsonProperty("secret")
        private String secret = "mySecretKey123456789012345678901234567890";

        @JsonProperty("expiration")
        private long expiration = 86400000; // 24 heures en millisecondes

        @JsonProperty("refreshExpiration")
        private long refreshExpiration = 2592000000L; // 30 jours en millisecondes
    }

    @Data
    public static class Cors {
        @JsonProperty("allowedOrigins")
        private String[] allowedOrigins = {
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:4200",
            "https://*.rental-api.com"
        };

        @JsonProperty("allowedMethods")
        private String[] allowedMethods = {
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        };

        @JsonProperty("allowedHeaders")
        private String[] allowedHeaders = {
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "X-User-Id"
        };

        @JsonProperty("allowCredentials")
        private boolean allowCredentials = true;

        @JsonProperty("maxAge")
        private long maxAge = 3600;
    }

    @Data
    public static class Security {
        @JsonProperty("maxFailedAttempts")
        private int maxFailedAttempts = 5;

        @JsonProperty("lockDurationHours")
        private int lockDurationHours = 1;

        @JsonProperty("passwordMinLength")
        private int passwordMinLength = 8;

        @JsonProperty("emailVerificationExpiryHours")
        private int emailVerificationExpiryHours = 24;

        @JsonProperty("passwordResetExpiryHours")
        private int passwordResetExpiryHours = 1;
    }
}
