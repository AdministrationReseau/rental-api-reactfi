package inc.yowyob.rental_api_reactive.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.CreateOrganizationRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OrganizationPolicies;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OrganizationResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OrganizationSettings;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UpdateOrganizationRequest;
import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Mapper pour les organisations, utilisant une classe abstraite pour la conversion JSON.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class OrganizationMapper {

    @Autowired
    private ObjectMapper objectMapper;

    // La méthode toResponse nécessite des méthodes custom pour policies et settings
    @Mapping(target = "fullAddress", expression = "java(organization.getFullAddress())")
    @Mapping(target = "canCreateAgency", expression = "java(organization.canCreateAgency())")
    @Mapping(target = "canAddVehicle", expression = "java(organization.canAddVehicle())")
    @Mapping(target = "canAddDriver", expression = "java(organization.canAddDriver())")
    @Mapping(target = "canAddUser", expression = "java(organization.canAddUser())")
    @Mapping(target = "agencyUsagePercentage", expression = "java(organization.getAgencyUsagePercentage())")
    @Mapping(target = "vehicleUsagePercentage", expression = "java(organization.getVehicleUsagePercentage())")
    @Mapping(target = "driverUsagePercentage", expression = "java(organization.getDriverUsagePercentage())")
    @Mapping(target = "userUsagePercentage", expression = "java(organization.getUserUsagePercentage())")
    @Mapping(target = "isSubscriptionActive", expression = "java(organization.isSubscriptionActive())")
    @Mapping(target = "isSubscriptionExpiringSoon", expression = "java(organization.isSubscriptionExpiringSoon())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract OrganizationResponse toResponse(Organization organization);


    // La méthode toEntity nécessite des méthodes custom pour policies et settings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "verificationDate", ignore = true)
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "currentAgencies", ignore = true)
    @Mapping(target = "currentVehicles", ignore = true)
    @Mapping(target = "currentDrivers", ignore = true)
    @Mapping(target = "currentUsers", ignore = true)
    @Mapping(target = "totalRentals", ignore = true)
    @Mapping(target = "monthlyRevenue", ignore = true)
    @Mapping(target = "yearlyRevenue", ignore = true)
    @Mapping(target = "lastActivityAt", ignore = true)
    @Mapping(target = "subscriptionPlanId", ignore = true)
    @Mapping(target = "subscriptionExpiresAt", ignore = true)
    @Mapping(target = "subscriptionAutoRenew", ignore = true)
    @Mapping(target = "maxAgencies", ignore = true)
    @Mapping(target = "maxVehicles", ignore = true)
    @Mapping(target = "maxDrivers", ignore = true)
    @Mapping(target = "maxUsers", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    // PAS besoin d'ignorer policies et settings car on fournit une méthode de conversion
    public abstract Organization toEntity(CreateOrganizationRequest request);


    // La méthode updateEntityFromRequest nécessite des méthodes custom pour policies et settings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "verificationDate", ignore = true)
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "currentAgencies", ignore = true)
    @Mapping(target = "currentVehicles", ignore = true)
    @Mapping(target = "currentDrivers", ignore = true)
    @Mapping(target = "currentUsers", ignore = true)
    @Mapping(target = "lastActivityAt", ignore = true)
    @Mapping(target = "maxAgencies", ignore = true)
    @Mapping(target = "maxVehicles", ignore = true)
    @Mapping(target = "maxDrivers", ignore = true)
    @Mapping(target = "maxUsers", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    @Mapping(target = "subscriptionPlanId", ignore = true)
    @Mapping(target = "subscriptionExpiresAt", ignore = true)
    @Mapping(target = "subscriptionAutoRenew", ignore = true)
    @Mapping(target = "totalRentals", ignore = true)
    @Mapping(target = "monthlyRevenue", ignore = true)
    @Mapping(target = "yearlyRevenue", ignore = true)
    public abstract void updateEntityFromRequest(UpdateOrganizationRequest request, @MappingTarget Organization organization);


    // === Méthodes de conversion JSON pour les Policies et Settings ===
    // MapStruct utilisera automatiquement ces méthodes quand il verra une conversion
    // entre un objet (ex: OrganizationPolicies) et une String.

    protected String mapPoliciesToString(OrganizationPolicies policies) {
        if (policies == null) return null;
        try {
            // Convertit l'objet OrganizationPolicies en une chaîne JSON
            return objectMapper.writeValueAsString(policies);
        } catch (JsonProcessingException e) {
            // Idéalement, loguer l'erreur
            return null;
        }
    }

    protected OrganizationPolicies mapPolicies(String policiesJson) {
        if (policiesJson == null || policiesJson.isEmpty()) return null;
        try {
            // Convertit la chaîne JSON en un objet OrganizationPolicies
            return objectMapper.readValue(policiesJson, OrganizationPolicies.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    protected String mapSettingsToString(OrganizationSettings settings) {
        if (settings == null) return null;
        try {
            // Convertit l'objet OrganizationSettings en une chaîne JSON
            return objectMapper.writeValueAsString(settings);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    protected OrganizationSettings mapSettings(String settingsJson) {
        if (settingsJson == null || settingsJson.isEmpty()) return null;
        try {
            // Convertit la chaîne JSON en un objet OrganizationSettings
            return objectMapper.readValue(settingsJson, OrganizationSettings.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
