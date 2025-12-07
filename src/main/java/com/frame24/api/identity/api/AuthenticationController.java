package com.frame24.api.identity.api;

import com.frame24.api.common.ratelimit.RateLimited;
import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.identity.application.dto.*;
import com.frame24.api.identity.application.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticação (login).
 * <p>
 * Suporta versionamento via header:
 * - Header: api-version: v1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticação")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @RateLimited(requests = 5, windowMinutes = 1)
    @PostMapping(value = "/login", version = "v1.0+")
    @Operation(summary = "Login de usuário", description = "Autentica um usuário e retorna tokens JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login realizado com sucesso"));
    }

    @PostMapping(value = "/logout", version = "v1.0+")
    @Operation(summary = "Logout", description = "Invalida o token atual")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer "
        authenticationService.logout(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Logout realizado com sucesso"));
    }

    @PostMapping(value = "/refresh", version = "v1.0+")
    @Operation(summary = "Refresh token", description = "Renova o access token usando refresh token")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authenticationService.refresh(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Token renovado com sucesso"));
    }

    @RateLimited(requests = 3, windowMinutes = 5)
    @PostMapping(value = "/forgot-password", version = "v1.0+")
    @Operation(summary = "Solicitar reset de senha", description = "Envia email com link para reset de senha")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Email de reset enviado"));
    }

    @RateLimited(requests = 3, windowMinutes = 5)
    @PostMapping(value = "/reset-password", version = "v1.0+")
    @Operation(summary = "Redefinir senha", description = "Redefine a senha usando token recebido por email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Senha redefinida com sucesso"));
    }
}
