package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Informations sur les limites d'une ressource
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceLimitInfo {
    @JsonProperty("current")
    private Integer current;

    @JsonProperty("maximum")
    private Integer maximum;

    @JsonProperty("available")
    private Integer available;

    @JsonProperty("usage_percentage")
    private Double usagePercentage;
}
