// PATH: src/main/java/inc/yowyob/rental_api_reactive/application/service/DriverReactiveService.java

package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.application.dto.UserType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverReactiveService {

    private final DriverReactiveRepository driverRepository; // Nom plus court et standard
    private final UserReactiveRepository userRepository;
    private final DriverMapper driverMapper;
    private final SubscriptionValidationReactiveService subscriptionValidationService;

    @Transactional
    public Mono<DriverResponse> createDriver(CreateDriverRequest createDto, UUID createdBy) {
        log.info("Attempting to create a driver for user ID {} by user {}", createDto.getUserId(), createdBy);

        // Validation améliorée
        if (createDto.getDateOfBirth() != null && Period.between(createDto.getDateOfBirth(), LocalDate.now()).getYears() < 18) {
            return Mono.error(new IllegalArgumentException("Driver must be at least 18 years old."));
        }

        // 1. Récupérer l'utilisateur et le garder dans la chaîne pour éviter une deuxième récupération
        Mono<User> userMono = userRepository.findById(createDto.getUserId())
            .switchIfEmpty(Mono.error(new NoSuchElementException("User not found with ID: " + createDto.getUserId())))
            .flatMap(user -> {
                // CORRECTION LOGIQUE : on ne peut pas créer un profil Driver pour un client simple.
                if (user.getUserType() == UserType.CLIENT) {
                    return Mono.error(new IllegalArgumentException("Cannot create a driver profile for a CUSTOMER user type."));
                }
                return Mono.just(user);
            });

        return userMono.flatMap(user ->
            // 2. Vérifier que cet utilisateur n'est pas déjà un chauffeur
            driverRepository.findByUserId(user.getId())
                .hasElement()
                .flatMap(isAlreadyDriver -> {
                    if (isAlreadyDriver) {
                        return Mono.error(new IllegalStateException("This user is already registered as a driver."));
                    }

                    // 3. Valider la limite d'abonnement
                    return subscriptionValidationService.validateDriverCreationLimit(createDto.getOrganizationId())
                        .flatMap(canCreate -> {
                            if (!canCreate) {
                                return Mono.error(new IllegalStateException("Driver creation limit reached for this organization."));
                            }
                            
                            // 4. Construire et sauvegarder l'entité
                            Driver newDriver = driverMapper.fromCreateRequest(createDto); // Utiliser un mapper est plus propre
                            newDriver.setDriverId(UUID.randomUUID());
                            newDriver.setCreatedAt(LocalDateTime.now());
                            newDriver.setUpdatedAt(LocalDateTime.now());
                            newDriver.setStatusUpdatedBy(createdBy);
                            
                            return driverRepository.save(newDriver);
                        });
                })
                // 5. Mapper vers la réponse en réutilisant l'objet 'user' du début de la chaîne
                .map(savedDriver -> driverMapper.toResponse(savedDriver, user))
        )
        .doOnSuccess(response -> log.info("Driver created successfully with ID {}", response.getDriverId()))
        .doOnError(e -> log.error("Failed to create driver for user {}: {}", createDto.getUserId(), e.getMessage()));
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
                // if (updateDto.getStatus() != null) {
                //     driver.setStatusUpdatedBy(updatedBy);
                // }
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
        return driverRepository.existsById(driverId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new NoSuchElementException("Driver not found with ID: " + driverId));
                    }
                    return driverRepository.deleteById(driverId);
                })
                .doOnSuccess(v -> log.info("Driver with ID {} deleted successfully", driverId))
                .doOnError(error -> log.error("Failed to delete driver {}: {}", driverId, error));
    }

    public Flux<DriverResponse> getAllDriversByAgency(UUID agencyId, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllDriversByAgency'");
    }
}