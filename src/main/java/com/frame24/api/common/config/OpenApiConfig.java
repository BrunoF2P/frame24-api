package com.frame24.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🎬 Frame 24 - Cinema Management API")
                        .version("1.0.0")
                        .description("""
                                # 🍿 Bem-vindo à API Frame 24!
                                
                                Sistema completo de gestão para redes de cinema com recursos avançados para:
                                
                                ## 🎯 Principais Funcionalidades
                                
                                - **🎫 Vendas & Ingressos**: Gestão completa de vendas de ingressos e combos
                                - **👥 CRM & Fidelidade**: Programa de pontos e gestão de clientes
                                - **🎞️ Catálogo de Filmes**: Gerenciamento de filmes, sessões e salas
                                - **💰 Financeiro**: Controle de receitas, despesas e acertos com distribuidoras
                                - **📊 Operações**: Planejamento de grade, ocupação e relatórios
                                - **🏢 Multi-tenant**: Suporte a múltiplos complexos e empresas
                                - **🔐 Segurança**: Autenticação JWT e controle de permissões granular
                                
                                ## 🚀 Começando
                                
                                1. **Autentique-se** no endpoint `/api/v1/auth/login`
                                2. **Copie o token JWT** recebido na resposta
                                3. **Clique em "Authorize"** e cole o token no formato: `Bearer seu-token-aqui`
                                4. **Explore os endpoints** disponíveis nas categorias abaixo
                                
                                ## 🆘 Suporte
                                
                                Precisa de ajuda? Entre em contato:
                                - 📧 Email: dev@frame24.com
                                - 💬 Slack: [frame24.slack.com](https://frame24.slack.com)
                                - 🐛 Reportar bug: [github.com/frame24/issues](https://github.com/frame24/issues)
                                """)
                        .contact(new Contact()
                                .name("Equipe Frame 24 - Suporte Técnico")
                                .email("dev@frame24.com")
                                .url("https://frame24.com/suporte"))
                        .license(new License()
                                .name("Proprietário © Frame 24")
                                .url("https://frame24.com/termos-de-uso")))

                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("🛠️ Ambiente de Desenvolvimento Local"),
                        new Server()
                                .url("https://api.frame24.com")
                                .description("🚀 Ambiente de Produção")
                ))

                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))

                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("""
                                                ### 🔐 Autenticação via JWT (JSON Web Token)
                                                
                                                Para acessar endpoints protegidos, você precisa incluir um token JWT válido.
                                                
                                                **Como obter o token:**
                                                1. Faça login em `/api/v1/auth/login` com suas credenciais
                                                2. Copie o `accessToken` retornado na resposta
                                                3. Use o token no header Authorization de cada requisição
                                                
                                                **Formato do header:**
                                                ```
                                                Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                                                ```
                                                
                                                **⏱️ Validade:** O token expira em 24 horas. Use o refresh token para renovar.
                                                
                                                **🔄 Renovação:** Endpoint `/api/v1/auth/refresh` com o refresh token
                                                """)
                        )
                );
    }
}
