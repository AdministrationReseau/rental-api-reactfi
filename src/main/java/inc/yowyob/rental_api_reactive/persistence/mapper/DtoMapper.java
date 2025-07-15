package inc.yowyob.rental_api_reactive.persistence.mapper;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import org.springframework.stereotype.Component;

/**
 * Mapper simple pour les DTOs (sans MapStruct pour plus de contrôle)
 */
@Component
public class DtoMapper {

    /**
     * Convertit les informations de sécurité utilisateur en contexte de sécurité
     */
    public SecurityContext toSecurityContext(inc.yowyob.rental_api_reactive.persistence.entity.User user) {
        if (user == null) return null;

        return SecurityContext.builder()
            .userId(user.getId())
            .userType(user.getUserType())
            .organizationId(user.getOrganizationId())
            .agencyId(user.getAgencyId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .isActive(user.getIsActive())
            .isEmailVerified(user.getIsEmailVerified())
            .build();
    }

    /**
     * Convertit un filtre tenant
     */
    public TenantFilter toTenantFilter(inc.yowyob.rental_api_reactive.persistence.entity.User user) {
        if (user == null) return null;

        TenantFilter.TenantFilterBuilder builder = TenantFilter.builder()
            .userId(user.getId())
            .userType(user.getUserType());

        // Super admin voit tout
        if (user.getUserType() == inc.yowyob.rental_api_reactive.application.dto.UserType.SUPER_ADMIN) {
            builder.isGlobalAccess(true);
        } else {
            builder.organizationId(user.getOrganizationId());

            // Si l'utilisateur est lié à une agence spécifique
            if (user.getAgencyId() != null &&
                (user.getUserType() == inc.yowyob.rental_api_reactive.application.dto.UserType.AGENCY_MANAGER ||
                    user.getUserType() == inc.yowyob.rental_api_reactive.application.dto.UserType.RENTAL_AGENT)) {
                builder.agencyId(user.getAgencyId());
                builder.isAgencyRestricted(true);
            }
        }

        return builder.build();
    }

    /**
     * Convertit les statistiques d'agence
     */
    public AgencyStatisticsResponse toAgencyStatisticsResponse(inc.yowyob.rental_api_reactive.persistence.entity.Agency agency) {
        if (agency == null) return null;

        return AgencyStatisticsResponse.builder()
            .agencyId(agency.getId())
            .totalVehicles(agency.getTotalVehicles())
            .activeVehicles(agency.getActiveVehicles())
            .totalDrivers(agency.getTotalDrivers())
            .activeDrivers(agency.getActiveDrivers())
            .totalPersonnel(agency.getTotalPersonnel())
            .totalRentals(agency.getTotalRentals())
            .monthlyRevenue(agency.getMonthlyRevenue())
            .vehicleUtilizationRate(agency.getVehicleUtilizationRate())
            .driverActivityRate(agency.getDriverActivityRate())
            .lastUpdated(agency.getUpdatedAt())
            .build();
    }

    /**
     * Convertit les limites d'abonnement
     */
    public SubscriptionLimitsResponse toSubscriptionLimitsResponse(
        inc.yowyob.rental_api_reactive.persistence.entity.Organization organization,
        int activeAgencies) {

        if (organization == null) return null;

        return SubscriptionLimitsResponse.builder()
            .organizationId(organization.getId())
            .subscriptionActive(organization.isSubscriptionActive())
            .subscriptionExpiresAt(organization.getSubscriptionExpiresAt())
            .agencyLimits(ResourceLimitInfo.builder()
                .current(activeAgencies)
                .maximum(organization.getMaxAgencies())
                .available(organization.getMaxAgencies() - activeAgencies)
                .usagePercentage(organization.getAgencyUsagePercentage())
                .build())
            .vehicleLimits(ResourceLimitInfo.builder()
                .current(organization.getCurrentVehicles())
                .maximum(organization.getMaxVehicles())
                .available(organization.getMaxVehicles() - organization.getCurrentVehicles())
                .usagePercentage(organization.getVehicleUsagePercentage())
                .build())
            .driverLimits(ResourceLimitInfo.builder()
                .current(organization.getCurrentDrivers())
                .maximum(organization.getMaxDrivers())
                .available(organization.getMaxDrivers() - organization.getCurrentDrivers())
                .usagePercentage(organization.getDriverUsagePercentage())
                .build())
            .userLimits(ResourceLimitInfo.builder()
                .current(organization.getCurrentUsers())
                .maximum(organization.getMaxUsers())
                .available(organization.getMaxUsers() - organization.getCurrentUsers())
                .usagePercentage(organization.getUserUsagePercentage())
                .build())
            .build();
    }

    /**
     * Convertit les statistiques d'organisation
     */
    public OrganizationStatisticsResponse toOrganizationStatisticsResponse(
        inc.yowyob.rental_api_reactive.persistence.entity.Organization organization,
        int totalAgencies,
        int activeAgencies) {

        if (organization == null) return null;

        return OrganizationStatisticsResponse.builder()
            .organizationId(organization.getId())
            .totalAgencies(totalAgencies)
            .activeAgencies(activeAgencies)
            .maxAgencies(organization.getMaxAgencies())
            .totalVehicles(organization.getCurrentVehicles())
            .maxVehicles(organization.getMaxVehicles())
            .totalDrivers(organization.getCurrentDrivers())
            .maxDrivers(organization.getMaxDrivers())
            .totalUsers(organization.getCurrentUsers())
            .maxUsers(organization.getMaxUsers())
            .monthlyRevenue(organization.getMonthlyRevenue())
            .yearlyRevenue(organization.getYearlyRevenue())
            .totalRentals(organization.getTotalRentals())
            .agencyUsagePercentage(organization.getAgencyUsagePercentage())
            .vehicleUsagePercentage(organization.getVehicleUsagePercentage())
            .driverUsagePercentage(organization.getDriverUsagePercentage())
            .userUsagePercentage(organization.getUserUsagePercentage())
            .isSubscriptionActive(organization.isSubscriptionActive())
            .subscriptionExpiresAt(organization.getSubscriptionExpiresAt())
            .lastActivityAt(organization.getLastActivityAt())
            .build();
    }
}
