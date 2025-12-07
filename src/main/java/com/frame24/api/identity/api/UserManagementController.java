package com.frame24.api.identity.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.identity.application.dto.*;
import com.frame24.api.identity.application.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para gerenciamento de usuários.
 * Segue padrões enterprise com documentação completa e segurança.
 */
@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs para gerenciamento de usuários do sistema")
@SecurityRequirement(name = "bearerAuth")
public class UserManagementController {

    private final UserManagementService userManagementService;

    /**
     * Cria um novo usuário no sistema.
     */
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema com validação completa de permissões e RLS para allowed_complexes. "
            +
            "Requer permissão 'users:create'. O employee_id será gerado automaticamente.")
    @PostMapping(version = "v1.0+")
    @PreAuthorize("hasAuthority('users:create')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("API: Criando usuário - email={}", request.email());
        UserResponse response = userManagementService.createUser(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Usuário criado com sucesso"));
    }

    /**
     * Lista todos os usuários da empresa com paginação.
     */
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários da empresa com suporte a paginação e ordenação. "
            +
            "RLS garante que apenas usuários da mesma empresa sejam retornados.")
    @GetMapping(version = "v1.0+")
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<ApiResponse<UserListResponse>> listUsers(
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("API: Listando usuários - page={}, size={}", pageable.getPageNumber(),
                pageable.getPageSize());

        UserListResponse response = userManagementService.listUsers(pageable, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuários listados com sucesso"));
    }

    /**
     * Busca usuários com filtros avançados.
     */
    @Operation(summary = "Buscar usuários", description = "Busca usuários com filtros avançados incluindo employee_id, email, nome, departamento e status. "
            +
            "Suporta busca parcial em texto e paginação.")
    @GetMapping(value = "/search", version = "v1.0+")
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<ApiResponse<UserListResponse>> searchUsers(
            @ModelAttribute SearchUserRequest searchRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("API: Buscando usuários - employeeId={}, email={}, name={}",
                searchRequest.employeeId(), searchRequest.email(), searchRequest.fullName());

        UserListResponse response = userManagementService.searchUsers(searchRequest, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Busca realizada com sucesso"));
    }

    /**
     * Busca usuário por ID.
     */
    @Operation(summary = "Buscar usuário por ID", description = "Retorna detalhes completos de um usuário específico incluindo complexos permitidos. "
            +
            "RLS valida que o usuário pertence à mesma empresa.")
    @GetMapping(value = "/{id}", version = "v1.0+")
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "ID do usuário", required = true, example = "123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("API: Buscando usuário por ID - id={}", id);
        UserResponse response = userManagementService.getUserById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuário encontrado"));
    }

    /**
     * Busca usuário por employee ID.
     */
    @Operation(summary = "Buscar usuário por Employee ID", description = "Busca rápida de usuário usando o employee_id sequencial da empresa. "
            +
            "Útil para operações de RH e busca rápida.")
    @GetMapping(value = "/employee/{employeeId}", version = "v1.0+")
    @PreAuthorize("hasAuthority('users:read')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmployeeId(
            @Parameter(description = "Employee ID", required = true, example = "1001") @PathVariable Long employeeId,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("API: Buscando usuário por employeeId - employeeId={}", employeeId);
        UserResponse response = userManagementService.getUserByEmployeeId(employeeId, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuário encontrado"));
    }

    /**
     * Atualiza um usuário existente.
     */
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados de um usuário existente. Valida hierarquia de permissões para prevenir "
            +
            "escalação de privilégios. Todos os campos são opcionais - apenas os fornecidos serão atualizados.")
    @PutMapping(value = "/{id}", version = "v1.0+")
    @PreAuthorize("hasAuthority('users:update')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "ID do usuário", required = true, example = "123456789") @PathVariable Long id,

            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("API: Atualizando usuário - id={}", id);
        UserResponse response = userManagementService.updateUser(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuário atualizado com sucesso"));
    }

    /**
     * Desativa um usuário (soft delete).
     */
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário no sistema (soft delete). O usuário ficará marcado como inativo, "
            +
            "suas sessões serão revogadas, mas os dados permanecerão no banco para auditoria. " +
            "Valida hierarquia de permissões.")
    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @PreAuthorize("hasAuthority('users:delete')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @Parameter(description = "ID do usuário", required = true, example = "123456789") @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("API: Desativando usuário - id={}", id);
        userManagementService.deactivateUser(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Usuário desativado com sucesso"));
    }
}
