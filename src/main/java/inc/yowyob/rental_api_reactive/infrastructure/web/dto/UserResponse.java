package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse utilisateur
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @JsonProperty("id")
    public UUID id;

    @JsonProperty("email")
    public String email;

    @JsonProperty("firstName")
    public String firstName;

    @JsonProperty("lastName")
    public String lastName;

    @JsonProperty("fullName")
    public String fullName;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("userType")
    public UserType userType;

    @JsonProperty("organizationId")
    public UUID organizationId;

    @JsonProperty("agencyId")
    public UUID agencyId;

    @JsonProperty("profilePicture")
    public String profilePicture;

    @JsonProperty("address")
    public String address;

    @JsonProperty("city")
    public String city;

    @JsonProperty("country")
    public String country;

    @JsonProperty("isEmailVerified")
    public Boolean isEmailVerified;

    @JsonProperty("isPhoneVerified")
    public Boolean isPhoneVerified;

    @JsonProperty("preferredLanguage")
    public String preferredLanguage;

    @JsonProperty("timezone")
    public String timezone;

    @JsonProperty("currency")
    public String currency;

    @JsonProperty("dateFormat")
    public String dateFormat;

    @JsonProperty("emailNotifications")
    public Boolean emailNotifications;

    @JsonProperty("smsNotifications")
    public Boolean smsNotifications;

    @JsonProperty("pushNotifications")
    public Boolean pushNotifications;

    @JsonProperty("employeeId")
    public String employeeId;

    @JsonProperty("department")
    public String department;

    @JsonProperty("position")
    public String position;

    @JsonProperty("supervisorId")
    public UUID supervisorId;

    @JsonProperty("hiredAt")
    public LocalDateTime hiredAt;

    @JsonProperty("mustChangePassword")
    public Boolean mustChangePassword;

    @JsonProperty("isActive")
    public Boolean isActive;

    @JsonProperty("lastLoginAt")
    public LocalDateTime lastLoginAt;

    @JsonProperty("createdAt")
    public LocalDateTime createdAt;

    /**
     * Vérifie si l'utilisateur est du personnel
     */
    @JsonProperty("isPersonnel")
    public Boolean getIsPersonnel() {
        return userType == UserType.AGENCY_MANAGER ||
            userType == UserType.RENTAL_AGENT;
    }

    /**
     * Vérifie si l'utilisateur est un client
     */
    @JsonProperty("isClient")
    public Boolean getIsClient() {
        return userType == UserType.CLIENT;
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     */
    @JsonProperty("isAdmin")
    public Boolean getIsAdmin() {
        return userType == UserType.SUPER_ADMIN ||
            userType == UserType.ORGANIZATION_OWNER;
    }

    /**
     * Vérifie si l'utilisateur peut gérer une agence
     */
    @JsonProperty("canManageAgency")
    public Boolean getCanManageAgency() {
        return userType == UserType.ORGANIZATION_OWNER ||
            userType == UserType.AGENCY_MANAGER;
    }

    /**
     * Vérifie si l'utilisateur peut effectuer des locations
     */
    @JsonProperty("canHandleRentals")
    public Boolean getCanHandleRentals() {
        return userType == UserType.AGENCY_MANAGER ||
            userType == UserType.RENTAL_AGENT;
    }

    /**
     * Vérifie si l'utilisateur est assigné à une agence
     */
    @JsonProperty("isAssignedToAgency")
    public Boolean getIsAssignedToAgency() {
        return agencyId != null;
    }

    /**
     * Obtient le niveau hiérarchique de l'utilisateur
     */
    @JsonProperty("hierarchyLevel")
    public Integer getHierarchyLevel() {
        return userType != null ? userType.getHierarchyLevel() : 0;
    }

    /**
     * Indique si l'utilisateur a besoin de vérifier son email
     */
    @JsonProperty("needsEmailVerification")
    public Boolean getNeedsEmailVerification() {
        return !Boolean.TRUE.equals(isEmailVerified);
    }

    /**
     * Indique si l'utilisateur a besoin de vérifier son téléphone
     */
    @JsonProperty("needsPhoneVerification")
    public Boolean getNeedsPhoneVerification() {
        return !Boolean.TRUE.equals(isPhoneVerified);
    }
}
