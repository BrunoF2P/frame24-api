/**
 * Módulo de Vendas.
 * 
 * <p>
 * Responsável por gerenciar vendas de ingressos, transações, tipos de venda
 * e processamento de pagamentos.
 * </p>
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 * <li>Processamento de vendas de ingressos</li>
 * <li>Gerenciamento de tipos de venda e ticket</li>
 * <li>Controle de status de vendas</li>
 * <li>Vendas de concessão (pipoca, bebidas, etc)</li>
 * </ul>
 * 
 * <h3>API Pública:</h3>
 * <ul>
 * <li>{@code SalesApi} - Interface para operações de venda</li>
 * <li>{@code TicketApi} - Interface para consulta de ingressos</li>
 * </ul>
 * 
 * <h3>Eventos Publicados:</h3>
 * <ul>
 * <li>{@code SaleCompletedEvent} - Quando uma venda é concluída</li>
 * <li>{@code SaleCancelledEvent} - Quando uma venda é cancelada</li>
 * <li>{@code TicketIssuedEvent} - Quando um ingresso é emitido</li>
 * </ul>
 * 
 * <h3>Dependências:</h3>
 * <ul>
 * <li>{@code common} - Infraestrutura compartilhada</li>
 * <li>{@code identity} - Para validação de usuários e clientes</li>
 * <li>{@code operations} - Para validação de sessões e assentos</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Sales Management", allowedDependencies = { "common",
        "identity", "operations" })
package com.frame24.api.sales;
