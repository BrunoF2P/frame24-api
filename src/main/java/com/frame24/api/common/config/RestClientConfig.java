package com.frame24.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuração do RestClient para chamadas HTTP.
 */
@Configuration
public class RestClientConfig {

    /**
     * Cria o builder do RestClient para injeção de dependência.
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
