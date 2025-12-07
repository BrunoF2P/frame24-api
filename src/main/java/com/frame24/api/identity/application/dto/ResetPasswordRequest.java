package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request para reset de senha.
 */
@Schema(description = "Dados para redefinir senha")
public record ResetPasswordRequest(

        @Schema(description = "Token de reset recebido por email", example = "abc123-def456") @NotBlank(message = "Token é obrigatório") String token,

        @Schema(description = "Nova senha", example = "NovaSenha123") @NotBlank(message = "Nova senha é obrigatória") String newPassword

) {
}
