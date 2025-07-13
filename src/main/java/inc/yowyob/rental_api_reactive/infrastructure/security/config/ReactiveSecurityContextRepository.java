package inc.yowyob.rental_api_reactive.infrastructure.security.config;

import inc.yowyob.rental_api_reactive.infrastructure.security.jwt.JwtReactiveTokenProvider;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Repository réactif pour la gestion du contexte de sécurité
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtReactiveTokenProvider jwtTokenProvider;
    private final UserReactiveRepository userRepository;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return extractTokenFromRequest(exchange)
            .flatMap(token -> jwtTokenProvider.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.empty();
                    }

                    return jwtTokenProvider.getEmailFromToken(token)
                        .flatMap(userRepository::findByEmail)
                        .map(user -> {
                            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                "",
                                user.getIsActive(),
                                true,
                                true,
                                true,
                                Collections.emptyList()
                            );

                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                            // CORRECTION: Cast explicite vers SecurityContext
                            SecurityContext securityContext = new SecurityContextImpl(authentication);
                            return securityContext;
                        });
                }))
            .onErrorResume(ex -> {
                log.debug("Error loading security context: {}", ex.getMessage());
                return Mono.empty();
            });
    }

    private Mono<String> extractTokenFromRequest(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
            return null;
        });
    }
}
