-- ============================================================================
-- Financial Management
-- ============================================================================
-- Migration: V4__finance_schema.sql
-- Description: Financial Management
-- ============================================================================

-- Financial Management

CREATE TABLE "finance"."account_natures" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "account_natures_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."account_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "account_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."accounting_movement_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "accounting_movement_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."accounts_payable" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT,
    "supplier_id" BIGINT,
    "source_type" TEXT,
    "source_id" BIGINT,
    "document_number" VARCHAR(50) NOT NULL,
    "description" TEXT NOT NULL,
    "issue_date" DATE NOT NULL,
    "due_date" DATE NOT NULL,
    "competence_date" DATE NOT NULL,
    "original_amount" DECIMAL(15,2) NOT NULL,
    "interest_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "penalty_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "discount_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "paid_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "remaining_amount" DECIMAL(15,2) NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),
    "expense_type" VARCHAR(50),

    CONSTRAINT "accounts_payable_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."accounts_receivable" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT,
    "customer_id" BIGINT,
    "sale_id" BIGINT,
    "document_number" VARCHAR(50) NOT NULL,
    "description" TEXT NOT NULL,
    "issue_date" DATE NOT NULL,
    "due_date" DATE NOT NULL,
    "competence_date" DATE NOT NULL,
    "original_amount" DECIMAL(15,2) NOT NULL,
    "interest_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "penalty_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "discount_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "paid_amount" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "remaining_amount" DECIMAL(15,2) NOT NULL,
    "status" VARCHAR(20) NOT NULL,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "accounts_receivable_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."bank_accounts" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "bank_name" VARCHAR(100) NOT NULL,
    "bank_code" VARCHAR(10),
    "agency" VARCHAR(20) NOT NULL,
    "agency_digit" VARCHAR(2),
    "account_number" VARCHAR(20) NOT NULL,
    "account_digit" VARCHAR(2),
    "account_type" VARCHAR(20) NOT NULL,
    "initial_balance" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "current_balance" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "active" BOOLEAN DEFAULT true,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "bank_accounts_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."bank_reconciliations" (
    "id" BIGINT NOT NULL,
    "bank_account_id" BIGINT NOT NULL,
    "reference_month" DATE NOT NULL,
    "opening_balance" DECIMAL(15,2) NOT NULL,
    "closing_balance" DECIMAL(15,2) NOT NULL,
    "bank_statement_balance" DECIMAL(15,2) NOT NULL,
    "total_receipts" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "total_payments" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "pending_receipts" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "pending_payments" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "reconciled_balance" DECIMAL(15,2) NOT NULL,
    "difference" DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    "status" VARCHAR(20),
    "notes" TEXT,
    "reconciled_by" TEXT,
    "reconciled_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "bank_reconciliations_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."cash_flow_entries" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT,
    "bank_account_id" BIGINT NOT NULL,
    "entry_type" VARCHAR(20) NOT NULL,
    "category" VARCHAR(50) NOT NULL,
    "amount" DECIMAL(15,2) NOT NULL,
    "entry_date" DATE NOT NULL,
    "competence_date" DATE,
    "description" TEXT NOT NULL,
    "document_number" VARCHAR(50),
    "source_type" VARCHAR(50),
    "source_id" BIGINT,
    "counterpart_type" VARCHAR(50),
    "counterpart_id" BIGINT,
    "status" VARCHAR(20),
    "reconciled" BOOLEAN DEFAULT false,
    "reconciled_at" TIMESTAMP(3),
    "created_by" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "cash_flow_entries_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."chart_of_accounts" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "account_code" VARCHAR(20) NOT NULL,
    "account_name" VARCHAR(200) NOT NULL,
    "account_type" BIGINT,
    "account_nature" BIGINT,
    "parent_account_id" BIGINT,
    "level" INTEGER NOT NULL,
    "allows_entry" BOOLEAN DEFAULT true,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "chart_of_accounts_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."contingency_reserves" (
    "id" BIGINT NOT NULL,
    "complex_id" BIGINT NOT NULL,
    "contingency_type" BIGINT,
    "reserve_amount" DECIMAL(15,2) NOT NULL,
    "reason" TEXT NOT NULL,
    "inclusion_date" DATE NOT NULL,
    "clearance_date" DATE,
    "status" BIGINT,
    "notes" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "contingency_reserves_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."contingency_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "contingency_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."contingency_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "contingency_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."distributor_settlement_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "distributor_settlement_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."distributor_settlements" (
    "id" BIGINT NOT NULL,
    "contract_id" BIGINT NOT NULL,
    "distributor_id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "calculation_base" BIGINT,
    "status" BIGINT,
    "competence_start_date" DATE NOT NULL,
    "competence_end_date" DATE NOT NULL,
    "total_tickets_sold" INTEGER DEFAULT 0,
    "gross_box_office_revenue" DECIMAL(15,2) NOT NULL,
    "taxes_deducted_amount" DECIMAL(15,2) DEFAULT 0.00,
    "settlement_base_amount" DECIMAL(15,2) NOT NULL,
    "distributor_percentage" DECIMAL(5,2) NOT NULL,
    "calculated_settlement_amount" DECIMAL(15,2) NOT NULL,
    "minimum_guarantee" DECIMAL(15,2) DEFAULT 0.00,
    "final_settlement_amount" DECIMAL(15,2) NOT NULL,
    "deductions_amount" DECIMAL(15,2) DEFAULT 0.00,
    "net_settlement_amount" DECIMAL(15,2) NOT NULL,
    "irrf_rate" DECIMAL(5,2) DEFAULT 0.00,
    "irrf_calculation_base" DECIMAL(15,2) DEFAULT 0.00,
    "irrf_amount" DECIMAL(15,2) DEFAULT 0.00,
    "irrf_exempt" BOOLEAN DEFAULT false,
    "retained_iss_amount" DECIMAL(15,2) DEFAULT 0.00,
    "net_payment_amount" DECIMAL(15,2) NOT NULL,
    "calculation_date" DATE,
    "approval_date" DATE,
    "payment_date" DATE,
    "notes" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "distributor_settlements_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."journal_entries" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "status" BIGINT,
    "entry_type" BIGINT,
    "origin_type" TEXT,
    "origin_id" BIGINT,
    "entry_number" TEXT NOT NULL,
    "entry_date" DATE NOT NULL,
    "description" TEXT NOT NULL,
    "total_amount" DECIMAL(15,2) NOT NULL,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "journal_entries_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."journal_entry_items" (
    "id" BIGINT NOT NULL,
    "journal_entry_id" BIGINT NOT NULL,
    "account_id" BIGINT NOT NULL,
    "movement_type" BIGINT,
    "amount" DECIMAL(15,2) NOT NULL,
    "item_description" TEXT,

    CONSTRAINT "journal_entry_items_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."journal_entry_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "journal_entry_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."journal_entry_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "nature" VARCHAR(20),
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "journal_entry_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."monthly_income_statement" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "year" INTEGER NOT NULL,
    "month" INTEGER NOT NULL,
    "total_gross_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "sales_deductions" DECIMAL(15,2) DEFAULT 0.00,
    "net_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "cost_of_goods_sold" DECIMAL(15,2) DEFAULT 0.00,
    "distributor_payouts" DECIMAL(15,2) DEFAULT 0.00,
    "gross_profit" DECIMAL(15,2) DEFAULT 0.00,
    "administrative_expenses" DECIMAL(15,2) DEFAULT 0.00,
    "selling_expenses" DECIMAL(15,2) DEFAULT 0.00,
    "financial_expenses" DECIMAL(15,2) DEFAULT 0.00,
    "financial_income" DECIMAL(15,2) DEFAULT 0.00,
    "operational_result" DECIMAL(15,2) DEFAULT 0.00,
    "irpj_provision" DECIMAL(15,2) DEFAULT 0.00,
    "csll_provision" DECIMAL(15,2) DEFAULT 0.00,
    "net_result" DECIMAL(15,2) DEFAULT 0.00,
    "gross_margin_percent" DECIMAL(5,2),
    "net_margin_percent" DECIMAL(5,2),
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "monthly_income_statement_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."payable_transactions" (
    "id" BIGINT NOT NULL,
    "account_payable_id" BIGINT NOT NULL,
    "transaction_date" DATE NOT NULL,
    "amount" DECIMAL(15,2) NOT NULL,
    "bank_account_id" BIGINT,
    "payment_method" BIGINT,
    "notes" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "payable_transactions_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."receivable_transactions" (
    "id" BIGINT NOT NULL,
    "account_receivable_id" BIGINT NOT NULL,
    "transaction_date" DATE NOT NULL,
    "amount" DECIMAL(15,2) NOT NULL,
    "bank_account_id" BIGINT,
    "payment_method" BIGINT,
    "notes" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "receivable_transactions_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "finance"."settlement_bases" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "settlement_bases_pkey" PRIMARY KEY ("id")
);

