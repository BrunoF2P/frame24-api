package com.frame24.api.common.event;

/**
 * Evento publicado quando uma nova empresa é criada.
 * <p>
 * Permite que outros módulos reajam à criação de uma empresa
 * sem criar dependências diretas entre módulos.
 */
public record CompanyCreatedEvent(
        Long companyId,
        String corporateName,
        String tenantSlug) {
}
