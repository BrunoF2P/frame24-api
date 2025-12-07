package com.frame24.api.identity.domain.enums;

/**
 * Tipo de identidade do usuário.
 * Mapeado para o tipo PostgreSQL: identity.identity_type
 */
public enum IdentityType {
    CUSTOMER,
    EMPLOYEE,
    SYSTEM
}
