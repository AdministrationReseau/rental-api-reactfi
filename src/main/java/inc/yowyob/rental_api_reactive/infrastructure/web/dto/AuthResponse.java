package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Réponse d'authentification (Mise à jour avec support personnel)
 */
@Data
@Builder
public class AuthResponse {
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("tokenType")
    private String tokenType;

    @JsonProperty("expiresIn")
    private long expiresIn;

    @JsonProperty("user")
    private UserResponse user;

    // NOUVEAUX CHAMPS pour la gestion du personnel
    @JsonProperty("isPersonnel")
    private Boolean isPersonnel;

    @JsonProperty("agencyRedirectInfo")
    private AgencyRedirectInfo agencyRedirectInfo;

    @JsonProperty("requiresPasswordChange")
    private Boolean requiresPasswordChange;

    @JsonProperty("loginMessage")
    private String loginMessage;
}
