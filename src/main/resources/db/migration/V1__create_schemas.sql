-- ============================================================================
-- Create all database schemas and enum types
-- ============================================================================
-- Migration: V1__create_schemas.sql
-- Description: Create all database schemas and enum types
-- ============================================================================

-- Create all database schemas
CREATE SCHEMA IF NOT EXISTS "audit";
CREATE SCHEMA IF NOT EXISTS "catalog";
CREATE SCHEMA IF NOT EXISTS "contracts";
CREATE SCHEMA IF NOT EXISTS "crm";
CREATE SCHEMA IF NOT EXISTS "finance";
CREATE SCHEMA IF NOT EXISTS "hr";
CREATE SCHEMA IF NOT EXISTS "identity";
CREATE SCHEMA IF NOT EXISTS "inventory";
CREATE SCHEMA IF NOT EXISTS "marketing";
CREATE SCHEMA IF NOT EXISTS "operations";
CREATE SCHEMA IF NOT EXISTS "projects";
CREATE SCHEMA IF NOT EXISTS "sales";
CREATE SCHEMA IF NOT EXISTS "stock";
CREATE SCHEMA IF NOT EXISTS "tax";


-- Create custom enum types
CREATE TYPE "crm"."customer_points_movement_type" AS ENUM ('EARNED', 'SPENT', 'ADJUSTMENT', 'EXPIRATION');

CREATE TYPE "crm"."customer_preferences_preferred_position" AS ENUM ('FRONT', 'MIDDLE', 'BACK');

CREATE TYPE "crm"."customers_gender" AS ENUM ('NOT_INFORMED', 'MALE', 'FEMALE', 'OTHER');

CREATE TYPE "identity"."company_plan_type" AS ENUM ('BASIC', 'PREMIUM', 'ENTERPRISE');

CREATE TYPE "identity"."identity_type" AS ENUM ('CUSTOMER', 'EMPLOYEE', 'SYSTEM');

CREATE TYPE "identity"."session_context" AS ENUM ('EMPLOYEE', 'CUSTOMER', 'SYSTEM');

CREATE TYPE "identity"."tax_regime_type" AS ENUM ('SIMPLES_NACIONAL', 'LUCRO_PRESUMIDO', 'LUCRO_REAL');

CREATE TYPE "sales"."concession_item_type" AS ENUM ('PRODUCT', 'COMBO');

