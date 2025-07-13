package inc.yowyob.rental_api_reactive.infrastructure.security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.infrastructure.config.AppProperties;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service réactif pour la gestion des tokens JWT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtReactiveTokenProvider {

    private final AppProperties appProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(appProperties.getJwt().getSecret().getBytes());
    }

    /**
     * Génère un token JWT pour un utilisateur
     */
    public Mono<String> generateToken(User user) {
        return Mono.fromCallable(() -> {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId().toString());
            claims.put("email", user.getEmail());
            claims.put("userType", user.getUserType().name());
            claims.put("isActive", user.getIsActive());

            // Informations organisation
            if (user.getOrganizationId() != null) {
                claims.put("organizationId", user.getOrganizationId().toString());
            }

            // Informations agence pour le personnel
            if (user.getAgencyId() != null) {
                claims.put("agencyId", user.getAgencyId().toString());
                claims.put("isAgencyBound", true);
            } else {
                claims.put("isAgencyBound", false);
            }

            return createToken(claims, user.getEmail(), appProperties.getJwt().getExpiration());
        });
    }

    /**
     * Génère un refresh token
     */
    public Mono<String> generateRefreshToken(User user) {
        return Mono.fromCallable(() -> {
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId().toString());
            claims.put("type", "refresh");

            return createToken(claims, user.getEmail(), appProperties.getJwt().getRefreshExpiration());
        });
    }

    /**
     * Crée un token JWT avec les claims spécifiés
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Extrait le token du header Authorization
     */
    public Mono<String> extractTokenFromHeader(String bearerToken) {
        return Mono.fromCallable(() -> {
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
            return null;
        });
    }

    /**
     * Valide un token JWT
     */
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                log.debug("Invalid JWT token: {}", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Extrait l'email du token
     */
    public Mono<String> getEmailFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = getClaims(token);
            return claims.getSubject();
        });
    }

    /**
     * Extrait l'ID utilisateur du token
     */
    public Mono<UUID> getUserIdFromToken(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = getClaims(token);
            return UUID.fromString(claims.get("userId", String.class));
        });
    }

    /**
     * Extrait les claims du token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Vérifie si le token est expiré
     */
    public Mono<Boolean> isTokenExpired(String token) {
        return Mono.fromCallable(() -> {
            try {
                Claims claims = getClaims(token);
                return claims.getExpiration().before(new Date());
            } catch (JwtException e) {
                return true;
            }
        });
    }

    /**
     * Extrait le contexte utilisateur du token
     */
    public Mono<TokenContext> extractTokenContext(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = getClaims(token);

            TokenContext context = new TokenContext();
            context.setUserId(UUID.fromString(claims.get("userId", String.class)));
            context.setEmail(claims.getSubject());
            context.setUserType(claims.get("userType", String.class));
            context.setIsActive(claims.get("isActive", Boolean.class));

            String orgId = claims.get("organizationId", String.class);
            if (orgId != null) {
                context.setOrganizationId(UUID.fromString(orgId));
            }

            String agencyId = claims.get("agencyId", String.class);
            if (agencyId != null) {
                context.setAgencyId(UUID.fromString(agencyId));
            }

            context.setIsAgencyBound(claims.get("isAgencyBound", Boolean.class));

            return context;
        });
    }

    /**
     * Contexte utilisateur extrait du token JWT
     */
    public static class TokenContext {
        @JsonProperty("userId")
        private UUID userId;

        @JsonProperty("email")
        private String email;

        @JsonProperty("userType")
        private String userType;

        @JsonProperty("organizationId")
        private UUID organizationId;

        @JsonProperty("agencyId")
        private UUID agencyId;

        @JsonProperty("isAgencyBound")
        private Boolean isAgencyBound;

        @JsonProperty("isActive")
        private Boolean isActive;

        // Constructeurs
        public TokenContext() {}

        // Getters et Setters
        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }

        public UUID getOrganizationId() { return organizationId; }
        public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }

        public UUID getAgencyId() { return agencyId; }
        public void setAgencyId(UUID agencyId) { this.agencyId = agencyId; }

        public Boolean getIsAgencyBound() { return isAgencyBound; }
        public void setIsAgencyBound(Boolean isAgencyBound) { this.isAgencyBound = isAgencyBound; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}
