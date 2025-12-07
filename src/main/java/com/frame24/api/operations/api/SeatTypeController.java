package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSeatTypeRequest;
import com.frame24.api.operations.application.dto.SeatTypeResponse;
import com.frame24.api.operations.application.dto.UpdateSeatTypeRequest;
import com.frame24.api.operations.application.service.SeatTypeService;
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
 * Controller para gerenciamento de tipos de assento.
 */
@RestController
@RequestMapping("/seat-types")
@RequiredArgsConstructor
@Tag(name = "Seat Types", description = "Endpoints para gerenciamento de tipos de assento")
public class SeatTypeController {

    private final SeatTypeService seatTypeService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar tipo de assento", description = "Cria um novo tipo de assento para a empresa")
    public ResponseEntity<ApiResponse<SeatTypeResponse>> create(
            @Valid @RequestBody CreateSeatTypeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatTypeResponse response = seatTypeService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tipo de assento criado com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar tipos de assento", description = "Lista todos os tipos de assento da empresa")
    public ResponseEntity<ApiResponse<List<SeatTypeResponse>>> list(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<SeatTypeResponse> response = seatTypeService.list(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipos de assento listados com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar tipo de assento por ID", description = "Retorna os detalhes de um tipo de assento específico")
    public ResponseEntity<ApiResponse<SeatTypeResponse>> getById(
            @Parameter(description = "ID do tipo de assento", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatTypeResponse response = seatTypeService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipo de assento encontrado"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar tipo de assento", description = "Atualiza os dados de um tipo de assento")
    public ResponseEntity<ApiResponse<SeatTypeResponse>> update(
            @Parameter(description = "ID do tipo de assento", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateSeatTypeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SeatTypeResponse response = seatTypeService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipo de assento atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar tipo de assento", description = "Deleta um tipo de assento (não pode estar em uso)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID do tipo de assento", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        seatTypeService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Tipo de assento deletado com sucesso"));
    }
}
