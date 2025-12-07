package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.BatchCreateSeatsRequest;
import com.frame24.api.operations.application.dto.CreateSeatRequest;
import com.frame24.api.operations.application.dto.SeatResponse;
import com.frame24.api.operations.application.dto.UpdateSeatRequest;
import com.frame24.api.operations.application.service.SeatService;
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
 * Controller para gerenciamento de assentos.
 */
@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
@Tag(name = "Seats", description = "Endpoints para gerenciamento de assentos de salas")
public class SeatController {

    private final SeatService seatService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar assento individual", description = "Cria um único assento em uma sala")
    public ResponseEntity<ApiResponse<SeatResponse>> create(
            @Valid @RequestBody CreateSeatRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatResponse response = seatService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Assento criado com sucesso"));
    }

    @PostMapping(value = "/batch", version = "v1.0+")
    @Operation(summary = "Criar assentos em lote", description = "Gera uma matriz de assentos para a sala")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> createBatch(
            @Valid @RequestBody BatchCreateSeatsRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        List<SeatResponse> response = seatService.createBatch(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Assentos criados com sucesso"));
    }

    @GetMapping(value = "/room/{roomId}", version = "v1.0+")
    @Operation(summary = "Listar assentos da sala", description = "Lista todos os assentos de uma sala específica")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> listByRoom(
            @Parameter(description = "ID da sala", example = "1234567890123456789") @PathVariable Long roomId,
            @AuthenticationPrincipal UserPrincipal principal) {

        List<SeatResponse> response = seatService.listByRoom(roomId, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Assentos listados com sucesso"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar assento", description = "Atualiza propriedades de um assento")
    public ResponseEntity<ApiResponse<SeatResponse>> update(
            @Parameter(description = "ID do assento", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateSeatRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatResponse response = seatService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Assento atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar assento", description = "Remove um assento da sala")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID do assento", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        seatService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Assento deletado com sucesso"));
    }

    @PatchMapping("/batch/status")
    @Operation(summary = "Atualizar status de assentos em lote (Manutenção)")
    public ResponseEntity<ApiResponse<Void>> updateBatchStatus(
            @Valid @RequestBody com.frame24.api.operations.application.dto.BatchUpdateSeatStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        seatService.updateBatchStatus(request, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Status dos assentos atualizado com sucesso"));
    }
}
