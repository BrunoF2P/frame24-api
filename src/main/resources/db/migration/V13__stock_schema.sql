-- ============================================================================
-- Stock Control
-- ============================================================================
-- Migration: V13__stock_schema.sql
-- Description: Stock Control
-- ============================================================================

-- Stock Control

CREATE TABLE "stock"."product_stock" (
    "id" BIGINT NOT NULL,
    "product_id" BIGINT NOT NULL,
    "complex_id" BIGINT NOT NULL,
    "current_quantity" INTEGER DEFAULT 0,
    "minimum_quantity" INTEGER DEFAULT 10,
    "maximum_quantity" INTEGER DEFAULT 100,
    "location" VARCHAR(100),
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "product_stock_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "stock"."stock_movement_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "affects_stock" BOOLEAN DEFAULT true,
    "operation_type" VARCHAR(10),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "stock_movement_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "stock"."stock_movements" (
    "id" BIGINT NOT NULL,
    "product_id" BIGINT NOT NULL,
    "complex_id" BIGINT NOT NULL,
    "user_id" BIGINT,
    "movement_type" BIGINT NOT NULL,
    "quantity" INTEGER NOT NULL,
    "previous_quantity" INTEGER NOT NULL,
    "current_quantity" INTEGER NOT NULL,
    "origin_type" TEXT,
    "origin_id" BIGINT,
    "unit_value" DECIMAL(10,2),
    "total_value" DECIMAL(15,2),
    "observations" TEXT,
    "movement_date" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "stock_movements_pkey" PRIMARY KEY ("id")
);

