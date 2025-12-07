-- ============================================================================
-- Tax Management
-- ============================================================================
-- Migration: V14__tax_schema.sql
-- Description: Tax Management
-- ============================================================================

-- Tax Management

CREATE TABLE "tax"."credit_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "credit_percentage" DECIMAL(5,2) DEFAULT 100.00,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "credit_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."federal_tax_rates" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "tax_regime" TEXT,
    "pis_cofins_regime" TEXT,
    "revenue_type" BIGINT,
    "pis_rate" DECIMAL(5,2) NOT NULL,
    "cofins_rate" DECIMAL(5,2) NOT NULL,
    "credit_allowed" BOOLEAN DEFAULT false,
    "irpj_base_rate" DECIMAL(5,2),
    "irpj_additional_rate" DECIMAL(5,2),
    "irpj_additional_limit" DECIMAL(15,2),
    "csll_rate" DECIMAL(5,2),
    "presumed_profit_percentage" DECIMAL(5,2),
    "validity_start" DATE NOT NULL,
    "validity_end" DATE,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "federal_tax_rates_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."iss_withholdings" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "service_received_id" BIGINT,
    "service_description" VARCHAR(200),
    "withholding_rate" DECIMAL(5,2) NOT NULL,
    "withholding_amount" DECIMAL(15,2) NOT NULL,
    "service_code" VARCHAR(10),
    "withholding_date" DATE NOT NULL,
    "notes" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "iss_withholdings_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."monthly_tax_settlement" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "status" BIGINT,
    "year" INTEGER NOT NULL,
    "month" INTEGER NOT NULL,
    "settlement_date" DATE NOT NULL,
    "tax_regime" TEXT,
    "pis_cofins_regime" TEXT,
    "gross_box_office_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "gross_concession_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "gross_advertising_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "gross_other_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "total_gross_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "total_deductions" DECIMAL(15,2) DEFAULT 0.00,
    "calculation_base_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "total_iss_box_office" DECIMAL(15,2) DEFAULT 0.00,
    "total_iss_concession" DECIMAL(15,2) DEFAULT 0.00,
    "total_iss" DECIMAL(15,2) DEFAULT 0.00,
    "total_pis_debit" DECIMAL(15,2) DEFAULT 0.00,
    "total_pis_credit" DECIMAL(15,2) DEFAULT 0.00,
    "total_pis_payable" DECIMAL(15,2) DEFAULT 0.00,
    "total_cofins_debit" DECIMAL(15,2) DEFAULT 0.00,
    "total_cofins_credit" DECIMAL(15,2) DEFAULT 0.00,
    "total_cofins_payable" DECIMAL(15,2) DEFAULT 0.00,
    "irpj_base" DECIMAL(15,2) DEFAULT 0.00,
    "irpj_base_15" DECIMAL(15,2) DEFAULT 0.00,
    "irpj_additional_10" DECIMAL(15,2) DEFAULT 0.00,
    "total_irpj" DECIMAL(15,2) DEFAULT 0.00,
    "csll_base" DECIMAL(15,2) DEFAULT 0.00,
    "total_csll" DECIMAL(15,2) DEFAULT 0.00,
    "gross_revenue_12m" DECIMAL(15,2),
    "effective_simples_rate" DECIMAL(5,2),
    "total_simples_amount" DECIMAL(15,2),
    "total_distributor_payment" DECIMAL(15,2) DEFAULT 0.00,
    "net_revenue_taxed" DECIMAL(15,2) DEFAULT 0.00,
    "net_total_revenue" DECIMAL(15,2) DEFAULT 0.00,
    "declaration_date" DATE,
    "payment_date" DATE,
    "notes" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "monthly_tax_settlement_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."municipal_tax_parameters" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "ibge_municipality_code" VARCHAR(7) NOT NULL,
    "municipality_name" VARCHAR(100) NOT NULL,
    "state" CHAR(2) NOT NULL,
    "iss_rate" DECIMAL(5,2) NOT NULL,
    "iss_service_code" VARCHAR(10),
    "iss_concession_applicable" BOOLEAN DEFAULT false,
    "iss_concession_service_code" VARCHAR(10),
    "iss_withholding" BOOLEAN DEFAULT false,
    "validity_start" DATE NOT NULL,
    "validity_end" DATE,
    "notes" TEXT,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "municipal_tax_parameters_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."pis_cofins_credits" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "credit_type" BIGINT,
    "description" TEXT NOT NULL,
    "fiscal_document" VARCHAR(100),
    "document_date" DATE NOT NULL,
    "competence_date" DATE NOT NULL,
    "base_amount" DECIMAL(15,2) NOT NULL,
    "pis_credit_rate" DECIMAL(5,2) NOT NULL,
    "pis_credit_amount" DECIMAL(15,2) NOT NULL,
    "cofins_credit_rate" DECIMAL(5,2) NOT NULL,
    "cofins_credit_amount" DECIMAL(15,2) NOT NULL,
    "processed" BOOLEAN DEFAULT false,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "pis_cofins_credits_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."revenue_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "applies_iss" BOOLEAN DEFAULT true,
    "applies_pis_cofins" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "revenue_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."settlement_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "settlement_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."simple_national_brackets" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "annex" VARCHAR(10) NOT NULL,
    "bracket" INTEGER NOT NULL,
    "gross_revenue_12m_from" DECIMAL(15,2) NOT NULL,
    "gross_revenue_12m_to" DECIMAL(15,2) NOT NULL,
    "nominal_rate" DECIMAL(5,2) NOT NULL,
    "irpj_percentage" DECIMAL(5,2),
    "csll_percentage" DECIMAL(5,2),
    "cofins_percentage" DECIMAL(5,2),
    "pis_percentage" DECIMAL(5,2),
    "cpp_percentage" DECIMAL(5,2),
    "iss_percentage" DECIMAL(5,2),
    "validity_start" DATE NOT NULL,
    "validity_end" DATE,
    "active" BOOLEAN DEFAULT true,

    CONSTRAINT "simple_national_brackets_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."state_icms_parameters" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "state" CHAR(2) NOT NULL,
    "icms_rate" DECIMAL(5,2),
    "mva_percentage" DECIMAL(5,2),
    "tax_substitution_applicable" BOOLEAN DEFAULT false,
    "validity_start" DATE NOT NULL,
    "validity_end" DATE,
    "active" BOOLEAN DEFAULT true,

    CONSTRAINT "state_icms_parameters_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."tax_compensations" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "tax_type" TEXT,
    "credit_amount" DECIMAL(15,2) NOT NULL,
    "compensated_amount" DECIMAL(15,2) DEFAULT 0.00,
    "credit_balance" DECIMAL(15,2) NOT NULL,
    "credit_competence_date" DATE NOT NULL,
    "usage_date" DATE,
    "notes" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "tax_compensations_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."tax_entries" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "source_type" TEXT,
    "source_id" BIGINT,
    "pis_cofins_regime" TEXT,
    "processing_user_id" BIGINT,
    "competence_date" DATE NOT NULL,
    "entry_date" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "gross_amount" DECIMAL(15,2) NOT NULL,
    "deductions_amount" DECIMAL(15,2) DEFAULT 0.00,
    "calculation_base" DECIMAL(15,2) NOT NULL,
    "apply_iss" BOOLEAN DEFAULT true,
    "iss_rate" DECIMAL(5,2) DEFAULT 0.00,
    "iss_amount" DECIMAL(15,2) DEFAULT 0.00,
    "ibge_municipality_code" VARCHAR(7),
    "iss_service_code" VARCHAR(10),
    "withheld_at_source" BOOLEAN DEFAULT false,
    "snapshot_rates" TEXT,
    "pis_rate" DECIMAL(5,2) NOT NULL,
    "pis_debit_amount" DECIMAL(15,2) NOT NULL,
    "pis_credit_amount" DECIMAL(15,2) DEFAULT 0.00,
    "pis_amount_payable" DECIMAL(15,2) NOT NULL,
    "cofins_rate" DECIMAL(5,2) NOT NULL,
    "cofins_debit_amount" DECIMAL(15,2) NOT NULL,
    "cofins_credit_amount" DECIMAL(15,2) DEFAULT 0.00,
    "cofins_amount_payable" DECIMAL(15,2) NOT NULL,
    "irpj_csll_base" DECIMAL(15,2),
    "presumed_percentage" DECIMAL(5,2),
    "processed" BOOLEAN DEFAULT false,
    "processing_date" TIMESTAMP(0),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "tax_entries_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "tax"."tax_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "jurisdiction" VARCHAR(20),
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "tax_types_pkey" PRIMARY KEY ("id")
);

