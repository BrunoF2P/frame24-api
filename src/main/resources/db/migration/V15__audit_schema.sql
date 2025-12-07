-- ============================================================================
-- Audit and Logging
-- ============================================================================
-- Migration: V15__audit_schema.sql
-- Description: Audit and Logging
-- ============================================================================

-- Audit and Logging

CREATE TABLE "audit"."audit_logs" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "event_type" TEXT NOT NULL,
    "resource_type" TEXT NOT NULL,
    "resource_id" BIGINT NOT NULL,
    "action" TEXT NOT NULL,
    "user_id" BIGINT,
    "correlation_id" BIGINT,
    "old_values" JSONB,
    "new_values" JSONB,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "audit_logs_pkey" PRIMARY KEY ("id")
);

