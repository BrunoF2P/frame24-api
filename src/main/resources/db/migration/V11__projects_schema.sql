-- ============================================================================
-- Project Management
-- ============================================================================
-- Migration: V11__projects_schema.sql
-- Description: Project Management
-- ============================================================================

-- Project Management

CREATE TABLE "projects"."recine_acquisition_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "recine_acquisition_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "projects"."recine_acquisitions" (
    "id" BIGINT NOT NULL,
    "recine_project_id" BIGINT NOT NULL,
    "acquisition_type" BIGINT,
    "item_type" BIGINT,
    "item_description" TEXT NOT NULL,
    "supplier" VARCHAR(200),
    "invoice_number" VARCHAR(50),
    "acquisition_date" DATE NOT NULL,
    "item_value" DECIMAL(15,2) NOT NULL,
    "pis_cofins_saved" DECIMAL(15,2) DEFAULT 0.00,
    "ipi_saved" DECIMAL(15,2) DEFAULT 0.00,
    "ii_saved" DECIMAL(15,2) DEFAULT 0.00,
    "total_benefit_value" DECIMAL(15,2) NOT NULL,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "recine_acquisitions_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "projects"."recine_deadline_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "recine_deadline_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "projects"."recine_deadlines" (
    "id" BIGINT NOT NULL,
    "project_id" BIGINT NOT NULL,
    "deadline_type" BIGINT,
    "due_date" DATE NOT NULL,
    "completion_date" DATE,
    "estimated_penalty" DECIMAL(15,2),
    "notes" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "recine_deadlines_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "projects"."recine_item_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "recine_item_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "projects"."recine_project_status" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "allows_modification" BOOLEAN DEFAULT true,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "recine_project_status_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "projects"."recine_project_types" (
    "id" BIGINT NOT NULL,
    "company_id" BIGINT NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "recine_project_types_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "projects"."recine_projects" (
    "id" BIGINT NOT NULL,
    "cinema_complex_id" BIGINT NOT NULL,
    "project_number" VARCHAR(50) NOT NULL,
    "description" TEXT NOT NULL,
    "project_type" BIGINT,
    "total_project_value" DECIMAL(15,2) NOT NULL,
    "estimated_benefit_value" DECIMAL(15,2) NOT NULL,
    "pis_cofins_suspended" DECIMAL(15,2) DEFAULT 0.00,
    "ipi_exempt" DECIMAL(15,2) DEFAULT 0.00,
    "ii_exempt" DECIMAL(15,2) DEFAULT 0.00,
    "start_date" DATE NOT NULL,
    "expected_completion_date" DATE NOT NULL,
    "actual_completion_date" DATE,
    "status" BIGINT,
    "ancine_process_number" VARCHAR(50),
    "ancine_approval_date" DATE,
    "observations" TEXT,
    "created_at" TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3),

    CONSTRAINT "recine_projects_pkey" PRIMARY KEY ("id")
);

