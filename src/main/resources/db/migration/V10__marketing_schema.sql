-- ============================================================================
-- Marketing Management
-- ============================================================================
-- Migration: V10__marketing_schema.sql
-- Description: Marketing Management
-- ============================================================================

-- Marketing Management

CREATE TABLE "marketing"."campaign_categories" (
    "id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "category_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "campaign_categories_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."campaign_complexes" (
    "id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "complex_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "campaign_complexes_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."campaign_movies" (
    "id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "movie_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "campaign_movies_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."campaign_rooms" (
    "id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "room_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "campaign_rooms_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."campaign_session_types" (
    "id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "projection_type_id" BIGINT NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "campaign_session_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."campaign_weekdays" (
    "id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "weekday" INTEGER NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "campaign_weekdays_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."promotion_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "code" VARCHAR(30) NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "promotion_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."promotional_campaigns" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "promotion_type_id" BIGINT NOT NULL,
    "campaign_code" TEXT NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "description" TEXT,
    "start_date" TIMESTAMP(0) NOT NULL,
    "end_date" TIMESTAMP(0) NOT NULL,
    "start_time" TIME(0),
    "end_time" TIME(0),
    "min_age" INTEGER,
    "max_age" INTEGER,
    "min_loyalty_level" VARCHAR(20),
    "new_customers_only" BOOLEAN DEFAULT false,
    "discount_value" DECIMAL(10,2),
    "discount_percentage" DECIMAL(5,2),
    "buy_quantity" INTEGER,
    "get_quantity" INTEGER,
    "fixed_price" DECIMAL(10,2),
    "points_multiplier" DECIMAL(5,2) DEFAULT 1.00,
    "max_total_uses" INTEGER,
    "used_count" INTEGER DEFAULT 0,
    "max_uses_per_customer" INTEGER,
    "min_purchase_value" DECIMAL(10,2),
    "combinable" BOOLEAN DEFAULT false,
    "priority" INTEGER DEFAULT 0,
    "active" BOOLEAN DEFAULT true,
    "requires_coupon" BOOLEAN DEFAULT false,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "promotional_campaigns_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "marketing"."promotional_coupons" (
    "id" BIGINT NOT NULL,
    "campaign_id" BIGINT NOT NULL,
    "customer_id" BIGINT,
    "coupon_code" TEXT NOT NULL,
    "start_date" TIMESTAMP(0) NOT NULL,
    "end_date" TIMESTAMP(0) NOT NULL,
    "max_uses" INTEGER DEFAULT 1,
    "used_count" INTEGER DEFAULT 0,
    "active" BOOLEAN DEFAULT true,
    "used" BOOLEAN DEFAULT false,
    "first_use_date" TIMESTAMP(0),
    "last_use_date" TIMESTAMP(0),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "promotional_coupons_pkey" PRIMARY KEY ("id")
);

