package inc.yowyob.rental_api_reactive.application.service;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.*;
import inc.yowyob.rental_api_reactive.persistence.entity.User;
import inc.yowyob.rental_api_reactive.persistence.repository.UserReactiveRepository;
import inc.yowyob.rental_api_reactive.application.dto.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service réactif pour la gestion du personnel
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelReactiveService {

    private final UserReactiveRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Types d'utilisateurs considérés comme personnel
    private static final List<UserType> PERSONNEL_TYPES = Arrays.asList(
        UserType.AGENCY_MANAGER,
        UserType.RENTAL_AGENT
    );

    /**
     * Crée un nouveau membre du personnel
     */
    public Mono<PersonnelResponse> createPersonnel(CreatePersonnelRequest createRequest, UUID createdBy) {
        log.info("Creating personnel: {} for organization: {}",
            createRequest.getEmail(), createRequest.getOrganizationId());

        // Vérifier que le type d'utilisateur est valide pour le personnel
        if (!PERSONNEL_TYPES.contains(createRequest.getUserType())) {
            return Mono.error(new IllegalArgumentException(
                "Type d'utilisateur invalide pour le personnel. Types autorisés: " + PERSONNEL_TYPES));
        }

        return userRepository.countByEmail(createRequest.getEmail())
            .flatMap(count -> {
                if (count > 0) {
                    return Mono.error(new IllegalArgumentException("Email déjà utilisé"));
                }

                // Créer le nouvel utilisateur personnel
                User personnel = new User();
                personnel.setId(UUID.randomUUID());
                personnel.setEmail(createRequest.getEmail());
                personnel.setPassword(passwordEncoder.encode(createRequest.getTemporaryPassword()));
                personnel.setFirstName(createRequest.getFirstName());
                personnel.setLastName(createRequest.getLastName());
                personnel.setPhone(createRequest.getPhone());
                personnel.setUserType(createRequest.getUserType());
                personnel.setOrganizationId(createRequest.getOrganizationId());
                personnel.setAgencyId(createRequest.getAgencyId());
                personnel.setIsActive(true);
                personnel.setIsEmailVerified(false);
                personnel.setIsPhoneVerified(false);

                // Informations employé spécifiques
                personnel.setEmployeeId(createRequest.getEmployeeId());
                personnel.setDepartment(createRequest.getDepartment());
                personnel.setPosition(createRequest.getPosition());
                personnel.setSupervisorId(createRequest.getSupervisorId());
                personnel.setHiredAt(LocalDateTime.now());

                // Forcer le changement de mot de passe à la première connexion
                personnel.setMustChangePassword(true);

                personnel.setCreatedAt(LocalDateTime.now());
                personnel.setUpdatedAt(LocalDateTime.now());
                personnel.setCreatedBy(createdBy);

                return userRepository.save(personnel)
                    .map(this::mapToPersonnelResponse)
                    .doOnSuccess(response -> log.info("Personnel created successfully: {}", createRequest.getEmail()));
            });
    }

    /**
     * Récupère le personnel selon les filtres
     */
    public Flux<PersonnelResponse> getPersonnelByFilters(UUID organizationId, UUID agencyId,
                                                         UserType userType, Boolean isActive) {
        log.debug("Fetching personnel for organization: {}, agency: {}, type: {}, active: {}",
            organizationId, agencyId, userType, isActive);

        return userRepository.findByOrganizationId(organizationId)
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .filter(user -> agencyId == null || agencyId.equals(user.getAgencyId()))
            .filter(user -> userType == null || userType.equals(user.getUserType()))
            .filter(user -> isActive == null || isActive.equals(user.getIsActive()))
            .map(this::mapToPersonnelResponse);
    }

    /**
     * Récupère un personnel par ID
     */
    public Mono<PersonnelResponse> getPersonnelById(UUID personnelId) {
        log.debug("Fetching personnel by ID: {}", personnelId);

        return userRepository.findById(personnelId)
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .map(this::mapToPersonnelResponse);
    }

    /**
     * Met à jour un membre du personnel
     */
    public Mono<PersonnelResponse> updatePersonnel(UUID personnelId, UpdatePersonnelRequest updateRequest, UUID updatedBy) {
        log.info("Updating personnel: {}", personnelId);

        return userRepository.findById(personnelId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Personnel non trouvé")))
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("L'utilisateur n'est pas un membre du personnel")))
            .flatMap(personnel -> {
                // Mettre à jour les champs modifiables
                if (updateRequest.getFirstName() != null) {
                    personnel.setFirstName(updateRequest.getFirstName());
                }
                if (updateRequest.getLastName() != null) {
                    personnel.setLastName(updateRequest.getLastName());
                }
                if (updateRequest.getPhone() != null) {
                    personnel.setPhone(updateRequest.getPhone());
                }
                if (updateRequest.getEmployeeId() != null) {
                    personnel.setEmployeeId(updateRequest.getEmployeeId());
                }
                if (updateRequest.getDepartment() != null) {
                    personnel.setDepartment(updateRequest.getDepartment());
                }
                if (updateRequest.getPosition() != null) {
                    personnel.setPosition(updateRequest.getPosition());
                }
                if (updateRequest.getSupervisorId() != null) {
                    personnel.setSupervisorId(updateRequest.getSupervisorId());
                }

                personnel.setUpdatedAt(LocalDateTime.now());
                personnel.setUpdatedBy(updatedBy);

                return userRepository.save(personnel);
            })
            .map(this::mapToPersonnelResponse);
    }

    /**
     * Assigne un personnel à une agence
     */
    public Mono<PersonnelResponse> assignToAgency(UUID personnelId, UUID agencyId, UUID assignedBy) {
        log.info("Assigning personnel {} to agency: {}", personnelId, agencyId);

        return userRepository.findById(personnelId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Personnel non trouvé")))
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("L'utilisateur n'est pas un membre du personnel")))
            .flatMap(personnel -> {
                personnel.setAgencyId(agencyId);
                personnel.setUpdatedAt(LocalDateTime.now());
                personnel.setUpdatedBy(assignedBy);

                return userRepository.save(personnel);
            })
            .map(this::mapToPersonnelResponse);
    }

    /**
     * Met à jour le statut d'un personnel
     */
    public Mono<PersonnelResponse> updateStatus(UUID personnelId, Boolean isActive, UUID updatedBy) {
        log.info("Updating personnel {} status to: {}", personnelId, isActive);

        return userRepository.findById(personnelId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Personnel non trouvé")))
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("L'utilisateur n'est pas un membre du personnel")))
            .flatMap(personnel -> {
                personnel.setIsActive(isActive);
                personnel.setUpdatedAt(LocalDateTime.now());
                personnel.setUpdatedBy(updatedBy);

                return userRepository.save(personnel);
            })
            .map(this::mapToPersonnelResponse);
    }

    /**
     * Supprime un personnel
     */
    public Mono<Void> deletePersonnel(UUID personnelId, UUID deletedBy) {
        log.info("Deleting personnel: {}", personnelId);

        return userRepository.findById(personnelId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Personnel non trouvé")))
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("L'utilisateur n'est pas un membre du personnel")))
            .flatMap(personnel -> userRepository.deleteById(personnelId));
    }

    /**
     * Récupère le personnel d'une agence
     */
    public Flux<PersonnelResponse> getPersonnelByAgency(UUID agencyId, UserType userType) {
        log.debug("Fetching personnel for agency: {}, type: {}", agencyId, userType);

        return userRepository.findAll()
            .filter(user -> agencyId.equals(user.getAgencyId()))
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .filter(user -> userType == null || userType.equals(user.getUserType()))
            .map(this::mapToPersonnelResponse);
    }

    /**
     * Vérifie si un utilisateur est du personnel
     */
    public Mono<Boolean> isPersonnel(UUID userId) {
        return userRepository.findById(userId)
            .map(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .defaultIfEmpty(false);
    }

    /**
     * Récupère les informations d'agence pour un personnel (pour la redirection après login)
     */
    public Mono<AgencyRedirectInfo> getPersonnelAgencyInfo(UUID personnelId) {
        log.debug("Getting agency info for personnel: {}", personnelId);

        return userRepository.findById(personnelId)
            .filter(user -> PERSONNEL_TYPES.contains(user.getUserType()))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Utilisateur n'est pas du personnel")))
            .map(personnel -> {
                AgencyRedirectInfo redirectInfo = new AgencyRedirectInfo();
                redirectInfo.setPersonnelId(personnel.getId());
                redirectInfo.setOrganizationId(personnel.getOrganizationId());
                redirectInfo.setAgencyId(personnel.getAgencyId());
                redirectInfo.setUserType(personnel.getUserType());
                redirectInfo.setEmployeeId(personnel.getEmployeeId());
                redirectInfo.setDepartment(personnel.getDepartment());
                redirectInfo.setPosition(personnel.getPosition());
                redirectInfo.setRequiresPasswordChange(personnel.getMustChangePassword());
                return redirectInfo;
            });
    }

    /**
     * Mappe une entité User vers PersonnelResponse
     */
    private PersonnelResponse mapToPersonnelResponse(User user) {
        PersonnelResponse response = new PersonnelResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setUserType(user.getUserType());
        response.setOrganizationId(user.getOrganizationId());
        response.setAgencyId(user.getAgencyId());
        response.setEmployeeId(user.getEmployeeId());
        response.setDepartment(user.getDepartment());
        response.setPosition(user.getPosition());
        response.setSupervisorId(user.getSupervisorId());
        response.setHiredAt(user.getHiredAt());
        response.setIsActive(user.getIsActive());
        response.setIsEmailVerified(user.getIsEmailVerified());
        response.setIsPhoneVerified(user.getIsPhoneVerified());
        response.setMustChangePassword(user.getMustChangePassword());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());
        response.setProfilePicture(user.getProfilePicture());
        return response;
    }
}
