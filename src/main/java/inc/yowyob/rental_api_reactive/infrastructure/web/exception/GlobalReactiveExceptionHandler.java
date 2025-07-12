package inc.yowyob.rental_api_reactive.infrastructure.web.exception;

import inc.yowyob.rental_api_reactive.infrastructure.web.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalReactiveExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleValidationErrors(
        WebExchangeBindException ex, ServerWebExchange exchange) {
        log.error("Validation error: {}", ex.getMessage());

        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        ApiResponse<Object> response = ApiResponse.error("Erreurs de validation: " + errors, 400);
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleConstraintViolation(
        ConstraintViolationException ex, ServerWebExchange exchange) {
        log.error("Constraint violation: {}", ex.getMessage());

        String errors = ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));

        ApiResponse<Object> response = ApiResponse.error("Violations de contraintes: " + errors, 400);
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleIllegalArgument(
        IllegalArgumentException ex, ServerWebExchange exchange) {
        log.error("Illegal argument: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), 400);
        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleRuntimeException(
        RuntimeException ex, ServerWebExchange exchange) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.error("Erreur interne du serveur", 500);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Object>>> handleGenericException(
        Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ApiResponse<Object> response = ApiResponse.error("Une erreur inattendue s'est produite", 500);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}
