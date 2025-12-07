package com.frame24.api.identity.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request de login com email e senha.
 */
@Schema(description = "Dados para autenticação do usuário")
public record LoginRequest(

        @Schema(description = "Email do usuário", example = "maria@cineestrela.com") @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") String email,

        @Schema(description = "Senha do usuário", example = "SenhaSegura123") @NotBlank(message = "Senha é obrigatória") String password

) {
}
