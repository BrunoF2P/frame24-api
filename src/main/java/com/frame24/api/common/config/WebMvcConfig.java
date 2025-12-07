package com.frame24.api.common.config;

import com.frame24.api.common.security.RLSContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração do Spring MVC para registrar o interceptor de Row Level Security
 * (RLS).
 *
 * <p>
 * Esta classe configura o {@link RLSContextInterceptor} para ser executado
 * em todas as requisições HTTP, exceto endpoints específicos que não precisam
 * de contexto RLS (como actuator e páginas de erro).
 * </p>
 *
 * <h3>Rotas Incluídas:</h3>
 * <ul>
 * <li><b>/**</b>: Todas as rotas da aplicação</li>
 * </ul>
 *
 * <h3>Rotas Excluídas:</h3>
 * <ul>
 * <li><b>/actuator/**</b>: Endpoints de monitoramento (health, metrics,
 * etc)</li>
 * <li><b>/h2-console/**</b>: Console do banco H2 (apenas desenvolvimento)</li>
 * <li><b>/error</b>: Página de erro padrão do Spring</li>
 * </ul>
 *
 * <h3>Ordem de Execução:</h3>
 *
 * <pre>
 * 1. Requisição HTTP chega
 * 2. WebMvcConfig verifica se deve aplicar interceptor
 * 3. Se sim, RLSContextInterceptor.preHandle() é chamado
 * 4. Controller é executado
 * 5. RLSContextInterceptor.afterCompletion() é chamado
 * </pre>
 *
 * <p>
 * <b>Nota:</b> O interceptor é registrado automaticamente pelo Spring
 * quando esta classe é carregada (devido à anotação @Configuration).
 * </p>
 *
 * @see RLSContextInterceptor
 * @see WebMvcConfigurer
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    // Injetado via @RequiredArgsConstructor do Lombok
    private final RLSContextInterceptor rlsContextInterceptor;

    /**
     * Registra os interceptors da aplicação.
     *
     * <p>
     * Configura o {@link RLSContextInterceptor} para ser executado em
     * todas as rotas, exceto as rotas de sistema que não precisam de RLS.
     * </p>
     *
     * @param registry Registro de interceptors do Spring MVC
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rlsContextInterceptor)
                .addPathPatterns("/**") // Aplica em todas as rotas
                .excludePathPatterns(
                        "/actuator/**", // Exclui endpoints do actuator
                        "/h2-console/**", // Exclui console do H2
                        "/v3/api-docs/**", // Exclui OpenAPI docs
                        "/scalar/**", // Exclui Scalar docs
                        "/register", // Exclui registro de empresas
                        "/error" // Exclui página de erro
                );
    }
}
