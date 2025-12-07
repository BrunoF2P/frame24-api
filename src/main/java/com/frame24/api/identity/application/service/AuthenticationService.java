package com.frame24.api.identity.application.service;

import com.frame24.api.common.email.EmailService;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.common.security.JwtService;
import com.frame24.api.common.security.TokenBlacklist;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.common.security.UserSessionCache;
import com.frame24.api.identity.application.dto.ForgotPasswordRequest;
import com.frame24.api.identity.application.dto.LoginRequest;
import com.frame24.api.identity.application.dto.LoginResponse;
import com.frame24.api.identity.application.dto.ResetPasswordRequest;
import com.frame24.api.identity.domain.CompanyUser;
import com.frame24.api.identity.domain.Identity;
import com.frame24.api.identity.infrastructure.repository.CompanyUserRepository;
import com.frame24.api.identity.infrastructure.repository.IdentityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Serviço de autenticação completo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final IdentityRepository identityRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklist tokenBlacklist;
    private final UserSessionCache sessionCache;
    private final EmailService emailService;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    /**
     * Realiza login do usuário e retorna tokens JWT.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Tentativa de login: {}", request.email());

        Identity identity = identityRepository.findByEmail(request.email())
                .orElseThrow(() -> new ValidationException("email", "Credenciais inválidas"));

        if (!identity.getActive()) {
            throw new ValidationException("email", "Conta inativa");
        }

        if (!passwordEncoder.matches(request.password(), identity.getPasswordHash())) {
            log.warn("Senha incorreta para email: {}", request.email());
            throw new ValidationException("password", "Credenciais inválidas");
        }

        CompanyUser companyUser = companyUserRepository.findByIdentityId(identity.getId())
                .stream()
                .filter(CompanyUser::getActive)
                .findFirst()
                .orElseThrow(() -> new ValidationException("email", "Usuário sem empresa associada"));

        UserPrincipal userPrincipal = createUserPrincipal(identity, companyUser);

        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        // Salva sessão no cache Redis
        sessionCache.save(identity.getId(), userPrincipal);

        log.info("Login bem-sucedido: {} - Company: {}", request.email(), companyUser.getCompany().getId());

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                accessTokenExpiration / 1000, // ms para segundos
                new LoginResponse.UserInfo(
                        identity.getId(),
                        identity.getPerson().getFullName(),
                        identity.getEmail(),
                        companyUser.getCompany().getId(),
                        companyUser.getCompany().getCorporateName(),
                        companyUser.getCompany().getTenantSlug(),
                        companyUser.getRole().getName(),
                        identity.getIdentityType().name()));
    }

    /**
     * Realiza logout do usuário.
     */
    public void logout(String token) {
        Long userId = jwtService.extractUserId(token);
        Date expiration = jwtService.extractClaims(token).getExpiration();
        long ttlMillis = expiration.getTime() - System.currentTimeMillis();
        Duration ttl = Duration.ofMillis(Math.max(ttlMillis, 0));

        tokenBlacklist.add(token, ttl);
        if (userId != null) {
            sessionCache.delete(userId);
        }

        log.info("Logout realizado: userId={}", userId);
    }

    /**
     * Renova access token usando refresh token.
     */
    @Transactional(readOnly = true)
    public LoginResponse refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new ValidationException("token", "Refresh token inválido");
        }

        if (tokenBlacklist.isBlacklisted(refreshToken)) {
            throw new ValidationException("token", "Refresh token revogado");
        }

        Long userId = jwtService.extractUserId(refreshToken);
        if (userId == null) {
            throw new ValidationException("token", "Token inválido");
        }

        Identity identity = identityRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("token", "Usuário não encontrado"));

        CompanyUser companyUser = companyUserRepository.findByIdentityId(identity.getId())
                .stream()
                .filter(CompanyUser::getActive)
                .findFirst()
                .orElseThrow(() -> new ValidationException("token", "Usuário sem empresa associada"));

        UserPrincipal userPrincipal = createUserPrincipal(identity, companyUser);

        String newAccessToken = jwtService.generateAccessToken(userPrincipal);
        String newRefreshToken = jwtService.generateRefreshToken(userPrincipal);

        sessionCache.save(userId, userPrincipal);

        log.info("Refresh token realizado: userId={}", userId);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                accessTokenExpiration / 1000,
                new LoginResponse.UserInfo(
                        identity.getId(),
                        identity.getPerson().getFullName(),
                        identity.getEmail(),
                        companyUser.getCompany().getId(),
                        companyUser.getCompany().getCorporateName(),
                        companyUser.getCompany().getTenantSlug(),
                        companyUser.getRole().getName(),
                        identity.getIdentityType().name()));
    }

    /**
     * Solicita reset de senha (envia email).
     */
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Identity identity = identityRepository.findByEmail(request.email())
                .orElse(null);

        // Por segurança, sempre retorna sucesso (não revela se email existe)
        if (identity == null) {
            log.warn("Tentativa de reset para email inexistente: {}", request.email());
            return;
        }

        // Gera token de reset
        String resetToken = UUID.randomUUID().toString();
        identity.setResetToken(resetToken);
        identity.setResetTokenExpiresAt(Instant.now().plusSeconds(3600)); // 1 hora
        identityRepository.save(identity);

        // Envia email
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
        String emailHtml = emailService.buildPasswordResetEmail(
                identity.getPerson().getFullName(),
                resetLink);
        emailService.sendHtmlEmail(
                identity.getEmail(),
                "Redefinição de Senha - Frame 24 ERP",
                emailHtml);

        log.info("Email de reset enviado para: {}", request.email());
    }

    /**
     * Redefine senha usando token.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Identity identity = identityRepository.findByResetToken(request.token())
                .orElseThrow(() -> new ValidationException("token", "Token inválido"));

        if (identity.getResetTokenExpiresAt().isBefore(Instant.now())) {
            throw new ValidationException("token", "Token expirado");
        }

        // Atualiza senha
        identity.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        identity.setResetToken(null);
        identity.setResetTokenExpiresAt(null);
        identity.setPasswordChangedAt(Instant.now());
        identityRepository.save(identity);

        log.info("Senha redefinida com sucesso: userId={}", identity.getId());
    }

    /**
     * Helper para criar UserPrincipal com todas as permissões (Role macro +
     * Permissões granulares).
     */
    private UserPrincipal createUserPrincipal(Identity identity, CompanyUser companyUser) {
        java.util.Set<org.springframework.security.core.GrantedAuthority> authorities = new java.util.HashSet<>();

        // 1. Adiciona a Role principal (ex: ROLE_ADMINISTRADOR)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + companyUser.getRole().getName()));

        // 2. Adiciona permissões granulares (ex: users:read, sales:create)
        if (companyUser.getRole().getRolePermissions() != null) {
            companyUser.getRole().getRolePermissions().stream()
                    .filter(rp -> rp.getPermission() != null
                            && Boolean.TRUE.equals(rp.getPermission().getActive()))
                    .forEach(rp -> authorities
                            .add(new SimpleGrantedAuthority(rp.getPermission().getCode())));
        }

        return UserPrincipal.builder()
                .userId(identity.getId())
                .email(identity.getEmail())
                .companyId(companyUser.getCompany().getId())
                .userType(identity.getIdentityType().name())
                .roleId(companyUser.getRole().getId())
                .roleName(companyUser.getRole().getName())
                .authorities(new java.util.ArrayList<>(authorities))
                .build();
    }
}
