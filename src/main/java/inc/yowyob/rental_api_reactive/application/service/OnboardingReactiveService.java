package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.*;
import inc.yowyob.rental_api_reactive.persistence.repository.*;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service d'onboarding
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingReactiveService {

    private final OnboardingSessionReactiveRepository onboardingRepository;
    private final UserReactiveRepository userRepository;
    private final OrganizationReactiveRepository organizationRepository;
    private final SubscriptionPlanReactiveRepository subscriptionPlanRepository;
    private final OrganizationSubscriptionReactiveRepository orgSubscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    /**
     * Démarre une nouvelle session d'onboarding
     */
    public Mono<OnboardingResponse> startOnboarding() {
        log.info("Starting new onboarding session");

        OnboardingSession session = new OnboardingSession();

        return onboardingRepository.save(session)
            .map(this::mapToResponse)
            .doOnSuccess(response -> log.info("Onboarding session started: {}", response.getSessionToken()));
    }

    /**
     * Sauvegarde les informations du propriétaire (Étape 1)
     */
    public Mono<OnboardingResponse> saveOwnerInfo(String sessionToken, OnboardingOwnerRequest ownerRequest) {
        log.info("Saving owner info for session: {}", sessionToken);

        return findSessionByToken(sessionToken)
            .flatMap(session -> {
                try {
                    // Valider que c'est la bonne étape
                    if (session.getCurrentStep() != 1) {
                        return Mono.error(new IllegalStateException("Étape d'onboarding incorrecte"));
                    }

                    // Vérifier que l'email n'existe pas déjà
                    return userRepository.countByEmail(ownerRequest.getEmail())
                        .flatMap(count -> {
                            if (count > 0) {
                                return Mono.error(new IllegalArgumentException("Cet email est déjà utilisé"));
                            }

                            Mono.fromCallable(() -> {
                                String ownerInfoJson = objectMapper.writeValueAsString(ownerRequest);

                                session.setOwnerInfo(ownerInfoJson);
                                session.nextStep();

                                return session;
                            });

                            return onboardingRepository.save(session);
                        });
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("Erreur lors de la sérialisation", e));
                }
            })
            .map(this::mapToResponse)
            .doOnSuccess(response -> log.info("Owner info saved for session: {}", sessionToken));
    }

    /**
     * Sauvegarde les informations de l'organisation (Étape 2)
     */
    public Mono<OnboardingResponse> saveOrganizationInfo(String sessionToken, OnboardingOrganizationRequest orgRequest) {
        log.info("Saving organization info for session: {}", sessionToken);

        return findSessionByToken(sessionToken)
            .flatMap(session -> {
                try {
                    // Valider que c'est la bonne étape
                    if (session.getCurrentStep() != 2) {
                        return Mono.error(new IllegalStateException("Étape d'onboarding incorrecte"));
                    }

                    // Vérifier que le nom d'organisation n'existe pas
                    return organizationRepository.existsByName(orgRequest.getName())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalArgumentException("Ce nom d'organisation existe déjà"));
                            }

                             Mono.fromCallable(() -> {
                                String orgInfoJson = objectMapper.writeValueAsString(orgRequest);

                                session.setOrganizationInfo(orgInfoJson);
                                session.nextStep();

                                return session;
                            });

                            return onboardingRepository.save(session);
                        });
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("Erreur lors de la sérialisation", e));
                }
            })
            .map(this::mapToResponse)
            .doOnSuccess(response -> log.info("Organization info saved for session: {}", sessionToken));
    }

    /**
     * Sauvegarde les informations d'abonnement (Étape 3)
     */
    public Mono<OnboardingResponse> saveSubscriptionInfo(String sessionToken, OnboardingSubscriptionRequest subscriptionRequest) {
        log.info("Saving subscription info for session: {}", sessionToken);

        return findSessionByToken(sessionToken)
            .flatMap(session -> {
                try {
                    // Valider que c'est la bonne étape
                    if (session.getCurrentStep() != 3) {
                        return Mono.error(new IllegalStateException("Étape d'onboarding incorrecte"));
                    }

                    // Vérifier que le plan d'abonnement existe
                    return subscriptionPlanRepository.findById(subscriptionRequest.getSubscriptionPlanId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Plan d'abonnement non trouvé")))
                        .flatMap(plan -> {

                            Mono.fromCallable(() -> {
                                String subscriptionInfoJson = objectMapper.writeValueAsString(subscriptionRequest);

                                session.setSubscriptionInfo(subscriptionInfoJson);
                                session.nextStep();

                                return session;
                            });

                            return onboardingRepository.save(session);
                        });
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("Erreur lors de la sérialisation", e));
                }
            })
            .map(this::mapToResponse)
            .doOnSuccess(response -> log.info("Subscription info saved for session: {}", sessionToken));
    }

    /**
     * Finalise l'onboarding en créant l'utilisateur et l'organisation
     */
    @Transactional
    public Mono<OnboardingCompletionResponse> completeOnboarding(String sessionToken) {
        log.info("Completing onboarding for session: {}", sessionToken);

        return findSessionByToken(sessionToken)
            .flatMap(session -> {
                try {
                    // Vérifier que toutes les étapes sont complétées
                    if (session.getCurrentStep() != 3 || session.getOwnerInfo() == null ||
                        session.getOrganizationInfo() == null || session.getSubscriptionInfo() == null) {
                        return Mono.error(new IllegalStateException("Toutes les étapes doivent être complétées"));
                    }

                    // Désérialiser les informations
                    OnboardingOwnerRequest ownerInfo = objectMapper.readValue(session.getOwnerInfo(), OnboardingOwnerRequest.class);
                    OnboardingOrganizationRequest orgInfo = objectMapper.readValue(session.getOrganizationInfo(), OnboardingOrganizationRequest.class);
                    OnboardingSubscriptionRequest subscriptionInfo = objectMapper.readValue(session.getSubscriptionInfo(), OnboardingSubscriptionRequest.class);

                    // Étape 1: Créer l'organisation
                    return createOrganization(orgInfo, subscriptionInfo)
                        .flatMap(organization ->
                            // Étape 2: Créer l'utilisateur propriétaire
                            createOwnerUser(ownerInfo, organization.getId())
                                .flatMap(user ->
                                    // Étape 3: Créer l'abonnement
                                    createOrganizationSubscription(organization.getId(), subscriptionInfo)
                                        .flatMap(subscription ->
                                            // Étape 4: Finaliser la session
                                            completeSession(session, user.getId(), organization.getId())
                                                .map(completedSession -> buildCompletionResponse(user, organization, subscription))
                                        )
                                )
                        );

                } catch (Exception e) {
                    return Mono.error(new RuntimeException("Erreur lors de la finalisation", e));
                }
            })
            .doOnSuccess(response -> log.info("Onboarding completed successfully for organization: {}", response.getOrganization().getId()))
            .doOnError(error -> log.error("Error completing onboarding for session {}: {}", sessionToken, error.getMessage()));
    }

    /**
     * Récupère l'état d'une session d'onboarding
     */
    public Mono<OnboardingResponse> getOnboardingStatus(String sessionToken) {
        return findSessionByToken(sessionToken)
            .map(this::mapToResponse);
    }

    // === MÉTHODES PRIVÉES ===

    /**
     * Trouve une session par token
     */
    private Mono<OnboardingSession> findSessionByToken(String sessionToken) {
        return onboardingRepository.findBySessionToken(sessionToken)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Session d'onboarding non trouvée")))
            .flatMap(session -> {
                if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
                    return Mono.error(new IllegalArgumentException("Session d'onboarding expirée"));
                }
                return Mono.just(session);
            });
    }

    /**
     * Crée l'organisation
     */
    private Mono<Organization> createOrganization(OnboardingOrganizationRequest orgInfo, OnboardingSubscriptionRequest subscriptionInfo) {
        return subscriptionPlanRepository.findById(subscriptionInfo.getSubscriptionPlanId())
            .flatMap(plan -> {
                Organization organization = new Organization(
                    orgInfo.getName(),
                    orgInfo.getOrganizationType(),
                    null // ownerId sera défini après la création de l'utilisateur
                );

                // Informations de base
                organization.setDescription(orgInfo.getDescription());
                organization.setAddress(orgInfo.getAddress());
                organization.setCity(orgInfo.getCity());
                organization.setCountry(orgInfo.getCountry() != null ? orgInfo.getCountry() : "CM");
                organization.setPostalCode(orgInfo.getPostalCode());
                organization.setRegion(orgInfo.getRegion());
                organization.setPhone(orgInfo.getPhone());
                organization.setEmail(orgInfo.getEmail());
                organization.setWebsite(orgInfo.getWebsite());

                // Informations légales
                organization.setRegistrationNumber(orgInfo.getRegistrationNumber());
                organization.setTaxNumber(orgInfo.getTaxNumber());
                organization.setBusinessLicense(orgInfo.getBusinessLicense());

                // Limites selon le plan d'abonnement
                organization.setMaxAgencies(plan.getMaxAgencies());
                organization.setMaxVehicles(plan.getMaxVehicles());
                organization.setMaxDrivers(plan.getMaxDrivers());
                organization.setMaxUsers(plan.getMaxUsers());

                // Configuration par défaut
                organization.setCurrency(orgInfo.getCurrency() != null ? orgInfo.getCurrency() : "XAF");
                organization.setTimezone(orgInfo.getTimezone() != null ? orgInfo.getTimezone() : "Africa/Douala");
                organization.setLanguage(orgInfo.getLanguage() != null ? orgInfo.getLanguage() : "fr");

                // Branding
                organization.setPrimaryColor(orgInfo.getPrimaryColor());
                organization.setSecondaryColor(orgInfo.getSecondaryColor());

                return organizationRepository.save(organization);
            });
    }

    /**
     * Crée l'utilisateur propriétaire
     */
    private Mono<User> createOwnerUser(OnboardingOwnerRequest ownerInfo, UUID organizationId) {
        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setEmail(ownerInfo.getEmail());
        owner.setPassword(passwordEncoder.encode(ownerInfo.getPassword()));
        owner.setFirstName(ownerInfo.getFirstName());
        owner.setLastName(ownerInfo.getLastName());
        owner.setPhone(ownerInfo.getPhone());
        owner.setUserType(UserType.ORGANIZATION_OWNER);
        owner.setOrganizationId(organizationId);
        owner.setAddress(ownerInfo.getAddress());
        owner.setCity(ownerInfo.getCity());
        owner.setCountry(ownerInfo.getCountry() != null ? ownerInfo.getCountry() : "CM");
        owner.setIsActive(true);
        owner.setIsEmailVerified(false);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(owner)
            .flatMap(savedUser -> {
                // Mettre à jour l'organisation avec l'ID du propriétaire
                return organizationRepository.findById(organizationId)
                    .flatMap(organization -> {
                        organization.setOwnerId(savedUser.getId());
                        organization.incrementAgencies(); // L'utilisateur compte comme +1 utilisateur
                        return organizationRepository.save(organization)
                            .then(Mono.just(savedUser));
                    });
            });
    }

    /**
     * Crée l'abonnement de l'organisation
     */
    private Mono<OrganizationSubscription> createOrganizationSubscription(UUID organizationId, OnboardingSubscriptionRequest subscriptionInfo) {
        return subscriptionPlanRepository.findById(subscriptionInfo.getSubscriptionPlanId())
            .flatMap(plan -> {
                OrganizationSubscription subscription = new OrganizationSubscription(
                    organizationId,
                    plan.getId(),
                    plan.getPrice(),
                    subscriptionInfo.getPaymentMethod()
                );

                // Configuration des dates
                LocalDateTime now = LocalDateTime.now();
                subscription.setStartDate(now);
                subscription.setEndDate(now.plusDays(plan.getDurationDays()));
                subscription.setNextBillingDate(now.plusDays(plan.getDurationDays()));

                // Informations de paiement
                subscription.setPaymentReference(subscriptionInfo.getPaymentReference());
                subscription.setAutoRenew(subscriptionInfo.getAutoRenew() != null ? subscriptionInfo.getAutoRenew() : true);

                return orgSubscriptionRepository.save(subscription)
                    .flatMap(savedSubscription -> {
                        // Mettre à jour l'organisation avec les informations d'abonnement
                        return organizationRepository.findById(organizationId)
                            .flatMap(organization -> {
                                organization.updateSubscription(
                                    plan.getId(),
                                    savedSubscription.getEndDate(),
                                    savedSubscription.getAutoRenew()
                                );
                                return organizationRepository.save(organization)
                                    .then(Mono.just(savedSubscription));
                            });
                    });
            });
    }

    /**
     * Finalise la session d'onboarding
     */
    private Mono<OnboardingSession> completeSession(OnboardingSession session, UUID userId, UUID organizationId) {
        session.complete(organizationId);
        session.setUserId(userId);
        return onboardingRepository.save(session);
    }

    /**
     * Construit la réponse de finalisation
     */
    private OnboardingCompletionResponse buildCompletionResponse(User user, Organization organization, OrganizationSubscription subscription) {
        return OnboardingCompletionResponse.builder()
            .success(true)
            .message("Onboarding complété avec succès")
            .user(UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userType(user.getUserType())
                .organizationId(user.getOrganizationId())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build())
            .organization(OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .organizationType(organization.getOrganizationType())
                .description(organization.getDescription())
                .ownerId(organization.getOwnerId())
                .email(organization.getEmail())
                .phone(organization.getPhone())
                .fullAddress(organization.getFullAddress())
                .isActive(organization.getIsActive())
                .isVerified(organization.getIsVerified())
                .maxAgencies(organization.getMaxAgencies())
                .maxVehicles(organization.getMaxVehicles())
                .maxDrivers(organization.getMaxDrivers())
                .maxUsers(organization.getMaxUsers())
                .createdAt(organization.getCreatedAt())
                .build())
            .subscription(SubscriptionResponse.builder()
                .id(subscription.getId())
                .organizationId(subscription.getOrganizationId())
                .subscriptionPlanId(subscription.getSubscriptionPlanId())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .amount(subscription.getAmount())
                .currency(subscription.getCurrency())
                .autoRenew(subscription.getAutoRenew())
                .isActive(subscription.isActive())
                .build())
            .nextSteps("Vérifiez votre email pour activer votre compte. Vous pouvez maintenant accéder à votre tableau de bord.")
            .build();
    }

    /**
     * Mappe une session vers une réponse
     */
    private OnboardingResponse mapToResponse(OnboardingSession session) {
        return OnboardingResponse.builder()
            .sessionToken(session.getSessionToken())
            .currentStep(session.getCurrentStep())
            .maxStep(session.getMaxStep())
            .isCompleted(session.getIsCompleted())
            .expiresAt(session.getExpiresAt())
            .hasOwnerInfo(session.getOwnerInfo() != null)
            .hasOrganizationInfo(session.getOrganizationInfo() != null)
            .hasSubscriptionInfo(session.getSubscriptionInfo() != null)
            .createdAt(session.getCreatedAt())
            .build();
    }
}
