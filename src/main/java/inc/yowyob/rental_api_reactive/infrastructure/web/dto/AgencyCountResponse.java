package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.UUID;

/**
 * DTO pour le comptage des agences
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyCountResponse {

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("total_agencies")
    private Integer totalAgencies;

    @JsonProperty("active_agencies")
    private Integer activeAgencies;

    @JsonProperty("inactive_agencies")
    private Integer inactiveAgencies;
}
