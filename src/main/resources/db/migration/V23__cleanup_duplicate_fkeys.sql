-- ============================================================================
-- Cleanup Duplicate Foreign Keys (All Schemas)
-- ============================================================================
-- Migration: V23__cleanup_duplicate_identity_fks.sql
-- Description: Drop legacy foreign key constraints (ending in _fkey) that
--              overlap with V16 constraints (prefixed with fk_).
--              Covers all schemas: identity, hr, finance, crm, sales,
--              operations, inventory, marketing, catalog, projects, contracts,
--              tax, stock, audit.
-- ============================================================================

-- ============================================================================
-- IDENTITY SCHEMA
-- ============================================================================
ALTER TABLE "identity"."identities" DROP CONSTRAINT IF EXISTS "identities_person_id_fkey";
ALTER TABLE "identity"."identities" DROP CONSTRAINT IF EXISTS "identities_linked_identity_id_fkey";
ALTER TABLE "identity"."custom_roles" DROP CONSTRAINT IF EXISTS "custom_roles_company_id_fkey";
ALTER TABLE "identity"."permissions" DROP CONSTRAINT IF EXISTS "permissions_company_id_fkey";
ALTER TABLE "identity"."role_permissions" DROP CONSTRAINT IF EXISTS "role_permissions_permission_id_fkey";
ALTER TABLE "identity"."role_permissions" DROP CONSTRAINT IF EXISTS "role_permissions_role_id_fkey";
ALTER TABLE "identity"."company_users" DROP CONSTRAINT IF EXISTS "company_users_company_id_fkey";
ALTER TABLE "identity"."company_users" DROP CONSTRAINT IF EXISTS "company_users_identity_id_fkey";
ALTER TABLE "identity"."company_users" DROP CONSTRAINT IF EXISTS "company_users_role_id_fkey";
ALTER TABLE "identity"."user_attributes" DROP CONSTRAINT IF EXISTS "user_attributes_user_id_fkey";
ALTER TABLE "identity"."user_sessions" DROP CONSTRAINT IF EXISTS "user_sessions_identity_id_fkey";

-- ============================================================================
-- HR SCHEMA
-- ============================================================================
ALTER TABLE "hr"."departments" DROP CONSTRAINT IF EXISTS "departments_manager_id_fkey";
ALTER TABLE "hr"."positions" DROP CONSTRAINT IF EXISTS "positions_department_id_fkey";
ALTER TABLE "hr"."employees" DROP CONSTRAINT IF EXISTS "employees_contract_type_fkey";
ALTER TABLE "hr"."employees" DROP CONSTRAINT IF EXISTS "employees_position_id_fkey";

-- ============================================================================
-- FINANCE SCHEMA
-- ============================================================================
ALTER TABLE "finance"."distributor_settlements" DROP CONSTRAINT IF EXISTS "distributor_settlements_calculation_base_fkey";
ALTER TABLE "finance"."distributor_settlements" DROP CONSTRAINT IF EXISTS "distributor_settlements_status_fkey";
ALTER TABLE "finance"."journal_entries" DROP CONSTRAINT IF EXISTS "journal_entries_entry_type_fkey";
ALTER TABLE "finance"."journal_entries" DROP CONSTRAINT IF EXISTS "journal_entries_status_fkey";
ALTER TABLE "finance"."journal_entry_items" DROP CONSTRAINT IF EXISTS "journal_entry_items_account_id_fkey";
ALTER TABLE "finance"."journal_entry_items" DROP CONSTRAINT IF EXISTS "journal_entry_items_journal_entry_id_fkey";
ALTER TABLE "finance"."journal_entry_items" DROP CONSTRAINT IF EXISTS "journal_entry_items_movement_type_fkey";
ALTER TABLE "finance"."chart_of_accounts" DROP CONSTRAINT IF EXISTS "chart_of_accounts_account_nature_fkey";
ALTER TABLE "finance"."chart_of_accounts" DROP CONSTRAINT IF EXISTS "chart_of_accounts_account_type_fkey";
ALTER TABLE "finance"."chart_of_accounts" DROP CONSTRAINT IF EXISTS "chart_of_accounts_parent_account_id_fkey";
ALTER TABLE "finance"."cash_flow_entries" DROP CONSTRAINT IF EXISTS "cash_flow_entries_bank_account_id_fkey";
ALTER TABLE "finance"."bank_reconciliations" DROP CONSTRAINT IF EXISTS "bank_reconciliations_bank_account_id_fkey";
ALTER TABLE "finance"."payable_transactions" DROP CONSTRAINT IF EXISTS "payable_transactions_account_payable_id_fkey";
ALTER TABLE "finance"."receivable_transactions" DROP CONSTRAINT IF EXISTS "receivable_transactions_account_receivable_id_fkey";

-- ============================================================================
-- CRM SCHEMA
-- ============================================================================
ALTER TABLE "crm"."customer_preferences" DROP CONSTRAINT IF EXISTS "customer_preferences_company_customer_id_fkey";
ALTER TABLE "crm"."customer_preferred_rows" DROP CONSTRAINT IF EXISTS "customer_preferred_rows_company_customer_id_fkey";
ALTER TABLE "crm"."customer_preferred_times" DROP CONSTRAINT IF EXISTS "customer_preferred_times_company_customer_id_fkey";
ALTER TABLE "crm"."customer_preferred_weekdays" DROP CONSTRAINT IF EXISTS "customer_preferred_weekdays_company_customer_id_fkey";
ALTER TABLE "crm"."customer_favorite_combos" DROP CONSTRAINT IF EXISTS "customer_favorite_combos_company_customer_id_fkey";
ALTER TABLE "crm"."customer_favorite_genres" DROP CONSTRAINT IF EXISTS "customer_favorite_genres_company_customer_id_fkey";
ALTER TABLE "crm"."customer_interactions" DROP CONSTRAINT IF EXISTS "customer_interactions_company_customer_id_fkey";
ALTER TABLE "crm"."customer_points" DROP CONSTRAINT IF EXISTS "customer_points_company_customer_id_fkey";
ALTER TABLE "crm"."customer_favorite_products" DROP CONSTRAINT IF EXISTS "customer_favorite_products_company_customer_id_fkey";
ALTER TABLE "crm"."company_customers" DROP CONSTRAINT IF EXISTS "company_customers_customer_id_fkey";

-- ============================================================================
-- SALES SCHEMA
-- ============================================================================
ALTER TABLE "sales"."sales" DROP CONSTRAINT IF EXISTS "sales_payment_method_fkey";
ALTER TABLE "sales"."sales" DROP CONSTRAINT IF EXISTS "sales_sale_type_fkey";
ALTER TABLE "sales"."sales" DROP CONSTRAINT IF EXISTS "sales_status_fkey";
ALTER TABLE "sales"."tickets" DROP CONSTRAINT IF EXISTS "tickets_sale_id_fkey";
ALTER TABLE "sales"."tickets" DROP CONSTRAINT IF EXISTS "tickets_ticket_type_fkey";
ALTER TABLE "sales"."concession_sales" DROP CONSTRAINT IF EXISTS "concession_sales_sale_id_fkey";
ALTER TABLE "sales"."concession_sales" DROP CONSTRAINT IF EXISTS "concession_sales_status_fkey";
ALTER TABLE "sales"."concession_sale_items" DROP CONSTRAINT IF EXISTS "concession_sale_items_concession_sale_id_fkey";
ALTER TABLE "sales"."promotions_used" DROP CONSTRAINT IF EXISTS "promotions_used_sale_id_fkey";

-- ============================================================================
-- OPERATIONS SCHEMA
-- ============================================================================
ALTER TABLE "operations"."rooms" DROP CONSTRAINT IF EXISTS "rooms_audio_type_fkey";
ALTER TABLE "operations"."rooms" DROP CONSTRAINT IF EXISTS "rooms_cinema_complex_id_fkey";
ALTER TABLE "operations"."rooms" DROP CONSTRAINT IF EXISTS "rooms_projection_type_fkey";
ALTER TABLE "operations"."seats" DROP CONSTRAINT IF EXISTS "seats_room_id_fkey";
ALTER TABLE "operations"."seats" DROP CONSTRAINT IF EXISTS "seats_seat_type_fkey";
ALTER TABLE "operations"."session_seat_status" DROP CONSTRAINT IF EXISTS "session_seat_status_seat_id_fkey";
ALTER TABLE "operations"."session_seat_status" DROP CONSTRAINT IF EXISTS "session_seat_status_showtime_id_fkey";
ALTER TABLE "operations"."showtime_schedule" DROP CONSTRAINT IF EXISTS "showtime_schedule_audio_type_fkey";
ALTER TABLE "operations"."showtime_schedule" DROP CONSTRAINT IF EXISTS "showtime_schedule_cinema_complex_id_fkey";
ALTER TABLE "operations"."showtime_schedule" DROP CONSTRAINT IF EXISTS "showtime_schedule_projection_type_fkey";
ALTER TABLE "operations"."showtime_schedule" DROP CONSTRAINT IF EXISTS "showtime_schedule_room_id_fkey";
ALTER TABLE "operations"."showtime_schedule" DROP CONSTRAINT IF EXISTS "showtime_schedule_session_language_fkey";
ALTER TABLE "operations"."showtime_schedule" DROP CONSTRAINT IF EXISTS "showtime_schedule_status_fkey";

-- ============================================================================
-- INVENTORY SCHEMA
-- ============================================================================
ALTER TABLE "inventory"."suppliers" DROP CONSTRAINT IF EXISTS "suppliers_supplier_type_id_fkey";

-- ============================================================================
-- MARKETING SCHEMA
-- ============================================================================
ALTER TABLE "marketing"."campaign_categories" DROP CONSTRAINT IF EXISTS "campaign_categories_campaign_id_fkey";
ALTER TABLE "marketing"."campaign_complexes" DROP CONSTRAINT IF EXISTS "campaign_complexes_campaign_id_fkey";
ALTER TABLE "marketing"."campaign_movies" DROP CONSTRAINT IF EXISTS "campaign_movies_campaign_id_fkey";
ALTER TABLE "marketing"."campaign_rooms" DROP CONSTRAINT IF EXISTS "campaign_rooms_campaign_id_fkey";
ALTER TABLE "marketing"."campaign_session_types" DROP CONSTRAINT IF EXISTS "campaign_session_types_campaign_id_fkey";
ALTER TABLE "marketing"."campaign_weekdays" DROP CONSTRAINT IF EXISTS "campaign_weekdays_campaign_id_fkey";
ALTER TABLE "marketing"."promotional_campaigns" DROP CONSTRAINT IF EXISTS "promotional_campaigns_promotion_type_id_fkey";
ALTER TABLE "marketing"."promotional_coupons" DROP CONSTRAINT IF EXISTS "promotional_coupons_campaign_id_fkey";

-- ============================================================================
-- CATALOG SCHEMA
-- ============================================================================
ALTER TABLE "catalog"."combo_products" DROP CONSTRAINT IF EXISTS "combo_products_combo_id_fkey";
ALTER TABLE "catalog"."combo_products" DROP CONSTRAINT IF EXISTS "combo_products_product_id_fkey";
ALTER TABLE "catalog"."movie_cast" DROP CONSTRAINT IF EXISTS "movie_cast_cast_type_fkey";
ALTER TABLE "catalog"."movie_cast" DROP CONSTRAINT IF EXISTS "movie_cast_movie_id_fkey";
ALTER TABLE "catalog"."movie_media" DROP CONSTRAINT IF EXISTS "movie_media_media_type_fkey";
ALTER TABLE "catalog"."movie_media" DROP CONSTRAINT IF EXISTS "movie_media_movie_id_fkey";
ALTER TABLE "catalog"."movies" DROP CONSTRAINT IF EXISTS "movies_age_rating_id_fkey";
ALTER TABLE "catalog"."movies_on_categories" DROP CONSTRAINT IF EXISTS "movies_on_categories_category_id_fkey";
ALTER TABLE "catalog"."movies_on_categories" DROP CONSTRAINT IF EXISTS "movies_on_categories_movie_id_fkey";
ALTER TABLE "catalog"."product_prices" DROP CONSTRAINT IF EXISTS "product_prices_product_id_fkey";
ALTER TABLE "catalog"."products" DROP CONSTRAINT IF EXISTS "products_category_id_fkey";

-- ============================================================================
-- PROJECTS SCHEMA
-- ============================================================================
ALTER TABLE "projects"."recine_acquisitions" DROP CONSTRAINT IF EXISTS "recine_acquisitions_recine_project_id_fkey";
ALTER TABLE "projects"."recine_deadlines" DROP CONSTRAINT IF EXISTS "recine_deadlines_project_id_fkey";

-- ============================================================================
-- CONTRACTS SCHEMA
-- ============================================================================
ALTER TABLE "contracts"."exhibition_contract_sliding_scales" DROP CONSTRAINT IF EXISTS "exhibition_contract_sliding_scales_contract_id_fkey";
ALTER TABLE "contracts"."exhibition_contracts" DROP CONSTRAINT IF EXISTS "exhibition_contracts_contract_type_fkey";

-- ============================================================================
-- TAX SCHEMA
-- ============================================================================
ALTER TABLE "tax"."monthly_tax_settlement" DROP CONSTRAINT IF EXISTS "monthly_tax_settlement_status_fkey";

-- ============================================================================
-- STOCK SCHEMA
-- ============================================================================
ALTER TABLE "stock"."stock_movements" DROP CONSTRAINT IF EXISTS "stock_movements_movement_type_fkey";


-- ============================================================================
-- PART 2: CLEANUP DUPLICATE INDEXES
-- ============================================================================
-- Remove regular indexes that duplicate unique indexes
-- (unique indexes already provide search functionality)
-- ============================================================================

-- identity.companies: idx_companies_cnpj duplicates uq_companies_cnpj
DROP INDEX IF EXISTS "identity"."idx_companies_cnpj";

-- identity.companies: idx_companies_tenant_slug duplicates uq_companies_tenant_slug
DROP INDEX IF EXISTS "identity"."idx_companies_tenant_slug";

-- identity.company_users: idx_company_users_company_id_identity_id duplicates uq_company_users_company_id_identity_id
DROP INDEX IF EXISTS "identity"."idx_company_users_company_id_identity_id";

-- crm.customers: idx_customers_cpf duplicates uq_customers_cpf
DROP INDEX IF EXISTS "crm"."idx_customers_cpf";

-- catalog.combos: idx_combos_combo_code duplicates uq_combos_combo_code
DROP INDEX IF EXISTS "catalog"."idx_combos_combo_code";

-- catalog.products: idx_products_barcode duplicates uq_products_barcode
DROP INDEX IF EXISTS "catalog"."idx_products_barcode";

-- catalog.products: idx_products_product_code duplicates uq_products_product_code (composite index is different but single-column can be removed)
DROP INDEX IF EXISTS "catalog"."idx_products_product_code";

-- marketing.promotional_campaigns: idx_promotional_campaigns_campaign_code duplicates uq_promotional_campaigns_campaign_code
DROP INDEX IF EXISTS "marketing"."idx_promotional_campaigns_campaign_code";

-- marketing.promotional_coupons: idx_promotional_coupons_coupon_code duplicates uq_promotional_coupons_coupon_code
DROP INDEX IF EXISTS "marketing"."idx_promotional_coupons_coupon_code";

-- operations.cinema_complexes: idx_cinema_complexes_code duplicates uq_cinema_complexes_code
DROP INDEX IF EXISTS "operations"."idx_cinema_complexes_code";


-- ============================================================================
-- PART 3: DROP LEGACY _OLD TABLES FROM PARTITIONING
-- ============================================================================
-- These tables were created in V19 as backups during partitioning
-- They are no longer needed - data was migrated to new partitioned tables
-- ============================================================================

-- Drop foreign key references first (if any exist)
ALTER TABLE "sales"."tickets_old" DROP CONSTRAINT IF EXISTS "fk_tickets_sales_sale";
ALTER TABLE "sales"."concession_sales" DROP CONSTRAINT IF EXISTS "fk_concession_sales_sales_sale";
ALTER TABLE "sales"."promotions_used" DROP CONSTRAINT IF EXISTS "fk_promotions_used_sales_sale";

-- Drop the legacy backup tables
DROP TABLE IF EXISTS "sales"."sales_old" CASCADE;
DROP TABLE IF EXISTS "sales"."tickets_old" CASCADE;
DROP TABLE IF EXISTS "audit"."audit_logs_old" CASCADE;
