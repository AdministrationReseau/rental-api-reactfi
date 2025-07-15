package inc.yowyob.rental_api_reactive.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.Agency;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;

/**
 * Mapper pour les agences.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {ObjectMapper.class})
public abstract class AgencyMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "fullAddress", expression = "java(agency.getFullAddress())")
    @Mapping(target = "hasLocation", expression = "java(agency.hasLocation())")
    @Mapping(target = "hasGeofencing", expression = "java(agency.hasGeofencing())")
    @Mapping(target = "isCurrentlyOpen", expression = "java(agency.isCurrentlyOpen())")
    @Mapping(target = "vehicleUtilizationRate", expression = "java(agency.getVehicleUtilizationRate())")
    @Mapping(target = "driverActivityRate", expression = "java(agency.getDriverActivityRate())")
    public abstract AgencyResponse toResponse(Agency agency);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "totalVehicles", ignore = true)
    @Mapping(target = "activeVehicles", ignore = true)
    @Mapping(target = "totalDrivers", ignore = true)
    @Mapping(target = "activeDrivers", ignore = true)
    @Mapping(target = "totalPersonnel", ignore = true)
    @Mapping(target = "totalRentals", ignore = true)
    @Mapping(target = "monthlyRevenue", ignore = true)
    public abstract Agency toEntity(CreateAgencyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    public abstract void updateEntityFromRequest(UpdateAgencyRequest request, @MappingTarget Agency agency);

    /**
     * Convertit un JSON String en Map.
     * Gère l'erreur de parsing.
     */
    protected Map<String, WorkingHoursInfo> mapWorkingHours(String workingHoursJson) {
        if (workingHoursJson == null || workingHoursJson.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            // Assurez-vous d'avoir un DTO WorkingHoursInfo
            return objectMapper.readValue(workingHoursJson, new TypeReference<Map<String, WorkingHoursInfo>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyMap();
        }
    }

    /**
     * Convertit une Map en JSON String.
     */
    protected String mapWorkingHoursToString(Map<String, WorkingHoursInfo> workingHours) {
        if (workingHours == null || workingHours.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(workingHours);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
