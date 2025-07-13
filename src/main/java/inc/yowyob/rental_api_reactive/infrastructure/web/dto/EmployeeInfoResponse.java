package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EmployeeInfoResponse {
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

    @JsonProperty("mustChangePassword")
    private Boolean mustChangePassword;

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @JsonProperty("isAssignedToAgency")
    public Boolean getIsAssignedToAgency() {
        return agencyId != null;
    }

    @JsonProperty("yearsOfService")
    public Integer getYearsOfService() {
        if (hiredAt == null) return null;
        return (int) java.time.temporal.ChronoUnit.YEARS.between(hiredAt.toLocalDate(), java.time.LocalDate.now());
    }
}
