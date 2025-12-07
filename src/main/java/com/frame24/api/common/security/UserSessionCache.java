package com.frame24.api.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Cache de sessões de usuário no Redis para melhorar performance.
 * <p>
 * Armazena UserPrincipal em cache para evitar queries repetidas no banco.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "user:session:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1); // 1h (mesmo do access token)

    /**
     * Salva UserPrincipal no cache.
     *
     * @param userId        ID do usuário
     * @param userPrincipal Dados do usuário
     */
    public void save(Long userId, UserPrincipal userPrincipal) {
        String key = KEY_PREFIX + userId;
        try {
            redisTemplate.opsForValue().set(key, userPrincipal, DEFAULT_TTL);
            log.debug("UserPrincipal salvo no cache: userId={}", userId);
        } catch (Exception e) {
            log.error("Erro ao salvar UserPrincipal no cache: {}", e.getMessage());
        }
    }

    /**
     * Busca UserPrincipal do cache.
     *
     * @param userId ID do usuário
     * @return UserPrincipal ou null se não existir
     */
    public UserPrincipal get(Long userId) {
        String key = KEY_PREFIX + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof UserPrincipal) {
                log.debug("UserPrincipal encontrado no cache: userId={}", userId);
                return (UserPrincipal) value;
            }
        } catch (Exception e) {
            log.error("Erro ao buscar UserPrincipal do cache: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Remove UserPrincipal do cache (usado no logout).
     *
     * @param userId ID do usuário
     */
    public void delete(Long userId) {
        String key = KEY_PREFIX + userId;
        try {
            redisTemplate.delete(key);
            log.debug("UserPrincipal removido do cache: userId={}", userId);
        } catch (Exception e) {
            log.error("Erro ao deletar UserPrincipal do cache: {}", e.getMessage());
        }
    }

    /**
     * Atualiza o TTL da sessão (usado ao fazer refresh).
     *
     * @param userId ID do usuário
     */
    public void updateTtl(Long userId) {
        String key = KEY_PREFIX + userId;
        try {
            redisTemplate.expire(key, DEFAULT_TTL);
            log.debug("TTL atualizado para UserPrincipal: userId={}", userId);
        } catch (Exception e) {
            log.error("Erro ao atualizar TTL: {}", e.getMessage());
        }
    }
}
