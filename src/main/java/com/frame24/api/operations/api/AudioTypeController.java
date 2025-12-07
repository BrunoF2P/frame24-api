package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.AudioTypeResponse;
import com.frame24.api.operations.application.dto.CreateAudioTypeRequest;
import com.frame24.api.operations.application.dto.UpdateAudioTypeRequest;
import com.frame24.api.operations.application.service.AudioTypeService;
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
 * Controller para gerenciamento de tipos de áudio.
 */
@RestController
@RequestMapping("/audio-types")
@RequiredArgsConstructor
@Tag(name = "Audio Types", description = "Endpoints para gerenciamento de tipos de áudio")
public class AudioTypeController {

    private final AudioTypeService audioTypeService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar tipo de áudio", description = "Cria um novo tipo de áudio para a empresa")
    public ResponseEntity<ApiResponse<AudioTypeResponse>> create(
            @Valid @RequestBody CreateAudioTypeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        AudioTypeResponse response = audioTypeService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tipo de áudio criado com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar tipos de áudio", description = "Lista todos os tipos de áudio da empresa")
    public ResponseEntity<ApiResponse<List<AudioTypeResponse>>> list(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<AudioTypeResponse> response = audioTypeService.list(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipos de áudio listados com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar tipo de áudio por ID", description = "Retorna os detalhes de um tipo de áudio específico")
    public ResponseEntity<ApiResponse<AudioTypeResponse>> getById(
            @Parameter(description = "ID do tipo de áudio", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        AudioTypeResponse response = audioTypeService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipo de áudio encontrado"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar tipo de áudio", description = "Atualiza os dados de um tipo de áudio")
    public ResponseEntity<ApiResponse<AudioTypeResponse>> update(
            @Parameter(description = "ID do tipo de áudio", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateAudioTypeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        AudioTypeResponse response = audioTypeService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Tipo de áudio atualizado com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar tipo de áudio", description = "Deleta um tipo de áudio (não pode estar em uso)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "ID do tipo de áudio", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        audioTypeService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Tipo de áudio deletado com sucesso"));
    }
}
