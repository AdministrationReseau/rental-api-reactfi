package inc.yowyob.rental_api_reactive.infrastructure.config;

import inc.yowyob.rental_api_reactive.infrastructure.security.config.CustomAuthenticationManager;
import inc.yowyob.rental_api_reactive.infrastructure.security.config.ReactiveSecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration de sécurité réactive pour WebFlux
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ReactiveSecurityConfig {

    private final CustomAuthenticationManager customAuthenticationManager;
    private final ReactiveSecurityContextRepository securityContextRepository;
    private final AppProperties appProperties;

    private static final String[] PUBLIC_PATHS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/webjars/**",
        "/swagger-resources/**",
        "/favicon.ico", // Important pour les requêtes de navigateur
        "/actuator/**",
        "/health",
        "/api/v1/auth/**",
        "/api/v1/onboarding/**",
        "/api/v1/subscription/plans/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(PUBLIC_PATHS).permitAll()
                .pathMatchers("/api/v1/profile/**").authenticated()
                .pathMatchers("/api/v1/personnel/**").authenticated()
                // Toutes les autres routes nécessitent une authentification
                .anyExchange().authenticated()
            )
            .authenticationManager(customAuthenticationManager)
            .securityContextRepository(securityContextRepository)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées depuis les propriétés
        configuration.setAllowedOriginPatterns(Arrays.asList(appProperties.getCors().getAllowedOrigins()));

        // Méthodes autorisées
        configuration.setAllowedMethods(Arrays.asList(appProperties.getCors().getAllowedMethods()));

        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(appProperties.getCors().getAllowedHeaders()));

        // Headers exposés
        configuration.setExposedHeaders(List.of(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization"
        ));

        // Permettre les credentials
        configuration.setAllowCredentials(appProperties.getCors().isAllowCredentials());

        // Durée de cache pour les preflight requests
        configuration.setMaxAge(appProperties.getCors().getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
