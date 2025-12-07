package com.frame24.api.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Envelope padrão para respostas da API")
public class ApiResponse<T> {

    @Schema(description = "Indica se a operação foi bem sucedida", example = "true")
    private boolean success;

    @Schema(description = "Mensagem informativa ou de erro", example = "Operação realizada com sucesso")
    private String message;

    @Schema(description = "Dados da resposta (payload)")
    private T data;

    @Schema(description = "Data e hora da resposta", example = "2024-12-06T20:00:00")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
