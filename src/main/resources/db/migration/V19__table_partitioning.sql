-- ============================================================================
-- Table Partitioning
-- ============================================================================
-- Migration: V19__table_partitioning.sql
-- Description: Implementa particionamento em tabelas transacionais
-- Partições: Dezembro 2024 + 2025 completo
-- ============================================================================

-- ============================================================================
-- PART 1: PARTITION MAINTENANCE FUNCTION
-- ============================================================================

-- Função para criar partições do próximo mês
CREATE OR REPLACE FUNCTION public.create_next_month_partitions()
RETURNS TABLE(partition_created TEXT, partition_table TEXT) AS $$
DECLARE
    next_month DATE;
    partition_suffix TEXT;
    start_date TEXT;
    end_date TEXT;
    result_text TEXT;
BEGIN
    -- Calcular próximo mês
    next_month := DATE_TRUNC('month', CURRENT_DATE + INTERVAL '1 month');
    partition_suffix := TO_CHAR(next_month, 'YYYY_MM');
    start_date := TO_CHAR(next_month, 'YYYY-MM-DD');
    end_date := TO_CHAR(next_month + INTERVAL '1 month', 'YYYY-MM-DD');
    
    -- Criar partição para sales
    BEGIN
        EXECUTE format(
            'CREATE TABLE IF NOT EXISTS sales.sales_%s PARTITION OF sales.sales
             FOR VALUES FROM (%L) TO (%L)',
            partition_suffix, start_date, end_date
        );
        partition_created := 'sales';
        partition_table := 'sales_' || partition_suffix;
        RETURN NEXT;
    EXCEPTION WHEN duplicate_table THEN
        -- Partição já existe, ignorar
    END;
    
    -- Criar partição para tickets
    BEGIN
        EXECUTE format(
            'CREATE TABLE IF NOT EXISTS sales.tickets_%s PARTITION OF sales.tickets
             FOR VALUES FROM (%L) TO (%L)',
            partition_suffix, start_date, end_date
        );
        partition_created := 'tickets';
        partition_table := 'tickets_' || partition_suffix;
        RETURN NEXT;
    EXCEPTION WHEN duplicate_table THEN
        -- Partição já existe, ignorar
    END;
    
    -- Criar partição para audit_logs
    BEGIN
        EXECUTE format(
            'CREATE TABLE IF NOT EXISTS audit.audit_logs_%s PARTITION OF audit.audit_logs
             FOR VALUES FROM (%L) TO (%L)',
            partition_suffix, start_date, end_date
        );
        partition_created := 'audit_logs';
        partition_table := 'audit_logs_' || partition_suffix;
        RETURN NEXT;
    EXCEPTION WHEN duplicate_table THEN
        -- Partição já existe, ignorar
    END;
    
    RETURN;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION public.create_next_month_partitions() IS 
'Cria partições do próximo mês para sales, tickets e audit_logs. Executado automaticamente via Spring Scheduler.';


-- ============================================================================
-- PART 2: SALES TABLE PARTITIONING
-- ============================================================================

-- Renomear tabela atual
ALTER TABLE sales.sales RENAME TO sales_old;

-- Criar tabela particionada
CREATE TABLE sales.sales (
    id BIGINT NOT NULL,
    cinema_complex_id BIGINT NOT NULL,
    user_id BIGINT,
    customer_id BIGINT,
    sale_type TEXT,
    payment_method TEXT,
    status sale_status_enum DEFAULT 'PENDING',
    sale_number VARCHAR(50) NOT NULL,
    sale_date TIMESTAMP(0) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    net_amount DECIMAL(10,2) NOT NULL,
    cancellation_date TIMESTAMP(0),
    cancellation_reason TEXT,
    created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    company_id BIGINT NOT NULL,
    
    PRIMARY KEY (id, sale_date)
) PARTITION BY RANGE (sale_date);

-- Partição atual (dezembro 2024)
CREATE TABLE sales.sales_2024_12 PARTITION OF sales.sales
    FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- Partições 2025
CREATE TABLE sales.sales_2025_01 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE sales.sales_2025_02 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

CREATE TABLE sales.sales_2025_03 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');

CREATE TABLE sales.sales_2025_04 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-04-01') TO ('2025-05-01');

CREATE TABLE sales.sales_2025_05 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-05-01') TO ('2025-06-01');

CREATE TABLE sales.sales_2025_06 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-06-01') TO ('2025-07-01');

CREATE TABLE sales.sales_2025_07 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-07-01') TO ('2025-08-01');

CREATE TABLE sales.sales_2025_08 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-08-01') TO ('2025-09-01');

CREATE TABLE sales.sales_2025_09 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');

CREATE TABLE sales.sales_2025_10 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');

CREATE TABLE sales.sales_2025_11 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

CREATE TABLE sales.sales_2025_12 PARTITION OF sales.sales
    FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

-- Migrar dados existentes
INSERT INTO sales.sales 
SELECT * FROM sales.sales_old;

-- Dropar índices antigos (estavam na tabela antiga)
DROP INDEX IF EXISTS sales.idx_sales_company_date;
DROP INDEX IF EXISTS sales.idx_sales_customer;
DROP INDEX IF EXISTS sales.idx_sales_status;
DROP INDEX IF EXISTS sales.idx_sales_number;

-- Recriar índices na tabela particionada
CREATE INDEX idx_sales_company_date ON sales.sales (company_id, sale_date);
CREATE INDEX idx_sales_customer ON sales.sales (customer_id) WHERE customer_id IS NOT NULL;
CREATE INDEX idx_sales_status ON sales.sales (status);
CREATE INDEX idx_sales_number ON sales.sales (sale_number);

-- Habilitar RLS
ALTER TABLE sales.sales ENABLE ROW LEVEL SECURITY;

CREATE POLICY sales_access_policy ON sales.sales
FOR ALL
USING (
    (is_employee() AND company_id = current_company_id())
    OR
    (is_customer() AND customer_id = current_customer_id())
);

-- NÃO dropar tabela antiga (tem foreign keys e views dependentes)
-- Manter sales_old como backup/referência
-- DROP TABLE sales.sales_old;

COMMENT ON TABLE sales.sales_old IS 'Tabela antiga mantida para compatibilidade com foreign keys existentes. Será removida em migration futura.';


-- ============================================================================
-- PART 3: TICKETS TABLE PARTITIONING
-- ============================================================================

ALTER TABLE sales.tickets RENAME TO tickets_old;

CREATE TABLE sales.tickets (
    id BIGINT NOT NULL,
    sale_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    seat_id BIGINT,
    ticket_type TEXT,
    ticket_number TEXT NOT NULL,
    seat TEXT,
    face_value DECIMAL(10,2) NOT NULL,
    service_fee DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    used BOOLEAN DEFAULT false,
    usage_date TIMESTAMP(0),
    created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- Dezembro 2024
CREATE TABLE sales.tickets_2024_12 PARTITION OF sales.tickets
    FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- 2025
CREATE TABLE sales.tickets_2025_01 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE sales.tickets_2025_02 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

CREATE TABLE sales.tickets_2025_03 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');

CREATE TABLE sales.tickets_2025_04 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-04-01') TO ('2025-05-01');

CREATE TABLE sales.tickets_2025_05 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-05-01') TO ('2025-06-01');

CREATE TABLE sales.tickets_2025_06 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-06-01') TO ('2025-07-01');

CREATE TABLE sales.tickets_2025_07 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-07-01') TO ('2025-08-01');

CREATE TABLE sales.tickets_2025_08 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-08-01') TO ('2025-09-01');

CREATE TABLE sales.tickets_2025_09 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');

CREATE TABLE sales.tickets_2025_10 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');

CREATE TABLE sales.tickets_2025_11 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

CREATE TABLE sales.tickets_2025_12 PARTITION OF sales.tickets
    FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

-- Migrar dados
INSERT INTO sales.tickets SELECT * FROM sales.tickets_old;

-- Dropar índices antigos
DROP INDEX IF EXISTS sales.idx_tickets_sale;
DROP INDEX IF EXISTS sales.idx_tickets_showtime;
DROP INDEX IF EXISTS sales.idx_tickets_number;

-- Recriar índices
CREATE INDEX idx_tickets_sale ON sales.tickets (sale_id);
CREATE INDEX idx_tickets_showtime ON sales.tickets (showtime_id);
CREATE INDEX idx_tickets_number ON sales.tickets (ticket_number);

-- NÃO dropar tabela antiga (tem foreign keys dependentes)
-- DROP TABLE sales.tickets_old;

COMMENT ON TABLE sales.tickets_old IS 'Tabela antiga mantida para compatibilidade. Será removida em migration futura.';


-- ============================================================================
-- PART 4: AUDIT_LOGS TABLE PARTITIONING
-- ============================================================================

ALTER TABLE audit.audit_logs RENAME TO audit_logs_old;

CREATE TABLE audit.audit_logs (
    id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    event_type TEXT NOT NULL,
    resource_type TEXT NOT NULL,
    resource_id BIGINT NOT NULL,
    action TEXT NOT NULL,
    user_id BIGINT,
    correlation_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- Dezembro 2024
CREATE TABLE audit.audit_logs_2024_12 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- 2025
CREATE TABLE audit.audit_logs_2025_01 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE audit.audit_logs_2025_02 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

CREATE TABLE audit.audit_logs_2025_03 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');

CREATE TABLE audit.audit_logs_2025_04 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-04-01') TO ('2025-05-01');

CREATE TABLE audit.audit_logs_2025_05 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-05-01') TO ('2025-06-01');

CREATE TABLE audit.audit_logs_2025_06 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-06-01') TO ('2025-07-01');

CREATE TABLE audit.audit_logs_2025_07 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-07-01') TO ('2025-08-01');

CREATE TABLE audit.audit_logs_2025_08 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-08-01') TO ('2025-09-01');

CREATE TABLE audit.audit_logs_2025_09 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');

CREATE TABLE audit.audit_logs_2025_10 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');

CREATE TABLE audit.audit_logs_2025_11 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

CREATE TABLE audit.audit_logs_2025_12 PARTITION OF audit.audit_logs
    FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

-- Migrar dados
INSERT INTO audit.audit_logs SELECT * FROM audit.audit_logs_old;

-- Dropar índices antigos
DROP INDEX IF EXISTS audit.idx_audit_company_date;
DROP INDEX IF EXISTS audit.idx_audit_resource;
DROP INDEX IF EXISTS audit.idx_audit_user;

-- Recriar índices
CREATE INDEX idx_audit_company_date ON audit.audit_logs (company_id, created_at);
CREATE INDEX idx_audit_resource ON audit.audit_logs (resource_type, resource_id);
CREATE INDEX idx_audit_user ON audit.audit_logs (user_id) WHERE user_id IS NOT NULL;

-- RLS
ALTER TABLE audit.audit_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY audit_logs_access_policy ON audit.audit_logs
FOR ALL
USING (is_employee() AND company_id = current_company_id());

-- NÃO dropar tabela antiga (tem views dependentes)
-- DROP TABLE audit.audit_logs_old;

COMMENT ON TABLE audit.audit_logs_old IS 'Tabela antiga mantida para compatibilidade. Será removida em migration futura.';


-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE sales.sales IS 'Tabela de vendas particionada por sale_date (mês). Partições criadas automaticamente via Spring Scheduler.';
COMMENT ON TABLE sales.tickets IS 'Tabela de tickets particionada por created_at (mês). Partições criadas automaticamente via Spring Scheduler.';
COMMENT ON TABLE audit.audit_logs IS 'Tabela de logs de auditoria particionada por created_at (mês). Partições criadas automaticamente via Spring Scheduler.';
