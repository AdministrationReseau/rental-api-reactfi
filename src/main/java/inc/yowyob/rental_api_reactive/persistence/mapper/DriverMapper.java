package inc.yowyob.rental_api_reactive.persistence.mapper;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.CreateDriverRequest;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.DriverResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UpdateDriverRequest;
import inc.yowyob.rental_api_reactive.persistence.entity.Driver;
import inc.yowyob.rental_api_reactive.persistence.entity.User;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DriverMapper {

    /**
     * Méthode 1: Crée une entité Driver à partir d'un DTO de création.
     */
    @Mapping(target = "driverId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "statusUpdatedAt", ignore = true)
    @Mapping(target = "statusUpdatedBy", ignore = true)
    @Mapping(target = "assignedVehicleIds", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "insuranceProvider", ignore = true)
    @Mapping(target = "insurancePolicy", ignore = true)
    
    // Mappings explicites avec les nouveaux noms
    @Mapping(source = "createDto.licenseExpiryDate", target = "licenseExpiryDate")
    @Mapping(source = "createDto.experience", target = "experience")
    @Mapping(source = "createDto.status", target = "status")
    @Mapping(source = "createDto.employeeId", target = "employeeId")
    @Mapping(source = "createDto.position", target = "position")
    @Mapping(source = "createDto.department", target = "department")
    @Mapping(source = "createDto.hireDate", target = "hireDate")
    @Mapping(source = "createDto.hourlyRate", target = "hourlyRate")
    @Mapping(source = "createDto.workingHours", target = "workingHours")
    @Mapping(source = "createDto.idCardUrl", target = "idCardUrl")
    @Mapping(source = "createDto.driverLicenseUrl", target = "driverLicenseUrl")
    
    // Valeurs par défaut 
    @Mapping(target = "rating", expression = "java(createDto.getRating() != null ? createDto.getRating() : 0.0)")
    Driver fromCreateRequest(CreateDriverRequest createDto);

    /**
     * Méthode 2: Met à jour une entité Driver existante à partir d'un DTO de mise à jour.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "driverId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "agencyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "statusUpdatedAt", ignore = true) // Géré par le service
    @Mapping(target = "statusUpdatedBy", ignore = true) // Géré par le service
    
    // Mappings explicites pour les champs renommés
    @Mapping(source = "dto.licenseExpiryDate", target = "licenseExpiryDate")
    @Mapping(source = "dto.experience", target = "experience")
    @Mapping(source = "dto.assignedVehicleIds", target = "assignedVehicleIds")
    @Mapping(source = "dto.status", target = "status")
    @Mapping(source = "dto.hireDate", target = "hireDate")
    @Mapping(source = "dto.hourlyRate", target = "hourlyRate")
    @Mapping(source = "dto.workingHours", target = "workingHours")
    @Mapping(source = "dto.insuranceProvider", target = "insuranceProvider")
    @Mapping(source = "dto.insurancePolicy", target = "insurancePolicy")
    @Mapping(source = "dto.idCardUrl", target = "idCardUrl")
    @Mapping(source = "dto.driverLicenseUrl", target = "driverLicenseUrl")
    void updateFromRequest(UpdateDriverRequest dto, @MappingTarget Driver entity);

    /**
     * Méthode 3: Combine les entités Driver et User en un seul DTO de réponse.
     */
    
    // === MAPPINGS DEPUIS DRIVER ===
    @Mapping(source = "driver.driverId", target = "driverId")
    @Mapping(source = "driver.organizationId", target = "organizationId")
    @Mapping(source = "driver.agencyId", target = "agencyId")
    @Mapping(source = "driver.createdAt", target = "createdAt")
    @Mapping(source = "driver.updatedAt", target = "updatedAt")
    @Mapping(source = "driver.createdBy", target = "createdBy")
    @Mapping(source = "driver.updatedBy", target = "updatedBy")
    
    // Informations permis et expérience
    @Mapping(source = "driver.licenseNumber", target = "licenseNumber")
    @Mapping(source = "driver.licenseType", target = "licenseType")
    @Mapping(source = "driver.licenseExpiryDate", target = "licenseExpiryDate")
    @Mapping(source = "driver.experience", target = "experienceYears")
    
    // Localisation et documents
    @Mapping(source = "driver.location", target = "location")
    @Mapping(source = "driver.idCardUrl", target = "idCardUrl")
    @Mapping(source = "driver.driverLicenseUrl", target = "driverLicenseUrl")
    
    // Véhicules et évaluation
    @Mapping(source = "driver.assignedVehicleIds", target = "assignedVehicleIds")
    @Mapping(source = "driver.rating", target = "rating")
    
    // Assurance
    @Mapping(source = "driver.insuranceProvider", target = "insuranceProvider")
    @Mapping(source = "driver.insurancePolicy", target = "insurancePolicy")
    
    // Statut du chauffeur
    @Mapping(source = "driver.status", target = "status")
    @Mapping(source = "driver.statusUpdatedAt", target = "statusUpdatedAt")
    @Mapping(source = "driver.statusUpdatedBy", target = "statusUpdatedBy")
    
    // Informations employé
    @Mapping(source = "driver.employeeId", target = "employeeId")
    @Mapping(source = "driver.position", target = "position")
    @Mapping(source = "driver.department", target = "department")
    @Mapping(source = "driver.hireDate", target = "hireDate")
    @Mapping(source = "driver.hourlyRate", target = "hourlyRate")
    @Mapping(source = "driver.workingHours", target = "workingHours")
    @Mapping(source = "driver.dateOfBirth", target = "dateOfBirth") // Depuis User, pas Driver
   
    // === MAPPINGS DEPUIS USER ===
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.profilePicture", target = "profileImageUrl")
    @Mapping(source = "user.userType", target = "userType")
   
    // === EXPRESSIONS POUR CHAMPS CALCULÉS ===
    @Mapping(target = "fullName", expression = "java(user != null ? user.getFirstName() + \" \" + user.getLastName() : \"\")")
    
    // Champs dérivés liés au statut
    @Mapping(target = "isAvailable", expression = "java(driver.isAvailable())")
    @Mapping(target = "isOnDuty", expression = "java(driver.isOnDuty())")
    @Mapping(target = "isOffDuty", expression = "java(driver.isOffDuty())")
    @Mapping(target = "isOnLeave", expression = "java(driver.isOnLeave())")
    @Mapping(target = "canBeAssigned", expression = "java(driver.canBeAssigned())")
    @Mapping(target = "isWorkReady", expression = "java(driver.isWorkReady())")
    
    // Champs dérivés métier
    @Mapping(target = "isLicenseExpired", expression = "java(driver.isLicenseExpired())")
    @Mapping(target = "assignedVehicleCount", expression = "java(driver.getAssignedVehicleCount())")
    @Mapping(target = "hasAssignedVehicles", expression = "java(driver.hasAssignedVehicles())")
    
    // Calculs d'âge et ancienneté (si dateOfBirth disponible via User)
    // @Mapping(target = "age", expression = "java(user != null && user.getDateOfBirth() != null ? java.time.Period.between(user.getDateOfBirth(), java.time.LocalDate.now()).getYears() : null)")
    @Mapping(target = "yearsOfService", expression = "java(driver.getHireDate() != null ? java.time.Period.between(driver.getHireDate(), java.time.LocalDate.now()).getYears() : null)")
    
    DriverResponse toResponse(Driver driver, User user);
}