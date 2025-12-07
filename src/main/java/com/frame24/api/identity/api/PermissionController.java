package com.frame24.api.identity.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.identity.application.dto.PermissionResponse;
import com.frame24.api.identity.domain.Permission;
import com.frame24.api.identity.infrastructure.repository.PermissionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para listagem de permissões disponíveis.
 * <p>
 * Suporta versionamento via header:
 * - Header: api-version: v1.0
 */
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Endpoints para consulta de permissões do sistema")
public class PermissionController {

    private final PermissionRepository permissionRepository;

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar permissões disponíveis", description = "Lista todas as permissões disponíveis para configuração em roles")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> listPermissions(
            @AuthenticationPrincipal UserPrincipal principal) {

        Long companyId = principal.getCompanyId();

        // Busca permissões ativas da empresa
        List<PermissionResponse> permissions = permissionRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(permissions, "Permissões listadas com sucesso"));
    }

    private PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getResource(),
                permission.getAction(),
                permission.getCode(),
                permission.getName(),
                permission.getDescription(),
                permission.getModule());
    }
}
