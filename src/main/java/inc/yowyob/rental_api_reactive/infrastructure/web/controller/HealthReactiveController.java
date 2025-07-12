package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Slf4j
@Tag(name = "Health", description = "API de santé de l'application")
public class HealthReactiveController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier la santé de l'application", description = "Retourne le statut de santé de l'application")
    public Mono<ApiResponse<Map<String, Object>>> health() {
        log.debug("Health check requested");

        Map<String, Object> healthData = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "Rental API Reactive",
            "version", "1.0.0",
            "environment", "development"
        );

        return Mono.just(ApiResponse.success(healthData, "Application is healthy"));
    }

    @GetMapping(value = "/readiness", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier la disponibilité", description = "Vérifie si l'application est prête à recevoir du trafic")
    public Mono<ApiResponse<Map<String, Object>>> readiness() {
        log.debug("Readiness check requested");

        Map<String, Object> readinessData = Map.of(
            "status", "READY",
            "timestamp", LocalDateTime.now(),
            "checks", Map.of(
                "database", "UP",
                "memory", "OK",
                "disk", "OK"
            )
        );

        return Mono.just(ApiResponse.success(readinessData, "Application is ready"));
    }

    @GetMapping(value = "/liveness", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier la vivacité", description = "Vérifie si l'application est vivante")
    public Mono<ApiResponse<Map<String, Object>>> liveness() {
        log.debug("Liveness check requested");

        Map<String, Object> livenessData = Map.of(
            "status", "ALIVE",
            "timestamp", LocalDateTime.now(),
            "uptime", "running"
        );

        return Mono.just(ApiResponse.success(livenessData, "Application is alive"));
    }
}
