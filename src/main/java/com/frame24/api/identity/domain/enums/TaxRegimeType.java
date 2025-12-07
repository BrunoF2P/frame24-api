package com.frame24.api.identity.domain.enums;

/**
 * Regime tributário da empresa.
 * Mapeado para o tipo PostgreSQL: identity.tax_regime_type
 */
public enum TaxRegimeType {
    SIMPLES_NACIONAL,
    LUCRO_PRESUMIDO,
    LUCRO_REAL
}
