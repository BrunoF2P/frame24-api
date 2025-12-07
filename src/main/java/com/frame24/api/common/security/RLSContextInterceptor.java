package com.frame24.api.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para configurar variáveis de sessão do PostgreSQL para Row Level
 * Security (RLS).
 *
 * <p>
 * Este interceptor é executado <b>antes</b> de cada requisição HTTP chegar ao
 * controller.
 * Ele extrai o contexto do usuário autenticado ({@link UserPrincipal}) e
 * configura
 * as variáveis de sessão do PostgreSQL que as políticas RLS usam para filtrar
 * dados.
 * </p>
 *
 * <h3>Variáveis de Sessão Configuradas:</h3>
 * <ul>
 * <li><b>app.user_type</b>: Tipo de usuário (CUSTOMER, EMPLOYEE, SYSTEM)</li>
 * <li><b>app.current_company_id</b>: ID da empresa do usuário</li>
 * <li><b>app.current_customer_id</b>: ID do cliente (apenas para CUSTOMER)</li>
 * <li><b>app.current_user_id</b>: ID do usuário (para auditoria)</li>
 * </ul>
 *
 * <h3>Fluxo de Execução:</h3>
 *
 * <pre>
 * 1. Requisição HTTP chega
 * 2. preHandle() é chamado
 * 3. Extrai Authentication do SecurityContext
 * 4. Converte para UserPrincipal
 * 5. Executa SET LOCAL no PostgreSQL
 * 6. Retorna true (continua requisição)
 * 7. Controller executa
 * 8. Repository faz query
 * 9. PostgreSQL aplica RLS automaticamente
 * </pre>
 *
 * <h3>Exemplo de SQL Executado:</h3>
 *
 * <pre>
 * SET LOCAL app.user_type = 'EMPLOYEE'
 * SET LOCAL app.current_company_id = 'company-123'
 * SET LOCAL app.current_user_id = 'user-456'
 * </pre>
 *
 * <p>
 * <b>Nota:</b> As variáveis são configuradas com SET LOCAL, o que significa que
 * elas só existem durante a transação atual e são automaticamente limpas ao
 * final.
 * </p>
 *
 * @see UserPrincipal
 * @see com.frame24.api.common.config.WebMvcConfig
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RLSContextInterceptor implements HandlerInterceptor {

    // Injetado via @RequiredArgsConstructor do Lombok
    private final JdbcTemplate jdbcTemplate;

    /**
     * Método executado ANTES de cada requisição HTTP.
     *
     * <p>
     * Extrai o contexto do usuário autenticado e configura as variáveis
     * de sessão do PostgreSQL para que o RLS funcione corretamente.
     * </p>
     *
     * @param request  Requisição HTTP
     * @param response Resposta HTTP
     * @param handler  Handler que será executado
     * @return true para continuar a execução da requisição
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // Pega o usuário autenticado do Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
                auth.getPrincipal() instanceof UserPrincipal user) {

            try {
                // Configura o tipo de usuário (CUSTOMER, EMPLOYEE, SYSTEM)
                setSessionVariable("app.user_type", user.getUserType());
                log.debug("RLS: Tipo de usuário configurado como {}", user.getUserType());

                // Para funcionários: configura company_id
                if ("EMPLOYEE".equals(user.getUserType()) && user.getCompanyId() != null) {
                    setSessionVariable("app.current_company_id", user.getCompanyId());
                    log.debug("RLS: Company ID configurado como {}", user.getCompanyId());

                    // Configura allowed_complexes se disponível
                    if (user.getAllowedComplexes() != null && !user.getAllowedComplexes().isEmpty()) {
                        String complexesStr = user.getAllowedComplexes().stream()
                                .map(String::valueOf)
                                .collect(java.util.stream.Collectors.joining(","));
                        setSessionVariable("app.allowed_complexes", complexesStr);
                        log.debug("RLS: Allowed complexes configurado como {}", complexesStr);
                    }
                }

                // Para clientes: configura customer_id e company_id
                if ("CUSTOMER".equals(user.getUserType()) && user.getCustomerId() != null) {
                    setSessionVariable("app.current_customer_id", user.getCustomerId());
                    log.debug("RLS: Customer ID configurado como {}", user.getCustomerId());

                    // Clientes também precisam de company_id para algumas queries
                    if (user.getCompanyId() != null) {
                        setSessionVariable("app.current_company_id", user.getCompanyId());
                    }
                }

                // Configura user_id para fins de auditoria
                if (user.getUserId() != null) {
                    setSessionVariable("app.current_user_id", user.getUserId());
                }

            } catch (Exception e) {
                // Loga erro mas não bloqueia a requisição
                log.error("Erro ao configurar contexto RLS: {}", e.getMessage(), e);
            }
        } else {
            // Para requisições não autenticadas, configura tipo SYSTEM
            try {
                setSessionVariable("app.user_type", "SYSTEM");
                log.debug("RLS: Usuário não autenticado, configurado como SYSTEM");
            } catch (Exception e) {
                log.error("Erro ao configurar contexto RLS padrão: {}", e.getMessage(), e);
            }
        }

        return true; // Continua a execução da requisição
    }

    /**
     * Configura uma variável de sessão do PostgreSQL usando SET LOCAL.
     *
     * <p>
     * SET LOCAL garante que a variável só existe durante a transação atual
     * e é automaticamente limpa ao final da transação.
     * </p>
     *
     * @param key   Nome da variável (ex: app.current_company_id)
     * @param value Valor da variável (Long)
     */
    private void setSessionVariable(String key, Long value) {
        if (value != null) {
            String sql = String.format("SET LOCAL %s = '%s'", key, value);
            jdbcTemplate.execute(sql);
            log.trace("RLS: Executado SQL: {}", sql);
        }
    }

    /**
     * Configura uma variável de sessão do PostgreSQL usando SET LOCAL.
     *
     * <p>
     * SET LOCAL garante que a variável só existe durante a transação atual
     * e é automaticamente limpa ao final da transação.
     * </p>
     *
     * @param key   Nome da variável (ex: app.user_type)
     * @param value Valor da variável (String)
     */
    private void setSessionVariable(String key, String value) {
        if (value != null && !value.isEmpty()) {
            // Escapa aspas simples para prevenir SQL injection
            String escapedValue = value.replace("'", "''");
            String sql = String.format("SET LOCAL %s = '%s'", key, escapedValue);

            jdbcTemplate.execute(sql);
            log.trace("RLS: Executado SQL: {}", sql);
        }
    }

    /**
     * Método executado APÓS a conclusão da requisição.
     *
     * <p>
     * As variáveis de sessão configuradas com SET LOCAL são automaticamente
     * limpas pelo PostgreSQL ao final da transação, então não é necessário
     * fazer limpeza manual aqui.
     * </p>
     *
     * @param request  Requisição HTTP
     * @param response Resposta HTTP
     * @param handler  Handler que foi executado
     * @param ex       Exceção que ocorreu (se houver)
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // Variáveis de sessão são automaticamente limpas após a transação
        // Não é necessário fazer limpeza manual
        log.trace("RLS: Contexto será limpo automaticamente pelo PostgreSQL");
    }
}
