-- ============================================================================
-- Operations Management
-- ============================================================================
-- Migration: V7__operations_schema.sql
-- Description: Operations Management
-- ============================================================================

-- Operations Management

-- Create ENUM for seat status
CREATE TYPE seat_status_enum AS ENUM ('AVAILABLE', 'RESERVED', 'SOLD', 'BLOCKED');

CREATE TABLE "operations"."audio_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "additional_value" DECIMAL(10,2) DEFAULT 0.00,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "audio_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."cinema_complexes" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "code" VARCHAR(50) NOT NULL,
    "cnpj" VARCHAR(18),
    "address" TEXT,
    "city" VARCHAR(100),
    "state" CHAR(2),
    "postal_code" VARCHAR(10),
    "ibge_municipality_code" VARCHAR(7) NOT NULL,
    "ancine_registry" VARCHAR(50),
    "opening_date" DATE,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "cinema_complexes_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."courtesy_parameters" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "courtesy_taxation_percentage" DECIMAL(5,2) DEFAULT 0.00,
    "monthly_courtesy_limit" INTEGER DEFAULT 1000,
    "validity_start" DATE NOT NULL,
    "validity_end" DATE,

    CONSTRAINT "courtesy_parameters_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."projection_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "additional_value" DECIMAL(10,2) DEFAULT 0.00,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "projection_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."rooms" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "room_number" VARCHAR(10) NOT NULL,
    "name" VARCHAR(100),
    "capacity" INTEGER NOT NULL,
    "projection_type" BIGINT,
    "audio_type" BIGINT,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "seat_layout" TEXT,
    "total_rows" INTEGER,
    "total_columns" INTEGER,
    "room_design" VARCHAR(30),
    "layout_image" VARCHAR(255),

    CONSTRAINT "rooms_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."seat_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "is_default" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "seat_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."seat_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "additional_value" DECIMAL(10,2) DEFAULT 0.00,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "seat_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."seats" (
    "id" BIGINT NOT NULL,
    "room_id" BIGINT NOT NULL,
    "seat_type" BIGINT,
    "seat_code" VARCHAR(10) NOT NULL,
    "row_code" VARCHAR(5) NOT NULL,
    "column_number" INTEGER NOT NULL,
    "position_x" INTEGER,
    "position_y" INTEGER,
    "accessible" BOOLEAN DEFAULT false,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "seats_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."session_languages" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "abbreviation" VARCHAR(10),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "session_languages_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."session_seat_status" (
    "id" BIGINT NOT NULL,
    "showtime_id" BIGINT NOT NULL,
    "seat_id" BIGINT NOT NULL,
    "status" seat_status_enum NOT NULL DEFAULT 'AVAILABLE',
    "sale_id" BIGINT,
    "reservation_uuid" VARCHAR(100),
    "reservation_date" TIMESTAMP(0),
    "expiration_date" TIMESTAMP(0),
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "session_seat_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."session_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "session_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "operations"."showtime_schedule" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "room_id" BIGINT NOT NULL,
    "movie_id" BIGINT NOT NULL,
    "audio_type" BIGINT,
    "projection_type" BIGINT,
    "session_language" BIGINT,
    "status" BIGINT,
    "available_seats" INTEGER DEFAULT 0,
    "sold_seats" INTEGER DEFAULT 0,
    "blocked_seats" INTEGER DEFAULT 0,
    "base_ticket_price" DECIMAL(10,2) NOT NULL,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "start_time" TIMESTAMP(3) NOT NULL,
    "end_time" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "showtime_schedule_pkey" PRIMARY KEY ("id")
);

