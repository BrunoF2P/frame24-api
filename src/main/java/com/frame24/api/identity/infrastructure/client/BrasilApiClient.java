package com.frame24.api.identity.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.frame24.api.common.exception.ExternalServiceException;
import com.frame24.api.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente para a BrasilAPI.
 * Consulta dados públicos de CNPJ e CEP.
 */
@Component
public class BrasilApiClient {

    private static final Logger log = LoggerFactory.getLogger(BrasilApiClient.class);
    private static final String BASE_URL = "https://brasilapi.com.br/api";

    private final RestClient restClient;

    public BrasilApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(BASE_URL)
                .build();
    }

    /**
     * Consulta dados de CNPJ na Receita Federal.
     *
     * @param cnpj CNPJ sem formatação (apenas números)
     * @return dados do CNPJ ou null se não encontrado
     * @throws ValidationException      se CNPJ for inválido
     * @throws ExternalServiceException se houver falha na API
     */
    public CnpjResponse getCnpjData(String cnpj) {
        String sanitizedCnpj = sanitize(cnpj);
        log.info("Consultando CNPJ na BrasilAPI: {}", maskCnpj(sanitizedCnpj));

        try {
            return restClient.get()
                    .uri("/cnpj/v1/{cnpj}", sanitizedCnpj)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new ValidationException("cnpj", "CNPJ não encontrado na Receita Federal");
                        }
                        throw new ValidationException("cnpj", "CNPJ inválido ou não disponível");
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        throw new ExternalServiceException("BrasilAPI", "Serviço temporariamente indisponível");
                    })
                    .body(CnpjResponse.class);
        } catch (ValidationException | ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao consultar BrasilAPI para CNPJ: {}", e.getMessage());
            throw new ExternalServiceException("BrasilAPI", "Falha na comunicação com a Receita Federal", e);
        }
    }

    /**
     * Consulta dados de CEP.
     *
     * @param cep CEP sem formatação
     * @return dados do CEP ou null se não encontrado
     */
    public CepResponse getCepData(String cep) {
        String sanitizedCep = sanitize(cep);
        log.info("Consultando CEP na BrasilAPI: {}", sanitizedCep);

        try {
            return restClient.get()
                    .uri("/cep/v1/{cep}", sanitizedCep)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        log.warn("CEP não encontrado: {}", sanitizedCep);
                    })
                    .body(CepResponse.class);
        } catch (Exception e) {
            log.warn("Erro ao consultar CEP: {}", e.getMessage());
            return null;
        }
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private String maskCnpj(String cnpj) {
        if (cnpj.length() != 14)
            return "***";
        return cnpj.substring(0, 4) + "****" + cnpj.substring(8);
    }

    /**
     * Resposta da consulta de CNPJ.
     */
    public record CnpjResponse(
            String cnpj,

            @JsonProperty("razao_social") String razaoSocial,

            @JsonProperty("nome_fantasia") String nomeFantasia,

            @JsonProperty("opcao_pelo_simples") Boolean opcaoPeloSimples,

            @JsonProperty("porte") String porte,

            @JsonProperty("natureza_juridica") String naturezaJuridica,

            @JsonProperty("situacao_cadastral") String situacaoCadastral,

            @JsonProperty("descricao_situacao_cadastral") String descricaoSituacaoCadastral,

            @JsonProperty("logradouro") String logradouro,
            @JsonProperty("numero") String numero,
            @JsonProperty("complemento") String complemento,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("cep") String cep,
            @JsonProperty("municipio") String municipio,
            @JsonProperty("uf") String uf) {
        /**
         * Verifica se a empresa está optante pelo Simples Nacional.
         */
        public boolean isOptanteSimplesNacional() {
            return Boolean.TRUE.equals(opcaoPeloSimples);
        }

        /**
         * Verifica se o CNPJ está ativo na Receita.
         */
        public boolean isAtivo() {
            return "ATIVA".equalsIgnoreCase(descricaoSituacaoCadastral);
        }
    }

    /**
     * Resposta da consulta de CEP.
     */
    public record CepResponse(
            String cep,
            String state,
            String city,
            String neighborhood,
            String street) {
    }
}
