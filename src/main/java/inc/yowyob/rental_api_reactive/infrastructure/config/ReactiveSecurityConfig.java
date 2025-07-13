package inc.yowyob.rental_api_reactive.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class ReactiveSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Documentation API - IMPORTANT pour Swagger
                .pathMatchers("/v3/api-docs/**").permitAll()
                .pathMatchers("/swagger-ui/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/swagger-resources/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()

                // Health checks et actuator
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/health").permitAll()

                // Routes publiques - Authentification
                .pathMatchers("/api/v1/auth/**").permitAll()

                // Routes publiques - Onboarding
                .pathMatchers("/api/v1/onboarding/**").permitAll()

                // Routes publiques - Forfaits (consultation uniquement)
                .pathMatchers("/api/v1/subscription/plans/**").permitAll()

                // Pour la Phase 1&2, permettre l'accès aux APIs de base
                .pathMatchers("/api/v1/users/**").permitAll()
                .pathMatchers("/api/v1/organizations/**").permitAll()

                // Toutes les autres routes nécessitent une authentification (Phase 3+)
                .anyExchange().permitAll() // Temporaire pour développement
            )
            .build();
    }
}
