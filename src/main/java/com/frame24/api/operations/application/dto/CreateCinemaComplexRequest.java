package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request para criação de um complexo de cinema.
 */
@Schema(description = "Dados para criação de um complexo de cinema")
public record CreateCinemaComplexRequest(

        @Schema(description = "Nome do complexo de cinema", example = "Cinemark Shopping Center") @NotBlank(message = "Nome é obrigatório") @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres") String name,

        @Schema(description = "Código único do complexo (gerado automaticamente se não fornecido)", example = "CMRK-001") @Size(max = 50, message = "Código deve ter no máximo 50 caracteres") String code,

        @Schema(description = "CNPJ do complexo", example = "12.345.678/0001-99") @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX") String cnpj,

        @Schema(description = "Endereço completo", example = "Av. Paulista, 1000") String address,

        @Schema(description = "Cidade", example = "São Paulo") @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres") String city,

        @Schema(description = "Estado (UF)", example = "SP") @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres") @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ser uma sigla válida (ex: SP)") String state,

        @Schema(description = "CEP (com ou sem hífen)", example = "01310-100") @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX ou XXXXXXXX") String postalCode,

        @Schema(description = "Código IBGE do município", example = "3550308") @NotBlank(message = "Código IBGE do município é obrigatório") @Size(max = 7, message = "Código IBGE deve ter no máximo 7 caracteres") String ibgeMunicipalityCode,

        @Schema(description = "Registro na ANCINE", example = "REG-2024-001") @Size(max = 50, message = "Registro ANCINE deve ter no máximo 50 caracteres") String ancineRegistry,

        @Schema(description = "Data de abertura do complexo", example = "2024-01-15") LocalDate openingDate,

        @Schema(description = "Status ativo", example = "true") @NotNull(message = "Status ativo é obrigatório") Boolean active

) {
}
