package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSessionStatusRequest;
import com.frame24.api.operations.application.dto.SessionStatusResponse;
import com.frame24.api.operations.application.dto.UpdateSessionStatusRequest;
import com.frame24.api.operations.application.service.SessionStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/session-statuses")
@RequiredArgsConstructor
@Tag(name = "Session Statuses", description = "Endpoints para gerenciamento de status de sessão")
public class SessionStatusController {

    private final SessionStatusService sessionStatusService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar status de sessão")
    public ResponseEntity<ApiResponse<SessionStatusResponse>> create(
            @Valid @RequestBody CreateSessionStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        SessionStatusResponse response = sessionStatusService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Criado com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar status de sessão")
    public ResponseEntity<ApiResponse<List<SessionStatusResponse>>> list(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SessionStatusResponse> response = sessionStatusService.list(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar status por ID")
    public ResponseEntity<ApiResponse<SessionStatusResponse>> getById(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        SessionStatusResponse response = sessionStatusService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Encontrado"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar status de sessão")
    public ResponseEntity<ApiResponse<SessionStatusResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateSessionStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        SessionStatusResponse response = sessionStatusService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar status de sessão")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        sessionStatusService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Deletado com sucesso"));
    }
}
