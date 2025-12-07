package com.frame24.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Configuração global do Jackson 3 para Spring Boot 4.
 *
 * <p>
 * Serializa todos os tipos Long como String para prevenir perda de precisão
 * quando IDs Snowflake (64 bits) são enviados para JavaScript (53 bits).
 * </p>
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public JsonMapper jsonMapper() {
        SimpleModule longModule = new SimpleModule("LongToStringModule");
        longModule.addSerializer(Long.class, ToStringSerializer.instance);
        longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        return JsonMapper.builder()
                .addModule(longModule)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule longModule = new SimpleModule("LongToStringModule");
        longModule.addSerializer(Long.class, ToStringSerializer.instance);
        longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        return JsonMapper.builder()
                .addModule(longModule)
                .build();
    }
}

