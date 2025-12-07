package com.frame24.api.common.security;

import com.frame24.api.common.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Handler customizado para erros 403 (Acesso Negado).
 * Retorna uma resposta JSON padronizada ao invés do erro padrão do Spring
 * Security.
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final JsonMapper jsonMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        String traceId = UUID.randomUUID().toString().substring(0, 8);
        log.warn("[{}] Acesso negado: {} - {}", traceId, request.getRequestURI(),
                accessDeniedException.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .error(ApiErrorResponse.ErrorDetails.builder()
                        .code("FORBIDDEN")
                        .title("Acesso Negado")
                        .detail("Você não tem permissão para acessar este recurso")
                        .suggestion("Verifique suas permissões ou entre em contato com o administrador")
                        .build())
                .timestamp(Instant.now())
                .traceId(traceId)
                .build();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonMapper.writeValueAsString(errorResponse));
    }
}
