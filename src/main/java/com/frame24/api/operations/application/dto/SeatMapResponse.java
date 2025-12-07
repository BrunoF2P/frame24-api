package com.frame24.api.operations.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Mapa de assentos da sessão (Merge de layout físico e status da sessão)")
public record SeatMapResponse(
        @Schema(description = "ID do assento") Long seatId,

        @Schema(description = "Código do assento (ex: A1)") String seatCode,

        @Schema(description = "Código da fileira") String rowCode,

        @Schema(description = "Número da coluna") Integer columnNumber,

        @Schema(description = "Tipo de assento") String seatType,

        @Schema(description = "Coordenada X") Integer x,

        @Schema(description = "Coordenada Y") Integer y,

        @Schema(description = "Status do assento na sessão (AVAILABLE, SOLD, BLOCKED, MAINTENANCE)") String status,

        @Schema(description = "Indica se é acessível") Boolean accessible) {
}
