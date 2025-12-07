package com.frame24.api.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para geração e validação de tokens JWT.
 * <p>
 * Usa Spring Security + jjwt para criar tokens assinados com HS256.
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * Gera um access token JWT para o usuário autenticado.
     *
     * @param userPrincipal Dados do usuário autenticado
     * @return JWT access token
     */
    public String generateAccessToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getUserId());
        claims.put("companyId", userPrincipal.getCompanyId());
        claims.put("userType", userPrincipal.getUserType());
        claims.put("customerId", userPrincipal.getCustomerId());
        claims.put("roleId", userPrincipal.getRoleId());
        claims.put("roleName", userPrincipal.getRoleName());
        claims.put("tokenType", "access");

        return generateToken(claims, userPrincipal.getEmail(), accessTokenExpiration);
    }

    /**
     * Gera um refresh token JWT.
     *
     * @param userPrincipal Dados do usuário autenticado
     * @return JWT refresh token
     */
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getUserId());
        claims.put("roleId", userPrincipal.getRoleId());
        claims.put("roleName", userPrincipal.getRoleName());
        claims.put("tokenType", "refresh");

        return generateToken(claims, userPrincipal.getEmail(), refreshTokenExpiration);
    }

    /**
     * Gera um token JWT com claims customizados.
     */
    private String generateToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrai o email (subject) do token.
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extrai todos os claims do token.
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida se o token é válido.
     *
     * @param token Token JWT
     * @return true se válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string vazio: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Verifica se o token está expirado.
     */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /**
     * Cria a chave de assinatura a partir do secret configurado.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrai o userId do token.
     */
    public Long extractUserId(String token) {
        Object userId = extractClaims(token).get("userId");
        return userId != null ? ((Number) userId).longValue() : null;
    }

    /**
     * Extrai o companyId do token.
     */
    public Long extractCompanyId(String token) {
        Object companyId = extractClaims(token).get("companyId");
        return companyId != null ? ((Number) companyId).longValue() : null;
    }

    /**
     * Extrai o userType do token.
     */
    public String extractUserType(String token) {
        return (String) extractClaims(token).get("userType");
    }

    /**
     * Extrai o customerId do token.
     */
    public Long extractCustomerId(String token) {
        Object customerId = extractClaims(token).get("customerId");
        return customerId != null ? ((Number) customerId).longValue() : null;
    }
}
