-- ============================================================================
-- Product Catalog
-- ============================================================================
-- Migration: V8__catalog_schema.sql
-- Description: Product Catalog
-- ============================================================================

-- Product Catalog

CREATE TABLE "catalog"."age_ratings" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "code" VARCHAR(5) NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "minimum_age" INTEGER,
    "description" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "age_ratings_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."cast_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "cast_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."combo_products" (
    "id" BIGINT NOT NULL,
    "combo_id" BIGINT NOT NULL,
    "product_id" BIGINT NOT NULL,
    "quantity" INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT "combo_products_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."combos" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "combo_code" VARCHAR(50) NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "description" TEXT,
    "sale_price" DECIMAL(10,2) NOT NULL,
    "promotional_price" DECIMAL(10,2),
    "promotion_start_date" DATE,
    "promotion_end_date" DATE,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "combos_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."media_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "media_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."movie_cast" (
    "id" BIGINT NOT NULL,
    "movie_id" BIGINT NOT NULL,
    "cast_type" BIGINT NOT NULL,
    "artist_name" VARCHAR(200) NOT NULL,
    "character_name" VARCHAR(200),
    "credit_order" INTEGER DEFAULT 0,
    "photo_url" VARCHAR(500),
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "movie_cast_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."movie_categories" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "minimum_age" INTEGER DEFAULT 0,
    "slug" VARCHAR(100) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "movie_categories_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."movie_media" (
    "id" BIGINT NOT NULL,
    "movie_id" BIGINT NOT NULL,
    "media_type" BIGINT NOT NULL,
    "media_url" VARCHAR(500) NOT NULL,
    "description" TEXT,
    "width" INTEGER,
    "height" INTEGER,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "title" VARCHAR(200),

    CONSTRAINT "movie_media_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."movies" (
    "id" BIGINT NOT NULL,
    "distributor_id" BIGINT NOT NULL,
    "original_title" VARCHAR(300) NOT NULL,
    "brazil_title" VARCHAR(300),
    "ancine_number" VARCHAR(50),
    "duration_minutes" INTEGER NOT NULL,
    "country_of_origin" VARCHAR(50),
    "production_year" INTEGER,
    "national" BOOLEAN NOT NULL DEFAULT false,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "synopsis" TEXT,
    "short_synopsis" VARCHAR(500),
    "budget" DECIMAL(15,2),
    "website" VARCHAR(200),
    "tmdb_id" VARCHAR(50),
    "imdb_id" VARCHAR(20),
    "tags_json" TEXT,
    "worldwide_release_date" DATE,
    "original_language" VARCHAR(50),
    "slug" VARCHAR(200),
    "company_id" BIGINT NOT NULL,
    "age_rating_id" BIGINT,
    "movie_categoriesId" TEXT,

    CONSTRAINT "movies_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."movies_on_categories" (
    "movie_id" BIGINT NOT NULL,
    "category_id" BIGINT NOT NULL,
    "age_ratingsId" TEXT,

    CONSTRAINT "movies_on_categories_pkey" PRIMARY KEY ("movie_id","category_id")
);

CREATE TABLE "catalog"."product_categories" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,

    CONSTRAINT "product_categories_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."product_prices" (
    "id" BIGINT NOT NULL,
    "product_id" BIGINT NOT NULL,
    "complex_id" BIGINT,
    "company_id" BIGINT NOT NULL,
    "sale_price" DECIMAL(10,2) NOT NULL,
    "cost_price" DECIMAL(10,2) NOT NULL,
    "valid_from" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "valid_to" TIMESTAMP(3),
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "product_prices_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "catalog"."products" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "category_id" BIGINT NOT NULL,
    "product_code" VARCHAR(50) NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "description" TEXT,
    "ncm_code" VARCHAR(10),
    "unit" VARCHAR(10) DEFAULT 'UN',
    "minimum_stock" INTEGER DEFAULT 0,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "barcode" VARCHAR(50),
    "image_url" VARCHAR(500),
    "supplier_id" BIGINT,
    "is_available_online" BOOLEAN DEFAULT false,

    CONSTRAINT "products_pkey" PRIMARY KEY ("id")
);

