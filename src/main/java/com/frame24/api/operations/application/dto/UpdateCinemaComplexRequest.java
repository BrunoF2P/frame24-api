package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request para atualização de um complexo de cinema.
 */
@Schema(description = "Dados para atualização de um complexo de cinema")
public record UpdateCinemaComplexRequest(

        @Schema(description = "Nome do complexo de cinema", example = "Cinemark Shopping Center") @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres") String name,

        @Schema(description = "CNPJ do complexo", example = "12.345.678/0001-99") @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX") String cnpj,

        @Schema(description = "Endereço completo", example = "Av. Paulista, 1000") String address,

        @Schema(description = "Cidade", example = "São Paulo") @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres") String city,

        @Schema(description = "Estado (UF)", example = "SP") @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres") @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ser uma sigla válida (ex: SP)") String state,

        @Schema(description = "CEP (com ou sem hífen)", example = "01310-100") @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX ou XXXXXXXX") String postalCode,

        @Schema(description = "Código IBGE do município", example = "3550308") @Size(max = 7, message = "Código IBGE deve ter no máximo 7 caracteres") String ibgeMunicipalityCode,

        @Schema(description = "Registro na ANCINE", example = "REG-2024-001") @Size(max = 50, message = "Registro ANCINE deve ter no máximo 50 caracteres") String ancineRegistry,

        @Schema(description = "Data de abertura do complexo", example = "2024-01-15") LocalDate openingDate,

        @Schema(description = "Status ativo", example = "true") Boolean active

) {
}
