package com.frame24.api.common.security;

import com.frame24.api.common.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Handler customizado para erros 401 (Não Autenticado).
 * Retorna uma resposta JSON padronizada ao invés do erro padrão do Spring
 * Security.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);
    private final JsonMapper jsonMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String traceId = UUID.randomUUID().toString().substring(0, 8);
        log.warn("[{}] Não autenticado: {} - {}", traceId, request.getRequestURI(), authException.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("UNAUTHORIZED")
                        .title("Não Autenticado")
                        .detail("Você precisa estar autenticado para acessar este recurso")
                        .suggestion("Faça login e tente novamente")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(errorResponse));
    }
}
