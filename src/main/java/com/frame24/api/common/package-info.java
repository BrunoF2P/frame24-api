/**
 * Pacote de infraestrutura compartilhada.
 * 
 * <p>
 * Contém configurações, utilitários e componentes compartilhados
 * por todos os módulos da aplicação.
 * </p>
 * 
 * <h3>Componentes:</h3>
 * <ul>
 * <li>{@code config} - Configurações do Spring (Security, Cache, MVC)</li>
 * <li>{@code security} - Componentes de segurança (RLS, UserPrincipal)</li>
 * <li>{@code events} - Eventos de domínio compartilhados</li>
 * <li>{@code response} - DTOs de resposta (ApiResponse)</li>
 * </ul>
 * 
 * <p>
 * Este módulo é OPEN, permitindo que todos os seus pacotes sejam
 * acessados por outros módulos da aplicação.
 * </p>
 */
@ApplicationModule(displayName = "Common", allowedDependencies = {}, type = ApplicationModule.Type.OPEN)
package com.frame24.api.common;

import org.springframework.modulith.ApplicationModule;
