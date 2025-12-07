-- ============================================================================
-- Customer Relationship Management
-- ============================================================================
-- Migration: V5__crm_schema.sql
-- Description: Customer Relationship Management
-- ============================================================================

-- Customer Relationship Management

CREATE TABLE "crm"."company_customers" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "customer_id" BIGINT NOT NULL,
    "is_active_in_loyalty" BOOLEAN DEFAULT true,
    "tenant_loyalty_number" VARCHAR(50),
    "accumulated_points" INTEGER DEFAULT 0,
    "loyalty_level" VARCHAR(20) DEFAULT 'BRONZE',
    "loyalty_join_date" TIMESTAMP(0),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "company_customers_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_favorite_combos" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "combo_id" BIGINT NOT NULL,
    "purchase_count" INTEGER DEFAULT 0,
    "last_purchase" TIMESTAMP(0),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_favorite_combos_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_favorite_genres" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "genre" VARCHAR(100) NOT NULL,
    "preference_order" INTEGER DEFAULT 0,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_favorite_genres_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_favorite_products" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "product_id" BIGINT NOT NULL,
    "purchase_count" INTEGER DEFAULT 0,
    "last_purchase" TIMESTAMP(0),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_favorite_products_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_interactions" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "interaction_type" VARCHAR(50) NOT NULL,
    "channel" VARCHAR(30),
    "description" TEXT,
    "metadata" TEXT,
    "origin_id" BIGINT,
    "origin_type" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_interactions_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_points" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "movement_type" "crm"."customer_points_movement_type" NOT NULL,
    "points" INTEGER NOT NULL,
    "previous_balance" INTEGER NOT NULL,
    "current_balance" INTEGER NOT NULL,
    "origin_type" TEXT,
    "origin_id" BIGINT,
    "description" TEXT,
    "expiration_date" DATE,
    "valid" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_points_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_preferences" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "preferred_session_type" VARCHAR(30),
    "preferred_language" VARCHAR(30),
    "preferred_position" "crm"."customer_preferences_preferred_position",
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "customer_preferences_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_preferred_rows" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "row_code" VARCHAR(5) NOT NULL,
    "usage_count" INTEGER DEFAULT 0,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_preferred_rows_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_preferred_times" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "time_slot" VARCHAR(20) NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_preferred_times_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customer_preferred_weekdays" (
    "id" BIGINT NOT NULL,
    "company_customer_id" BIGINT NOT NULL,
    "weekday" INTEGER NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "customer_preferred_weekdays_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "crm"."customers" (
    "id" BIGINT NOT NULL,
    "identity_id" BIGINT NOT NULL,
    "cpf" VARCHAR(14) NOT NULL,
    "full_name" VARCHAR(200) NOT NULL,
    "email" VARCHAR(100),
    "phone" VARCHAR(20),
    "birth_date" DATE,
    "gender" "crm"."customers_gender" DEFAULT 'NOT_INFORMED',
    "zip_code" VARCHAR(10),
    "address" TEXT,
    "city" VARCHAR(100),
    "state" CHAR(2),
    "accepts_marketing" BOOLEAN DEFAULT false,
    "accepts_sms" BOOLEAN DEFAULT false,
    "accepts_email" BOOLEAN DEFAULT true,
    "terms_accepted" BOOLEAN DEFAULT false,
    "terms_acceptance_date" TIMESTAMP(0),
    "data_anonymized" BOOLEAN DEFAULT false,
    "anonymization_date" TIMESTAMP(0),
    "acceptance_ip" VARCHAR(45),
    "anonymization_requested" BOOLEAN DEFAULT false,
    "collection_purposes" TEXT,
    "subject_aware_rights" BOOLEAN DEFAULT false,
    "rights_awareness_date" TIMESTAMP(0),
    "active" BOOLEAN DEFAULT true,
    "blocked" BOOLEAN DEFAULT false,
    "block_reason" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),
    "registration_source" VARCHAR(50),
    "registration_responsible" TEXT,

    CONSTRAINT "customers_pkey" PRIMARY KEY ("id")
);

