package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("user_type")
    private UserType userType;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("profile_picture")
    private String profilePicture;

    @JsonProperty("is_email_verified")
    private Boolean isEmailVerified;

    @JsonProperty("is_phone_verified")
    private Boolean isPhoneVerified;

    @JsonProperty("preferred_language")
    private String preferredLanguage;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("last_login_at")
    private LocalDateTime lastLoginAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("is_active")
    private Boolean isActive;
}
