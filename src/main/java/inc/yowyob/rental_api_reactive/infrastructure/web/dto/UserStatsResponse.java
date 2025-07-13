package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO pour les statistiques des utilisateurs
 */
@Data
public class UserStatsResponse {
    @JsonProperty("totalUsers")
    public Long totalUsers;

    @JsonProperty("activeUsers")
    public Long activeUsers;

    @JsonProperty("personnelCount")
    public Long personnelCount;

    @JsonProperty("clientCount")
    public Long clientCount;

    @JsonProperty("inactiveUsers")
    public Long getInactiveUsers() {
        return totalUsers - activeUsers;
    }

    @JsonProperty("personnelPercentage")
    public Double getPersonnelPercentage() {
        return totalUsers > 0 ? (personnelCount.doubleValue() / totalUsers.doubleValue()) * 100 : 0.0;
    }

    @JsonProperty("clientPercentage")
    public Double getClientPercentage() {
        return totalUsers > 0 ? (clientCount.doubleValue() / totalUsers.doubleValue()) * 100 : 0.0;
    }
}
