package com.frame24.api.identity.domain.enums;

/**
 * Contexto da sessão do usuário.
 * Mapeado para o tipo PostgreSQL: identity.session_context
 */
public enum SessionContext {
    EMPLOYEE,
    CUSTOMER,
    SYSTEM
}
