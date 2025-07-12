package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.OrganizationReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.OrganizationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Organizations", description = "API de gestion des organisations")
public class OrganizationReactiveController {

    private final OrganizationReactiveService organizationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir toutes les organisations", description = "Récupère la liste de toutes les organisations")
    public Mono<ApiResponse<Flux<OrganizationResponse>>> getAllOrganizations() {
        log.info("GET /api/v1/organizations - Getting all organizations");
        Flux<OrganizationResponse> organizations = organizationService.findAll();
        return Mono.just(ApiResponse.success(organizations, "Organisations récupérées avec succès"));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir une organisation par ID", description = "Récupère une organisation spécifique par son ID")
    public Mono<ApiResponse<OrganizationResponse>> getOrganizationById(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID id) {
        log.info("GET /api/v1/organizations/{} - Getting organization by ID", id);
        return organizationService.findById(id)
            .map(org -> ApiResponse.success(org, "Organisation trouvée"))
            .defaultIfEmpty(ApiResponse.error("Organisation non trouvée", 404));
    }

    @GetMapping(value = "/owner/{ownerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir une organisation par propriétaire", description = "Récupère l'organisation d'un propriétaire")
    public Mono<ApiResponse<OrganizationResponse>> getOrganizationByOwner(
        @Parameter(description = "ID du propriétaire") @PathVariable UUID ownerId) {
        log.info("GET /api/v1/organizations/owner/{} - Getting organization by owner", ownerId);
        return organizationService.findByOwnerId(ownerId)
            .map(org -> ApiResponse.success(org, "Organisation du propriétaire trouvée"))
            .defaultIfEmpty(ApiResponse.error("Organisation non trouvée pour ce propriétaire", 404));
    }

    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les organisations actives", description = "Récupère toutes les organisations actives")
    public Mono<ApiResponse<Flux<OrganizationResponse>>> getActiveOrganizations() {
        log.info("GET /api/v1/organizations/active - Getting active organizations");
        Flux<OrganizationResponse> organizations = organizationService.findAllActive();
        return Mono.just(ApiResponse.success(organizations, "Organisations actives récupérées"));
    }

    @GetMapping(value = "/exists/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier l'existence d'un nom d'organisation", description = "Vérifie si un nom d'organisation est déjà utilisé")
    public Mono<ApiResponse<Boolean>> checkOrganizationNameExists(
        @Parameter(description = "Nom à vérifier") @PathVariable String name) {
        log.info("GET /api/v1/organizations/exists/{} - Checking if organization name exists", name);
        return organizationService.existsByName(name)
            .map(exists -> ApiResponse.success(exists, exists ? "Nom existe" : "Nom disponible"));
    }
}
