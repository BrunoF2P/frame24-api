-- ============================================================================
-- Inventory Management
-- ============================================================================
-- Migration: V9__inventory_schema.sql
-- Description: Inventory Management
-- ============================================================================

-- Inventory Management

CREATE TABLE "inventory"."supplier_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "supplier_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "inventory"."suppliers" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "corporate_name" VARCHAR(200) NOT NULL,
    "trade_name" VARCHAR(200),
    "cnpj" VARCHAR(18),
    "phone" VARCHAR(20),
    "email" VARCHAR(100),
    "address" TEXT,
    "contact_name" VARCHAR(200),
    "contact_phone" VARCHAR(20),
    "delivery_days" INTEGER DEFAULT 7,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),
    "is_film_distributor" BOOLEAN DEFAULT false,
    "supplier_type_id" BIGINT,

    CONSTRAINT "suppliers_pkey" PRIMARY KEY ("id")
);

