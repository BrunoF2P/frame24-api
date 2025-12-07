-- ============================================================================
-- Decouple Customer from Company (FK Removal)
-- ============================================================================
-- Migration: V22__decouple_customer_company.sql
-- Description: Drop foreign key constraint between crm.customers and identity.companies
--              to respect module boundaries, but keep company_id for RLS usage.
-- ============================================================================

-- Drop the foreign key constraint
ALTER TABLE "crm"."customers"
DROP CONSTRAINT "fk_customers_companies_company";
