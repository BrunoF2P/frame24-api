package com.frame24.api.common.id;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Conversor JPA para IDs Snowflake.
 *
 * <p>
 * Permite usar Long nas entidades JPA com armazenamento otimizado no banco.
 * </p>
 *
 * <h3>Uso em Entidades:</h3>
 *
 * <pre>
 * {
 *     &#64;code
 *     &#64;Entity
 *     public class Sale {
 *         &#64;Id
 *         private Long id;
 *
 *         @PrePersist
 *         void generateId() {
 *             if (id == null) {
 *                 id = idGenerator.nextId();
 *             }
 *         }
 *     }
 * }
 * </pre>
 */
@Component
@Converter(autoApply = false)
public class SnowflakeIdConverter implements AttributeConverter<Long, Long> {

    private static SnowflakeIdGenerator idGenerator;

    @Autowired
    public void setIdGenerator(SnowflakeIdGenerator generator) {
        SnowflakeIdConverter.idGenerator = generator;
    }

    @Override
    public Long convertToDatabaseColumn(Long attribute) {
        return attribute;
    }

    @Override
    public Long convertToEntityAttribute(Long dbData) {
        return dbData;
    }
}
