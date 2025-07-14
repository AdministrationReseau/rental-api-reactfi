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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            // Configuration CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Désactiver CSRF pour API REST
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // Désactiver l'authentification HTTP Basic
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

            // Désactiver les formulaires de login
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

            // Configuration des autorisations
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/v3/api-docs/**").permitAll()
                .pathMatchers("/swagger-ui/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/swagger-resources/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()

                // Health checks et actuator
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/health").permitAll()

                // Routes publiques - Authentification
                .pathMatchers("/api/v1/auth/register").permitAll()
                .pathMatchers("/api/v1/auth/login").permitAll()
                .pathMatchers("/api/v1/auth/refresh").permitAll()
                .pathMatchers("/api/v1/auth/forgot-password").permitAll()
                .pathMatchers("/api/v1/auth/reset-password").permitAll()
                .pathMatchers("/api/v1/auth/verify-email").permitAll()

                // Routes publiques - Onboarding
                .pathMatchers("/api/v1/onboarding/**").permitAll()

                // Routes publiques - Forfaits (consultation uniquement)
                .pathMatchers("/api/v1/subscription/plans/**").permitAll()

                // Routes sécurisées - Authentification
                .pathMatchers("/api/v1/auth/me").authenticated()
                .pathMatchers("/api/v1/auth/change-password").authenticated()
                .pathMatchers("/api/v1/auth/logout").authenticated()

                // Routes sécurisées - Profil utilisateur
                .pathMatchers("/api/v1/profile/**").authenticated()

                // Gestion du personnel (Propriétaires d'organisation uniquement)
                .pathMatchers("/api/v1/personnel/**").authenticated()

                // À sécuriser dans les phases suivantes
                .pathMatchers("/api/v1/users/**").permitAll()
                .pathMatchers("/api/v1/organizations/**").permitAll()
                .pathMatchers("/api/v1/permissions").permitAll()
                .pathMatchers("/api/v1/roles").permitAll()
                .pathMatchers("/api/v1/user-roles").permitAll()

                // Toutes les autres routes nécessitent une authentification
                .anyExchange().authenticated()
            )

            // Configuration du gestionnaire d'authentification
            .authenticationManager(customAuthenticationManager)

            // Configuration du repository de contexte de sécurité
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
