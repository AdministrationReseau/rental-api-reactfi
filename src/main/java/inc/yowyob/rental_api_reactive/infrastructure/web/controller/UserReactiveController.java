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

        return Mono.fromCallable(() -> ApiResponse.<Flux<UserResponse>>builder()
                .success(true)
                .message("Utilisateurs récupérés avec succès")
                .data(users)
                .build())
            .onErrorReturn(ApiResponse.<Flux<UserResponse>>builder()
                .success(false)
                .message("Erreur lors de la récupération des utilisateurs")
                .data(null)
                .build());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un utilisateur par ID", description = "Récupère un utilisateur spécifique par son ID")
    public Mono<ApiResponse<UserResponse>> getUserById(
        @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        log.info("GET /api/v1/users/{} - Getting user by ID", id);

        return userService.findById(id)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Utilisateur trouvé")
                .data(user)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<UserResponse>builder()
                .success(false)
                .message("Utilisateur non trouvé")
                .data(null)
                .build()))
            .onErrorReturn(ApiResponse.<UserResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération de l'utilisateur")
                .data(null)
                .build());
    }

    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un utilisateur par email", description = "Récupère un utilisateur par son adresse email")
    public Mono<ApiResponse<UserResponse>> getUserByEmail(
        @Parameter(description = "Email de l'utilisateur") @PathVariable String email) {
        log.info("GET /api/v1/users/email/{} - Getting user by email", email);

        return userService.findByEmail(email)
            .map(user -> ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Utilisateur trouvé")
                .data(user)
                .build())
            .switchIfEmpty(Mono.just(ApiResponse.<UserResponse>builder()
                .success(false)
                .message("Utilisateur non trouvé")
                .data(null)
                .build()))
            .onErrorReturn(ApiResponse.<UserResponse>builder()
                .success(false)
                .message("Erreur lors de la récupération de l'utilisateur")
                .data(null)
                .build());
    }

    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les utilisateurs d'une organisation", description = "Récupère tous les utilisateurs d'une organisation")
    public Mono<ApiResponse<Flux<UserResponse>>> getUsersByOrganization(
        @Parameter(description = "ID de l'organisation") @PathVariable UUID organizationId) {
        log.info("GET /api/v1/users/organization/{} - Getting users by organization", organizationId);

        Flux<UserResponse> users = userService.findByOrganizationId(organizationId);

        return Mono.fromCallable(() -> ApiResponse.<Flux<UserResponse>>builder()
                .success(true)
                .message("Utilisateurs de l'organisation récupérés")
                .data(users)
                .build())
            .onErrorReturn(ApiResponse.<Flux<UserResponse>>builder()
                .success(false)
                .message("Erreur lors de la récupération des utilisateurs de l'organisation")
                .data(null)
                .build());
    }

    @GetMapping(value = "/exists/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier l'existence d'un email", description = "Vérifie si un email est déjà utilisé")
    public Mono<ApiResponse<Boolean>> checkEmailExists(
        @Parameter(description = "Email à vérifier") @PathVariable String email) {
        log.info("GET /api/v1/users/exists/{} - Checking if email exists", email);

        return userService.existsByEmail(email)
            .map(exists -> ApiResponse.<Boolean>builder()
                .success(true)
                .message(exists ? "Email existe" : "Email disponible")
                .data(exists)
                .build())
            .onErrorReturn(ApiResponse.<Boolean>builder()
                .success(false)
                .message("Erreur lors de la vérification de l'email")
                .data(null)
                .build());
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur par son ID")
    public Mono<ApiResponse<Void>> deleteUser(
        @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);

        return userService.deleteById(id)
            .then(Mono.just(ApiResponse.<Void>builder()
                .success(true)
                .message("Utilisateur supprimé avec succès")
                .data(null)
                .build()))
            .onErrorReturn(ApiResponse.<Void>builder()
                .success(false)
                .message("Erreur lors de la suppression de l'utilisateur")
                .data(null)
                .build());
    }
}
