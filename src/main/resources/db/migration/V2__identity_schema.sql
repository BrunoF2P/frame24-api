-- ============================================================================
-- Identity and Access Management
-- ============================================================================
-- Migration: V2__identity_schema.sql
-- Description: Identity and Access Management
-- ============================================================================

-- Identity and Access Management

CREATE TABLE "identity"."companies" (
    "id" BIGINT NOT NULL,
    "corporate_name" VARCHAR(200) NOT NULL,
    "trade_name" VARCHAR(200),
    "cnpj" VARCHAR(18) NOT NULL,
    "zip_code" VARCHAR(10),
    "street_address" VARCHAR(300),
    "address_number" VARCHAR(20),
    "address_complement" VARCHAR(100),
    "neighborhood" VARCHAR(100),
    "city" VARCHAR(100),
    "state" VARCHAR(2),
    "country" VARCHAR(2) DEFAULT 'BR',
    "phone" VARCHAR(20),
    "mobile" VARCHAR(20),
    "email" VARCHAR(100),
    "website" VARCHAR(200),
    "state_registration" VARCHAR(20),
    "municipal_registration" VARCHAR(20),
    "recine_opt_in" BOOLEAN DEFAULT false,
    "recine_join_date" DATE,
    "tenant_slug" VARCHAR(50) NOT NULL,
    "logo_url" VARCHAR(500),
    "max_complexes" INTEGER DEFAULT 1,
    "max_employees" INTEGER DEFAULT 50,
    "max_storage_gb" INTEGER DEFAULT 10,
    "plan_type" "identity"."company_plan_type" NOT NULL DEFAULT 'BASIC',
    "plan_expires_at" DATE,
    "active" BOOLEAN DEFAULT true,
    "suspended" BOOLEAN DEFAULT false,
    "suspension_reason" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),
    "tax_regime" "identity"."tax_regime_type" NOT NULL,

    CONSTRAINT "companies_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."company_users" (
    "id" BIGINT NOT NULL,
    "identity_id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "employee_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "department" VARCHAR(100),
    "job_level" VARCHAR(50),
    "location" VARCHAR(100),
    "allowed_complexes" TEXT,
    "ip_whitelist" TEXT,
    "active" BOOLEAN DEFAULT true,
    "start_date" DATE DEFAULT CURRENT_TIMESTAMP,
    "end_date" DATE,
    "assigned_by" TEXT,
    "assigned_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "last_access" TIMESTAMP(0),
    "access_count" INTEGER DEFAULT 0,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "company_users_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."custom_roles" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "is_system_role" BOOLEAN NOT NULL DEFAULT false,
    "hierarchy_level" INTEGER DEFAULT 0,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "custom_roles_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."identities" (
    "id" BIGINT NOT NULL,
    "person_id" BIGINT,
    "email" VARCHAR(100) NOT NULL,
    "external_id" VARCHAR(200),
    "identity_type" "identity"."identity_type" NOT NULL DEFAULT 'CUSTOMER',
    "password_hash" VARCHAR(255),
    "password_changed_at" TIMESTAMP(0),
    "password_expires_at" TIMESTAMP(0),
    "active" BOOLEAN DEFAULT true,
    "email_verified" BOOLEAN DEFAULT false,
    "email_verification_token" VARCHAR(100),
    "email_verification_expires_at" TIMESTAMP(0),
    "blocked_until" TIMESTAMP(0),
    "block_reason" TEXT,
    "failed_login_attempts" INTEGER DEFAULT 0,
    "last_failed_login" TIMESTAMP(0),
    "requires_2fa" BOOLEAN DEFAULT false,
    "secret_2fa" VARCHAR(100),
    "backup_codes_2fa" TEXT,
    "reset_token" VARCHAR(100),
    "reset_token_expires_at" TIMESTAMP(0),
    "last_login_date" TIMESTAMP(0),
    "last_login_ip" VARCHAR(45),
    "last_user_agent" VARCHAR(500),
    "login_count" INTEGER DEFAULT 0,
    "linked_identity_id" BIGINT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "identities_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."permissions" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "resource" VARCHAR(100) NOT NULL,
    "action" VARCHAR(50) NOT NULL,
    "code" VARCHAR(150) NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "description" TEXT,
    "module" VARCHAR(50),
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "permissions_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."persons" (
    "id" BIGINT NOT NULL,
    "cpf" VARCHAR(14),
    "passport_number" VARCHAR(50),
    "full_name" VARCHAR(200) NOT NULL,
    "birth_date" DATE,
    "phone" VARCHAR(20),
    "mobile" VARCHAR(20),
    "email" VARCHAR(100),
    "zip_code" VARCHAR(10),
    "street_address" VARCHAR(300),
    "address_number" VARCHAR(20),
    "address_complement" VARCHAR(100),
    "neighborhood" VARCHAR(100),
    "city" VARCHAR(100),
    "state" VARCHAR(2),
    "country" VARCHAR(2) DEFAULT 'BR',
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "persons_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."role_permissions" (
    "id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "permission_id" BIGINT NOT NULL,
    "conditions" TEXT,
    "granted_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "granted_by" TEXT,

    CONSTRAINT "role_permissions_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."user_attributes" (
    "id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "key" VARCHAR(100) NOT NULL,
    "value" VARCHAR(500) NOT NULL,
    "data_type" VARCHAR(20),
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),
    "created_by" TEXT,

    CONSTRAINT "user_attributes_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "identity"."user_sessions" (
    "id" BIGINT NOT NULL,
    "identity_id" BIGINT NOT NULL,
    "company_id" BIGINT,
    "access_token_hash" VARCHAR(255) NOT NULL,
    "refresh_token_hash" VARCHAR(255),
    "session_id" VARCHAR(100) NOT NULL,
    "session_context" "identity"."session_context" NOT NULL,
    "expires_at" TIMESTAMP(0) NOT NULL,
    "last_activity" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "ip_address" VARCHAR(45),
    "user_agent" VARCHAR(500),
    "device_fingerprint" VARCHAR(255),
    "active" BOOLEAN DEFAULT true,
    "revoked" BOOLEAN DEFAULT false,
    "revoked_at" TIMESTAMP(0),
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "user_sessions_pkey" PRIMARY KEY ("id")
);

