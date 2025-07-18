package inc.yowyob.rental_api_reactive.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity // Active la sécurité au niveau des méthodes (ex: @PreAuthorize)
public class SecurityConfig {

    // Liste des chemins publics pour Swagger UI et la documentation OpenAPI
    private static final String[] SWAGGER_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**" 
    };
    
    // Liste des chemins publics pour l'authentification
    private static final String[] AUTH_PATHS = {
            "/api/v1/auth/**" // Adaptez si votre chemin d'authentification est différent
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Appliquer la configuration CORS en premier
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                
                // Désactiver CSRF car c'est une API stateless (utilisant des JWTs)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                
                // Définir les règles d'autorisation
                .authorizeExchange(exchange -> exchange
                        // 1. Autoriser l'accès non authentifié aux chemins de Swagger
                        .pathMatchers(SWAGGER_PATHS).permitAll()
                        
                        // 2. Autoriser l'accès non authentifié aux chemins d'authentification (login, register)
                        .pathMatchers(AUTH_PATHS).permitAll()
                        
                        // 3. Exiger une authentification pour toutes les autres requêtes
                        .anyExchange().authenticated()
                )
                // Ici, vous ajouterez plus tard votre filtre JWT
                // .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * Bean de configuration pour CORS, basé sur votre application.properties.
     * C'est une meilleure pratique que de laisser Spring Boot le faire implicitement.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Vous pouvez récupérer ces valeurs depuis votre application.properties avec @Value
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080", "http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Appliquer cette config à tous les chemins
        return source;
    }
}