package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PersonnelResponse {
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("userType")
    private UserType userType;

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @JsonProperty("profilePicture")
    private String profilePicture;

    // Informations employé
    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("supervisorId")
    private UUID supervisorId;

    @JsonProperty("hiredAt")
    private LocalDateTime hiredAt;

    // Statuts
    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isEmailVerified")
    private Boolean isEmailVerified;

    @JsonProperty("isPhoneVerified")
    private Boolean isPhoneVerified;

    @JsonProperty("mustChangePassword")
    private Boolean mustChangePassword;

    @JsonProperty("lastLoginAt")
    private LocalDateTime lastLoginAt;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
