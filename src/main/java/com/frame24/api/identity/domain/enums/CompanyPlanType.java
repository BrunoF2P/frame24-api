package com.frame24.api.identity.domain.enums;

/**
 * Tipo de plano da empresa.
 * Mapeado para o tipo PostgreSQL: identity.company_plan_type
 */

import java.util.Set;

/**
 * Tipo de plano da empresa.
 * Mapeado para o tipo PostgreSQL: identity.company_plan_type
 */
public enum CompanyPlanType {
    BASIC(Set.of(
            SystemPermission.USERS_READ,
            SystemPermission.CUSTOMERS_READ,
            SystemPermission.SALES_POS)),

    PREMIUM(Set.of(// Basic Plans
            SystemPermission.USERS_READ,
            SystemPermission.CUSTOMERS_READ,
            SystemPermission.SALES_POS, // Premium additions
            SystemPermission.USERS_CREATE,
            SystemPermission.USERS_UPDATE,
            SystemPermission.USERS_DELETE,
            SystemPermission.CUSTOMERS_Manage,
            SystemPermission.FINANCE_PAYABLES_READ,
            SystemPermission.FINANCE_RECEIVABLES_READ)),

    ENTERPRISE(Set.of(SystemPermission.values())); // Enterprise tem tudo

    private final Set<SystemPermission> permissions;

    CompanyPlanType(Set<SystemPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<SystemPermission> getPermissions() {
        return permissions;
    }
}
