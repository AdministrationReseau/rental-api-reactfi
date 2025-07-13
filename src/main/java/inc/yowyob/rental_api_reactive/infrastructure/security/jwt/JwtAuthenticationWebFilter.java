package inc.yowyob.rental_api_reactive.infrastructure.security.jwt;

import inc.yowyob.rental_api_reactive.infrastructure.security.jwt.JwtReactiveTokenProvider;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Filtre Web réactif pour l'authentification JWT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtReactiveTokenProvider jwtTokenProvider;
    private final UserReactiveRepository userRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Ignorer les routes publiques
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        return extractToken(exchange)
            .flatMap(token -> jwtTokenProvider.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return chain.filter(exchange);
                    }

                    return jwtTokenProvider.getEmailFromToken(token)
                        .flatMap(userRepository::findByEmail)
                        .flatMap(user -> {
                            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                "",
                                user.getIsActive(),
                                true,
                                true,
                                true,
                                Collections.emptyList()
                            );

                            UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                            return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                        })
                        .switchIfEmpty(chain.filter(exchange));
                }))
            .switchIfEmpty(chain.filter(exchange))
            .onErrorResume(ex -> {
                log.debug("JWT authentication failed: {}", ex.getMessage());
                return chain.filter(exchange);
            });
    }

    private Mono<String> extractToken(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
            return null;
        });
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/auth/") ||
            path.startsWith("/api/v1/onboarding/") ||
            path.startsWith("/api/v1/subscription/plans") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/actuator/health");
    }
}
