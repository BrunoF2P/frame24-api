-- ============================================================================
-- Sales Management
-- ============================================================================
-- Migration: V6__sales_schema.sql
-- Description: Sales Management
-- ============================================================================

-- Sales Management

-- Create ENUM for sale status
CREATE TYPE sale_status_enum AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED', 'REFUNDED');

CREATE TABLE "sales"."concession_sale_items" (
    "id" BIGINT NOT NULL,
    "concession_sale_id" BIGINT NOT NULL,
    "item_type" "sales"."concession_item_type" NOT NULL,
    "item_id" BIGINT NOT NULL,
    "quantity" INTEGER NOT NULL,
    "unit_price" DECIMAL(10,2) NOT NULL,
    "total_price" DECIMAL(10,2) NOT NULL,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "concession_sale_items_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."concession_sales" (
    "id" BIGINT NOT NULL,
    "sale_id" BIGINT NOT NULL,
    "status" BIGINT,
    "sale_date" TIMESTAMP(0) NOT NULL,
    "total_amount" DECIMAL(10,2) NOT NULL,
    "discount_amount" DECIMAL(10,2) DEFAULT 0.00,
    "net_amount" DECIMAL(10,2) NOT NULL,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "concession_sales_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."concession_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "concession_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."payment_methods" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "operator_fee" DECIMAL(5,2) DEFAULT 0.00,
    "settlement_days" INTEGER DEFAULT 0,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "payment_methods_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."promotions_used" (
    "id" BIGINT NOT NULL,
    "sale_id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "coupon_id" BIGINT,
    "customer_id" BIGINT,
    "promotion_type_code" VARCHAR(50),
    "discount_applied" DECIMAL(10,2) NOT NULL,
    "original_value" DECIMAL(10,2) NOT NULL,
    "final_value" DECIMAL(10,2) NOT NULL,
    "points_earned" INTEGER DEFAULT 0,
    "usage_date" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "promotions_used_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."sale_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "sale_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."sale_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "convenience_fee" DECIMAL(5,2) DEFAULT 0.00,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "sale_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."sales" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "user_id" BIGINT,
    "customer_id" BIGINT,
    "sale_type" BIGINT,
    "payment_method" BIGINT,
    "status" sale_status_enum DEFAULT 'PENDING',
    "sale_number" VARCHAR(50) NOT NULL,
    "sale_date" TIMESTAMP(0) NOT NULL,
    "total_amount" DECIMAL(10,2) NOT NULL,
    "discount_amount" DECIMAL(10,2) DEFAULT 0.00,
    "net_amount" DECIMAL(10,2) NOT NULL,
    "cancellation_date" TIMESTAMP(0),
    "cancellation_reason" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "sales_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."ticket_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "discount_percentage" DECIMAL(5,2) DEFAULT 0.00,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "ticket_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "sales"."tickets" (
    "id" BIGINT NOT NULL,
    "sale_id" BIGINT NOT NULL,
    "showtime_id" BIGINT NOT NULL,
    "seat_id" BIGINT,
    "ticket_type" BIGINT,
    "ticket_number" TEXT NOT NULL,
    "seat" TEXT,
    "face_value" DECIMAL(10,2) NOT NULL,
    "service_fee" DECIMAL(10,2) DEFAULT 0.00,
    "total_amount" DECIMAL(10,2) NOT NULL,
    "used" BOOLEAN DEFAULT false,
    "usage_date" TIMESTAMP(0),
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "tickets_pkey" PRIMARY KEY ("id")
);

