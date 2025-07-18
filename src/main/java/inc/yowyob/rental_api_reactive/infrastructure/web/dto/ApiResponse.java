package inc.yowyob.rental_api_reactive.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

/**
 * DTO de réponse API standard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("timestamp")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @JsonProperty("count")
    private Long count;

    @JsonProperty("total")
    private Long total;

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("size")
    private Integer size;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus notFound) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .build();
    }

    public static Object success(DriverResponse driverDto, String string, HttpStatus created) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'success'");
    }
}
