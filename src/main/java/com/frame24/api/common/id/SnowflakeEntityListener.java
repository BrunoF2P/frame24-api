package com.frame24.api.common.id;

import jakarta.persistence.PrePersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 */
@Component
public class SnowflakeEntityListener {

    private static SnowflakeIdGenerator idGenerator;

    @Autowired
    public void setIdGenerator(SnowflakeIdGenerator generator) {
        SnowflakeEntityListener.idGenerator = generator;
    }

    @PrePersist
    public void generateId(Object entity) {
        try {
            var idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);

            if (idField.get(entity) == null) {
                idField.set(entity, idGenerator.nextId());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Entidade não tem campo 'id' ou não é acessível
            // Ignora silenciosamente
        }
    }
}
