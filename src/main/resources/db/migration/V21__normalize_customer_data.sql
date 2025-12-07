-- ============================================================================
-- Customer Data Normalization and View Updates
-- ============================================================================
-- Migration: V21__normalize_customer_data.sql
-- Description: Normalize crm.customers, drop dependent views, and recreate them
-- ============================================================================

-- 1. Drop dependent views (they block column drops)
DROP VIEW IF EXISTS "finance"."v_sales_summary";
DROP VIEW IF EXISTS "crm"."v_customer_profile";
DROP VIEW IF EXISTS "crm"."v_customer_segmentation";

-- 2. Add gender column to identity.persons (it was missing in V2)
-- Using the global enum created in V20. Must be done before creating views that use it.
ALTER TABLE "identity"."persons"
    ADD COLUMN IF NOT EXISTS "gender" gender_enum DEFAULT 'PREFER_NOT_TO_SAY';

-- 3. Remove redundant columns from crm.customers
-- These fields already exist in identity.persons or identity.identities
ALTER TABLE "crm"."customers"
    DROP COLUMN "cpf",
    DROP COLUMN "full_name",
    DROP COLUMN "email",
    DROP COLUMN "phone",
    DROP COLUMN "birth_date",
    DROP COLUMN "gender",
    DROP COLUMN "zip_code",
    DROP COLUMN "address",
    DROP COLUMN "city",
    DROP COLUMN "state";

-- 4. Create unified view for easy access (The "Fat" Customer Object)
CREATE OR REPLACE VIEW "crm"."v_customer_details" AS
SELECT 
    c.id AS customer_id,
    c.identity_id,
    c.company_id,
    -- Personal Data (from identity.persons)
    p.full_name,
    p.cpf,
    p.birth_date,
    p.gender,
    -- Contact Data (from identity.persons/identities)
    i.email,
    p.phone,
    p.mobile,
    -- Address (from identity.persons)
    p.zip_code,
    p.street_address,
    p.address_number,
    p.address_complement,
    p.neighborhood,
    p.city,
    p.state,
    p.country,
    -- CRM Specific Data
    c.accepts_marketing,
    c.accepts_sms,
    c.accepts_email,
    c.terms_accepted,
    c.terms_acceptance_date,
    c.active AS crm_active,
    c.blocked AS crm_blocked,
    c.registration_source,
    c.created_at AS crm_created_at
FROM "crm"."customers" c
JOIN "identity"."identities" i ON c.identity_id = i.id
LEFT JOIN "identity"."persons" p ON i.person_id = p.id;

COMMENT ON VIEW "crm"."v_customer_details" IS 'Unified view combining CRM data with Identity/Person data';

-- 5. Recreate dropped views using the new structure

-- 5.1 Recreate finance.v_sales_summary
CREATE OR REPLACE VIEW finance.v_sales_summary AS
SELECT 
    s.id AS sale_id,
    s.sale_number,
    s.sale_date,
    s.company_id,
    c.corporate_name AS company_name,
    complex.name AS cinema_complex,
    cust.full_name AS customer_name,
    cust.email AS customer_email,
    
    -- Totais
    s.total_amount,
    s.discount_amount,
    s.net_amount,
    
    -- Contagens
    COUNT(DISTINCT t.id) AS ticket_count,
    COUNT(DISTINCT cs.id) AS concession_count,
    
    -- Status
    s.status,
    s.cancellation_date,
    s.cancellation_reason,
    s.created_at
    
FROM sales.sales s
JOIN identity.companies c ON c.id = s.company_id
JOIN operations.cinema_complexes complex ON complex.id = s.cinema_complex_id
LEFT JOIN crm.v_customer_details cust ON cust.customer_id = s.customer_id -- Using new view
LEFT JOIN sales.tickets t ON t.sale_id = s.id
LEFT JOIN sales.concession_sales cs ON cs.sale_id = s.id
GROUP BY 
    s.id, 
    s.sale_number, 
    s.sale_date, 
    s.company_id, 
    c.corporate_name, 
    s.cinema_complex_id, 
    complex.name, 
    s.customer_id, 
    cust.full_name, 
    cust.email, 
    s.total_amount, 
    s.discount_amount, 
    s.net_amount, 
    s.status, 
    s.cancellation_date, 
    s.cancellation_reason, 
    s.created_at;

COMMENT ON VIEW finance.v_sales_summary IS 'Resumo completo de vendas com informações agregadas de tickets e concessões';

-- 5.2 Recreate crm.v_customer_profile
CREATE OR REPLACE VIEW crm.v_customer_profile AS
SELECT 
    c.customer_id, -- Changed from c.id
    c.full_name,
    c.email,
    c.cpf,
    c.company_id,
    c.crm_active AS active, -- Changed from c.active
    
    -- Dados de fidelidade
    cc.accumulated_points,
    cc.loyalty_level,
    cc.loyalty_join_date,
    cc.is_active_in_loyalty,
    
    -- Estatísticas de compra
    COUNT(DISTINCT s.id) AS total_purchases,
    COALESCE(SUM(s.net_amount), 0) AS lifetime_value,
    MAX(s.sale_date) AS last_purchase_date,
    MIN(s.sale_date) AS first_purchase_date,
    
    -- Preferências
    pref.preferred_session_type,
    pref.preferred_language,
    pref.preferred_position,
    
    -- Gêneros favoritos (array)
    ARRAY_AGG(DISTINCT fg.genre) FILTER (WHERE fg.genre IS NOT NULL) AS favorite_genres,
    
    -- Datas
    c.crm_created_at AS registration_date
    
FROM crm.v_customer_details c -- Using new view
JOIN crm.company_customers cc ON cc.customer_id = c.customer_id AND cc.company_id = c.company_id
LEFT JOIN sales.sales s ON s.customer_id = c.customer_id
LEFT JOIN crm.customer_preferences pref ON pref.company_customer_id = cc.id
LEFT JOIN crm.customer_favorite_genres fg ON fg.company_customer_id = cc.id
GROUP BY 
    c.customer_id, 
    c.full_name, 
    c.email, 
    c.cpf, 
    c.company_id, 
    c.crm_active, 
    cc.id,
    cc.accumulated_points, 
    cc.loyalty_level, 
    cc.loyalty_join_date, 
    cc.is_active_in_loyalty,
    pref.id,
    pref.preferred_session_type, 
    pref.preferred_language, 
    pref.preferred_position,
    c.crm_created_at;

COMMENT ON VIEW crm.v_customer_profile IS 'Perfil 360° do cliente com estatísticas de compra e preferências';

-- 5.3 Recreate crm.v_customer_segmentation
CREATE OR REPLACE VIEW crm.v_customer_segmentation AS
WITH customer_metrics AS (
    SELECT 
        c.id AS customer_id,
        c.company_id,
        COUNT(s.id) AS frequency,
        COALESCE(SUM(s.net_amount), 0) AS monetary,
        MAX(s.sale_date) AS last_purchase,
        COALESCE(CURRENT_DATE - MAX(s.sale_date)::date, 999) AS recency_days
    FROM crm.customers c
    LEFT JOIN sales.sales s ON s.customer_id = c.id
    GROUP BY c.id, c.company_id
)
SELECT 
    customer_id,
    company_id,
    frequency,
    monetary,
    recency_days,
    last_purchase,
    
    -- Segmentação RFM
    CASE 
        WHEN recency_days <= 30 AND frequency >= 5 AND monetary >= 500 THEN 'VIP'
        WHEN recency_days <= 60 AND frequency >= 3 AND monetary >= 200 THEN 'LOYAL'
        WHEN recency_days <= 90 AND frequency >= 1 THEN 'ACTIVE'
        WHEN recency_days > 90 AND recency_days <= 180 THEN 'AT_RISK'
        WHEN recency_days > 180 THEN 'CHURNED'
        ELSE 'NEW'
    END AS segment,
    
    -- Score (0-100)
    (
        (100 - LEAST(recency_days, 100)) * 0.3 +
        (LEAST(frequency * 10, 100)) * 0.3 +
        (LEAST(monetary / 10, 100)) * 0.4
    )::INTEGER AS customer_score
    
FROM customer_metrics;

COMMENT ON VIEW crm.v_customer_segmentation IS 'Segmentação automática de clientes usando modelo RFM (Recency, Frequency, Monetary)';
