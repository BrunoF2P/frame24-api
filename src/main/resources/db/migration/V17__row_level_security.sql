-- ============================================================================
-- Row Level Security Implementation
-- ============================================================================
-- Migration: V17__row_level_security.sql
-- Description: Add RLS policies for multi-tenant data isolation
-- ============================================================================

-- ============================================================================
-- PART 1: SCHEMA MODIFICATIONS
-- ============================================================================

-- Add company_id to customers table
ALTER TABLE "crm"."customers" 
ADD COLUMN "company_id" BIGINT;

-- Add company_id to sales table
ALTER TABLE "sales"."sales" 
ADD COLUMN "company_id" BIGINT;

-- Migrate data for customers (get company_id from company_customers)
UPDATE "crm"."customers" c
SET company_id = (
    SELECT cc.company_id 
    FROM "crm"."company_customers" cc
    WHERE cc.customer_id = c.id
    LIMIT 1
);

-- Migrate data for sales (get company_id from cinema_complex)
UPDATE "sales"."sales" s
SET company_id = (
    SELECT cc.company_id 
    FROM "operations"."cinema_complexes" cc
    WHERE cc.id = s.cinema_complex_id
);

-- Make company_id NOT NULL after migration
ALTER TABLE "crm"."customers" 
ALTER COLUMN "company_id" SET NOT NULL;

ALTER TABLE "sales"."sales" 
ALTER COLUMN "company_id" SET NOT NULL;

-- Add foreign keys
ALTER TABLE "crm"."customers"
ADD CONSTRAINT fk_customers_companies_company
FOREIGN KEY (company_id) REFERENCES "identity"."companies"(id)
ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "sales"."sales"
ADD CONSTRAINT fk_sales_companies_company
FOREIGN KEY (company_id) REFERENCES "identity"."companies"(id)
ON DELETE RESTRICT ON UPDATE CASCADE;

-- Add indexes for performance
CREATE INDEX idx_customers_company_id ON "crm"."customers"(company_id);
CREATE INDEX idx_sales_company_id ON "sales"."sales"(company_id);


-- ============================================================================
-- PART 2: HELPER FUNCTIONS
-- ============================================================================

-- Function to get current company ID from session
CREATE OR REPLACE FUNCTION current_company_id() 
RETURNS BIGINT AS $$
BEGIN
    RETURN current_setting('app.current_company_id', true)::BIGINT;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

-- Function to get current customer ID from session
CREATE OR REPLACE FUNCTION current_customer_id() 
RETURNS BIGINT AS $$
BEGIN
    RETURN current_setting('app.current_customer_id', true)::BIGINT;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

-- Function to get current user type from session
CREATE OR REPLACE FUNCTION current_user_type() 
RETURNS TEXT AS $$
BEGIN
    RETURN current_setting('app.user_type', true);
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'SYSTEM';
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

-- Function to check if user is employee
CREATE OR REPLACE FUNCTION is_employee() 
RETURNS BOOLEAN AS $$
BEGIN
    RETURN current_user_type() = 'EMPLOYEE';
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;

-- Function to check if user is customer
CREATE OR REPLACE FUNCTION is_customer() 
RETURNS BOOLEAN AS $$
BEGIN
    RETURN current_user_type() = 'CUSTOMER';
END;
$$ LANGUAGE plpgsql STABLE SECURITY DEFINER;


-- ============================================================================
-- PART 3: RLS POLICIES - CUSTOMER DATA
-- ============================================================================

-- Policy for customers table
ALTER TABLE "crm"."customers" ENABLE ROW LEVEL SECURITY;

CREATE POLICY customers_access_policy ON "crm"."customers"
FOR ALL
USING (
    -- Employees see customers from their company
    (is_employee() AND company_id = current_company_id())
    OR
    -- Customers see only themselves
    (is_customer() AND id = current_customer_id())
);

-- Policy for company_customers table
ALTER TABLE "crm"."company_customers" ENABLE ROW LEVEL SECURITY;

CREATE POLICY company_customers_access_policy ON "crm"."company_customers"
FOR ALL
USING (
    -- Employees see all company_customers from their company
    (is_employee() AND company_id = current_company_id())
    OR
    -- Customers see only their own company_customer records
    (is_customer() AND customer_id = current_customer_id())
);

-- Policy for customer_points
ALTER TABLE "crm"."customer_points" ENABLE ROW LEVEL SECURITY;

CREATE POLICY customer_points_access_policy ON "crm"."customer_points"
FOR ALL
USING (
    company_customer_id IN (
        SELECT id FROM "crm"."company_customers"
        WHERE (is_employee() AND company_id = current_company_id())
           OR (is_customer() AND customer_id = current_customer_id())
    )
);

-- Policy for customer_preferences
ALTER TABLE "crm"."customer_preferences" ENABLE ROW LEVEL SECURITY;

CREATE POLICY customer_preferences_access_policy ON "crm"."customer_preferences"
FOR ALL
USING (
    company_customer_id IN (
        SELECT id FROM "crm"."company_customers"
        WHERE (is_employee() AND company_id = current_company_id())
           OR (is_customer() AND customer_id = current_customer_id())
    )
);


-- ============================================================================
-- PART 4: RLS POLICIES - SALES DATA
-- ============================================================================

-- Policy for sales table
ALTER TABLE "sales"."sales" ENABLE ROW LEVEL SECURITY;

CREATE POLICY sales_access_policy ON "sales"."sales"
FOR ALL
USING (
    -- Employees see sales from their company
    (is_employee() AND company_id = current_company_id())
    OR
    -- Customers see only their own purchases
    (is_customer() AND customer_id = current_customer_id())
);

-- Policy for tickets
ALTER TABLE "sales"."tickets" ENABLE ROW LEVEL SECURITY;

CREATE POLICY tickets_access_policy ON "sales"."tickets"
FOR ALL
USING (
    sale_id IN (
        SELECT id FROM "sales"."sales"
        WHERE (is_employee() AND company_id = current_company_id())
           OR (is_customer() AND customer_id = current_customer_id())
    )
);

-- Policy for concession_sales
ALTER TABLE "sales"."concession_sales" ENABLE ROW LEVEL SECURITY;

CREATE POLICY concession_sales_access_policy ON "sales"."concession_sales"
FOR ALL
USING (
    sale_id IN (
        SELECT id FROM "sales"."sales"
        WHERE (is_employee() AND company_id = current_company_id())
           OR (is_customer() AND customer_id = current_customer_id())
    )
);


-- ============================================================================
-- PART 5: RLS POLICIES - COMPANY-SCOPED TABLES
-- ============================================================================

-- Operations: cinema_complexes
ALTER TABLE "operations"."cinema_complexes" ENABLE ROW LEVEL SECURITY;

CREATE POLICY cinema_complexes_access_policy ON "operations"."cinema_complexes"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);

-- Operations: rooms
ALTER TABLE "operations"."rooms" ENABLE ROW LEVEL SECURITY;

CREATE POLICY rooms_access_policy ON "operations"."rooms"
FOR ALL
USING (
    cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = current_company_id()
    )
);

-- HR: employees
ALTER TABLE "hr"."employees" ENABLE ROW LEVEL SECURITY;

CREATE POLICY employees_access_policy ON "hr"."employees"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);

-- HR: departments
ALTER TABLE "hr"."departments" ENABLE ROW LEVEL SECURITY;

CREATE POLICY departments_access_policy ON "hr"."departments"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);

-- Finance: bank_accounts
ALTER TABLE "finance"."bank_accounts" ENABLE ROW LEVEL SECURITY;

CREATE POLICY bank_accounts_access_policy ON "finance"."bank_accounts"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);

-- Catalog: products
ALTER TABLE "catalog"."products" ENABLE ROW LEVEL SECURITY;

CREATE POLICY products_access_policy ON "catalog"."products"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);


-- ============================================================================
-- PART 6: RLS POLICIES - PUBLIC DATA (READ-ONLY FOR CUSTOMERS)
-- ============================================================================

-- Showtime schedule - customers can read, employees can modify
ALTER TABLE "operations"."showtime_schedule" ENABLE ROW LEVEL SECURITY;

CREATE POLICY showtime_schedule_read_policy ON "operations"."showtime_schedule"
FOR SELECT
USING (
    -- Customers can see sessions from their company's complexes
    (is_customer() AND cinema_complex_id IN (
        SELECT complex.id
        FROM "operations"."cinema_complexes" complex
        JOIN "crm"."company_customers" cc ON cc.company_id = complex.company_id
        WHERE cc.customer_id = current_customer_id()
    ))
    OR
    -- Employees see sessions from their company
    (is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = current_company_id()
    ))
);


CREATE POLICY showtime_schedule_write_policy ON "operations"."showtime_schedule"
FOR INSERT
WITH CHECK (
    is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = current_company_id()
    )
);

CREATE POLICY showtime_schedule_update_policy ON "operations"."showtime_schedule"
FOR UPDATE
USING (
    is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = current_company_id()
    )
)
WITH CHECK (
    is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = current_company_id()
    )
);

CREATE POLICY showtime_schedule_delete_policy ON "operations"."showtime_schedule"
FOR DELETE
USING (
    is_employee() AND cinema_complex_id IN (
        SELECT id FROM "operations"."cinema_complexes"
        WHERE company_id = current_company_id()
    )
);

-- Movies - read-only for customers
ALTER TABLE "catalog"."movies" ENABLE ROW LEVEL SECURITY;

CREATE POLICY movies_read_policy ON "catalog"."movies"
FOR SELECT
USING (
    -- Customers can see movies from their company
    (is_customer() AND company_id IN (
        SELECT company_id FROM "crm"."company_customers"
        WHERE customer_id = current_customer_id()
    ))
    OR
    -- Employees see movies from their company
    (is_employee() AND company_id = current_company_id())
);


CREATE POLICY movies_insert_policy ON "catalog"."movies"
FOR INSERT
WITH CHECK (
    is_employee() AND company_id = current_company_id()
);

CREATE POLICY movies_update_policy ON "catalog"."movies"
FOR UPDATE
USING (
    is_employee() AND company_id = current_company_id()
)
WITH CHECK (
    is_employee() AND company_id = current_company_id()
);

CREATE POLICY movies_delete_policy ON "catalog"."movies"
FOR DELETE
USING (
    is_employee() AND company_id = current_company_id()
);


-- ============================================================================
-- PART 7: IDENTITY & ACCESS POLICIES
-- ============================================================================

-- company_users - employees see users from their company
ALTER TABLE "identity"."company_users" ENABLE ROW LEVEL SECURITY;

CREATE POLICY company_users_access_policy ON "identity"."company_users"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);

-- custom_roles - employees see roles from their company
ALTER TABLE "identity"."custom_roles" ENABLE ROW LEVEL SECURITY;

CREATE POLICY custom_roles_access_policy ON "identity"."custom_roles"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);

-- permissions - employees see permissions from their company
ALTER TABLE "identity"."permissions" ENABLE ROW LEVEL SECURITY;

CREATE POLICY permissions_access_policy ON "identity"."permissions"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);


-- ============================================================================
-- PART 8: AUDIT LOG POLICY
-- ============================================================================

ALTER TABLE "audit"."audit_logs" ENABLE ROW LEVEL SECURITY;

CREATE POLICY audit_logs_access_policy ON "audit"."audit_logs"
FOR ALL
USING (
    is_employee() AND company_id = current_company_id()
);


-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON FUNCTION current_company_id() IS 'Returns the current company ID from session context';
COMMENT ON FUNCTION current_customer_id() IS 'Returns the current customer ID from session context';
COMMENT ON FUNCTION current_user_type() IS 'Returns the current user type (CUSTOMER, EMPLOYEE, SYSTEM)';
COMMENT ON FUNCTION is_employee() IS 'Returns true if current user is an employee';
COMMENT ON FUNCTION is_customer() IS 'Returns true if current user is a customer';
