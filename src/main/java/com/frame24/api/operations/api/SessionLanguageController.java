package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateSessionLanguageRequest;
import com.frame24.api.operations.application.dto.SessionLanguageResponse;
import com.frame24.api.operations.application.dto.UpdateSessionLanguageRequest;
import com.frame24.api.operations.application.service.SessionLanguageService;
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
@RequestMapping("/session-languages")
@RequiredArgsConstructor
@Tag(name = "Session Languages", description = "Endpoints para gerenciamento de idiomas de sessão")
public class SessionLanguageController {

    private final SessionLanguageService sessionLanguageService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar idioma de sessão")
    public ResponseEntity<ApiResponse<SessionLanguageResponse>> create(
            @Valid @RequestBody CreateSessionLanguageRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        SessionLanguageResponse response = sessionLanguageService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Criado com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar idiomas de sessão")
    public ResponseEntity<ApiResponse<List<SessionLanguageResponse>>> list(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<SessionLanguageResponse> response = sessionLanguageService.list(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Listado com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar idioma por ID")
    public ResponseEntity<ApiResponse<SessionLanguageResponse>> getById(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        SessionLanguageResponse response = sessionLanguageService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Encontrado"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar idioma de sessão")
    public ResponseEntity<ApiResponse<SessionLanguageResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateSessionLanguageRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        SessionLanguageResponse response = sessionLanguageService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar idioma de sessão")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        sessionLanguageService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Deletado com sucesso"));
    }
}
