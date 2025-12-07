-- ============================================================================
-- Database Views for Reporting and Performance
-- ============================================================================
-- Migration: V18__database_views.sql
-- Description: Create views for simplified queries and better performance
-- ============================================================================

-- ============================================================================
-- PART 1: FINANCIAL VIEWS
-- ============================================================================

-- View: Resumo completo de vendas
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
LEFT JOIN crm.customers cust ON cust.id = s.customer_id
LEFT JOIN sales.tickets t ON t.sale_id = s.id
LEFT JOIN sales.concession_sales cs ON cs.sale_id = s.id
GROUP BY s.id, c.id, complex.id, cust.id;

COMMENT ON VIEW finance.v_sales_summary IS 'Resumo completo de vendas com informações agregadas de tickets e concessões';


-- View: Receita diária por cinema
CREATE OR REPLACE VIEW finance.v_daily_revenue AS
SELECT 
    s.company_id,
    s.cinema_complex_id,
    complex.name AS cinema_name,
    DATE(s.sale_date) AS sale_date,
    
    -- Receitas
    SUM(s.total_amount) AS gross_revenue,
    SUM(s.discount_amount) AS total_discounts,
    SUM(s.net_amount) AS net_revenue,
    
    -- Breakdown por tipo
    SUM(CASE WHEN t.id IS NOT NULL THEN t.total_amount ELSE 0 END) AS ticket_revenue,
    SUM(CASE WHEN cs.id IS NOT NULL THEN cs.total_amount ELSE 0 END) AS concession_revenue,
    
    -- Contagens
    COUNT(DISTINCT s.id) AS sale_count,
    COUNT(DISTINCT s.customer_id) AS unique_customers,
    COUNT(DISTINCT t.id) AS tickets_sold
    
FROM sales.sales s
JOIN operations.cinema_complexes complex ON complex.id = s.cinema_complex_id
LEFT JOIN sales.tickets t ON t.sale_id = s.id
LEFT JOIN sales.concession_sales cs ON cs.sale_id = s.id
-- WHERE s.status != 'CANCELLED' -- Removed: status is now BIGINT, not TEXT
GROUP BY s.company_id, s.cinema_complex_id, complex.name, DATE(s.sale_date);

COMMENT ON VIEW finance.v_daily_revenue IS 'Receita diária agregada por cinema com breakdown de tickets e concessões';


-- ============================================================================
-- PART 2: CRM VIEWS
-- ============================================================================

-- View: Perfil completo do cliente
CREATE OR REPLACE VIEW crm.v_customer_profile AS
SELECT 
    c.id AS customer_id,
    c.full_name,
    c.email,
    c.cpf,
    c.company_id,
    c.active,
    
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
    c.created_at AS registration_date
    
FROM crm.customers c
JOIN crm.company_customers cc ON cc.customer_id = c.id AND cc.company_id = c.company_id
LEFT JOIN sales.sales s ON s.customer_id = c.id -- Removed: AND s.status != 'CANCELLED' (status is BIGINT)
LEFT JOIN crm.customer_preferences pref ON pref.company_customer_id = cc.id
LEFT JOIN crm.customer_favorite_genres fg ON fg.company_customer_id = cc.id
GROUP BY c.id, cc.id, pref.id;

COMMENT ON VIEW crm.v_customer_profile IS 'Perfil 360° do cliente com estatísticas de compra e preferências';


-- View: Segmentação RFM de clientes
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
    LEFT JOIN sales.sales s ON s.customer_id = c.id -- Removed: AND s.status != 'CANCELLED' (status is BIGINT)
    GROUP BY c.id
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


-- ============================================================================
-- PART 3: OPERATIONAL VIEWS
-- ============================================================================

-- View: Disponibilidade de sessões
CREATE OR REPLACE VIEW operations.v_showtime_availability AS
SELECT 
    st.id AS showtime_id,
    st.cinema_complex_id,
    complex.company_id,
    st.room_id,
    room.name AS room_name,
    st.movie_id,
    COALESCE(m.brazil_title, m.original_title) AS movie_title,
    st.start_time,
    st.end_time,
    
    -- Capacidade
    (SELECT COUNT(*) FROM operations.seats WHERE room_id = st.room_id) AS total_seats,
    
    -- Ocupação
    COUNT(DISTINCT sss.seat_id) FILTER (WHERE sss.status = 'SOLD') AS seats_sold,
    COUNT(DISTINCT sss.seat_id) FILTER (WHERE sss.status = 'RESERVED') AS seats_reserved,
    COUNT(DISTINCT sss.seat_id) FILTER (WHERE sss.status = 'AVAILABLE') AS seats_available,
    
    -- Percentuais
    (COUNT(DISTINCT sss.seat_id) FILTER (WHERE sss.status = 'SOLD')::FLOAT / 
     NULLIF((SELECT COUNT(*) FROM operations.seats WHERE room_id = st.room_id), 0) * 100)::DECIMAL(5,2) AS occupancy_percentage,
    
    -- Status da sessão
    CASE 
        WHEN COUNT(DISTINCT sss.seat_id) FILTER (WHERE sss.status = 'AVAILABLE') = 0 THEN 'SOLD_OUT'
        WHEN st.start_time < CURRENT_TIMESTAMP THEN 'PAST'
        ELSE 'AVAILABLE'
    END AS session_status,
    
    -- Preços
    st.base_ticket_price,
    st.created_at
    
FROM operations.showtime_schedule st
JOIN operations.rooms room ON room.id = st.room_id
JOIN operations.cinema_complexes complex ON complex.id = st.cinema_complex_id
JOIN catalog.movies m ON m.id = st.movie_id
LEFT JOIN operations.session_seat_status sss ON sss.showtime_id = st.id
GROUP BY st.id, room.id, complex.id, m.id;

COMMENT ON VIEW operations.v_showtime_availability IS 'Disponibilidade em tempo real de assentos por sessão com status e ocupação';


-- ============================================================================
-- PART 4: CATALOG VIEWS
-- ============================================================================

-- View: Performance de filmes
CREATE OR REPLACE VIEW catalog.v_movie_performance AS
SELECT 
    m.id AS movie_id,
    COALESCE(m.brazil_title, m.original_title) AS title,
    m.company_id,
    m.duration_minutes,
    m.worldwide_release_date AS release_date,
    
    -- Sessões
    COUNT(DISTINCT st.id) AS total_sessions,
    COUNT(DISTINCT st.cinema_complex_id) AS cinemas_showing,
    
    -- Vendas
    COUNT(DISTINCT t.id) AS tickets_sold,
    COALESCE(SUM(t.total_amount), 0) AS total_revenue,
    COALESCE(AVG(t.total_amount), 0)::DECIMAL(10,2) AS avg_ticket_price,
    
    -- Ocupação média
    COALESCE(AVG(
        (SELECT COUNT(*) FROM operations.session_seat_status sss 
         WHERE sss.showtime_id = st.id AND sss.status = 'SOLD')::FLOAT /
        NULLIF((SELECT COUNT(*) FROM operations.seats s WHERE s.room_id = st.room_id), 0) * 100
    ), 0)::DECIMAL(5,2) AS avg_occupancy_rate,
    
    -- Período
    MIN(DATE(st.start_time)) AS first_session,
    MAX(DATE(st.start_time)) AS last_session,
    
    -- Status
    CASE 
        WHEN MAX(st.start_time) < CURRENT_TIMESTAMP THEN 'FINISHED'
        WHEN MIN(st.start_time) > CURRENT_TIMESTAMP THEN 'UPCOMING'
        ELSE 'SHOWING'
    END AS movie_status
    
FROM catalog.movies m
LEFT JOIN operations.showtime_schedule st ON st.movie_id = m.id
LEFT JOIN sales.tickets t ON t.showtime_id = st.id
GROUP BY m.id;

COMMENT ON VIEW catalog.v_movie_performance IS 'Análise de performance de filmes com métricas de vendas e ocupação';


-- ============================================================================
-- PART 5: AUDIT VIEWS
-- ============================================================================

-- View: Trilha de auditoria enriquecida
CREATE OR REPLACE VIEW audit.v_audit_trail AS
SELECT 
    al.id,
    al.company_id,
    c.corporate_name AS company_name,
    al.user_id,
    p.full_name AS user_name,
    i.email AS user_email,
    al.event_type,
    al.action,
    al.resource_type,
    al.resource_id,
    al.correlation_id,
    al.old_values,
    al.new_values,
    al.created_at,
    
    -- Classificação de risco
    CASE 
        WHEN al.action IN ('DELETE', 'HARD_DELETE') THEN 'HIGH'
        WHEN al.action IN ('UPDATE', 'CREATE') AND al.resource_type IN ('companies', 'employees', 'bank_accounts') THEN 'MEDIUM'
        ELSE 'LOW'
    END AS risk_level
    
FROM audit.audit_logs al
JOIN identity.companies c ON c.id = al.company_id
LEFT JOIN identity.identities i ON i.id = al.user_id
LEFT JOIN identity.persons p ON p.id = i.person_id;

COMMENT ON VIEW audit.v_audit_trail IS 'Trilha de auditoria enriquecida com informações de usuário e classificação de risco';


-- ============================================================================
-- PART 6: INDEXES FOR VIEW PERFORMANCE
-- ============================================================================

-- Indexes para melhorar performance das views

-- Sales
CREATE INDEX IF NOT EXISTS idx_sales_sale_date_status 
ON sales.sales(sale_date, status); -- Removed WHERE clause: status is BIGINT, not TEXT

CREATE INDEX IF NOT EXISTS idx_sales_customer_company 
ON sales.sales(customer_id, company_id);

-- Tickets
CREATE INDEX IF NOT EXISTS idx_tickets_showtime_sale 
ON sales.tickets(showtime_id, sale_id);

-- Session seat status
CREATE INDEX IF NOT EXISTS idx_session_seat_showtime_status 
ON operations.session_seat_status(showtime_id, status);

-- Customer favorites
CREATE INDEX IF NOT EXISTS idx_customer_fav_genres_company_customer 
ON crm.customer_favorite_genres(company_customer_id);


-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON SCHEMA finance IS 'Schema para dados financeiros e contábeis';
COMMENT ON SCHEMA crm IS 'Schema para Customer Relationship Management';
COMMENT ON SCHEMA operations IS 'Schema para operações de cinema';
COMMENT ON SCHEMA catalog IS 'Schema para catálogo de filmes e produtos';
COMMENT ON SCHEMA audit IS 'Schema para auditoria e logs';
