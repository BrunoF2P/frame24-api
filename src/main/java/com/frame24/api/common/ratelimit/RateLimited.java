package com.frame24.api.common.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation para aplicar rate limiting em endpoints.
 * Usa Redis + Bucket4j para controle distribuído.
 *
 * @example          <pre>
 * @RateLimited(requests = 5, windowMinutes = 1)
 * public ResponseEntity login(...) { ... }
 *          </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    /**
     * Número máximo de requisições permitidas.
     */
    int requests() default 10;

    /**
     * Janela de tempo em minutos.
     */
    int windowMinutes() default 1;

    /**
     * Chave customizada para rate limit.
     * Usa IP por padrão. Pode usar: "user", "ip", "ip+endpoint"
     */
    String keyType() default "ip";
}
