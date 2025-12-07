-- ============================================================================
-- Fix RLS Policy Function Qualifiers
-- ============================================================================
-- Migration: V24__fix_rls_function_qualifiers.sql
-- Description: Recreate all RLS policies with properly qualified function 
--              calls (public.is_employee(), public.current_company_id(), etc.)
--              to avoid 'Unknown database function' errors.
-- ============================================================================

-- ============================================================================
-- PART 1: DROP EXISTING POLICIES
-- ============================================================================

-- CRM Schema
DROP POLICY IF EXISTS customers_access_policy ON "crm"."customers";
DROP POLICY IF EXISTS company_customers_access_policy ON "crm"."company_customers";
DROP POLICY IF EXISTS customer_points_access_policy ON "crm"."customer_points";
DROP POLICY IF EXISTS customer_preferences_access_policy ON "crm"."customer_preferences";

-- Sales Schema
DROP POLICY IF EXISTS sales_access_policy ON "sales"."sales";
DROP POLICY IF EXISTS tickets_access_policy ON "sales"."tickets";
DROP POLICY IF EXISTS concession_sales_access_policy ON "sales"."concession_sales";

-- Operations Schema
DROP POLICY IF EXISTS cinema_complexes_access_policy ON "operations"."cinema_complexes";
DROP POLICY IF EXISTS rooms_access_policy ON "operations"."rooms";
DROP POLICY IF EXISTS showtime_schedule_read_policy ON "operations"."showtime_schedule";
DROP POLICY IF EXISTS showtime_schedule_write_policy ON "operations"."showtime_schedule";
DROP POLICY IF EXISTS showtime_schedule_update_policy ON "operations"."showtime_schedule";
DROP POLICY IF EXISTS showtime_schedule_delete_policy ON "operations"."showtime_schedule";

-- HR Schema
DROP POLICY IF EXISTS employees_access_policy ON "hr"."employees";
DROP POLICY IF EXISTS departments_access_policy ON "hr"."departments";

-- Finance Schema
DROP POLICY IF EXISTS bank_accounts_access_policy ON "finance"."bank_accounts";

-- Catalog Schema
DROP POLICY IF EXISTS products_access_policy ON "catalog"."products";
DROP POLICY IF EXISTS movies_read_policy ON "catalog"."movies";
DROP POLICY IF EXISTS movies_insert_policy ON "catalog"."movies";
DROP POLICY IF EXISTS movies_update_policy ON "catalog"."movies";
DROP POLICY IF EXISTS movies_delete_policy ON "catalog"."movies";

-- Identity Schema
DROP POLICY IF EXISTS company_users_access_policy ON "identity"."company_users";
DROP POLICY IF EXISTS custom_roles_access_policy ON "identity"."custom_roles";
DROP POLICY IF EXISTS permissions_access_policy ON "identity"."permissions";

-- Audit Schema
DROP POLICY IF EXISTS audit_logs_access_policy ON "audit"."audit_logs";


-- ============================================================================
-- PART 2: RECREATE POLICIES WITH QUALIFIED FUNCTION CALLS
-- ============================================================================

-- ============================================================================
-- CRM SCHEMA POLICIES
-- ============================================================================

CREATE POLICY customers_access_policy ON "crm"."customers"
FOR ALL
USING (
    (public.is_employee() AND company_id = public.current_company_id())
    OR
    (public.is_customer() AND id = public.current_customer_id())
);

CREATE POLICY company_customers_access_policy ON "crm"."company_customers"
FOR ALL
USING (
    (public.is_employee() AND company_id = public.current_company_id())
    OR
    (public.is_customer() AND customer_id = public.current_customer_id())
);

CREATE POLICY customer_points_access_policy ON "crm"."customer_points"
FOR ALL
USING (
    company_customer_id IN (
        SELECT id FROM "crm"."company_customers"
        WHERE (public.is_employee() AND company_id = public.current_company_id())
           OR (public.is_customer() AND customer_id = public.current_customer_id())
    )
);

CREATE POLICY customer_preferences_access_policy ON "crm"."customer_preferences"
FOR ALL
USING (
    company_customer_id IN (
        SELECT id FROM "crm"."company_customers"
        WHERE (public.is_employee() AND company_id = public.current_company_id())
           OR (public.is_customer() AND customer_id = public.current_customer_id())
    )
);


-- ============================================================================
-- SALES SCHEMA POLICIES
-- ============================================================================

CREATE POLICY sales_access_policy ON "sales"."sales"
FOR ALL
USING (
    (public.is_employee() AND company_id = public.current_company_id())
    OR
    (public.is_customer() AND customer_id = public.current_customer_id())
);

CREATE POLICY tickets_access_policy ON "sales"."tickets"
FOR ALL
USING (
    sale_id IN (
        SELECT id FROM "sales"."sales"
        WHERE (public.is_employee() AND company_id = public.current_company_id())
           OR (public.is_customer() AND customer_id = public.current_customer_id())
    )
);

CREATE POLICY concession_sales_access_policy ON "sales"."concession_sales"
FOR ALL
USING (
    sale_id IN (
        SELECT id FROM "sales"."sales"
        WHERE (public.is_employee() AND company_id = public.current_company_id())
           OR (public.is_customer() AND customer_id = public.current_customer_id())
    )
);


-- ============================================================================
-- OPERATIONS SCHEMA POLICIES
-- ============================================================================

CREATE POLICY cinema_complexes_access_policy ON "operations"."cinema_complexes"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);

CREATE POLICY rooms_access_policy ON "operations"."rooms"
FOR ALL
USING (
    cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = public.current_company_id()
    )
);

CREATE POLICY showtime_schedule_read_policy ON "operations"."showtime_schedule"
FOR SELECT
USING (
    (public.is_customer() AND cinema_complex_id IN (
        SELECT complex.id
        FROM "operations"."cinema_complexes" complex
        JOIN "crm"."company_customers" cc ON cc.company_id = complex.company_id
        WHERE cc.customer_id = public.current_customer_id()
    ))
    OR
    (public.is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = public.current_company_id()
    ))
);

CREATE POLICY showtime_schedule_write_policy ON "operations"."showtime_schedule"
FOR INSERT
WITH CHECK (
    public.is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = public.current_company_id()
    )
);

CREATE POLICY showtime_schedule_update_policy ON "operations"."showtime_schedule"
FOR UPDATE
USING (
    public.is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = public.current_company_id()
    )
)
WITH CHECK (
    public.is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = public.current_company_id()
    )
);

CREATE POLICY showtime_schedule_delete_policy ON "operations"."showtime_schedule"
FOR DELETE
USING (
    public.is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = public.current_company_id()
    )
);


-- ============================================================================
-- HR SCHEMA POLICIES
-- ============================================================================

CREATE POLICY employees_access_policy ON "hr"."employees"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);

CREATE POLICY departments_access_policy ON "hr"."departments"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);


-- ============================================================================
-- FINANCE SCHEMA POLICIES
-- ============================================================================

CREATE POLICY bank_accounts_access_policy ON "finance"."bank_accounts"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);


-- ============================================================================
-- CATALOG SCHEMA POLICIES
-- ============================================================================

CREATE POLICY products_access_policy ON "catalog"."products"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);

CREATE POLICY movies_read_policy ON "catalog"."movies"
FOR SELECT
USING (
    (public.is_customer() AND company_id IN (
        SELECT company_id FROM "crm"."company_customers"
        WHERE customer_id = public.current_customer_id()
    ))
    OR
    (public.is_employee() AND company_id = public.current_company_id())
);

CREATE POLICY movies_insert_policy ON "catalog"."movies"
FOR INSERT
WITH CHECK (
    public.is_employee() AND company_id = public.current_company_id()
);

CREATE POLICY movies_update_policy ON "catalog"."movies"
FOR UPDATE
USING (
    public.is_employee() AND company_id = public.current_company_id()
)
WITH CHECK (
    public.is_employee() AND company_id = public.current_company_id()
);

CREATE POLICY movies_delete_policy ON "catalog"."movies"
FOR DELETE
USING (
    public.is_employee() AND company_id = public.current_company_id()
);


-- ============================================================================
-- IDENTITY SCHEMA POLICIES
-- ============================================================================

CREATE POLICY company_users_access_policy ON "identity"."company_users"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);

CREATE POLICY custom_roles_access_policy ON "identity"."custom_roles"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);

CREATE POLICY permissions_access_policy ON "identity"."permissions"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);


-- ============================================================================
-- AUDIT SCHEMA POLICIES
-- ============================================================================

CREATE POLICY audit_logs_access_policy ON "audit"."audit_logs"
FOR ALL
USING (
    public.is_employee() AND company_id = public.current_company_id()
);
