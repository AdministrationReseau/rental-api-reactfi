package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.application.service.UserReactiveService;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import inc.yowyob.rental_api_reactive.infrastructure.web.dto.UserResponse;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "API de gestion des utilisateurs")
public class UserReactiveController {

    private final UserReactiveService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir tous les utilisateurs", description = "Récupère la liste de tous les utilisateurs")
    public Mono<ApiResponse<Flux<UserResponse>>> getAllUsers() {
        log.info("GET /api/v1/users - Getting all users");
        Flux<UserResponse> users = userService.findAll();
        return Mono.just(ApiResponse.success(users, "Utilisateurs récupérés avec succès"));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un utilisateur par ID", description = "Récupère un utilisateur spécifique par son ID")
    public Mono<ApiResponse<UserResponse>> getUserById(
        @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        log.info("GET /api/v1/users/{} - Getting user by ID", id);
        return userService.findById(id)
            .map(user -> ApiResponse.success(user, "Utilisateur trouvé"))
            .defaultIfEmpty(ApiResponse.error("Utilisateur non trouvé", 404));
    }

    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un utilisateur par email", description = "Récupère un utilisateur par son adresse email")
    public Mono<ApiResponse<UserResponse>> getUserByEmail(
        @Parameter(description = "Email de l'utilisateur") @PathVariable String email) {
        log.info("GET /api/v1/users/email/{} - Getting user by email", email);
        return userService.findByEmail(email)
            .map(user -> ApiResponse.success(user, "Utilisateur trouvé"))
            .defaultIfEmpty(ApiResponse.error("Utilisateur non trouvé", 404));
    }

    @GetMapping(value = "/users/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les utilisateurs d'une organisation", description = "Récupère tous les utilisateurs d'une organisation")
    public Mono<ApiResponse<Flux<UserResponse>>> getUsersByOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID organizationId) {
        log.info("GET /api/v1/users/organization/{} - Getting users by organization", organizationId);
        Flux<UserResponse> users = userService.findByOrganizationId(organizationId);
        return Mono.just(ApiResponse.success(users, "Utilisateurs de l'organisation récupérés"));
    }

    @GetMapping(value = "/exists/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier l'existence d'un email", description = "Vérifie si un email est déjà utilisé")
    public Mono<ApiResponse<Boolean>> checkEmailExists(
        @Parameter(description = "Email à vérifier") @PathVariable String email) {
        log.info("GET /api/v1/users/exists/{} - Checking if email exists", email);
        return userService.existsByEmail(email)
            .map(exists -> ApiResponse.success(exists, exists ? "Email existe" : "Email disponible"));
    }
}
