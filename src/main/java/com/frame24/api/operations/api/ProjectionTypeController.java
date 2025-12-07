package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateProjectionTypeRequest;
import com.frame24.api.operations.application.dto.ProjectionTypeResponse;
import com.frame24.api.operations.application.dto.UpdateProjectionTypeRequest;
import com.frame24.api.operations.application.service.ProjectionTypeService;
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
 * Controller para gerenciamento de tipos de projeção.
 */
@RestController
@RequestMapping("/projection-types")
@RequiredArgsConstructor
@Tag(name = "Projection Types", description = "Endpoints para gerenciamento de tipos de projeção")
public class ProjectionTypeController {

    private final ProjectionTypeService projectionTypeService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar tipo de projeção", description = "Cria um novo tipo de projeção para a empresa")
    public ResponseEntity<ApiResponse<ProjectionTypeResponse>> create(
            @Valid @RequestBody CreateProjectionTypeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        ProjectionTypeResponse response = projectionTypeService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tipo de projeção criado com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar tipos de projeção", description = "Lista todos os tipos de projeção da empresa")
    public ResponseEntity<ApiResponse<List<ProjectionTypeResponse>>> list(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<ProjectionTypeResponse> response = projectionTypeService.list(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipos de projeção listados com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar tipo de projeção por ID", description = "Retorna os detalhes de um tipo de projeção específico")
    public ResponseEntity<ApiResponse<ProjectionTypeResponse>> getById(
            @Parameter(description = "ID do tipo de projeção", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        ProjectionTypeResponse response = projectionTypeService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipo de projeção encontrado"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar tipo de projeção", description = "Atualiza os dados de um tipo de projeção")
    public ResponseEntity<ApiResponse<ProjectionTypeResponse>> update(
            @Parameter(description = "ID do tipo de projeção", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateProjectionTypeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        ProjectionTypeResponse response = projectionTypeService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipo de projeção atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar tipo de projeção", description = "Deleta um tipo de projeção (não pode estar em uso)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID do tipo de projeção", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        projectionTypeService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Tipo de projeção deletado com sucesso"));
    }
}
