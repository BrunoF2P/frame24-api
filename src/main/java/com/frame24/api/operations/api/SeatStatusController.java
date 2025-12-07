package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSeatStatusRequest;
import com.frame24.api.operations.application.dto.SeatStatusResponse;
import com.frame24.api.operations.application.dto.UpdateSeatStatusRequest;
import com.frame24.api.operations.application.service.SeatStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de status de assento.
 */
@RestController
@RequestMapping("/seat-statuses")
@RequiredArgsConstructor
@Tag(name = "Seat Statuses", description = "Endpoints para gerenciamento de status de assento")
public class SeatStatusController {

    private final SeatStatusService seatStatusService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar status de assento", description = "Cria um novo status de assento para a empresa")
    public ResponseEntity<ApiResponse<SeatStatusResponse>> create(
            @Valid @RequestBody CreateSeatStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatStatusResponse response = seatStatusService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Status de assento criado com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar status de assento", description = "Lista todos os status de assento da empresa")
    public ResponseEntity<ApiResponse<List<SeatStatusResponse>>> list(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<SeatStatusResponse> response = seatStatusService.list(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Status de assento listados com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar status por ID", description = "Retorna os detalhes de um status de assento específico")
    public ResponseEntity<ApiResponse<SeatStatusResponse>> getById(
            @Parameter(description = "ID do status", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatStatusResponse response = seatStatusService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Status de assento encontrado"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar status de assento", description = "Atualiza os dados de um status de assento")
    public ResponseEntity<ApiResponse<SeatStatusResponse>> update(
            @Parameter(description = "ID do status", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateSeatStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatStatusResponse response = seatStatusService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Status de assento atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar status de assento", description = "Deleta um status de assento (não pode ser o padrão)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID do status", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        seatStatusService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Status de assento deletado com sucesso"));
    }
}
