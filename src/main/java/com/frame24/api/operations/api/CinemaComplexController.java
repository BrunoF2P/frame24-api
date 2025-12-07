package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CinemaComplexResponse;
import com.frame24.api.operations.application.dto.CreateCinemaComplexRequest;
import com.frame24.api.operations.application.dto.UpdateCinemaComplexRequest;
import com.frame24.api.operations.application.service.CinemaComplexService;
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
 * Controller para gerenciamento de complexos de cinema.
 * <p>
 * Suporta versionamento via header:
 * - Header: api-version: v1.0
 */
@RestController
@RequestMapping("/cinema-complexes")
@RequiredArgsConstructor
@Tag(name = "Cinema Complexes", description = "Endpoints para gerenciamento de complexos de cinema")
public class CinemaComplexController {

    private final CinemaComplexService cinemaComplexService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar complexo de cinema", description = "Cria um novo complexo de cinema para a empresa")
    public ResponseEntity<ApiResponse<CinemaComplexResponse>> createCinemaComplex(
            @Valid @RequestBody CreateCinemaComplexRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        CinemaComplexResponse response = cinemaComplexService.createCinemaComplex(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Complexo de cinema criado com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar complexos de cinema", description = "Lista todos os complexos de cinema da empresa")
    public ResponseEntity<ApiResponse<List<CinemaComplexResponse>>> listCinemaComplexes(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<CinemaComplexResponse> response = cinemaComplexService.listCinemaComplexes(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Complexos listados com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar complexo por ID", description = "Retorna os detalhes de um complexo de cinema específico")
    public ResponseEntity<ApiResponse<CinemaComplexResponse>> getCinemaComplexById(
            @Parameter(description = "ID do complexo de cinema", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        CinemaComplexResponse response = cinemaComplexService.getCinemaComplexById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Complexo encontrado"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar complexo de cinema", description = "Atualiza os dados de um complexo de cinema")
    public ResponseEntity<ApiResponse<CinemaComplexResponse>> updateCinemaComplex(
            @Parameter(description = "ID do complexo de cinema", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateCinemaComplexRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        CinemaComplexResponse response = cinemaComplexService.updateCinemaComplex(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Complexo atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar complexo de cinema", description = "Deleta um complexo de cinema (não pode ter salas cadastradas)")
    public ResponseEntity<ApiResponse<Void>> deleteCinemaComplex(
            @Parameter(description = "ID do complexo de cinema", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        cinemaComplexService.deleteCinemaComplex(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Complexo deletado com sucesso"));
    }
}
