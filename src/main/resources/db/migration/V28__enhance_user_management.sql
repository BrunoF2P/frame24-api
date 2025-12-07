-- ============================================================================
-- User Management Enhancements
-- ============================================================================
-- Migration: V28__enhance_user_management.sql
-- Description: Add RLS policies, validation functions, and indexes for
--              comprehensive user management with allowed_complexes enforcement
-- ============================================================================
-- ============================================================================
-- PART 1: CONSTRAINTS AND INDEXES
-- ============================================================================
-- Make employee_id nullable (will be generated for employees, null for external users)
-- Already nullable in schema, but let's ensure it
-- Add unique constraint for employee_id within company
CREATE UNIQUE INDEX IF NOT EXISTS idx_company_users_employee_id_company ON "identity"."company_users" (employee_id                                                      , company_id)
              WHERE employee_id IS NOT NULL ;
-- Add index for fast employee_id lookup
CREATE INDEX IF NOT EXISTS idx_company_users_employee_id ON "identity"."company_users" (employee_id)
       WHERE employee_id IS NOT NULL ;
-- Add index for active users filtering
CREATE INDEX IF NOT EXISTS idx_company_users_active ON "identity"."company_users" (company_id                                        , active) ;
-- Add index for allowed_complexes (GIN index for array operations when using jsonb)
-- Note: allowed_complexes is stored as TEXT, will be handled in application layer
-- If we decide to use JSONB in future, we can create: CREATE INDEX idx_company_users_allowed_complexes ON "identity"."company_users" USING gin ((allowed_complexes::jsonb));
-- Add index for department filtering
CREATE INDEX IF NOT EXISTS idx_company_users_department ON "identity"."company_users" (company_id                                        , department)
       WHERE department IS NOT NULL ;
-- ============================================================================
-- PART 2: VALIDATION FUNCTIONS
-- ============================================================================
-- Function to get allowed complexes from session context
CREATE OR REPLACE FUNCTION get_allowed_complexes
  (
  ) RETURNS BIGINT [] AS $$ DECLARE complexes_text TEXT ; complexes_array BIGINT [] ; BEGIN
          -- Try to get allowed complexes from session
          complexes_text := current_setting
  ('app.allowed_complexes'
     , true
  ) ; IF complexes_text IS NULL
       OR complexes_text = '' THEN RETURN ARRAY[] ::BIGINT [] ;
END
          IF ;
-- Parse comma-separated list to array
SELECT ARRAY_AGG(elem ::BIGINT) INTO complexes_array
  FROM unnest (string_to_array (complexes_text, ',')) AS elem
 WHERE elem ~ '^\d+$' ; RETURN COALESCE(complexes_array                                               , ARRAY[] ::BIGINT []) ; EXCEPTION
       WHEN OTHERS THEN RETURN ARRAY[] ::BIGINT [] ;
END ; $$ LANGUAGE plpgsql STABLE SECURITY DEFINER ;
-- Function to check if user can access a specific complex
CREATE OR REPLACE FUNCTION can_access_complex
  (  check_complex_id BIGINT
  ) RETURNS BOOLEAN AS $$ DECLARE user_complexes BIGINT [] ; user_type_val TEXT ; BEGIN user_type_val := current_setting
  ('app.user_type'
     , true
  ) ;
          -- System users and non-employee users have full access
          IF user_type_val IS NULL
       OR user_type_val != 'EMPLOYEE' THEN RETURN TRUE ;
END
          IF ;
             -- Get user's allowed complexes
             user_complexes := get_allowed_complexes() ;
          -- If no complexes set, deny access (defensive)
          IF array_length(user_complexes
                          , 1) IS NULL THEN RETURN FALSE ;
END
          IF ;
             -- Check if complex_id is in allowed list
             RETURN check_complex_id = ANY(user_complexes) ;
END ; $$ LANGUAGE plpgsql STABLE SECURITY DEFINER ;
-- Function to validate allowed_complexes for a company_user
CREATE OR REPLACE FUNCTION validate_allowed_complexes_for_user
  (
              user_id BIGINT
     , complexes_text TEXT
     ,   p_company_id BIGINT
  ) RETURNS BOOLEAN AS $$ DECLARE complex_id_str TEXT ; complex_id BIGINT ; complex_exists BOOLEAN ; BEGIN
          -- If no complexes specified, return false (at least one required)
          IF complexes_text IS NULL
       OR complexes_text = ''
       OR complexes_text = '[]' THEN RETURN FALSE ;
END
          IF ;
             -- Remove JSON array brackets if present and split by comma
             complexes_text := REPLACE(REPLACE(complexes_text
                                                 , '['
                                                 , '')
                                         , ']'
                                         , '') ;
             -- Validate each complex ID
             FOR complex_id_str IN
SELECT unnest(string_to_array(complexes_text
                              , ',')) LOOP
       -- Trim whitespace and quotes
       complex_id_str := TRIM(both ' "' FROM complex_id_str) ;
       -- Skip empty strings
       IF complex_id_str = '' THEN CONTINUE ;
END
       IF ;
          -- Convert to BIGINT
          BEGIN complex_id := complex_id_str ::BIGINT ; EXCEPTION
          WHEN OTHERS THEN RAISE EXCEPTION 'Invalid complex ID format: %'               , complex_id_str ;
END ;
                 -- Check if complex exists and belongs to the company
                 SELECT EXISTS ( SELECT 1
                            FROM "operations"."cinema_complexes"
                           WHERE id = complex_id
                             AND company_id = p_company_id
                             AND active = true ) INTO complex_exists ;
                        IF NOT complex_exists THEN RAISE EXCEPTION 'Complex ID % does not exist or does not belong to company %'
                        , complex_id
                        , p_company_id ;
END
                          IF ;
END LOOP ; RETURN TRUE ;
END ; $$ LANGUAGE plpgsql SECURITY DEFINER ;
-- ============================================================================
-- PART 3: TRIGGERS FOR VALIDATION
-- ============================================================================
-- Trigger function to validate allowed_complexes on insert/update
CREATE OR REPLACE FUNCTION trg_validate_user_allowed_complexes
  (
  ) RETURNS TRIGGER AS $$ BEGIN
          -- Only validate for employee users (not external)
 -- Validate that allowed_complexes contains valid complex IDs for the company
          IF NEW.allowed_complexes IS NOT NULL THEN
          IF NOT validate_allowed_complexes_for_user(NEW.id
                                                         , NEW.allowed_complexes
                                                         , NEW.company_id) THEN RAISE EXCEPTION 'Invalid allowed_complexes for user' ;
END
          IF ;
END
          IF ; RETURN NEW ;
END ; $$ LANGUAGE plpgsql ;
                -- Create trigger on company_users table
                DROP TRIGGER IF EXISTS trg_validate_allowed_complexes ON "identity"."company_users" ;
CREATE TRIGGER trg_validate_allowed_complexes BEFORE
                 INSERT
                     OR
                UPDATE OF allowed_complexes
ON "identity"."company_users" FOR EACH ROW EXECUTE FUNCTION trg_validate_user_allowed_complexes() ;
-- ============================================================================
-- PART 4: RLS POLICIES FOR COMPLEX-BASED ACCESS
-- ============================================================================
-- Add RLS policy to cinema_complexes to restrict based on allowed_complexes
-- This policy works in addition to existing company-based RLS
DROP POLICY IF EXISTS cinema_complexes_allowed_policy ON "operations"."cinema_complexes" ;
CREATE POLICY cinema_complexes_allowed_policy
ON "operations"."cinema_complexes" FOR ALL USING (
    -- System/non-employee users see all complexes from their company
    ( NOT is_employee()
     AND company_id = current_company_id()
)
    OR
       -- Employees only see complexes in their allowed list
       (is_employee()
        AND company_id = current_company_id()
        AND can_access_complex(id))) ;
     -- Since we already have cinema_complexes_access_policy, we need to drop it first
DROP POLICY IF EXISTS cinema_complexes_access_policy ON "operations"."cinema_complexes" ;
       -- Now apply the new policy
CREATE POLICY cinema_complexes_access_policy
ON "operations"."cinema_complexes" FOR ALL USING (
    -- System/non-employee users see all complexes from their company
    ( NOT is_employee()
     AND company_id = current_company_id()
)
    OR
       -- Employees only see complexes in their allowed list
       (is_employee()
        AND company_id = current_company_id()
        AND can_access_complex(id))) ;
     -- Update rooms policy to respect allowed_complexes
DROP POLICY IF EXISTS rooms_access_policy ON "operations"."rooms" ;
CREATE POLICY rooms_access_policy
ON "operations"."rooms" FOR ALL USING (cinema_complex_id IN ( SELECT id
                             FROM "operations"."cinema_complexes"
                            WHERE company_id = current_company_id()
                              AND ( NOT is_employee()
                                    OR can_access_complex(id)) )) ;
     -- Update showtime_schedule policies to respect allowed_complexes
DROP POLICY IF EXISTS showtime_schedule_read_policy ON "operations"."showtime_schedule" ;
CREATE POLICY showtime_schedule_read_policy
ON "operations"."showtime_schedule" FOR
SELECT USING (
         -- Customers can see sessions from their company's complexes
         (is_customer()
          AND cinema_complex_id IN ( SELECT complex.id
                                       FROM "operations"."cinema_complexes" complex
                                            JOIN "crm"."company_customers" cc
                                              ON cc.company_id = complex.company_id
                                      WHERE cc.customer_id = current_customer_id() ))
         OR
            -- Employees see sessions from their allowed complexes
            (is_employee()
             AND cinema_complex_id IN ( SELECT id
                                          FROM "operations"."cinema_complexes"
                                         WHERE company_id = current_company_id()
                                           AND can_access_complex(id) ))) ;
DROP POLICY IF EXISTS showtime_schedule_write_policy ON "operations"."showtime_schedule" ;
CREATE POLICY showtime_schedule_write_policy
ON "operations"."showtime_schedule" FOR
INSERT
WITH CHECK (is_employee
                         ()
              AND cinema_complex_id IN ( SELECT id
                                           FROM "operations"."cinema_complexes"
                                          WHERE company_id = current_company_id()
                                            AND can_access_complex(id) )) ;
DROP POLICY IF EXISTS showtime_schedule_update_policy ON "operations"."showtime_schedule" ;
CREATE POLICY showtime_schedule_update_policy
ON "operations"."showtime_schedule" FOR
UPDATE USING (is_employee()
              AND cinema_complex_id IN ( SELECT id
                                           FROM "operations"."cinema_complexes"
                                          WHERE company_id = current_company_id()
                                            AND can_access_complex(id) ))
WITH CHECK (is_employee
                         ()
              AND cinema_complex_id IN ( SELECT id
                                           FROM "operations"."cinema_complexes"
                                          WHERE company_id = current_company_id()
                                            AND can_access_complex(id) )) ;
DROP POLICY IF EXISTS showtime_schedule_delete_policy ON "operations"."showtime_schedule" ;
CREATE POLICY showtime_schedule_delete_policy
ON "operations"."showtime_schedule" FOR
DELETE USING (is_employee()
              AND cinema_complex_id IN ( SELECT id
                                           FROM "operations"."cinema_complexes"
                                          WHERE company_id = current_company_id()
                                            AND can_access_complex(id) )) ;
             -- ============================================================================
             -- PART 5: EMPLOYEE ID SEQUENCES PER COMPANY
             -- ============================================================================
             -- Create table to track employee_id sequences per company
CREATE TABLE IF NOT EXISTS "identity"."employee_id_sequences" (                             "company_id" BIGINT
                             NOT NULL
             , "last_employee_id" BIGINT
               NOT NULL DEFAULT 1000
             , "updated_at" TIMESTAMP (3) DEFAULT CURRENT_TIMESTAMP
             , CONSTRAINT "employee_id_sequences_pkey" PRIMARY KEY (company_id)
             , CONSTRAINT "fk_employee_sequences_company" FOREIGN KEY (company_id) REFERENCES "identity"."companies" (id)
ON
DELETE CASCADE) ;
          -- Function to get next employee_id for a company
CREATE OR REPLACE FUNCTION get_next_employee_id
  (  p_company_id BIGINT
  ) RETURNS BIGINT AS $$ DECLARE next_id BIGINT ; BEGIN
       -- Insert or update the sequence record
INSERT INTO "identity"."employee_id_sequences"
            (company_id
             , last_employee_id)
     VALUES ( p_company_id
              , 1001 )
ON CONFLICT (company_id)
DO UPDATE
      SET last_employee_id = "identity"."employee_id_sequences".last_employee_id + 1
          , updated_at = CURRENT_TIMESTAMP
   RETURNING last_employee_id INTO next_id ; RETURN next_id ;
END ; $$ LANGUAGE plpgsql ;
             -- ============================================================================
             -- COMMENTS
             -- ============================================================================
             COMMENT
ON FUNCTION get_allowed_complexes() IS 'Returns array of complex IDs the current user can access from session context' ; COMMENT ON FUNCTION can_access_complex(BIGINT) IS 'Checks if current user can access a specific cinema complex' ; COMMENT ON FUNCTION validate_allowed_complexes_for_user(BIGINT
                                                , TEXT
                                                , BIGINT) IS 'Validates that all complex IDs in allowed_complexes exist and belong to the company' ; COMMENT ON FUNCTION get_next_employee_id(BIGINT) IS 'Generates next sequential employee_id for a company' ; COMMENT ON TABLE "identity"."employee_id_sequences" IS 'Tracks employee_id sequences per company for auto-generation' ;
