package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request para renovar access token usando refresh token.
 */
@Schema(description = "Token de renovação")
public record RefreshTokenRequest(

        @Schema(description = "Refresh token válido", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") @NotBlank(message = "Refresh token é obrigatório") String refreshToken

) {
}
