/**
 * Módulo de Auditoria.
 * 
 * <p>
 * Responsável por registrar e consultar logs de auditoria de todas
 * as operações críticas do sistema.
 * </p>
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 * <li>Registro de eventos de auditoria</li>
 * <li>Consulta de histórico de alterações</li>
 * <li>Rastreabilidade de operações</li>
 * </ul>
 * 
 * <h3>API Pública:</h3>
 * <ul>
 * <li>{@code AuditApi} - Interface para consulta de logs</li>
 * </ul>
 * 
 * <h3>Eventos Consumidos:</h3>
 * <ul>
 * <li>Todos os eventos de domínio para auditoria</li>
 * </ul>
 * 
 * <h3>Dependências:</h3>
 * <ul>
 * <li>{@code common} - Infraestrutura compartilhada</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Audit & Logging", allowedDependencies = { "common" })
package com.frame24.api.audit;
