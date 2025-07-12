package inc.yowyob.rental_api_reactive.persistence.repository;

import inc.yowyob.rental_api_reactive.persistence.entity.Role;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RoleReactiveRepository extends ReactiveCassandraRepository<Role, UUID> {

    /**
     * Trouve les rôles par organisation
     */
    @Query("SELECT * FROM roles WHERE organization_id = ?0 ALLOW FILTERING")
    Flux<Role> findByOrganizationId(UUID organizationId);

    /**
     * Trouve un rôle par nom dans une organisation
     */
    @Query("SELECT * FROM roles WHERE organization_id = ?0 AND name = ?1 ALLOW FILTERING")
    Mono<Role> findByOrganizationIdAndName(UUID organizationId, String name);

    /**
     * Vérifie si un nom de rôle existe dans une organisation
     */
    @Query("SELECT COUNT(*) FROM roles WHERE organization_id = ?0 AND name = ?1 ALLOW FILTERING")
    Mono<Long> countByOrganizationIdAndName(UUID organizationId, String name);

    /**
     * Trouve les rôles par défaut d'une organisation
     */
    @Query("SELECT * FROM roles WHERE organization_id = ?0 AND is_default_role = true ALLOW FILTERING")
    Flux<Role> findDefaultRolesByOrganizationId(UUID organizationId);

    /**
     * Trouve les rôles système
     */
    @Query("SELECT * FROM roles WHERE is_system_role = true ALLOW FILTERING")
    Flux<Role> findSystemRoles();
}
