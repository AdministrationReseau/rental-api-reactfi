package inc.yowyob.rental_api_reactive.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(HealthReactiveController.class)
class HealthReactiveControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void health_ShouldReturnHealthStatus() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/health")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Application is healthy")
            .jsonPath("$.data").isNotEmpty()
            .jsonPath("$.data.status").isEqualTo("UP")
            .jsonPath("$.data.timestamp").isNotEmpty()
            .jsonPath("$.data.service").isEqualTo("Rental API Reactive")
            .jsonPath("$.data.version").isEqualTo("1.0.0")
            .jsonPath("$.data.environment").isEqualTo("development");
    }

    @Test
    void readiness_ShouldReturnReadinessStatus() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/health/readiness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Application is ready")
            .jsonPath("$.data").isNotEmpty()
            .jsonPath("$.data.status").isEqualTo("READY")
            .jsonPath("$.data.timestamp").isNotEmpty()
            .jsonPath("$.data.checks").isNotEmpty()
            .jsonPath("$.data.checks.database").isEqualTo("UP")
            .jsonPath("$.data.checks.memory").isEqualTo("OK")
            .jsonPath("$.data.checks.disk").isEqualTo("OK");
    }

    @Test
    void liveness_ShouldReturnLivenessStatus() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/health/liveness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.message").isEqualTo("Application is alive")
            .jsonPath("$.data").isNotEmpty()
            .jsonPath("$.data.status").isEqualTo("ALIVE")
            .jsonPath("$.data.timestamp").isNotEmpty()
            .jsonPath("$.data.uptime").isEqualTo("running");
    }

    @Test
    void health_ShouldReturnJsonContentType() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/health")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void health_ShouldRejectNonJsonAccept() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/health")
            .accept(MediaType.TEXT_PLAIN)
            .exchange()
            .expectStatus().is5xxServerError();
    }

    @Test
    void readiness_ShouldHaveCorrectStructure() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/health/readiness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").exists()
            .jsonPath("$.message").exists()
            .jsonPath("$.data").exists()
            .jsonPath("$.timestamp").exists()
            .jsonPath("$.status_code").exists();
    }

    @Test
    void liveness_ShouldHaveCorrectStructure() {
        // When & Then
        webTestClient.get()
            .uri("/api/v1/health/liveness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.success").exists()
            .jsonPath("$.message").exists()
            .jsonPath("$.data").exists()
            .jsonPath("$.timestamp").exists()
            .jsonPath("$.status_code").exists();
    }

    @Test
    void allHealthEndpoints_ShouldReturnStatusCode200() {
        // Health endpoint
        webTestClient.get()
            .uri("/api/v1/health")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status_code").isEqualTo(200);

        // Readiness endpoint
        webTestClient.get()
            .uri("/api/v1/health/readiness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status_code").isEqualTo(200);

        // Liveness endpoint
        webTestClient.get()
            .uri("/api/v1/health/liveness")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status_code").isEqualTo(200);
    }
}
