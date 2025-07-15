package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ResourceLimitInfo;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.SubscriptionChangeValidation;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.SubscriptionLimitsResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.SubscriptionValidationResult;
import inc.yowyob.rental_api_reactive.persistence.entity.Organization;
import inc.yowyob.rental_api_reactive.persistence.entity.SubscriptionPlan;
import inc.yowyob.rental_api_reactive.persistence.repository.OrganizationReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.SubscriptionPlanReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.AgencyReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service de validation des limites d'abonnement
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionValidationReactiveService {

    private final OrganizationReactiveRepository organizationRepository;
    private final SubscriptionPlanReactiveRepository subscriptionPlanRepository;
    private final AgencyReactiveRepository agencyRepository;

    /**
     * Valide si une organisation peut créer une nouvelle agence
     */
    public Mono<Boolean> validateAgencyCreationLimit(UUID organizationId) {
        log.debug("Validating agency creation limit for organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                // Vérifier que l'abonnement est actif
                if (!organization.isSubscriptionActive()) {
                    log.warn("Organization {} has expired subscription", organizationId);
                    return Mono.just(false);
                }

                // Compter les agences actuelles
                return agencyRepository.countActiveByOrganizationId(organizationId)
                    .map(currentAgencies -> {
                        boolean canCreate = currentAgencies < organization.getMaxAgencies();

                        if (!canCreate) {
                            log.warn("Organization {} has reached agency limit: {}/{}",
                                organizationId, currentAgencies, organization.getMaxAgencies());
                        }

                        return canCreate;
                    });
            })
            .doOnNext(canCreate -> log.debug("Agency creation validation result for organization {}: {}",
                organizationId, canCreate));
    }

    /**
     * Valide si une organisation peut ajouter un véhicule
     */
    public Mono<Boolean> validateVehicleCreationLimit(UUID organizationId) {
        log.debug("Validating vehicle creation limit for organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .map(organization -> {
                if (!organization.isSubscriptionActive()) {
                    log.warn("Organization {} has expired subscription", organizationId);
                    return false;
                }

                boolean canCreate = organization.canAddVehicle();

                if (!canCreate) {
                    log.warn("Organization {} has reached vehicle limit: {}/{}",
                        organizationId, organization.getCurrentVehicles(), organization.getMaxVehicles());
                }

                return canCreate;
            });
    }

    /**
     * Valide si une organisation peut ajouter un chauffeur
     */
    public Mono<Boolean> validateDriverCreationLimit(UUID organizationId) {
        log.debug("Validating driver creation limit for organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .map(organization -> {
                if (!organization.isSubscriptionActive()) {
                    log.warn("Organization {} has expired subscription", organizationId);
                    return false;
                }

                boolean canCreate = organization.canAddDriver();

                if (!canCreate) {
                    log.warn("Organization {} has reached driver limit: {}/{}",
                        organizationId, organization.getCurrentDrivers(), organization.getMaxDrivers());
                }

                return canCreate;
            });
    }

    /**
     * Valide si une organisation peut ajouter un utilisateur
     */
    public Mono<Boolean> validateUserCreationLimit(UUID organizationId) {
        log.debug("Validating user creation limit for organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .map(organization -> {
                if (!organization.isSubscriptionActive()) {
                    log.warn("Organization {} has expired subscription", organizationId);
                    return false;
                }

                boolean canCreate = organization.canAddUser();

                if (!canCreate) {
                    log.warn("Organization {} has reached user limit: {}/{}",
                        organizationId, organization.getCurrentUsers(), organization.getMaxUsers());
                }

                return canCreate;
            });
    }

    /**
     * Obtient les limites actuelles d'une organisation
     */
    public Mono<SubscriptionLimitsResponse> getOrganizationLimits(UUID organizationId) {
        log.debug("Getting subscription limits for organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                return agencyRepository.countActiveByOrganizationId(organizationId)
                    .map(activeAgencies -> buildLimitsResponse(organization, activeAgencies.intValue()));
            });
    }

    /**
     * Vérifie si une fonctionnalité est disponible selon le plan d'abonnement
     */
    public Mono<Boolean> isFeatureAvailable(UUID organizationId, String featureName) {
        log.debug("Checking feature availability: {} for organization: {}", featureName, organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                if (!organization.isSubscriptionActive()) {
                    return Mono.just(false);
                }

                if (organization.getSubscriptionPlanId() == null) {
                    return Mono.just(false);
                }

                return subscriptionPlanRepository.findById(organization.getSubscriptionPlanId())
                    .map(plan -> isFeatureEnabledInPlan(plan, featureName))
                    .defaultIfEmpty(false);
            });
    }

    /**
     * Valide toutes les limites pour une organisation
     */
    public Mono<SubscriptionValidationResult> validateAllLimits(UUID organizationId) {
        log.debug("Validating all limits for organization: {}", organizationId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .flatMap(organization -> {
                return agencyRepository.countActiveByOrganizationId(organizationId)
                    .map(activeAgencies -> {
                        SubscriptionValidationResult result = new SubscriptionValidationResult();
                        result.setOrganizationId(organizationId);
                        result.setSubscriptionActive(organization.isSubscriptionActive());

                        // Vérification des limites
                        result.setCanCreateAgency(activeAgencies < organization.getMaxAgencies());
                        result.setCanAddVehicle(organization.canAddVehicle());
                        result.setCanAddDriver(organization.canAddDriver());
                        result.setCanAddUser(organization.canAddUser());

                        // Calcul des pourcentages d'utilisation
                        result.setAgencyUsagePercentage(organization.getAgencyUsagePercentage());
                        result.setVehicleUsagePercentage(organization.getVehicleUsagePercentage());
                        result.setDriverUsagePercentage(organization.getDriverUsagePercentage());
                        result.setUserUsagePercentage(organization.getUserUsagePercentage());

                        // Alertes
                        result.setHasLimitWarnings(
                            result.getAgencyUsagePercentage() > 80 ||
                                result.getVehicleUsagePercentage() > 80 ||
                                result.getDriverUsagePercentage() > 80 ||
                                result.getUserUsagePercentage() > 80
                        );

                        result.setSubscriptionExpiringSoon(organization.isSubscriptionExpiringSoon());

                        return result;
                    });
            });
    }

    /**
     * Valide et applique les limites lors de l'upgrade/downgrade d'abonnement
     */
    public Mono<SubscriptionChangeValidation> validateSubscriptionChange(UUID organizationId, UUID newPlanId) {
        log.debug("Validating subscription change for organization {} to plan {}", organizationId, newPlanId);

        return organizationRepository.findById(organizationId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Organisation non trouvée")))
            .zipWith(subscriptionPlanRepository.findById(newPlanId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Plan d'abonnement non trouvé"))))
            .flatMap(tuple -> {
                Organization organization = tuple.getT1();
                SubscriptionPlan newPlan = tuple.getT2();

                return agencyRepository.countActiveByOrganizationId(organizationId)
                    .map(activeAgencies -> {
                        SubscriptionChangeValidation validation = new SubscriptionChangeValidation();
                        validation.setOrganizationId(organizationId);
                        validation.setNewPlanId(newPlanId);
                        validation.setIsUpgrade(newPlan.getPrice().compareTo(getCurrentPlanPrice(organization)) > 0);

                        // Vérifier les contraintes du nouveau plan
                        validation.setAgencyLimitSufficient(activeAgencies <= newPlan.getMaxAgencies());
                        validation.setVehicleLimitSufficient(organization.getCurrentVehicles() <= newPlan.getMaxVehicles());
                        validation.setDriverLimitSufficient(organization.getCurrentDrivers() <= newPlan.getMaxDrivers());
                        validation.setUserLimitSufficient(organization.getCurrentUsers() <= newPlan.getMaxUsers());

                        validation.setCanChange(
                            validation.getAgencyLimitSufficient() &&
                                validation.getVehicleLimitSufficient() &&
                                validation.getDriverLimitSufficient() &&
                                validation.getUserLimitSufficient()
                        );

                        if (!validation.getCanChange()) {
                            validation.setBlockingReasons(buildBlockingReasons(validation, organization, newPlan));
                        }

                        return validation;
                    });
            });
    }

    // === MÉTHODES PRIVÉES ===

    /**
     * Construit la réponse des limites
     */
    private SubscriptionLimitsResponse buildLimitsResponse(Organization organization, int activeAgencies) {
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
     * Vérifie si une fonctionnalité est activée dans un plan
     */
    private boolean isFeatureEnabledInPlan(SubscriptionPlan plan, String featureName) {
        // TODO: Implémenter selon les fonctionnalités du plan
        switch (featureName.toLowerCase()) {
            case "geofencing":
                return plan.getHasGeofencing();
            case "chat":
                return plan.getHasChat();
            case "analytics":
                return plan.getHasAnalytics();
            case "api_access":
            default:
                return false;
        }
    }

    /**
     * Obtient le prix du plan actuel
     */
    private java.math.BigDecimal getCurrentPlanPrice(Organization organization) {
        // TODO: Récupérer le prix du plan actuel
        return java.math.BigDecimal.ZERO;
    }

    /**
     * Construit les raisons bloquantes pour un changement d'abonnement
     */
    private java.util.List<String> buildBlockingReasons(SubscriptionChangeValidation validation, Organization organization, SubscriptionPlan newPlan) {
        java.util.List<String> reasons = new java.util.ArrayList<>();

        if (!validation.getAgencyLimitSufficient()) {
            reasons.add(String.format("Trop d'agences actives (%d) pour le nouveau plan (limite: %d)",
                organization.getCurrentAgencies(), newPlan.getMaxAgencies()));
        }

        if (!validation.getVehicleLimitSufficient()) {
            reasons.add(String.format("Trop de véhicules (%d) pour le nouveau plan (limite: %d)",
                organization.getCurrentVehicles(), newPlan.getMaxVehicles()));
        }

        if (!validation.getDriverLimitSufficient()) {
            reasons.add(String.format("Trop de chauffeurs (%d) pour le nouveau plan (limite: %d)",
                organization.getCurrentDrivers(), newPlan.getMaxDrivers()));
        }

        if (!validation.getUserLimitSufficient()) {
            reasons.add(String.format("Trop d'utilisateurs (%d) pour le nouveau plan (limite: %d)",
                organization.getCurrentUsers(), newPlan.getMaxUsers()));
        }

        return reasons;
    }
}
