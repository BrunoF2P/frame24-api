/**
 * Módulo Financeiro.
 * 
 * <p>
 * Responsável por gerenciar contas a pagar, contas a receber,
 * fluxo de caixa, lançamentos contábeis e relatórios financeiros.
 * </p>
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 * <li>Contas a pagar e receber</li>
 * <li>Fluxo de caixa</li>
 * <li>Lançamentos contábeis</li>
 * <li>Relatórios financeiros</li>
 * <li>Conciliação bancária</li>
 * </ul>
 * 
 * <h3>API Pública:</h3>
 * <ul>
 * <li>{@code FinanceApi} - Interface para operações financeiras</li>
 * <li>{@code ReportApi} - Interface para relatórios</li>
 * </ul>
 * 
 * <h3>Eventos Consumidos:</h3>
 * <ul>
 * <li>{@code SaleCompletedEvent} - Para criar contas a receber</li>
 * <li>{@code PurchaseCompletedEvent} - Para criar contas a pagar</li>
 * </ul>
 * 
 * <h3>Eventos Publicados:</h3>
 * <ul>
 * <li>{@code PaymentReceivedEvent} - Quando um pagamento é recebido</li>
 * <li>{@code PaymentMadeEvent} - Quando um pagamento é efetuado</li>
 * </ul>
 * 
 * <h3>Dependências:</h3>
 * <ul>
 * <li>{@code common} - Infraestrutura compartilhada</li>
 * <li>{@code identity} - Para validação de empresas</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Financial Management", allowedDependencies = { "common",
        "identity" })
package com.frame24.api.finance;
