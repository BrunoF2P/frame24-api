-- ============================================================================
-- Adjust CompanyUser for Registration Flow
-- ============================================================================
-- Migration: V25__adjust_company_user_for_registration.sql
-- Description: Make employee_id nullable in company_users to allow admin 
--              registration without requiring HR employee record first.
-- ============================================================================

-- Allow company_users without employee_id (for initial admin registration)
ALTER TABLE "identity"."company_users" 
    ALTER COLUMN "employee_id" DROP NOT NULL;

-- Add comment explaining the design decision
COMMENT ON COLUMN "identity"."company_users"."employee_id" IS 
    'Optional reference to HR employee. NULL for external users or admins without formal employment.';
