package com.frame24.api.identity.application.dto;

import com.frame24.api.identity.domain.enums.CompanyPlanType;
import com.frame24.api.identity.domain.enums.TaxRegimeType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response do registro de empresa.
 */
@Schema(description = "Dados da empresa registrada")
public record CompanyRegistrationResponse(

        @Schema(description = "ID da empresa", example = "1234567890123456789") Long companyId,

        @Schema(description = "Razão social", example = "Cinema Estrela LTDA") String corporateName,

        @Schema(description = "Nome fantasia", example = "CineEstrela") String tradeName,

        @Schema(description = "Slug único do tenant", example = "cineestrela") String tenantSlug,

        @Schema(description = "Regime tributário detectado", example = "SIMPLES_NACIONAL") TaxRegimeType taxRegime,

        @Schema(description = "Tipo do plano", example = "BASIC") CompanyPlanType planType,

        @Schema(description = "ID da identidade do administrador", example = "9876543210987654321") Long adminIdentityId,

        @Schema(description = "Email do administrador", example = "maria@cineestrela.com") String adminEmail

) {
}
