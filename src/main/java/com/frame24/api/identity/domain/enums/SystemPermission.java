package com.frame24.api.identity.domain.enums;

import lombok.Getter;

/**
 * Catálogo central de todas as permissões do sistema.
 * <p>
 * Define o recurso, ação, nome legível e módulo de cada permissão.
 * Usado para padronização e geração automática de permissões.
 */
@Getter
public enum SystemPermission {

    // --- Module: Identity (Usuários, Roles, etc.) ---
    USERS_READ("users", "read", "Visualizar Usuários", "identity", "Permite listar e visualizar detalhes de usuários"),
    USERS_CREATE("users", "create", "Criar Usuários", "identity", "Permite cadastrar novos usuários"),
    USERS_UPDATE("users", "update", "Editar Usuários", "identity", "Permite editar dados de usuários"),
    USERS_DELETE("users", "delete", "Deletar Usuários", "identity", "Permite remover usuários do sistema"),

    ROLES_READ("roles", "read", "Visualizar Roles", "identity", "Permite listar roles e permissões"),
    ROLES_MANAGE("roles", "manage", "Gerenciar Roles", "identity", "Permite criar, editar e deletar roles"),

    // --- Module: CRM (Clientes) ---
    CUSTOMERS_READ("customers", "read", "Visualizar Clientes", "crm", "Permite visualizar carteira de clientes"),
    CUSTOMERS_Manage("customers", "manage", "Gerenciar Clientes", "crm", "Permite cadastrar e editar clientes"),

    // --- Module: Finance (Financeiro) ---
    FINANCE_DASHBOARD("finance", "view_dashboard", "Dashboard Financeiro", "finance",
            "Acesso aos indicadores financeiros"),
    FINANCE_PAYABLES_READ("payables", "read", "Visualizar Contas a Pagar", "finance", "Listar contas a pagar"),
    FINANCE_RECEIVABLES_READ("receivables", "read", "Visualizar Contas a Receber", "finance",
            "Listar contas a receber"),
    FINANCE_TRANSACTIONS_MANAGE("transactions", "manage", "Gerenciar Transações", "finance",
            "Lançar pagamentos e recebimentos"),

    // --- Module: Sales (Vendas) ---
    SALES_POS("pos", "access", "Acesso ao PDV", "sales", "Permite realizar vendas no ponto de venda"),
    SALES_REPORTS("sales_reports", "read", "Relatórios de Vendas", "sales",
            "Acesso aos relatórios detalhados de vendas");

    private final String resource;
    private final String action;
    private final String name;
    private final String module;
    private final String description;
    private final String code;

    SystemPermission(String resource, String action, String name, String module, String description) {
        this.resource = resource;
        this.action = action;
        this.name = name;
        this.module = module;
        this.description = description;
        this.code = resource + ":" + action;
    }
}
