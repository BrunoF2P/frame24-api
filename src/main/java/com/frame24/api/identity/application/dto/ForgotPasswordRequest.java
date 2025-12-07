package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request para solicitar reset de senha.
 */
@Schema(description = "Solicitação de reset de senha")
public record ForgotPasswordRequest(

        @Schema(description = "Email do usuário", example = "maria@cineestrela.com") @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email

) {
}
