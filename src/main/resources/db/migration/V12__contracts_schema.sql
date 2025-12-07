-- ============================================================================
-- Contract Management
-- ============================================================================
-- Migration: V12__contracts_schema.sql
-- Description: Contract Management
-- ============================================================================

-- Contract Management

CREATE TABLE "contracts"."contract_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "contract_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "contracts"."exhibition_contract_sliding_scales" (
    "id" BIGINT NOT NULL,
    "contract_id" BIGINT NOT NULL,
    "week_number" INTEGER NOT NULL,
    "distributor_percentage" DECIMAL(5,2) NOT NULL,
    "exhibitor_percentage" DECIMAL(5,2) NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "exhibition_contract_sliding_scales_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "contracts"."exhibition_contracts" (
    "id" BIGINT NOT NULL,
    "movie_id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "distributor_id" BIGINT NOT NULL,
    "contract_type" BIGINT,
    "revenue_base" TEXT,
    "contract_number" VARCHAR(50),
    "start_date" DATE NOT NULL,
    "end_date" DATE NOT NULL,
    "distributor_percentage" DECIMAL(5,2) NOT NULL,
    "exhibitor_percentage" DECIMAL(5,2) NOT NULL,
    "guaranteed_minimum" DECIMAL(15,2) DEFAULT 0.00,
    "minimum_guarantee" DECIMAL(15,2) DEFAULT 0.00,
    "contract_terms" TEXT,
    "notes" TEXT,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "exhibition_contracts_pkey" PRIMARY KEY ("id")
);

