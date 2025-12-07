-- ============================================================================
-- Schema Improvements
-- ============================================================================
-- Migration: V20__schema_improvements.sql
-- Description: Add ENUMs, NOT NULL constraints, validations and indexes
-- ============================================================================

-- ============================================================================
-- PART 1: CREATE ENUMS FOR FIXED VALUES
-- ============================================================================

-- Fiscal/Tax ENUMs (valores fixos por legislação brasileira)
CREATE TYPE tax_regime_enum AS ENUM (
    'SIMPLES_NACIONAL',
    'LUCRO_PRESUMIDO', 
    'LUCRO_REAL',
    'MEI'
);

CREATE TYPE pis_cofins_regime_enum AS ENUM (
    'CUMULATIVO',
    'NAO_CUMULATIVO',
    'ISENTO'
);

-- Auditoria ENUMs
CREATE TYPE audit_event_type_enum AS ENUM (
    'CREATE',
    'UPDATE',
    'DELETE',
    'LOGIN',
    'LOGOUT',
    'ACCESS_DENIED',
    'PERMISSION_CHANGE',
    'DATA_EXPORT',
    'CONFIGURATION_CHANGE'
);

CREATE TYPE audit_action_enum AS ENUM (
    'INSERT',
    'UPDATE',
    'DELETE',
    'SELECT',
    'EXECUTE'
);

-- Estoque ENUMs
CREATE TYPE stock_operation_type_enum AS ENUM (
    'IN',
    'OUT',
    'TRANSFER',
    'ADJUSTMENT'
);

-- Financeiro ENUMs
CREATE TYPE entry_type_enum AS ENUM (
    'DEBIT',
    'CREDIT'
);

CREATE TYPE account_nature_enum AS ENUM (
    'DEBIT',
    'CREDIT'
);

-- Identidade ENUMs
CREATE TYPE gender_enum AS ENUM (
    'M',
    'F',
    'OTHER',
    'PREFER_NOT_TO_SAY'
);

-- RH ENUMs
CREATE TYPE contract_type_enum AS ENUM (
    'CLT',
    'PJ',
    'TEMPORARY',
    'INTERN',
    'APPRENTICE',
    'AUTONOMOUS'
);

-- ============================================================================
-- PART 2: ADD NOT NULL CONSTRAINTS (Critical Fields)
-- ============================================================================

-- Identity Schema
ALTER TABLE "identity"."persons" 
    ALTER COLUMN "full_name" SET NOT NULL,
    ALTER COLUMN "birth_date" SET NOT NULL;

ALTER TABLE "identity"."companies"
    ALTER COLUMN "corporate_name" SET NOT NULL,
    ALTER COLUMN "cnpj" SET NOT NULL;

ALTER TABLE "identity"."identities"
    ALTER COLUMN "email" SET NOT NULL,
    ALTER COLUMN "password_hash" SET NOT NULL;

-- Sales Schema
ALTER TABLE "sales"."sales"
    ALTER COLUMN "sale_date" SET NOT NULL,
    ALTER COLUMN "total_amount" SET NOT NULL,
    ALTER COLUMN "net_amount" SET NOT NULL,
    ALTER COLUMN "cinema_complex_id" SET NOT NULL;

ALTER TABLE "sales"."tickets"
    ALTER COLUMN "sale_id" SET NOT NULL,
    ALTER COLUMN "showtime_id" SET NOT NULL,
    ALTER COLUMN "face_value" SET NOT NULL,
    ALTER COLUMN "total_amount" SET NOT NULL;

-- Operations Schema
ALTER TABLE "operations"."cinema_complexes"
    ALTER COLUMN "name" SET NOT NULL,
    ALTER COLUMN "code" SET NOT NULL;

ALTER TABLE "operations"."rooms"
    ALTER COLUMN "room_number" SET NOT NULL,
    ALTER COLUMN "capacity" SET NOT NULL,
    ALTER COLUMN "cinema_complex_id" SET NOT NULL;

ALTER TABLE "operations"."showtime_schedule"
    ALTER COLUMN "start_time" SET NOT NULL,
    ALTER COLUMN "end_time" SET NOT NULL,
    ALTER COLUMN "base_ticket_price" SET NOT NULL,
    ALTER COLUMN "cinema_complex_id" SET NOT NULL,
    ALTER COLUMN "room_id" SET NOT NULL,
    ALTER COLUMN "movie_id" SET NOT NULL;

ALTER TABLE "operations"."seats"
    ALTER COLUMN "room_id" SET NOT NULL,
    ALTER COLUMN "seat_code" SET NOT NULL,
    ALTER COLUMN "row_code" SET NOT NULL,
    ALTER COLUMN "column_number" SET NOT NULL;

-- Catalog Schema
ALTER TABLE "catalog"."movies"
    ALTER COLUMN "original_title" SET NOT NULL,
    ALTER COLUMN "duration_minutes" SET NOT NULL;

-- CRM Schema
ALTER TABLE "crm"."customers"
    ALTER COLUMN "full_name" SET NOT NULL;

-- Finance Schema
ALTER TABLE "finance"."accounts_payable"
    ALTER COLUMN "due_date" SET NOT NULL,
    ALTER COLUMN "original_amount" SET NOT NULL,
    ALTER COLUMN "company_id" SET NOT NULL;

ALTER TABLE "finance"."accounts_receivable"
    ALTER COLUMN "due_date" SET NOT NULL,
    ALTER COLUMN "original_amount" SET NOT NULL,
    ALTER COLUMN "company_id" SET NOT NULL;

-- ============================================================================
-- PART 3: ADD CHECK CONSTRAINTS (Validations)
-- ============================================================================

-- Monetary Values (must be positive)
ALTER TABLE "sales"."sales"
    ADD CONSTRAINT "chk_sales_total_amount_positive" 
    CHECK ("total_amount" >= 0),
    ADD CONSTRAINT "chk_sales_discount_amount_positive" 
    CHECK ("discount_amount" >= 0),
    ADD CONSTRAINT "chk_sales_net_amount_positive" 
    CHECK ("net_amount" >= 0);

ALTER TABLE "sales"."tickets"
    ADD CONSTRAINT "chk_tickets_face_value_positive" 
    CHECK ("face_value" > 0),
    ADD CONSTRAINT "chk_tickets_service_fee_positive" 
    CHECK ("service_fee" >= 0),
    ADD CONSTRAINT "chk_tickets_total_amount_positive" 
    CHECK ("total_amount" >= 0);

ALTER TABLE "finance"."accounts_payable"
    ADD CONSTRAINT "chk_payable_original_amount_positive" 
    CHECK ("original_amount" > 0),
    ADD CONSTRAINT "chk_payable_paid_amount_positive" 
    CHECK ("paid_amount" >= 0);

ALTER TABLE "finance"."accounts_receivable"
    ADD CONSTRAINT "chk_receivable_original_amount_positive" 
    CHECK ("original_amount" > 0),
    ADD CONSTRAINT "chk_receivable_received_amount_positive" 
    CHECK ("paid_amount" >= 0);

-- Dates (logical validations)
ALTER TABLE "operations"."showtime_schedule"
    ADD CONSTRAINT "chk_showtime_end_after_start" 
    CHECK ("end_time" > "start_time");

ALTER TABLE "catalog"."movies"
    ADD CONSTRAINT "chk_movie_duration_positive" 
    CHECK ("duration_minutes" > 0);

ALTER TABLE "identity"."persons"
    ADD CONSTRAINT "chk_person_birth_date_past"
    CHECK ("birth_date" < CURRENT_DATE);

-- Percentages (0-100)
ALTER TABLE "marketing"."promotional_campaigns"
    ADD CONSTRAINT "chk_campaign_discount_valid"
    CHECK ("discount_percentage" IS NULL OR ("discount_percentage" >= 0 AND "discount_percentage" <= 100));

-- Capacity (must be positive)
ALTER TABLE "operations"."rooms"
    ADD CONSTRAINT "chk_room_capacity_positive" 
    CHECK ("capacity" > 0);

-- Brazilian Documents Format
ALTER TABLE "identity"."persons"
    ADD CONSTRAINT "chk_cpf_format" 
    CHECK ("cpf" IS NULL OR "cpf" ~ '^\d{11}$');

ALTER TABLE "identity"."companies"
    ADD CONSTRAINT "chk_cnpj_format" 
    CHECK ("cnpj" ~ '^\d{14}$');

ALTER TABLE "operations"."cinema_complexes"
    ADD CONSTRAINT "chk_postal_code_format" 
    CHECK ("postal_code" IS NULL OR "postal_code" ~ '^\d{8}$');

-- Email Format
ALTER TABLE "identity"."identities"
    ADD CONSTRAINT "chk_email_format" 
    CHECK ("email" ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- ============================================================================
-- PART 4: UNIQUE CONSTRAINTS
-- ============================================================================
-- Note: Unique constraints were already created as indexes in V16__foreign_keys.sql
-- Skipping duplicate constraint definitions

-- ============================================================================
-- PART 5: ADD DEFAULT VALUES
-- ============================================================================

-- Sensible defaults
ALTER TABLE "sales"."sales"
    ALTER COLUMN "discount_amount" SET DEFAULT 0.00;

ALTER TABLE "sales"."tickets"
    ALTER COLUMN "service_fee" SET DEFAULT 0.00,
    ALTER COLUMN "used" SET DEFAULT false;

ALTER TABLE "operations"."seats"
    ALTER COLUMN "accessible" SET DEFAULT false,
    ALTER COLUMN "active" SET DEFAULT true;

ALTER TABLE "operations"."cinema_complexes"
    ALTER COLUMN "active" SET DEFAULT true;

ALTER TABLE "crm"."customers"
    ALTER COLUMN "active" SET DEFAULT true;

ALTER TABLE "finance"."accounts_payable"
    ALTER COLUMN "paid_amount" SET DEFAULT 0.00;

ALTER TABLE "finance"."accounts_receivable"
    ALTER COLUMN "paid_amount" SET DEFAULT 0.00;

-- ============================================================================
-- PART 6: ADD PERFORMANCE INDEXES
-- ============================================================================
-- Note: Basic indexes for cpf, cnpj already exist in V16__foreign_keys.sql

-- Email searches (case-insensitive)
CREATE INDEX IF NOT EXISTS "idx_identities_email_lower"
ON "identity"."identities" (LOWER("email"));

-- Sales queries by date range
CREATE INDEX IF NOT EXISTS "idx_sales_company_date_range"
ON "sales"."sales" ("company_id", "sale_date" DESC);

CREATE INDEX IF NOT EXISTS "idx_sales_customer_date"
ON "sales"."sales" ("customer_id", "sale_date" DESC)
WHERE "customer_id" IS NOT NULL;

-- Showtime queries (without time predicate since CURRENT_TIMESTAMP is not immutable)
CREATE INDEX IF NOT EXISTS "idx_showtime_complex_time"
ON "operations"."showtime_schedule" ("cinema_complex_id", "start_time");

CREATE INDEX IF NOT EXISTS "idx_showtime_movie_time"
ON "operations"."showtime_schedule" ("movie_id", "start_time");

-- Unused tickets
CREATE INDEX IF NOT EXISTS "idx_tickets_unused"
ON "sales"."tickets" ("showtime_id", "sale_id")
WHERE "used" = false;

-- Payables by status and due date (without date predicate since CURRENT_DATE is not immutable)
CREATE INDEX IF NOT EXISTS "idx_payables_status_due_date"
ON "finance"."accounts_payable" ("company_id", "status", "due_date");

-- Receivables by status and due date (without date predicate since CURRENT_DATE is not immutable)
CREATE INDEX IF NOT EXISTS "idx_receivables_status_due_date"
ON "finance"."accounts_receivable" ("company_id", "status", "due_date");

-- Audit logs by period
CREATE INDEX IF NOT EXISTS "idx_audit_company_created"
ON "audit"."audit_logs" ("company_id", "created_at" DESC);

CREATE INDEX IF NOT EXISTS "idx_audit_user_created"
ON "audit"."audit_logs" ("user_id", "created_at" DESC)
WHERE "user_id" IS NOT NULL;

-- Customer activity
CREATE INDEX IF NOT EXISTS "idx_customers_active"
ON "crm"."customers" ("company_id", "created_at" DESC)
WHERE "active" = true;

-- ============================================================================
-- PART 7: ADD COMMENTS FOR DOCUMENTATION
-- ============================================================================

COMMENT ON TYPE tax_regime_enum IS 'Brazilian tax regimes defined by law';
COMMENT ON TYPE pis_cofins_regime_enum IS 'PIS/COFINS calculation regimes';
COMMENT ON TYPE audit_event_type_enum IS 'Types of auditable events in the system';
COMMENT ON TYPE audit_action_enum IS 'Database actions for audit trail';
COMMENT ON TYPE stock_operation_type_enum IS 'Stock movement operation types';
COMMENT ON TYPE entry_type_enum IS 'Accounting entry types (debit/credit)';
COMMENT ON TYPE account_nature_enum IS 'Chart of accounts nature (debit/credit)';
COMMENT ON TYPE gender_enum IS 'Gender options for person records';
COMMENT ON TYPE contract_type_enum IS 'Brazilian employment contract types';

COMMENT ON CONSTRAINT "chk_cpf_format" ON "identity"."persons" IS 'CPF must be exactly 11 digits';
COMMENT ON CONSTRAINT "chk_cnpj_format" ON "identity"."companies" IS 'CNPJ must be exactly 14 digits';
COMMENT ON CONSTRAINT "chk_postal_code_format" ON "operations"."cinema_complexes" IS 'Brazilian postal code (CEP) must be 8 digits';
COMMENT ON CONSTRAINT "chk_email_format" ON "identity"."identities" IS 'Basic email format validation';
