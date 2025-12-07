-- V20: Security Improvements - Password History, Account Blocking, and Performance Indexes
-- Author: Security Team
-- Date: 2025-12-07
-- Description: Adds password history tracking, improves account security, and optimizes query performance
-- =====================================================
-- 1. CREATE PASSWORD HISTORY TABLE
-- =====================================================
CREATE TABLE IF NOT EXISTS identity.password_history(id BIGINT PRIMARY KEY
                                                                , identity_id BIGINT
                                                                  NOT NULL
             , password_hash VARCHAR (255)
               NOT NULL
             , created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
 NOT NULL
    , CONSTRAINT fk_password_history_identity FOREIGN KEY
      (identity_id) REFERENCES identity.identities(id) ON
DELETE CASCADE) ; COMMENT
ON TABLE identity.password_history IS 'Histórico de senhas dos usuários para prevenir reutilização' ; COMMENT ON COLUMN identity.password_history.id IS 'ID único do registro (Snowflake)' ; COMMENT ON COLUMN identity.password_history.identity_id IS 'Referência ao identity (chave estrangeira)' ; COMMENT ON COLUMN identity.password_history.password_hash IS 'Hash BCrypt da senha antiga' ; COMMENT ON COLUMN identity.password_history.created_at IS 'Data/hora de criação do registro' ;
            -- =====================================================
            -- 2. ADD NEW COLUMN TO IDENTITIES
            -- =====================================================
ALTER TABLE identity.identities
        ADD COLUMN IF NOT EXISTS last_password_change TIMESTAMP
WITH TIME ZONE ; COMMENT
ON COLUMN identity.identities.last_password_change IS 'Data/hora da última mudança de senha' ;
                                                                -- Populate last_password_change with existing password_changed_at
                                                                UPDATE identity.identities
                                                                   SET last_password_change = password_changed_at
                                                                 WHERE last_password_change IS NULL
                                                                   AND password_changed_at IS NOT NULL ;
-- =====================================================
-- 3. CREATE FUNCTION TO CLEANUP OLD PASSWORD HISTORY
-- =====================================================
CREATE OR REPLACE FUNCTION identity.cleanup_old_password_history
  (
  ) RETURNS void LANGUAGE plpgsql AS $$ BEGIN
                                                                 -- Keep only last 5 passwords per identity
                                                                 DELETE FROM identity.password_history ph
                                                                  WHERE ph.id IN ( SELECT id
                                                                                     FROM ( SELECT id
                                                                                                   , ROW_NUMBER() OVER(PARTITION BY identity_id
                                                                                                                       ORDER BY created_at DESC) AS rn
                                                                                              FROM identity.password_history ) t
                                                                                    WHERE t.rn > 5 ) ;
END ; $$ ; COMMENT
ON FUNCTION identity.cleanup_old_password_history() IS 'Remove histórico de senhas antigas, mantendo apenas as últimas 5 por usuário' ;
