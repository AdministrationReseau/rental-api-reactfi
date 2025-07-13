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

/**
 * Entité User mise à jour pour supporter le personnel
 * Route: src/main/java/inc/yowyob/rental_api_reactive/persistence/entity/User.java
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {

    @PrimaryKey
    @JsonProperty("id")
    private UUID id;

    // === INFORMATIONS DE BASE ===
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
    @JsonProperty("firstName")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column("last_name")
    @JsonProperty("lastName")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column("phone")
    @JsonProperty("phone")
    private String phone;

    @NotNull(message = "User type is required")
    @Column("user_type")
    @JsonProperty("userType")
    private UserType userType;

    // === INFORMATIONS ORGANISATION/AGENCE ===
    @Column("organization_id")
    @JsonProperty("organizationId")
    private UUID organizationId;

    @Column("agency_id")
    @JsonProperty("agencyId")
    private UUID agencyId;

    // === INFORMATIONS PROFIL ===
    @Column("profile_picture")
    @JsonProperty("profilePicture")
    private String profilePicture;

    @Column("address")
    @JsonProperty("address")
    private String address;

    @Column("city")
    @JsonProperty("city")
    private String city;

    @Column("country")
    @JsonProperty("country")
    private String country;

    // === VÉRIFICATIONS ===
    @Column("is_email_verified")
    @JsonProperty("isEmailVerified")
    private Boolean isEmailVerified = false;

    @Column("is_phone_verified")
    @JsonProperty("isPhoneVerified")
    private Boolean isPhoneVerified = false;

    @Column("email_verification_token")
    @JsonProperty("emailVerificationToken")
    private String emailVerificationToken;

    @Column("email_verification_expiry")
    @JsonProperty("emailVerificationExpiry")
    private LocalDateTime emailVerificationExpiry;

    @Column("phone_verification_code")
    @JsonProperty("phoneVerificationCode")
    private String phoneVerificationCode;

    @Column("phone_verification_expiry")
    @JsonProperty("phoneVerificationExpiry")
    private LocalDateTime phoneVerificationExpiry;

    // === SÉCURITÉ MOT DE PASSE ===
    @Column("password_reset_token")
    @JsonProperty("passwordResetToken")
    private String passwordResetToken;

    @Column("password_reset_expiry")
    @JsonProperty("passwordResetExpiry")
    private LocalDateTime passwordResetExpiry;

    @Column("must_change_password")
    @JsonProperty("mustChangePassword")
    private Boolean mustChangePassword = false;

    // === SÉCURITÉ COMPTE ===
    @Column("last_login_at")
    @JsonProperty("lastLoginAt")
    private LocalDateTime lastLoginAt;

    @Column("last_login_ip")
    @JsonProperty("lastLoginIp")
    private String lastLoginIp;

    @Column("failed_login_attempts")
    @JsonProperty("failedLoginAttempts")
    private Integer failedLoginAttempts = 0;

    @Column("locked_until")
    @JsonProperty("lockedUntil")
    private LocalDateTime lockedUntil;

    // === INFORMATIONS EMPLOYÉ (PERSONNEL) ===
    @Column("employee_id")
    @JsonProperty("employeeId")
    private String employeeId; // ID employé interne à l'organisation

    @Column("department")
    @JsonProperty("department")
    private String department; // Service/Département

    @Column("position")
    @JsonProperty("position")
    private String position; // Poste/Fonction

    @Column("supervisor_id")
    @JsonProperty("supervisorId")
    private UUID supervisorId; // Superviseur direct

    @Column("hired_at")
    @JsonProperty("hiredAt")
    private LocalDateTime hiredAt; // Date d'embauche

    @Column("salary")
    @JsonProperty("salary")
    private java.math.BigDecimal salary; // Salaire (optionnel)

    @Column("contract_type")
    @JsonProperty("contractType")
    private String contractType; // Type de contrat (CDI, CDD, etc.)

    // === PRÉFÉRENCES UTILISATEUR ===
    @Column("preferred_language")
    @JsonProperty("preferredLanguage")
    private String preferredLanguage = "fr";

    @Column("timezone")
    @JsonProperty("timezone")
    private String timezone = "Africa/Douala";

    @Column("theme")
    @JsonProperty("theme")
    private String theme = "light"; // light, dark

    @Column("currency")
    @JsonProperty("currency")
    private String currency = "XAF";

    @Column("date_format")
    @JsonProperty("dateFormat")
    private String dateFormat = "DD/MM/YYYY";

    // === NOTIFICATIONS ===
    @Column("email_notifications")
    @JsonProperty("emailNotifications")
    private Boolean emailNotifications = true;

    @Column("sms_notifications")
    @JsonProperty("smsNotifications")
    private Boolean smsNotifications = false;

    @Column("push_notifications")
    @JsonProperty("pushNotifications")
    private Boolean pushNotifications = true;

    // === STATUTS ===
    @Column("is_active")
    @JsonProperty("isActive")
    private Boolean isActive = true;

    @Column("is_deleted")
    @JsonProperty("isDeleted")
    private Boolean isDeleted = false;

    @Column("deleted_at")
    @JsonProperty("deletedAt")
    private LocalDateTime deletedAt;

    // === AUDIT ===
    @Column("created_at")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Column("updated_at")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    @Column("created_by")
    @JsonProperty("createdBy")
    private UUID createdBy;

    @Column("updated_by")
    @JsonProperty("updatedBy")
    private UUID updatedBy;

    // === CONSTRUCTEURS ===
    public User(String email, String password, String firstName, String lastName, UserType userType) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // === MÉTHODES UTILITAIRES ===

    /**
     * Obtient le nom complet de l'utilisateur
     */
    @JsonProperty("fullName")
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Vérifie si l'utilisateur est un propriétaire d'organisation
     */
    public boolean isOrganizationOwner() {
        return userType == UserType.ORGANIZATION_OWNER;
    }

    /**
     * Vérifie si l'utilisateur est du personnel
     */
    public boolean isPersonnel() {
        return userType == UserType.AGENCY_MANAGER ||
            userType == UserType.RENTAL_AGENT;
    }

    /**
     * Vérifie si l'utilisateur est un client
     */
    public boolean isClient() {
        return userType == UserType.CLIENT;
    }

    /**
     * Vérifie si l'utilisateur est un administrateur
     */
    public boolean isAdmin() {
        return userType == UserType.SUPER_ADMIN ||
            userType == UserType.ORGANIZATION_OWNER;
    }

    /**
     * Vérifie si l'utilisateur peut gérer une agence
     */
    public boolean canManageAgency() {
        return userType == UserType.ORGANIZATION_OWNER ||
            userType == UserType.AGENCY_MANAGER;
    }

    /**
     * Vérifie si l'utilisateur peut effectuer des locations
     */
    public boolean canHandleRentals() {
        return userType == UserType.AGENCY_MANAGER ||
            userType == UserType.RENTAL_AGENT;
    }

    /**
     * Vérifie si le compte est verrouillé
     */
    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    /**
     * Vérifie si l'utilisateur peut se connecter
     */
    public boolean canLogin() {
        return isActive && !isDeleted && !isLocked();
    }

    /**
     * Vérifie si l'utilisateur est assigné à une agence
     */
    public boolean isAssignedToAgency() {
        return agencyId != null;
    }

    /**
     * Obtient le niveau hiérarchique de l'utilisateur
     */
    public int getHierarchyLevel() {
        return userType.getHierarchyLevel();
    }

    /**
     * Méthode appelée avant la persistance
     */
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    /**
     * Méthode appelée avant la mise à jour
     */
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Remet à zéro les tentatives de connexion échouées
     */
    public void resetFailedLoginAttempts() {
        failedLoginAttempts = 0;
        lockedUntil = null;
    }

    /**
     * Incrémente les tentatives de connexion échouées
     */
    public void incrementFailedLoginAttempts() {
        if (failedLoginAttempts == null) {
            failedLoginAttempts = 0;
        }
        failedLoginAttempts++;

        // Verrouiller après 5 tentatives
        if (failedLoginAttempts >= 5) {
            lockedUntil = LocalDateTime.now().plusHours(1);
        }
    }

    /**
     * Marque l'utilisateur comme supprimé (soft delete)
     */
    public void markAsDeleted(UUID deletedBy) {
        isDeleted = true;
        isActive = false;
        deletedAt = LocalDateTime.now();
        updatedBy = deletedBy;
        updatedAt = LocalDateTime.now();
    }

    /**
     * Restaure un utilisateur supprimé
     */
    public void restore(UUID restoredBy) {
        isDeleted = false;
        isActive = true;
        deletedAt = null;
        updatedBy = restoredBy;
        updatedAt = LocalDateTime.now();
    }

    /**
     * Met à jour les informations d'employé
     */
    public void updateEmployeeInfo(String employeeId, String department, String position, UUID supervisorId) {
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
        this.supervisorId = supervisorId;
        preUpdate();
    }

    /**
     * Assigne l'utilisateur à une agence
     */
    public void assignToAgency(UUID agencyId, UUID assignedBy) {
        this.agencyId = agencyId;
        this.updatedBy = assignedBy;
        preUpdate();
    }

    /**
     * Désassigne l'utilisateur d'une agence
     */
    public void unassignFromAgency(UUID unassignedBy) {
        this.agencyId = null;
        this.updatedBy = unassignedBy;
        preUpdate();
    }

    /**
     * Met à jour les informations de dernière connexion
     */
    public void updateLastLogin(String ipAddress) {
        lastLoginAt = LocalDateTime.now();
        lastLoginIp = ipAddress;
        resetFailedLoginAttempts();
        preUpdate();
    }

    /**
     * Active la vérification email
     */
    public void setEmailVerificationToken(String token) {
        emailVerificationToken = token;
        emailVerificationExpiry = LocalDateTime.now().plusDays(1);
    }

    /**
     * Marque l'email comme vérifié
     */
    public void verifyEmail() {
        isEmailVerified = true;
        emailVerificationToken = null;
        emailVerificationExpiry = null;
        preUpdate();
    }

    /**
     * Active la réinitialisation du mot de passe
     */
    public void setPasswordResetToken(String token) {
        passwordResetToken = token;
        passwordResetExpiry = LocalDateTime.now().plusHours(1);
    }

    /**
     * Réinitialise le mot de passe
     */
    public void resetPassword(String newPassword) {
        password = newPassword;
        passwordResetToken = null;
        passwordResetExpiry = null;
        mustChangePassword = false;
        preUpdate();
    }
}
