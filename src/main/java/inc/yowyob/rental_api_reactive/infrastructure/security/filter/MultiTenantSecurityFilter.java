package inc.yowyob.rental_api_reactive.infrastructure.security.filter;

import inc.yowyob.rental_api_reactive.application.service.AuthorizationReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MultiTenantSecurityFilter implements WebFilter {

    private final AuthorizationReactiveService authorizationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
            .cast(org.springframework.security.core.context.SecurityContext.class)
            .map(org.springframework.security.core.context.SecurityContext::getAuthentication)
            .cast(Authentication.class)
            .flatMap(authentication -> {
                if (authentication == null || !authentication.isAuthenticated()) {
                    return chain.filter(exchange);
                }

                return validateTenantAccess(exchange, authentication)
                    .flatMap(isValid -> {
                        if (isValid) {
                            return chain.filter(exchange);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    });
            })
            .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Boolean> validateTenantAccess(ServerWebExchange exchange, Authentication authentication) {
        String organizationIdHeader = exchange.getRequest().getHeaders().getFirst("X-Organization-Id");
        String agencyIdHeader = exchange.getRequest().getHeaders().getFirst("X-Agency-Id");

        if (organizationIdHeader == null && agencyIdHeader == null) {
            return Mono.just(true); // Pas de contrainte multi-tenant
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            return Mono.just(false);
        }

        UUID userId = userPrincipal.getId();

        // Validation de l'accès organisation
        if (organizationIdHeader != null) {
            try {
                UUID organizationId = UUID.fromString(organizationIdHeader);
                return authorizationService.canAccessOrganization(userId, organizationId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid organization ID in header: {}", organizationIdHeader);
                return Mono.just(false);
            }
        }

        // Validation de l'accès agence
        if (agencyIdHeader != null) {
            try {
                UUID agencyId = UUID.fromString(agencyIdHeader);
                return authorizationService.canAccessAgency(userId, agencyId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid agency ID in header: {}", agencyIdHeader);
                return Mono.just(false);
            }
        }

        return Mono.just(true);
    }
}
