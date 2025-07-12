package inc.yowyob.rental_api_reactive.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class WebConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));

        // Méthodes autorisées
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));

        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));

        // Headers exposés
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));

        // Permettre les credentials
        configuration.setAllowCredentials(allowCredentials);

        // Durée de cache pour les preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return new CorsWebFilter(source);
    }
}
