package inc.yowyob.rental_api_reactive.infrastructure.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("userType")
    private String userType;

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isEmailVerified")
    private Boolean isEmailVerified;

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.getFirstName(),
            user.getLastName(),
            user.getUserType().name(),
            user.getOrganizationId(),
            user.getAgencyId(),
            user.getIsActive(),
            user.getIsEmailVerified()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // À implémenter selon les rôles et permissions
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive && isEmailVerified;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
