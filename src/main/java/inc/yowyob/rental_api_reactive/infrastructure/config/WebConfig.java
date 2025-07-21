package inc.yowyob.rental_api_reactive.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration Web pour l'application réactive
 * Gestion CORS et ressources statiques (images uploads)
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    /**
     * Configuration CORS pour WebFlux
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));

        // Méthodes autorisées
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));

        // Headers autorisés - Important pour Swagger et uploads
        if ("*".equals(allowedHeaders)) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }

        // Headers exposés pour les réponses
        configuration.setExposedHeaders(List.of(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Content-Type",
            "Authorization",
            "X-User-Id",
            "X-Total-Count",
            "X-Request-ID"
        ));

        // Permettre les credentials
        configuration.setAllowCredentials(allowCredentials);

        // Durée de cache pour les preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsWebFilter(source);
    }

    /**
     * Configuration des ressources statiques pour servir les fichiers upload
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir tous les fichiers uploadés
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadDir + "/");

        // Servir spécifiquement les images de véhicules
        registry.addResourceHandler("/api/v1/files/vehicles/**")
            .addResourceLocations("file:" + uploadDir + "/images/vehicles/");

        // Servir spécifiquement les images de chauffeurs
        registry.addResourceHandler("/api/v1/files/drivers/**")
            .addResourceLocations("file:" + uploadDir + "/images/drivers/");

        // Servir spécifiquement les images d'utilisateurs
        registry.addResourceHandler("/api/v1/files/users/**")
            .addResourceLocations("file:" + uploadDir + "/images/users/");

        // Servir les documents
        registry.addResourceHandler("/api/v1/files/documents/**")
            .addResourceLocations("file:" + uploadDir + "/files/documents/");

        // Configuration pour Swagger UI et autres ressources statiques
        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    }
}
