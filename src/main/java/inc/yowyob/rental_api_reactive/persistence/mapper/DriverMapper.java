// PATH: src/main/java/inc/yowyob/rental_api_reactive/persistence/mapper/DriverMapper.java

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
     * Les annotations ici ignorent les champs qui seront définis par le service.
     */
    @Mapping(target = "driverId", ignore = true)
    // @Mapping(target = "createdAt", ignore = true)
    // @Mapping(target = "updatedAt", ignore = true)
    // @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusUpdatedAt", ignore = true)
    @Mapping(target = "statusUpdatedBy", ignore = true)
    Driver fromCreateRequest(CreateDriverRequest dto);

    /**
     * Méthode 2: Met à jour une entité Driver existante à partir d'un DTO de mise à jour.
     * Les annotations ici empêchent d'écraser des champs avec des valeurs nulles.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "driverId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateFromRequest(UpdateDriverRequest dto, @MappingTarget Driver entity);

    /**
     * Méthode 3: Combine les entités Driver et User en un seul DTO de réponse.
     * C'EST ICI QUE TOUTES LES ANNOTATIONS DE MAPPING EXPLICITES DOIVENT SE TROUVER.
     */
    // Mappings pour lever les ambiguïtés (champs avec le même nom)
    @Mapping(source = "driver.createdAt", target = "createdAt")
    @Mapping(source = "driver.updatedAt", target = "updatedAt")
    @Mapping(source = "driver.organizationId", target = "organizationId")
    @Mapping(source = "driver.position", target = "position")
    
    // Mappings depuis l'entité User
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.profilePicture", target = "profileImageUrl")
    @Mapping(source = "user.userType", target = "userType")
    
    // Mappings depuis l'entité Driver (champs restants)
    @Mapping(source = "driver.driverId", target = "driverId")
    @Mapping(source = "driver.agencyId", target = "agencyId")
    @Mapping(source = "driver.department", target = "department")
    @Mapping(source = "driver.employeeId", target = "employeeId") // Vérifiez ce nom dans votre entité
    @Mapping(source = "driver.licenseExpiryDate", target = "licenseExpiryDate") // Vérifiez ce nom
    @Mapping(source = "driver.experience", target = "experience") // Vérifiez ce nom
    
    // Expression pour les champs dérivés
    @Mapping(target = "fullName", expression = "java(user != null ? user.getFirstName() + \" \" + user.getLastName() : \"\")")

    // Ignorer les champs calculés du DTO
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "yearsOfService", ignore = true)
    @Mapping(target = "isLicenseExpired", ignore = true)
    DriverResponse toResponse(Driver driver, User user);
}