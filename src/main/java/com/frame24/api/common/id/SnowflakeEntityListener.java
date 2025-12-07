package com.frame24.api.common.id;

import jakarta.persistence.PrePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entity Listener para gerar IDs Snowflake automaticamente.
 *
 * <p>
 * Adicione este listener às suas entidades para gerar IDs automaticamente:
 * </p>
 *
 * <pre>
 * {
 *     &#64;code
 *     &#64;Entity
 *     &#64;EntityListeners(SnowflakeEntityListener.class)
 *     public class Sale {
 *         @Id
 *         private Long id;
 *
 *         // ID será gerado automaticamente antes de persistir
 *     }
 * }
 * </pre>
 *
 * <p>
 * Otimizações implementadas:
 * </p>
 * <ul>
 * <li>Cache de reflexão para evitar lookups repetidos</li>
 * <li>Logging para debug em desenvolvimento</li>
 * <li>Tratamento robusto de erros</li>
 * </ul>
 */
@Component
public class SnowflakeEntityListener {

    private static final Logger log = LoggerFactory.getLogger(SnowflakeEntityListener.class);
    private static SnowflakeIdGenerator idGenerator;

    // Cache de Fields para evitar reflexão repetida (melhoria de performance)
    private static final ConcurrentHashMap<Class<?>, Field> fieldCache = new ConcurrentHashMap<>();

    @Autowired
    public void setIdGenerator(SnowflakeIdGenerator generator) {
        SnowflakeEntityListener.idGenerator = generator;
    }

    /**
     * Gera o ID Snowflake antes da entidade ser persistida.
     *
     * @param entity Entidade sendo persistida
     */
    @PrePersist
    public void generateId(Object entity) {
        try {
            // Usa cache para evitar reflexão repetida
            Field idField = fieldCache.computeIfAbsent(
                    entity.getClass(),
                    clazz -> {
                        try {
                            Field field = clazz.getDeclaredField("id");
                            field.setAccessible(true);
                            return field;
                        } catch (NoSuchFieldException e) {
                            return null;
                        }
                    }
            );

            // Se não encontrou o campo 'id', ignora silenciosamente
            if (idField == null) {
                return;
            }

            // Gera ID apenas se ainda não foi definido
            if (idField.get(entity) == null) {
                long snowflakeId = idGenerator.nextId();
                idField.set(entity, snowflakeId);

                log.debug("ID Snowflake gerado para {}: {}",
                        entity.getClass().getSimpleName(),
                        snowflakeId);
            }

        } catch (IllegalAccessException e) {
            log.error("Erro ao acessar campo 'id' em {}: {}",
                    entity.getClass().getSimpleName(),
                    e.getMessage());
            throw new RuntimeException(
                    String.format("Falha ao gerar Snowflake ID para %s",
                            entity.getClass().getSimpleName()),
                    e);
        } catch (Exception e) {
            log.error("Erro inesperado ao gerar Snowflake ID para {}: {}",
                    entity.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
            throw new RuntimeException(
                    String.format("Erro ao gerar Snowflake ID para %s",
                            entity.getClass().getSimpleName()),
                    e);
        }
    }
}
