package com.frame24.api.identity.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.identity.application.dto.CreateRoleRequest;
import com.frame24.api.identity.application.dto.RoleResponse;
import com.frame24.api.identity.application.dto.UpdateRoleRequest;
import com.frame24.api.identity.application.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de roles customizadas.
 * <p>
 * Suporta versionamento via header:
 * - Header: api-version: v1.0
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Endpoints para gerenciamento de roles customizadas")
public class RoleController {

    private final RoleService roleService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar role customizada", description = "Cria uma nova role customizada para a empresa")
    @CacheEvict(value = "roles", key = "#principal.companyId")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(
            @Valid @RequestBody CreateRoleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        RoleResponse response = roleService.createRole(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Role criada com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar todas as roles", description = "Lista todas as roles (sistema e customizadas) da empresa")
    @Cacheable(value = "roles", key = "#principal.companyId")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> listRoles(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<RoleResponse> response = roleService.listRoles(principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Roles listadas com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar role por ID", description = "Retorna os detalhes de uma role específica")
    @Cacheable(value = "role", key = "#id")
    public ResponseEntity<ApiResponse<RoleResponse>>

    getRoleById(
            @Parameter(description = "ID da role", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        RoleResponse response = roleService.getRoleById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Role encontrada"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar role", description = "Atualiza uma role customizada (não pode atualizar roles do sistema)")
    @CacheEvict(value = {"roles", "role"}, key = "#id", allEntries = true)
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @Parameter(description = "ID da role", example = "1234567890123456789") @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        RoleResponse response = roleService.updateRole(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Role atualizada com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar role", description = "Deleta uma role customizada (não pode deletar roles do sistema ou em uso)")
    @CacheEvict(value = {"roles", "role"}, key = "#id", allEntries = true)
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @Parameter(description = "ID da role", example = "1234567890123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        roleService.deleteRole(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deletada com sucesso"));
    }
}
