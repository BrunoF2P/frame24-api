/**
 * Módulo de Identidade e Controle de Acesso.
 * 
 * <p>
 * Responsável por gerenciar autenticação, autorização, usuários, empresas,
 * permissões e controle de acesso ao sistema.
 * </p>
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 * <li>Autenticação e autorização de usuários</li>
 * <li>Gerenciamento de empresas (multi-tenant)</li>
 * <li>Controle de permissões e roles customizados</li>
 * <li>Gerenciamento de identidades (funcionários e clientes)</li>
 * </ul>
 * 
 * <h3>API Pública:</h3>
 * <ul>
 * <li>{@code IdentityApi} - Interface para consultas de identidade</li>
 * <li>{@code AuthenticationApi} - Interface para autenticação</li>
 * <li>{@code PermissionApi} - Interface para verificação de permissões</li>
 * </ul>
 * 
 * <h3>Eventos Publicados:</h3>
 * <ul>
 * <li>{@code UserCreatedEvent} - Quando um novo usuário é criado</li>
 * <li>{@code CompanyCreatedEvent} - Quando uma nova empresa é criada</li>
 * <li>{@code PermissionChangedEvent} - Quando permissões são alteradas</li>
 * </ul>
 * 
 * <h3>Dependências:</h3>
 * <ul>
 * <li>{@code common} - Infraestrutura compartilhada</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Identity & Access Management", allowedDependencies = {
        "common" })
package com.frame24.api.identity;
