package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.DriverStatus;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ChangeDriverStatusRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.CreateDriverRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.DriverResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UpdateDriverRequest;
import inc.yowyob.rental_api_reactive.persistence.entity.Driver;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.mapper.DriverMapper;
import inc.yowyob.rental_api_reactive.persistence.repository.DriverReactiveRepository;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// import java.time.LocalDate;
import java.time.LocalDateTime;
// import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverReactiveService {

    private final DriverReactiveRepository driverRepository;
    private final UserReactiveRepository userRepository;
    private final DriverMapper driverMapper;
    private final SubscriptionValidationReactiveService subscriptionValidationService;

    @Transactional
    public Mono<DriverResponse> createDriver(CreateDriverRequest createDto, UUID createdBy) {
        log.info("Attempting to create a driver for user ID {} by user {}", createDto.getUserId(), createdBy);

        // 1. Trouver l'utilisateur ET valider le type
        return userRepository.findById(createDto.getUserId())
            .switchIfEmpty(Mono.error(new NoSuchElementException("User not found with ID: " + createDto.getUserId())))
            .flatMap(user -> { // FlatMap sur user pour continuer le traitement
                if (user.getUserType() == UserType.CLIENT) {
                    return Mono.error(new IllegalArgumentException("Cannot create a driver profile for a CUSTOMER user type."));
                }

                // 2. Vérifier que cet utilisateur n'est pas déjà un chauffeur
                return driverRepository.findByUserId(user.getId())
                     .flatMap(existingDriver -> 
                        Mono.<DriverResponse>error(new IllegalStateException("This user is already registered as a driver."))
                    )
                    .switchIfEmpty(
                        // 3. Valider la limite d'abonnement ET créer le driver si tout est bon
                        subscriptionValidationService.validateDriverCreationLimit(createDto.getOrganizationId())
                            .flatMap(canCreate -> {
                                if (!canCreate) {
                                    return Mono.error(new IllegalStateException("Driver creation limit reached for this organization."));
                                }

                                // 4. Créer le driver (séparé pour forcer le type)
                                return createAndMapDriver(createDto, user, createdBy);
                            })
                    );
            })
            .doOnSuccess(response -> log.info("Driver created successfully with ID {}", response.getDriverId()))
            .doOnError(e -> log.error("Failed to create driver for user {}: {}", createDto.getUserId(), e.getMessage()));
    }

    // Fonction utilitaire pour créer et mapper le driver
    private Mono<DriverResponse> createAndMapDriver(CreateDriverRequest createDto, User user, UUID createdBy) {
        Driver newDriver = driverMapper.fromCreateRequest(createDto);
        newDriver.setDriverId(UUID.randomUUID());
        newDriver.setCreatedAt(LocalDateTime.now());
        newDriver.setUpdatedAt(LocalDateTime.now());
        newDriver.setStatusUpdatedBy(createdBy);
        return driverRepository.save(newDriver)
            .map(savedDriver -> driverMapper.toResponse(savedDriver, user)); // On utilise user pour le mapping
    }

    
    public Mono<DriverResponse> getDriverById(UUID driverId) {
        log.info("Fetching driver with ID {}", driverId);

        return driverRepository.findById(driverId)
            .switchIfEmpty(Mono.error(new NoSuchElementException("Driver not found with ID: " + driverId)))
            .flatMap(driver -> {
                // Enrichir avec les informations de l'utilisateur
                Mono<User> userMono = userRepository.findById(driver.getUserId())
                    .switchIfEmpty(Mono.error(new IllegalStateException("Data inconsistency: User not found for driver " + driverId))); // Erreur plus précise
                
                return Mono.zip(Mono.just(driver), userMono)
                           .map(tuple -> driverMapper.toResponse(tuple.getT1(), tuple.getT2()));
            })
            .doOnError(e -> log.error("Error fetching driver {}: {}", driverId, e.getMessage()));
    }
    
    // NOUVELLE MÉTHODE : SOLUTION AU PROBLÈME N+1
    public Flux<DriverResponse> getAllDriversByOrganization(UUID organizationId, Pageable pageable) {
        log.info("Fetching all drivers for organization {}", organizationId);

        Flux<Driver> driversFlux = driverRepository.findByOrganizationId(organizationId, pageable);

        return driversFlux.collectList()
            .flatMapMany(drivers -> {
                if (drivers.isEmpty()) {
                    return Flux.empty();
                }

                // 1. Collecter tous les user IDs
                List<UUID> userIds = drivers.stream().map(Driver::getUserId).distinct().collect(Collectors.toList());

                // 2. Récupérer tous les utilisateurs en UNE SEULE requête
                return userRepository.findAllById(userIds)
                    .collectMap(User::getId) // Créer une Map<UUID, User> pour un accès facile
                    .flatMapMany(userMap -> {
                        // 3. Combiner les drivers et les users
                        return Flux.fromIterable(drivers)
                            .map(driver -> {
                                User user = userMap.get(driver.getUserId());
                                // Gérer le cas où un utilisateur pourrait être manquant
                                if (user == null) {
                                    log.warn("Data inconsistency: User {} not found for driver {}", driver.getUserId(), driver.getDriverId());
                                    return driverMapper.toResponse(driver, null); // ou filtrer cet enregistrement
                                }
                                return driverMapper.toResponse(driver, user);
                            });
                    });
            });
    }

    @Transactional
    public Mono<DriverResponse> updateDriver(UUID driverId, UpdateDriverRequest updateDto, UUID updatedBy) {
        log.info("Updating driver {} by user {}", driverId, updatedBy);

        return driverRepository.findById(driverId)
            .switchIfEmpty(Mono.error(new NoSuchElementException("Driver not found with ID: " + driverId)))
            .flatMap(driver -> {
                // Utiliser un mapper pour la mise à jour est plus propre et plus sûr
                driverMapper.updateFromRequest(updateDto, driver);
                driver.setUpdatedAt(LocalDateTime.now());
                if (updateDto.getStatus() != null) {
                    driver.setStatusUpdatedBy(updatedBy);
                }
                return driverRepository.save(driver);
            })
            .flatMap(savedDriver ->
                // Utiliser la même technique de 'zip' que dans getDriverById pour une meilleure performance
                Mono.zip(
                    Mono.just(savedDriver),
                    userRepository.findById(savedDriver.getUserId())
                ).map(tuple -> driverMapper.toResponse(tuple.getT1(), tuple.getT2()))
            )
            .doOnSuccess(response -> log.info("Driver {} updated successfully.", driverId))
            .doOnError(e -> log.error("Failed to update driver {}: {}", driverId, e.getMessage()));
    }


    @Transactional
    public Mono<Void> deleteDriver(UUID driverId) {
        log.info("Deleting driver with ID {}", driverId);
        // La logique est plus simple : on vérifie l'existence puis on supprime.
        return driverRepository.findById(driverId) // Recherche par ID et non plus existsById
            .switchIfEmpty(Mono.error(new NoSuchElementException("Driver not found with ID: " + driverId)))
            .flatMap(driver -> driverRepository.deleteById(driverId))
            .doOnSuccess(v -> log.info("Driver with ID {} deleted successfully", driverId))
            .doOnError(error -> log.error("Failed to delete driver {}: {}", driverId, error));
    }

    public Flux<DriverResponse> getAllDriversByAgency(UUID agencyId, Pageable pageable) {
        log.info("Fetching all AVAILABLE drivers for agency {}", agencyId);

        return driverRepository.findAvailableDriversByAgencyId(agencyId) // Utilisation de la nouvelle méthode
            .flatMap(driver -> {
                Mono<User> userMono = userRepository.findById(driver.getUserId())
                    .switchIfEmpty(Mono.error(new IllegalStateException("Data inconsistency: User not found for driver " + driver.getDriverId()))); // Erreur plus précise

                return userMono.map(user -> driverMapper.toResponse(driver, user));
            })
            .doOnError(e -> log.error("Error fetching drivers for agency {}: {}", agencyId, e.getMessage()));
    }

    // Ajoutez ces méthodes à votre DriverReactiveService

    /**
     * Change le statut d'un chauffeur
     */
    // Si vos repositories sont vraiment réactifs, utilisez cette version :

@Transactional
public Mono<DriverResponse> changeDriverStatus(UUID driverId, ChangeDriverStatusRequest request, UUID updatedBy) {
    log.info("Changing driver {} status to {} by user {}", driverId, request.getStatus(), updatedBy);

    return driverRepository.findById(driverId)
        .switchIfEmpty(Mono.error(new NoSuchElementException("Driver not found with ID: " + driverId)))
        .flatMap(driver -> {
            // Vérifier la transition de statut
            if (!DriverStatus.canTransitionTo(driver.getStatus(), request.getStatus())) {
                return Mono.error(new IllegalStateException(
                    String.format("Cannot transition from %s to %s", driver.getStatus(), request.getStatus())
                ));
            }

            // Mettre à jour le statut
            driver.setStatus(request.getStatus());
            driver.setStatusUpdatedAt(LocalDateTime.now());
            driver.setStatusUpdatedBy(updatedBy);
            driver.setUpdatedAt(LocalDateTime.now());

            return driverRepository.save(driver);  // ← Retourne Mono<Driver>
        })
        .flatMap(savedDriver ->
            Mono.zip(
                Mono.just(savedDriver),
                userRepository.findById(savedDriver.getUserId())  // ← Retourne Mono<User>
                    .switchIfEmpty(Mono.error(new IllegalStateException("User not found for driver " + driverId)))
            ).map(tuple -> driverMapper.toResponse(tuple.getT1(), tuple.getT2()))
        )
        .doOnSuccess(response -> log.info("Driver {} status changed to {} successfully", driverId, request.getStatus()))
        .doOnError(e -> log.error("Failed to change driver {} status: {}", driverId, e.getMessage()));
    }
    // Si vos repositories sont vraiment réactifs (ReactiveCassandraRepository), utilisez cette version :

/**
 * Récupère les chauffeurs disponibles par organisation - Version complètement réactive
 */
public Flux<DriverResponse> getAvailableDriversByOrganization(UUID organizationId) {
    log.info("Fetching available drivers for organization {}", organizationId);

    return driverRepository.findAvailableDriversByOrganizationId(organizationId)
        .flatMap(driver -> 
            userRepository.findById(driver.getUserId()) // ← Retourne Mono<User>
                .switchIfEmpty(Mono.error(new IllegalStateException(
                    "Data inconsistency: User not found for driver " + driver.getDriverId()
                )))
                .map(user -> driverMapper.toResponse(driver, user))
        )
        .doOnError(e -> log.error("Error fetching available drivers for organization {}: {}", organizationId, e.getMessage()));
}

/**
 * Récupère les chauffeurs par statut - Version complètement réactive
 */
public Flux<DriverResponse> getDriversByStatus(DriverStatus status, UUID organizationId) {
    log.info("Fetching drivers with status {} for organization {}", status, organizationId);

    return driverRepository.findByStatus(status)
        .filter(driver -> driver.getOrganizationId().equals(organizationId))
        .flatMap(driver -> 
            userRepository.findById(driver.getUserId()) // ← Retourne Mono<User>
                .switchIfEmpty(Mono.error(new IllegalStateException(
                    "Data inconsistency: User not found for driver " + driver.getDriverId()
                )))
                .map(user -> driverMapper.toResponse(driver, user))
        )
        .doOnError(e -> log.error("Error fetching drivers by status {} for organization {}: {}", 
                                status, organizationId, e.getMessage()));
}
    /**
     * Met un chauffeur en service (AVAILABLE)
     */
    @Transactional
    public Mono<DriverResponse> setDriverOnDuty(UUID driverId, UUID updatedBy) {
        log.info("Setting driver {} on duty by user {}", driverId, updatedBy);
        
        ChangeDriverStatusRequest request = new ChangeDriverStatusRequest();
        request.setStatus(DriverStatus.AVAILABLE);
        request.setReason("Set on duty");
        
        return changeDriverStatus(driverId, request, updatedBy);
    }

    /**
     * Met un chauffeur hors service (OFF_DUTY)
     */
    @Transactional
    public Mono<DriverResponse> setDriverOffDuty(UUID driverId, UUID updatedBy) {
        log.info("Setting driver {} off duty by user {}", driverId, updatedBy);
        
        ChangeDriverStatusRequest request = new ChangeDriverStatusRequest();
        request.setStatus(DriverStatus.OFF_DUTY);
        request.setReason("Set off duty");
        
        return changeDriverStatus(driverId, request, updatedBy);
    }


    /**
     * Met un chauffeur disponible pour mission (AVAILABLE)
     */
    @Transactional
    public Mono<DriverResponse> setDriverAvailable(UUID driverId, UUID updatedBy) {
        log.info("Setting driver {} as available by user {}", driverId, updatedBy);
        
        ChangeDriverStatusRequest request = new ChangeDriverStatusRequest();
        request.setStatus(DriverStatus.AVAILABLE);
        request.setReason("Set available for missions");
        
        return changeDriverStatus(driverId, request, updatedBy);
    }

    /**
     * Met un chauffeur en congé (ON_LEAVE)
     */
    @Transactional
    public Mono<DriverResponse> setDriverOnLeave(UUID driverId, UUID updatedBy) {
        log.info("Setting driver {} on leave by user {}", driverId, updatedBy);
        
        ChangeDriverStatusRequest request = new ChangeDriverStatusRequest();
        request.setStatus(DriverStatus.ON_LEAVE);
        request.setReason("Set on leave");
        
        return changeDriverStatus(driverId, request, updatedBy);
    }

    /**
     * Récupère les chauffeurs en service par organisation
     */
    public Flux<DriverResponse> getOnDutyDriversByOrganization(UUID organizationId) {
        log.info("Fetching on-duty drivers for organization {}", organizationId);
        return getDriversByStatus(DriverStatus.ON_DUTY, organizationId);
    }

    /**
     * Statistiques des chauffeurs par statut pour une organisation
     */
    public Mono<Map<DriverStatus, Long>> getDriverStatusStatistics(UUID organizationId) {
        log.info("Fetching driver status statistics for organization {}", organizationId);

        return Flux.fromArray(DriverStatus.values())
            .flatMap(status -> 
                driverRepository.countByOrganizationIdAndStatus(organizationId, status)
                    .map(count -> Map.entry(status, count))
            )
            .collectMap(Map.Entry::getKey, Map.Entry::getValue)
            .doOnError(e -> log.error("Error fetching driver statistics for organization {}: {}", 
                                    organizationId, e.getMessage()));
    }

   
}