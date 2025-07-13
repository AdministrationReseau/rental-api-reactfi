package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO pour les informations du propriétaire (Étape 1)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerInfoRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @JsonProperty("phone")
    private String phone;

    @NotBlank(message = "ID card number is required")
    @JsonProperty("id_card_number")
    private String idCardNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @JsonProperty("city")
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    @JsonProperty("country")
    private String country;

    @Past(message = "Date of birth must be in the past")
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("nationality")
    private String nationality;

    @JsonProperty("profession")
    private String profession;
}
