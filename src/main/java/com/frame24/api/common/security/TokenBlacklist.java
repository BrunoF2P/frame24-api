package com.frame24.api.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Blacklist de tokens JWT para implementar logout.
 * <p>
 * Tokens adicionados aqui são considerados inválidos mesmo que não tenham
 * expirado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklist {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "token:blacklist:";

    /**
     * Adiciona um token à blacklist.
     *
     * @param token Token JWT completo
     * @param ttl   Tempo até expiração original do token
     */
    public void add(String token, Duration ttl) {
        String key = KEY_PREFIX + hashToken(token);
        try {
            redisTemplate.opsForValue().set(key, "blacklisted", ttl);
            log.debug("Token adicionado à blacklist (TTL: {}s)", ttl.getSeconds());
        } catch (Exception e) {
            log.error("Erro ao adicionar token à blacklist: {}", e.getMessage());
        }
    }

    /**
     * Verifica se um token está na blacklist.
     *
     * @param token Token JWT completo
     * @return true se estiver na blacklist, false caso contrário
     */
    public boolean isBlacklisted(String token) {
        String key = KEY_PREFIX + hashToken(token);
        try {
            Boolean exists = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(exists)) {
                log.debug("Token encontrado na blacklist");
                return true;
            }
        } catch (Exception e) {
            log.error("Erro ao verificar blacklist: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Gera hash do token para usar como chave no Redis.
     * Usamos hash para economizar espaço (tokens JW T são grandes).
     *
     * @param token Token JWT
     * @return Hash do token
     */
    private String hashToken(String token) {
        return String.valueOf(token.hashCode());
    }
}
