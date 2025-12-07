package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateRoomRequest;
import com.frame24.api.operations.application.dto.RoomResponse;
import com.frame24.api.operations.application.dto.UpdateRoomRequest;
import com.frame24.api.operations.application.service.RoomService;
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
 * Controller para gerenciamento de salas de cinema.
 */
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Endpoints para gerenciamento de salas de cinema")
public class RoomController {

    private final RoomService roomService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar sala", description = "Cria uma nova sala em um complexo de cinema")
    public ResponseEntity<ApiResponse<RoomResponse>> create(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        RoomResponse response = roomService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Sala criada com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar todas as salas", description = "Lista todas as salas da empresa")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> listAll(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<RoomResponse> response = roomService.listAll(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Salas listadas com sucesso"));
    }

    @GetMapping(value = "/by-complex/{cinemaComplexId}", version = "v1.0+")
    @Operation(summary = "Listar salas por complexo", description = "Lista todas as salas de um complexo específico")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> listByComplex(
            @Parameter(description = "ID do complexo de cinema", example = "1234567890123456789") @PathVariable Long cinemaComplexId,
            @AuthenticationPrincipal UserPrincipal principal) {

        List<RoomResponse> response = roomService.listByComplex(cinemaComplexId, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Salas do complexo listadas com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar sala por ID", description = "Retorna os detalhes de uma sala específica")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(
            @Parameter(description = "ID da sala", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        RoomResponse response = roomService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Sala encontrada"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala")
    public ResponseEntity<ApiResponse<RoomResponse>> update(
            @Parameter(description = "ID da sala", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        RoomResponse response = roomService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Sala atualizada com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar sala", description = "Deleta uma sala (não pode ter sessões agendadas)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID da sala", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        roomService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Sala deletada com sucesso"));
    }
}
