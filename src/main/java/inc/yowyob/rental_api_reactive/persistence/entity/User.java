package inc.yowyob.rental_api_reactive.persistence.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("users")
public class User extends BaseEntity {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column("email")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column("password")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column("first_name")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column("last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column("phone")
    @JsonProperty("phone")
    private String phone;

    @NotNull(message = "User type is required")
    @Column("user_type")
    @JsonProperty("user_type")
    private UserType userType;

    @Column("organization_id")
    @JsonProperty("organization_id")
    private UUID organizationId;

    @Column("agency_id")
    @JsonProperty("agency_id")
    private UUID agencyId;

    @Column("profile_picture")
    @JsonProperty("profile_picture")
    private String profilePicture;

    @Column("is_email_verified")
    @JsonProperty("is_email_verified")
    private Boolean isEmailVerified = false;

    @Column("is_phone_verified")
    @JsonProperty("is_phone_verified")
    private Boolean isPhoneVerified = false;

    @Column("last_login_at")
    @JsonProperty("last_login_at")
    private LocalDateTime lastLoginAt;

    @Column("password_reset_token")
    @JsonProperty("password_reset_token")
    private String passwordResetToken;

    @Column("password_reset_expires")
    @JsonProperty("password_reset_expires")
    private LocalDateTime passwordResetExpires;

    @Column("email_verification_token")
    @JsonProperty("email_verification_token")
    private String emailVerificationToken;

    @Column("preferred_language")
    @JsonProperty("preferred_language")
    private String preferredLanguage = "fr";

    @Column("timezone")
    @JsonProperty("timezone")
    private String timezone = "Africa/Douala";

    // Constructors
    public User(String email, String password, String firstName, String lastName, UserType userType) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        prePersist();
    }

    /**
     * Obtient le nom complet de l'utilisateur
     */
    @JsonProperty("full_name")
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Vérifie si l'utilisateur est un propriétaire d'organisation
     */
    @JsonProperty("is_organization_owner")
    public boolean isOrganizationOwner() {
        return userType == UserType.ORGANIZATION_OWNER;
    }

    /**
     * Vérifie si l'utilisateur peut créer des organisations
     */
    @JsonProperty("can_create_organization")
    public boolean canCreateOrganization() {
        return userType == UserType.SUPER_ADMIN ||
            (userType == UserType.ORGANIZATION_OWNER && organizationId == null);
    }
}
