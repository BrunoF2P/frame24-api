package com.frame24.api.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT que intercepta todas as requisições HTTP e valida o token.
 * <p>
 * Executa ANTES do controller e extrai o JWT do header Authorization.
 * Usa Redis para cache de sessões e blacklist de tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserSessionCache sessionCache;
    private final TokenBlacklist tokenBlacklist;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            if (tokenBlacklist.isBlacklisted(jwt)) {
                log.warn("Token na blacklist (logout realizado)");
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtService.validateToken(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtService.extractUserId(jwt);

            if (userId == null) {
                log.warn("Token sem userId");
                filterChain.doFilter(request, response);
                return;
            }

            UserPrincipal userPrincipal = sessionCache.get(userId);

            if (userPrincipal == null) {
                Claims claims = jwtService.extractClaims(jwt);
                String roleName = (String) claims.get("roleName");

                userPrincipal = UserPrincipal.builder()
                        .userId(userId)
                        .email(jwtService.extractEmail(jwt))
                        .companyId(jwtService.extractCompanyId(jwt))
                        .userType(jwtService.extractUserType(jwt))
                        .customerId(jwtService.extractCustomerId(jwt))
                        .roleId(claims.get("roleId", Long.class))
                        .roleName(roleName)
                        .authorities(
                                roleName != null ? List.of(new SimpleGrantedAuthority("ROLE_" + roleName)) : List.of())
                        .build();

                sessionCache.save(userId, userPrincipal);
                log.debug("UserPrincipal criado do token e salvo no cache");
            } else {
                log.debug("UserPrincipal recuperado do cache Redis");
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 7. Injeta no SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("JWT: Usuário autenticado - email: {}, companyId: {}, userType: {}",
                    userPrincipal.getEmail(), userPrincipal.getCompanyId(), userPrincipal.getUserType());

        } catch (Exception e) {
            log.error("Erro ao processar JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
