package inc.yowyob.rental_api_reactive.infrastructure.config;

import inc.yowyob.rental_api_reactive.application.dto.Permission;
import inc.yowyob.rental_api_reactive.application.dto.RoleType;
import inc.yowyob.rental_api_reactive.persistence.entity.Role;
import inc.yowyob.rental_api_reactive.persistence.repository.RoleReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class DefaultRoleConfiguration implements CommandLineRunner {

    private final RoleReactiveRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing default system roles...");

        createSystemRoles()
            .doOnSuccess(v -> log.info("System roles initialization completed"))
            .doOnError(error -> log.error("Failed to initialize system roles", error))
            .subscribe();
    }

    private Mono<Void> createSystemRoles() {
        return createSuperAdminRole()
            .then(createClientRole())
            .then();
    }

    private Mono<Void> createSuperAdminRole() {
        return roleRepository.findSystemRoles()
            .any(role -> RoleType.SUPER_ADMIN.equals(role.getRoleType()))
            .flatMap(exists -> {
                if (exists) {
                    log.info("Super Admin role already exists");
                    return Mono.empty();
                }

                Set<String> allPermissions = Arrays.stream(Permission.values())
                    .map(Permission::getCode)
                    .collect(Collectors.toSet());

                Role superAdminRole = new Role(
                    "Super Administrateur",
                    "Administrateur système avec tous les privilèges",
                    null // Pas d'organisation pour les rôles système
                );

                superAdminRole.setRoleType(RoleType.SUPER_ADMIN);
                superAdminRole.setIsSystemRole(true);
                superAdminRole.setPriority(100);
                superAdminRole.setPermissions(allPermissions);
                superAdminRole.setColor("#dc2626");
                superAdminRole.setIcon("crown");

                return roleRepository.save(superAdminRole);
            })
            .then();
    }

    private Mono<Void> createClientRole() {
        return roleRepository.findSystemRoles()
            .any(role -> RoleType.CLIENT.equals(role.getRoleType()))
            .flatMap(exists -> {
                if (exists) {
                    log.info("Client role already exists");
                    return Mono.empty();
                }

                Set<String> clientPermissions = Set.of(
                    Permission.VEHICLE_READ.getCode(),
                    Permission.RENTAL_READ.getCode(),
                    Permission.RENTAL_WRITE.getCode(),
                    Permission.USER_READ.getCode(),
                    Permission.USER_UPDATE.getCode()
                );

                Role clientRole = new Role(
                    "Client",
                    "Utilisateur client avec accès aux fonctionnalités de location",
                    null // Pas d'organisation pour les rôles système
                );

                clientRole.setRoleType(RoleType.CLIENT);
                clientRole.setIsSystemRole(true);
                clientRole.setIsDefaultRole(true);
                clientRole.setPriority(10);
                clientRole.setPermissions(clientPermissions);
                clientRole.setColor("#059669");
                clientRole.setIcon("user");

                return roleRepository.save(clientRole);
            })
            .then();
    }
}
