-- V30: Performance Indexes (CONCURRENT - non-transactional)
-- Author: Security Team
-- Date: 2025-12-07
-- Description: Performance indexes created with CONCURRENT to avoid table locks
-- Note: CREATE INDEX CONCURRENTLY cannot run inside a transaction block,
-- so this migration is separate from V29
-- =====================================================
-- PERFORMANCE INDEXES
-- =====================================================
-- Index for email lookups (login, password reset, etc)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_identities_email ON identity.identities(email) ;
-- Composite index for active and verified users (most common query)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_identities_active_verified ON identity.identities(active
                                                                                            , email_verified)
       WHERE active = true ;
-- Index for blocked accounts check
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_identities_blocked ON identity.identities(blocked_until)
       WHERE blocked_until IS NOT NULL ;
-- Index for employee_id lookups
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_company_users_employee_company ON identity.company_users(employee_id
                                                                                                   , company_id) ;
-- Index for password history lookups (get last N passwords)
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_password_history_identity_date ON identity.password_history(identity_id
                                                                                                      , created_at DESC) ;
-- Index for company users with role
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_company_users_role ON identity.company_users(role_id)
       WHERE active = true ;
