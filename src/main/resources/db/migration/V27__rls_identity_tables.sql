-- ============================================================================
-- Row Level Security for Identity Tables
-- ============================================================================
-- Migration: V27__rls_identity_tables.sql
-- Description: Add RLS policies for companies, identities, and persons tables
-- ============================================================================

-- ============================================================================
-- PART 1: RLS for identity.companies
-- ============================================================================

-- Enable RLS on companies table
ALTER TABLE "identity"."companies" ENABLE ROW LEVEL SECURITY;

-- Policy: During registration (SYSTEM), allow ALL operations
-- After registration, employees see only their company
CREATE POLICY companies_access_policy ON "identity"."companies"
FOR ALL
USING (
    -- During unauthenticated registration (user_type = SYSTEM), allow access
    (current_user_type() = 'SYSTEM')
    OR
    -- Authenticated employees see only their company
    (is_employee() AND id = current_company_id())
);

COMMENT ON POLICY companies_access_policy ON "identity"."companies" 
IS 'Employees see only their company. During registration (SYSTEM), allows all operations.';


-- ============================================================================
-- PART 2: RLS for identity.identities
-- ============================================================================

-- Enable RLS on identities table
ALTER TABLE "identity"."identities" ENABLE ROW LEVEL SECURITY;

-- Policy: Employees see identities from their company
-- Customers see only their own identity
CREATE POLICY identities_access_policy ON "identity"."identities"
FOR ALL
USING (
    -- During unauthenticated registration/operations, allow access
    (current_user_type() = 'SYSTEM')
    OR
    -- Employees see identities via company_users
    (is_employee() AND id IN (
        SELECT identity_id FROM "identity"."company_users"
        WHERE company_id = current_company_id()
    ))
    OR
    -- Customers see identities via crm.customers
    (is_customer() AND id IN (
        SELECT identity_id FROM "crm"."customers"  
        WHERE id = current_customer_id()
    ))
);

COMMENT ON POLICY identities_access_policy ON "identity"."identities" 
IS 'Employees see identities from their company via company_users. Customers see only their own identity. SYSTEM mode allows registration.';


-- ============================================================================
-- PART 3: RLS for identity.persons
-- ============================================================================

-- Enable RLS on persons table
ALTER TABLE "identity"."persons" ENABLE ROW LEVEL SECURITY;

-- Policy: Employees see persons via identities from their company
-- Customers see only their own person
CREATE POLICY persons_access_policy ON "identity"."persons"
FOR ALL
USING (
    -- During unauthenticated registration/operations, allow access
    (current_user_type() = 'SYSTEM')
    OR
    -- Employees see persons via identities->company_users
    (is_employee() AND id IN (
        SELECT i.person_id 
        FROM "identity"."identities" i
        JOIN "identity"."company_users" cu ON cu.identity_id = i.id
        WHERE cu.company_id = current_company_id()
    ))
    OR
    -- Customers see persons via identities->customers
    (is_customer() AND id IN (
        SELECT i.person_id 
        FROM "identity"."identities" i
        JOIN "crm"."customers" c ON c.identity_id = i.id
        WHERE c.id = current_customer_id()
    ))
);

COMMENT ON POLICY persons_access_policy ON "identity"."persons" 
IS 'Employees see persons from their company via identities. Customers see only their own person. SYSTEM mode allows registration.';


-- ============================================================================
-- VERIFICATION
-- ============================================================================

-- Verify that RLS is enabled on all three tables
DO $$
BEGIN
    IF NOT (SELECT relrowsecurity FROM pg_class WHERE relname = 'companies' AND relnamespace = 'identity'::regnamespace) THEN
        RAISE EXCEPTION 'RLS not enabled on identity.companies';
    END IF;
    
    IF NOT (SELECT relrowsecurity FROM pg_class WHERE relname = 'identities' AND relnamespace = 'identity'::regnamespace) THEN
        RAISE EXCEPTION 'RLS not enabled on identity.identities';
    END IF;
    
    IF NOT (SELECT relrowsecurity FROM pg_class WHERE relname = 'persons' AND relnamespace = 'identity'::regnamespace) THEN
        RAISE EXCEPTION 'RLS not enabled on identity.persons';
    END IF;
    
    RAISE NOTICE 'RLS successfully enabled on all identity tables';
END $$;
