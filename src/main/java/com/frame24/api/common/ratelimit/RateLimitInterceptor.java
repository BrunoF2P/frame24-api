package com.frame24.api.common.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Interceptor para rate limiting de endpoints.
 * Usa Redis para armazenar contadores por IP/endpoint.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimited rateLimited = handlerMethod.getMethodAnnotation(RateLimited.class);

        if (rateLimited == null) {
            return true; // Sem rate limit
        }

        String key = buildKey(request, rateLimited.keyType(), handlerMethod);
        int maxRequests = rateLimited.requests();
        long windowSeconds = rateLimited.windowMinutes() * 60L;

        // Incrementa contador
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == null) {
            currentCount = 0L;
        }

        // Define TTL na primeira requisição
        if (currentCount == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }

        // Verifica se excedeu o limite
        if (currentCount > maxRequests) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            log.warn("Rate limit excedido: key={}, count={}, max={}", key, currentCount, maxRequests);

            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(ttl != null ? ttl : 0));

            response.getWriter().write(String.format(
                    "{\"error\":{\"code\":\"TOO_MANY_REQUESTS\"," +
                            "\"message\":\"Muitas requisições. Tente novamente em %d segundos\"," +
                            "\"retryAfter\":%d}}",
                    ttl != null ? ttl : 0, ttl != null ? ttl : 0));

            return false;
        }

        // Adiciona headers de rate limit
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(maxRequests - currentCount));

        return true;
    }

    private String buildKey(HttpServletRequest request, String keyType, HandlerMethod method) {
        String baseKey = "ratelimit:";

        switch (keyType.toLowerCase()) {
            case "user":
                // TODO: Extrair user ID do token JWT
                String userId = request.getHeader("X-User-Id");
                if (userId != null) {
                    return baseKey + "user:" + userId;
                }
                // Fallback para IP se não tiver user
                return baseKey + "ip:" + getClientIP(request);

            case "ip+endpoint":
                return baseKey + "ip_endpoint:" + getClientIP(request) + ":" +
                        method.getMethod().getName();

            case "ip":
            default:
                return baseKey + "ip:" + getClientIP(request);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }
}
