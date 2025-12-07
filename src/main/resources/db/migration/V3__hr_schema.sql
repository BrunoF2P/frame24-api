-- ============================================================================
-- Human Resources
-- ============================================================================
-- Migration: V3__hr_schema.sql
-- Description: Human Resources
-- ============================================================================

-- Human Resources

CREATE TABLE "hr"."departments" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "complex_id" BIGINT NOT NULL,
    "manager_id" BIGINT,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "cost_center" VARCHAR(50),
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "departments_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "hr"."employees" (
    "id" BIGINT NOT NULL,
    "person_id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "complex_id" BIGINT NOT NULL,
    "position_id" BIGINT NOT NULL,
    "employee_number" TEXT NOT NULL,
    "work_email" VARCHAR(100),
    "hire_date" DATE NOT NULL,
    "termination_date" DATE,
    "contract_type" BIGINT NOT NULL,
    "current_salary" DECIMAL(10,2) NOT NULL,
    "photo_url" VARCHAR(500),
    "address" TEXT,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(0),

    CONSTRAINT "employees_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "hr"."employment_contract_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "employment_contract_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "hr"."positions" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "department_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "base_salary" DECIMAL(10,2),
    "weekly_hours" INTEGER DEFAULT 44,
    "active" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "positions_pkey" PRIMARY KEY ("id")
);

