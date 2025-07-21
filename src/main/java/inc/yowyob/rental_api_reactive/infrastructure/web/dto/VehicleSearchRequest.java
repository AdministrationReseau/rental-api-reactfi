package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import inc.yowyob.rental_api_reactive.application.dto.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour la recherche avancée de véhicules
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSearchRequest {

    @JsonProperty("organizationId")
    private UUID organizationId;

    @JsonProperty("agencyId")
    private UUID agencyId;

    @JsonProperty("brandIds")
    private List<UUID> brandIds;

    @JsonProperty("vehicleTypes")
    private List<VehicleType> vehicleTypes;

    @JsonProperty("statuses")
    private List<VehicleStatus> statuses;

    @JsonProperty("fuelTypes")
    private List<FuelType> fuelTypes;

    @JsonProperty("transmissionTypes")
    private List<TransmissionType> transmissionTypes;

    @JsonProperty("minYear")
    private Integer minYear;

    @JsonProperty("maxYear")
    private Integer maxYear;

    @JsonProperty("minDailyPrice")
    private BigDecimal minDailyPrice;

    @JsonProperty("maxDailyPrice")
    private BigDecimal maxDailyPrice;

    @JsonProperty("minSeats")
    private Integer minSeats;

    @JsonProperty("maxSeats")
    private Integer maxSeats;

    @JsonProperty("searchTerm")
    private String searchTerm; // Recherche dans modele, description

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("availableOnly")
    private Boolean availableOnly;

    @JsonProperty("withImages")
    private Boolean withImages;

    @JsonProperty("sortBy")
    private String sortBy; // model, year, price, created_at

    @JsonProperty("sortDirection")
    private String sortDirection; // asc, desc

    @JsonProperty("page")
    private Integer page = 0;

    @JsonProperty("size")
    private Integer size = 20;
}
