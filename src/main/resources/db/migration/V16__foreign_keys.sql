-- ============================================================================
-- Add all foreign key constraints and indexes
-- ============================================================================
-- Migration: V16__foreign_keys.sql
-- Description: Add all foreign key constraints and indexes
-- ============================================================================

-- ============================================================================
-- FOREIGN KEY CONSTRAINTS
-- ============================================================================
-- Naming convention: fk_{source_table}_{target_table}_{column}
-- ============================================================================

ALTER TABLE "catalog"."combo_products" ADD CONSTRAINT "fk_combo_products_combos_combo" FOREIGN KEY ("combo_id") REFERENCES "catalog"."combos"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "catalog"."combo_products" ADD CONSTRAINT "fk_combo_products_products_product" FOREIGN KEY ("product_id") REFERENCES "catalog"."products"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "catalog"."movie_cast" ADD CONSTRAINT "fk_movie_cast_cast_types_cast_type" FOREIGN KEY ("cast_type") REFERENCES "catalog"."cast_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "catalog"."movie_cast" ADD CONSTRAINT "fk_movie_cast_movies_movie" FOREIGN KEY ("movie_id") REFERENCES "catalog"."movies"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "catalog"."movie_media" ADD CONSTRAINT "fk_movie_media_media_types_media_type" FOREIGN KEY ("media_type") REFERENCES "catalog"."media_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "catalog"."movie_media" ADD CONSTRAINT "fk_movie_media_movies_movie" FOREIGN KEY ("movie_id") REFERENCES "catalog"."movies"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "catalog"."movies" ADD CONSTRAINT "fk_movies_age_ratings_age_rating" FOREIGN KEY ("age_rating_id") REFERENCES "catalog"."age_ratings"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- Removed: movie_categoriesId is TEXT field, not a foreign key

-- Removed: age_ratingsId is TEXT field, not a foreign key

ALTER TABLE "catalog"."movies_on_categories" ADD CONSTRAINT "fk_movies_on_categories_movie_categories_category" FOREIGN KEY ("category_id") REFERENCES "catalog"."movie_categories"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "catalog"."movies_on_categories" ADD CONSTRAINT "fk_movies_on_categories_movies_movie" FOREIGN KEY ("movie_id") REFERENCES "catalog"."movies"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "catalog"."product_prices" ADD CONSTRAINT "fk_product_prices_products_product" FOREIGN KEY ("product_id") REFERENCES "catalog"."products"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "catalog"."products" ADD CONSTRAINT "fk_products_product_categories_category" FOREIGN KEY ("category_id") REFERENCES "catalog"."product_categories"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "contracts"."exhibition_contract_sliding_scales" ADD CONSTRAINT "fk_exhibition_contract_sliding_scales_exhibition_contracts_contract" FOREIGN KEY ("contract_id") REFERENCES "contracts"."exhibition_contracts"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "contracts"."exhibition_contracts" ADD CONSTRAINT "fk_exhibition_contracts_contract_types_contract_type" FOREIGN KEY ("contract_type") REFERENCES "contracts"."contract_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "crm"."company_customers" ADD CONSTRAINT "fk_company_customers_customers_customer" FOREIGN KEY ("customer_id") REFERENCES "crm"."customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_favorite_combos" ADD CONSTRAINT "fk_customer_favorite_combos_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_favorite_genres" ADD CONSTRAINT "fk_customer_favorite_genres_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_favorite_products" ADD CONSTRAINT "fk_customer_favorite_products_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_interactions" ADD CONSTRAINT "fk_customer_interactions_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_points" ADD CONSTRAINT "fk_customer_points_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_preferences" ADD CONSTRAINT "fk_customer_preferences_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_preferred_rows" ADD CONSTRAINT "fk_customer_preferred_rows_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_preferred_times" ADD CONSTRAINT "fk_customer_preferred_times_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "crm"."customer_preferred_weekdays" ADD CONSTRAINT "fk_customer_preferred_weekdays_company_customers_company_customer" FOREIGN KEY ("company_customer_id") REFERENCES "crm"."company_customers"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "finance"."bank_reconciliations" ADD CONSTRAINT "fk_bank_reconciliations_bank_accounts_bank_account" FOREIGN KEY ("bank_account_id") REFERENCES "finance"."bank_accounts"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "finance"."cash_flow_entries" ADD CONSTRAINT "fk_cash_flow_entries_bank_accounts_bank_account" FOREIGN KEY ("bank_account_id") REFERENCES "finance"."bank_accounts"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "finance"."chart_of_accounts" ADD CONSTRAINT "fk_chart_of_accounts_account_natures_account_nature" FOREIGN KEY ("account_nature") REFERENCES "finance"."account_natures"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."chart_of_accounts" ADD CONSTRAINT "fk_chart_of_accounts_account_types_account_type" FOREIGN KEY ("account_type") REFERENCES "finance"."account_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."chart_of_accounts" ADD CONSTRAINT "fk_chart_of_accounts_chart_of_accounts_parent_account" FOREIGN KEY ("parent_account_id") REFERENCES "finance"."chart_of_accounts"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."distributor_settlements" ADD CONSTRAINT "fk_distributor_settlements_distributor_settlement_status_status" FOREIGN KEY ("status") REFERENCES "finance"."distributor_settlement_status"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."distributor_settlements" ADD CONSTRAINT "fk_distributor_settlements_settlement_bases_calculation_base" FOREIGN KEY ("calculation_base") REFERENCES "finance"."settlement_bases"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."journal_entries" ADD CONSTRAINT "fk_journal_entries_journal_entry_status_status" FOREIGN KEY ("status") REFERENCES "finance"."journal_entry_status"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."journal_entries" ADD CONSTRAINT "fk_journal_entries_journal_entry_types_entry_type" FOREIGN KEY ("entry_type") REFERENCES "finance"."journal_entry_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."journal_entry_items" ADD CONSTRAINT "fk_journal_entry_items_accounting_movement_types_movement_type" FOREIGN KEY ("movement_type") REFERENCES "finance"."accounting_movement_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "finance"."journal_entry_items" ADD CONSTRAINT "fk_journal_entry_items_chart_of_accounts_account" FOREIGN KEY ("account_id") REFERENCES "finance"."chart_of_accounts"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "finance"."journal_entry_items" ADD CONSTRAINT "fk_journal_entry_items_journal_entries_journal_entry" FOREIGN KEY ("journal_entry_id") REFERENCES "finance"."journal_entries"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "finance"."payable_transactions" ADD CONSTRAINT "fk_payable_transactions_accounts_payable_account_payable" FOREIGN KEY ("account_payable_id") REFERENCES "finance"."accounts_payable"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "finance"."receivable_transactions" ADD CONSTRAINT "fk_receivable_transactions_accounts_receivable_account_receivable" FOREIGN KEY ("account_receivable_id") REFERENCES "finance"."accounts_receivable"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "hr"."departments" ADD CONSTRAINT "fk_departments_employees_manager" FOREIGN KEY ("manager_id") REFERENCES "hr"."employees"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "hr"."employees" ADD CONSTRAINT "fk_employees_employment_contract_types_contract_type" FOREIGN KEY ("contract_type") REFERENCES "hr"."employment_contract_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "hr"."employees" ADD CONSTRAINT "fk_employees_positions_position" FOREIGN KEY ("position_id") REFERENCES "hr"."positions"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "hr"."positions" ADD CONSTRAINT "fk_positions_departments_department" FOREIGN KEY ("department_id") REFERENCES "hr"."departments"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "identity"."company_users" ADD CONSTRAINT "fk_company_users_companies_company" FOREIGN KEY ("company_id") REFERENCES "identity"."companies"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "identity"."company_users" ADD CONSTRAINT "fk_company_users_custom_roles_role" FOREIGN KEY ("role_id") REFERENCES "identity"."custom_roles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "identity"."company_users" ADD CONSTRAINT "fk_company_users_identities_identity" FOREIGN KEY ("identity_id") REFERENCES "identity"."identities"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "identity"."custom_roles" ADD CONSTRAINT "fk_custom_roles_companies_company" FOREIGN KEY ("company_id") REFERENCES "identity"."companies"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "identity"."identities" ADD CONSTRAINT "fk_identities_identities_linkedentity" FOREIGN KEY ("linked_identity_id") REFERENCES "identity"."identities"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "identity"."identities" ADD CONSTRAINT "fk_identities_persons_person" FOREIGN KEY ("person_id") REFERENCES "identity"."persons"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "identity"."permissions" ADD CONSTRAINT "fk_permissions_companies_company" FOREIGN KEY ("company_id") REFERENCES "identity"."companies"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "identity"."role_permissions" ADD CONSTRAINT "fk_role_permissions_custom_roles_role" FOREIGN KEY ("role_id") REFERENCES "identity"."custom_roles"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "identity"."role_permissions" ADD CONSTRAINT "fk_role_permissions_permissions_permission" FOREIGN KEY ("permission_id") REFERENCES "identity"."permissions"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "identity"."user_attributes" ADD CONSTRAINT "fk_user_attributes_company_users_user" FOREIGN KEY ("user_id") REFERENCES "identity"."company_users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "identity"."user_sessions" ADD CONSTRAINT "fk_user_sessions_identities_identity" FOREIGN KEY ("identity_id") REFERENCES "identity"."identities"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "inventory"."suppliers" ADD CONSTRAINT "fk_suppliers_supplier_types_supplier_type" FOREIGN KEY ("supplier_type_id") REFERENCES "inventory"."supplier_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "marketing"."campaign_categories" ADD CONSTRAINT "fk_campaign_categories_promotional_campaigns_campaign" FOREIGN KEY ("campaign_id") REFERENCES "marketing"."promotional_campaigns"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "marketing"."campaign_complexes" ADD CONSTRAINT "fk_campaign_complexes_promotional_campaigns_campaign" FOREIGN KEY ("campaign_id") REFERENCES "marketing"."promotional_campaigns"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "marketing"."campaign_movies" ADD CONSTRAINT "fk_campaign_movies_promotional_campaigns_campaign" FOREIGN KEY ("campaign_id") REFERENCES "marketing"."promotional_campaigns"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "marketing"."campaign_rooms" ADD CONSTRAINT "fk_campaign_rooms_promotional_campaigns_campaign" FOREIGN KEY ("campaign_id") REFERENCES "marketing"."promotional_campaigns"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "marketing"."campaign_session_types" ADD CONSTRAINT "fk_campaign_session_types_promotional_campaigns_campaign" FOREIGN KEY ("campaign_id") REFERENCES "marketing"."promotional_campaigns"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "marketing"."campaign_weekdays" ADD CONSTRAINT "fk_campaign_weekdays_promotional_campaigns_campaign" FOREIGN KEY ("campaign_id") REFERENCES "marketing"."promotional_campaigns"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "marketing"."promotional_campaigns" ADD CONSTRAINT "fk_promotional_campaigns_promotion_types_promotion_type" FOREIGN KEY ("promotion_type_id") REFERENCES "marketing"."promotion_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "marketing"."promotional_coupons" ADD CONSTRAINT "fk_promotional_coupons_promotional_campaigns_campaign" FOREIGN KEY ("campaign_id") REFERENCES "marketing"."promotional_campaigns"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "operations"."rooms" ADD CONSTRAINT "fk_rooms_audio_types_audio_type" FOREIGN KEY ("audio_type") REFERENCES "operations"."audio_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "operations"."rooms" ADD CONSTRAINT "fk_rooms_cinema_complexes_cinema_complex" FOREIGN KEY ("cinema_complex_id") REFERENCES "operations"."cinema_complexes"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "operations"."rooms" ADD CONSTRAINT "fk_rooms_projection_types_projection_type" FOREIGN KEY ("projection_type") REFERENCES "operations"."projection_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "operations"."seats" ADD CONSTRAINT "fk_seats_rooms_room" FOREIGN KEY ("room_id") REFERENCES "operations"."rooms"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "operations"."seats" ADD CONSTRAINT "fk_seats_seat_types_seat_type" FOREIGN KEY ("seat_type") REFERENCES "operations"."seat_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- Removed: status is now seat_status_enum, not a foreign key to seat_status table

ALTER TABLE "operations"."session_seat_status" ADD CONSTRAINT "fk_session_seat_status_seats_seat" FOREIGN KEY ("seat_id") REFERENCES "operations"."seats"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "operations"."session_seat_status" ADD CONSTRAINT "fk_session_seat_status_showtime_schedule_showtime" FOREIGN KEY ("showtime_id") REFERENCES "operations"."showtime_schedule"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "operations"."showtime_schedule" ADD CONSTRAINT "fk_showtime_schedule_audio_types_audio_type" FOREIGN KEY ("audio_type") REFERENCES "operations"."audio_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "operations"."showtime_schedule" ADD CONSTRAINT "fk_showtime_schedule_cinema_complexes_cinema_complex" FOREIGN KEY ("cinema_complex_id") REFERENCES "operations"."cinema_complexes"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "operations"."showtime_schedule" ADD CONSTRAINT "fk_showtime_schedule_projection_types_projection_type" FOREIGN KEY ("projection_type") REFERENCES "operations"."projection_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "operations"."showtime_schedule" ADD CONSTRAINT "fk_showtime_schedule_rooms_room" FOREIGN KEY ("room_id") REFERENCES "operations"."rooms"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "operations"."showtime_schedule" ADD CONSTRAINT "fk_showtime_schedule_session_languages_session_language" FOREIGN KEY ("session_language") REFERENCES "operations"."session_languages"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "operations"."showtime_schedule" ADD CONSTRAINT "fk_showtime_schedule_session_status_status" FOREIGN KEY ("status") REFERENCES "operations"."session_status"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "projects"."recine_acquisitions" ADD CONSTRAINT "fk_recine_acquisitions_recine_projects_recine_project" FOREIGN KEY ("recine_project_id") REFERENCES "projects"."recine_projects"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "projects"."recine_deadlines" ADD CONSTRAINT "fk_recine_deadlines_recine_projects_project" FOREIGN KEY ("project_id") REFERENCES "projects"."recine_projects"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "sales"."concession_sale_items" ADD CONSTRAINT "fk_concession_sale_items_concession_sales_concession_sale" FOREIGN KEY ("concession_sale_id") REFERENCES "sales"."concession_sales"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "sales"."concession_sales" ADD CONSTRAINT "fk_concession_sales_concession_status_status" FOREIGN KEY ("status") REFERENCES "sales"."concession_status"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "sales"."concession_sales" ADD CONSTRAINT "fk_concession_sales_sales_sale" FOREIGN KEY ("sale_id") REFERENCES "sales"."sales"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "sales"."promotions_used" ADD CONSTRAINT "fk_promotions_used_sales_sale" FOREIGN KEY ("sale_id") REFERENCES "sales"."sales"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "sales"."sales" ADD CONSTRAINT "fk_sales_payment_methods_payment_method" FOREIGN KEY ("payment_method") REFERENCES "sales"."payment_methods"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- Removed: status is now sale_status_enum, not a foreign key to sale_status table

ALTER TABLE "sales"."sales" ADD CONSTRAINT "fk_sales_sale_types_sale_type" FOREIGN KEY ("sale_type") REFERENCES "sales"."sale_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "sales"."tickets" ADD CONSTRAINT "fk_tickets_sales_sale" FOREIGN KEY ("sale_id") REFERENCES "sales"."sales"("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "sales"."tickets" ADD CONSTRAINT "fk_tickets_ticket_types_ticket_type" FOREIGN KEY ("ticket_type") REFERENCES "sales"."ticket_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "stock"."stock_movements" ADD CONSTRAINT "fk_stock_movements_stock_movement_types_movement_type" FOREIGN KEY ("movement_type") REFERENCES "stock"."stock_movement_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "tax"."monthly_tax_settlement" ADD CONSTRAINT "fk_monthly_tax_settlement_settlement_status_status" FOREIGN KEY ("status") REFERENCES "tax"."settlement_status"("id") ON DELETE SET NULL ON UPDATE CASCADE;


-- ============================================================================
-- INDEXES
-- ============================================================================
-- Naming conventions:
--   - Unique indexes: uq_{table}_{column1}_{column2}
--   - Regular indexes: idx_{table}_{column1}_{column2}
-- ============================================================================

CREATE INDEX "idx_account_natures_company_id" ON "finance"."account_natures"("company_id");

CREATE INDEX "idx_account_types_company_id" ON "finance"."account_types"("company_id");

CREATE INDEX "idx_accounting_movement_types_company_id" ON "finance"."accounting_movement_types"("company_id");

CREATE INDEX "idx_accounts_payable_company_id" ON "finance"."accounts_payable"("company_id");

CREATE INDEX "idx_accounts_payable_due_date" ON "finance"."accounts_payable"("due_date");

CREATE INDEX "idx_accounts_payable_expense_type" ON "finance"."accounts_payable"("expense_type");

CREATE INDEX "idx_accounts_payable_status" ON "finance"."accounts_payable"("status");

CREATE INDEX "idx_accounts_payable_supplier_id" ON "finance"."accounts_payable"("supplier_id");

CREATE INDEX "idx_accounts_receivable_company_id" ON "finance"."accounts_receivable"("company_id");

CREATE INDEX "idx_accounts_receivable_customer_id" ON "finance"."accounts_receivable"("customer_id");

CREATE INDEX "idx_accounts_receivable_due_date" ON "finance"."accounts_receivable"("due_date");

CREATE INDEX "idx_accounts_receivable_status" ON "finance"."accounts_receivable"("status");

CREATE INDEX "idx_age_ratings_company_id" ON "catalog"."age_ratings"("company_id");

CREATE INDEX "idx_audio_types_company_id" ON "operations"."audio_types"("company_id");

CREATE INDEX "idx_audit_logs_company_id" ON "audit"."audit_logs"("company_id");

CREATE INDEX "idx_audit_logs_company_id_resource_type_created_at" ON "audit"."audit_logs"("company_id", "resource_type", "created_at");

CREATE INDEX "idx_audit_logs_created_at" ON "audit"."audit_logs"("created_at");

CREATE INDEX "idx_audit_logs_resource_id" ON "audit"."audit_logs"("resource_id");

CREATE INDEX "idx_audit_logs_resource_type" ON "audit"."audit_logs"("resource_type");

CREATE INDEX "idx_audit_logs_user_id" ON "audit"."audit_logs"("user_id");

CREATE INDEX "idx_bank_accounts_active" ON "finance"."bank_accounts"("active");

CREATE INDEX "idx_bank_accounts_company_id" ON "finance"."bank_accounts"("company_id");

CREATE INDEX "idx_bank_reconciliations_bank_account_id" ON "finance"."bank_reconciliations"("bank_account_id");

CREATE INDEX "idx_bank_reconciliations_reference_month" ON "finance"."bank_reconciliations"("reference_month");

CREATE INDEX "idx_bank_reconciliations_status" ON "finance"."bank_reconciliations"("status");

CREATE INDEX "idx_campaign_categories_campaign_id" ON "marketing"."campaign_categories"("campaign_id");

CREATE INDEX "idx_campaign_categories_category_id" ON "marketing"."campaign_categories"("category_id");

CREATE INDEX "idx_campaign_complexes_campaign_id" ON "marketing"."campaign_complexes"("campaign_id");

CREATE INDEX "idx_campaign_complexes_complex_id" ON "marketing"."campaign_complexes"("complex_id");

CREATE INDEX "idx_campaign_movies_campaign_id" ON "marketing"."campaign_movies"("campaign_id");

CREATE INDEX "idx_campaign_movies_movie_id" ON "marketing"."campaign_movies"("movie_id");

CREATE INDEX "idx_campaign_rooms_campaign_id" ON "marketing"."campaign_rooms"("campaign_id");

CREATE INDEX "idx_campaign_rooms_room_id" ON "marketing"."campaign_rooms"("room_id");

CREATE INDEX "idx_campaign_session_types_campaign_id" ON "marketing"."campaign_session_types"("campaign_id");

CREATE INDEX "idx_campaign_session_types_projection_type_id" ON "marketing"."campaign_session_types"("projection_type_id");

CREATE INDEX "idx_campaign_weekdays_campaign_id" ON "marketing"."campaign_weekdays"("campaign_id");

CREATE INDEX "idx_cash_flow_entries_bank_account_id" ON "finance"."cash_flow_entries"("bank_account_id");

CREATE INDEX "idx_cash_flow_entries_category" ON "finance"."cash_flow_entries"("category");

CREATE INDEX "idx_cash_flow_entries_company_id" ON "finance"."cash_flow_entries"("company_id");

CREATE INDEX "idx_cash_flow_entries_entry_date" ON "finance"."cash_flow_entries"("entry_date");

CREATE INDEX "idx_cash_flow_entries_entry_type" ON "finance"."cash_flow_entries"("entry_type");

CREATE INDEX "idx_cash_flow_entries_source_type_source_id" ON "finance"."cash_flow_entries"("source_type", "source_id");

CREATE INDEX "idx_cash_flow_entries_status" ON "finance"."cash_flow_entries"("status");

CREATE INDEX "idx_cast_types_company_id" ON "catalog"."cast_types"("company_id");

CREATE INDEX "idx_chart_of_accounts_account_nature" ON "finance"."chart_of_accounts"("account_nature");

CREATE INDEX "idx_chart_of_accounts_account_type" ON "finance"."chart_of_accounts"("account_type");

CREATE INDEX "idx_chart_of_accounts_active" ON "finance"."chart_of_accounts"("active");

CREATE INDEX "idx_chart_of_accounts_allows_entry" ON "finance"."chart_of_accounts"("allows_entry");

CREATE INDEX "idx_chart_of_accounts_company_id" ON "finance"."chart_of_accounts"("company_id");

CREATE INDEX "idx_chart_of_accounts_company_id_active" ON "finance"."chart_of_accounts"("company_id", "active");

CREATE INDEX "idx_chart_of_accounts_level" ON "finance"."chart_of_accounts"("level");

CREATE INDEX "idx_chart_of_accounts_parent_account_id" ON "finance"."chart_of_accounts"("parent_account_id");

CREATE INDEX "idx_cinema_complexes_active" ON "operations"."cinema_complexes"("active");

CREATE INDEX "idx_cinema_complexes_code" ON "operations"."cinema_complexes"("code");

CREATE INDEX "idx_cinema_complexes_company_id" ON "operations"."cinema_complexes"("company_id");

CREATE INDEX "idx_cinema_complexes_ibge_municipality_code" ON "operations"."cinema_complexes"("ibge_municipality_code");

CREATE INDEX "idx_combo_products_combo_id" ON "catalog"."combo_products"("combo_id");

CREATE INDEX "idx_combo_products_product_id" ON "catalog"."combo_products"("product_id");

CREATE INDEX "idx_combos_active" ON "catalog"."combos"("active");

CREATE INDEX "idx_combos_combo_code" ON "catalog"."combos"("combo_code");

CREATE INDEX "idx_combos_company_id" ON "catalog"."combos"("company_id");

CREATE INDEX "idx_combos_promotion_start_date_promotion_end_date" ON "catalog"."combos"("promotion_start_date", "promotion_end_date");

CREATE INDEX "idx_companies_active" ON "identity"."companies"("active");

CREATE INDEX "idx_companies_cnpj" ON "identity"."companies"("cnpj");

CREATE INDEX "idx_companies_plan_expires_at" ON "identity"."companies"("plan_expires_at");

CREATE INDEX "idx_companies_suspended" ON "identity"."companies"("suspended");

CREATE INDEX "idx_companies_tenant_slug" ON "identity"."companies"("tenant_slug");

CREATE INDEX "idx_company_customers_company_id" ON "crm"."company_customers"("company_id");

CREATE INDEX "idx_company_customers_customer_id" ON "crm"."company_customers"("customer_id");

CREATE INDEX "idx_company_customers_is_active_in_loyalty" ON "crm"."company_customers"("is_active_in_loyalty");

CREATE INDEX "idx_company_customers_loyalty_level" ON "crm"."company_customers"("loyalty_level");

CREATE INDEX "idx_company_users_active" ON "identity"."company_users"("active");

CREATE INDEX "idx_company_users_company_id_identity_id" ON "identity"."company_users"("company_id", "identity_id");

CREATE INDEX "idx_company_users_employee_id" ON "identity"."company_users"("employee_id");

CREATE INDEX "idx_company_users_end_date" ON "identity"."company_users"("end_date");

CREATE INDEX "idx_company_users_last_access" ON "identity"."company_users"("last_access");

CREATE INDEX "idx_company_users_role_id_active" ON "identity"."company_users"("role_id", "active");

CREATE INDEX "idx_concession_sale_items_concession_sale_id" ON "sales"."concession_sale_items"("concession_sale_id");

CREATE INDEX "idx_concession_sale_items_item_type_item_id" ON "sales"."concession_sale_items"("item_type", "item_id");

CREATE INDEX "idx_concession_sales_sale_date" ON "sales"."concession_sales"("sale_date");

CREATE INDEX "idx_concession_sales_sale_id" ON "sales"."concession_sales"("sale_id");

CREATE INDEX "idx_concession_sales_status" ON "sales"."concession_sales"("status");

CREATE INDEX "idx_contingency_reserves_clearance_date" ON "finance"."contingency_reserves"("clearance_date");

CREATE INDEX "idx_contingency_reserves_complex_id" ON "finance"."contingency_reserves"("complex_id");

CREATE INDEX "idx_contingency_reserves_complex_id_status" ON "finance"."contingency_reserves"("complex_id", "status");

CREATE INDEX "idx_contingency_reserves_contingency_type" ON "finance"."contingency_reserves"("contingency_type");

CREATE INDEX "idx_contingency_reserves_status" ON "finance"."contingency_reserves"("status");

CREATE INDEX "idx_contingency_status_company_id" ON "finance"."contingency_status"("company_id");

CREATE INDEX "idx_contingency_types_company_id" ON "finance"."contingency_types"("company_id");

CREATE INDEX "idx_contract_types_company_id" ON "contracts"."contract_types"("company_id");

CREATE INDEX "idx_courtesy_parameters_cinema_complex_id" ON "operations"."courtesy_parameters"("cinema_complex_id");

CREATE INDEX "idx_courtesy_parameters_validity_start_validity_end" ON "operations"."courtesy_parameters"("validity_start", "validity_end");

CREATE INDEX "idx_credit_types_company_id" ON "tax"."credit_types"("company_id");

CREATE INDEX "idx_custom_roles_company_id_is_system_role" ON "identity"."custom_roles"("company_id", "is_system_role");

CREATE INDEX "idx_custom_roles_hierarchy_level" ON "identity"."custom_roles"("hierarchy_level");

CREATE INDEX "idx_customer_favorite_combos_combo_id" ON "crm"."customer_favorite_combos"("combo_id");

CREATE INDEX "idx_customer_favorite_combos_company_customer_id" ON "crm"."customer_favorite_combos"("company_customer_id");

CREATE INDEX "idx_customer_favorite_genres_company_customer_id" ON "crm"."customer_favorite_genres"("company_customer_id");

CREATE INDEX "idx_customer_favorite_products_company_customer_id" ON "crm"."customer_favorite_products"("company_customer_id");

CREATE INDEX "idx_customer_favorite_products_product_id" ON "crm"."customer_favorite_products"("product_id");

CREATE INDEX "idx_customer_interactions_company_customer_id" ON "crm"."customer_interactions"("company_customer_id");

CREATE INDEX "idx_customer_interactions_interaction_type" ON "crm"."customer_interactions"("interaction_type");

CREATE INDEX "idx_customer_interactions_origin_type_origin_id" ON "crm"."customer_interactions"("origin_type", "origin_id");

CREATE INDEX "idx_customer_points_company_customer_id" ON "crm"."customer_points"("company_customer_id");

CREATE INDEX "idx_customer_points_expiration_date_valid" ON "crm"."customer_points"("expiration_date", "valid");

CREATE INDEX "idx_customer_points_origin_type_origin_id" ON "crm"."customer_points"("origin_type", "origin_id");

CREATE INDEX "idx_customer_preferences_company_customer_id" ON "crm"."customer_preferences"("company_customer_id");

CREATE INDEX "idx_customer_preferred_rows_company_customer_id" ON "crm"."customer_preferred_rows"("company_customer_id");

CREATE INDEX "idx_customer_preferred_times_company_customer_id" ON "crm"."customer_preferred_times"("company_customer_id");

CREATE INDEX "idx_customer_preferred_weekdays_company_customer_id" ON "crm"."customer_preferred_weekdays"("company_customer_id");

CREATE INDEX "idx_customers_active_blocked" ON "crm"."customers"("active", "blocked");

CREATE INDEX "idx_customers_cpf" ON "crm"."customers"("cpf");

CREATE INDEX "idx_customers_email" ON "crm"."customers"("email");

CREATE INDEX "idx_customers_phone" ON "crm"."customers"("phone");

CREATE INDEX "idx_departments_active" ON "hr"."departments"("active");

CREATE INDEX "idx_departments_company_id" ON "hr"."departments"("company_id");

CREATE INDEX "idx_departments_complex_id" ON "hr"."departments"("complex_id");

CREATE INDEX "idx_departments_manager_id" ON "hr"."departments"("manager_id");

CREATE INDEX "idx_distributor_settlement_status_company_id" ON "finance"."distributor_settlement_status"("company_id");

CREATE INDEX "idx_distributor_settlements_approval_date" ON "finance"."distributor_settlements"("approval_date");

CREATE INDEX "idx_distributor_settlements_calculation_base" ON "finance"."distributor_settlements"("calculation_base");

CREATE INDEX "idx_distributor_settlements_cinema_complex_id" ON "finance"."distributor_settlements"("cinema_complex_id");

CREATE INDEX "idx_distributor_settlements_competence_start_date_competence_end_date" ON "finance"."distributor_settlements"("competence_start_date", "competence_end_date");

CREATE INDEX "idx_distributor_settlements_contract_id" ON "finance"."distributor_settlements"("contract_id");

CREATE INDEX "idx_distributor_settlements_distributor_id" ON "finance"."distributor_settlements"("distributor_id");

CREATE INDEX "idx_distributor_settlements_payment_date" ON "finance"."distributor_settlements"("payment_date");

CREATE INDEX "idx_distributor_settlements_status" ON "finance"."distributor_settlements"("status");

CREATE INDEX "idx_employees_active" ON "hr"."employees"("active");

CREATE INDEX "idx_employees_company_id" ON "hr"."employees"("company_id");

CREATE INDEX "idx_employees_complex_id" ON "hr"."employees"("complex_id");

CREATE INDEX "idx_employees_contract_type" ON "hr"."employees"("contract_type");

CREATE INDEX "idx_employees_hire_date" ON "hr"."employees"("hire_date");

CREATE INDEX "idx_employees_person_id" ON "hr"."employees"("person_id");

CREATE INDEX "idx_employees_position_id" ON "hr"."employees"("position_id");

CREATE INDEX "idx_employees_termination_date" ON "hr"."employees"("termination_date");

CREATE INDEX "idx_employees_work_email" ON "hr"."employees"("work_email");

CREATE INDEX "idx_employment_contract_types_company_id" ON "hr"."employment_contract_types"("company_id");

CREATE INDEX "idx_exhibition_contract_sliding_scales_contract_id" ON "contracts"."exhibition_contract_sliding_scales"("contract_id");

CREATE INDEX "idx_exhibition_contracts_active" ON "contracts"."exhibition_contracts"("active");

CREATE INDEX "idx_exhibition_contracts_cinema_complex_id" ON "contracts"."exhibition_contracts"("cinema_complex_id");

CREATE INDEX "idx_exhibition_contracts_contract_type" ON "contracts"."exhibition_contracts"("contract_type");

CREATE INDEX "idx_exhibition_contracts_distributor_id" ON "contracts"."exhibition_contracts"("distributor_id");

CREATE INDEX "idx_exhibition_contracts_movie_id" ON "contracts"."exhibition_contracts"("movie_id");

CREATE INDEX "idx_exhibition_contracts_revenue_base" ON "contracts"."exhibition_contracts"("revenue_base");

CREATE INDEX "idx_exhibition_contracts_start_date_end_date" ON "contracts"."exhibition_contracts"("start_date", "end_date");

CREATE INDEX "idx_federal_tax_rates_active" ON "tax"."federal_tax_rates"("active");

CREATE INDEX "idx_federal_tax_rates_company_id" ON "tax"."federal_tax_rates"("company_id");

CREATE INDEX "idx_federal_tax_rates_pis_cofins_regime" ON "tax"."federal_tax_rates"("pis_cofins_regime");

CREATE INDEX "idx_federal_tax_rates_tax_regime" ON "tax"."federal_tax_rates"("tax_regime");

CREATE INDEX "idx_federal_tax_rates_validity_start_validity_end" ON "tax"."federal_tax_rates"("validity_start", "validity_end");

CREATE INDEX "idx_identities_blocked_until" ON "identity"."identities"("blocked_until");

CREATE INDEX "idx_identities_email" ON "identity"."identities"("email");

CREATE INDEX "idx_identities_email_verification_expires_at" ON "identity"."identities"("email_verification_expires_at");

CREATE INDEX "idx_identities_email_verified" ON "identity"."identities"("email_verified");

CREATE INDEX "idx_identities_external_id" ON "identity"."identities"("external_id");

CREATE INDEX "idx_identities_identity_type_active" ON "identity"."identities"("identity_type", "active");

CREATE INDEX "idx_identities_reset_token_expires_at" ON "identity"."identities"("reset_token_expires_at");

CREATE INDEX "idx_iss_withholdings_cinema_complex_id" ON "tax"."iss_withholdings"("cinema_complex_id");

CREATE INDEX "idx_iss_withholdings_withholding_date" ON "tax"."iss_withholdings"("withholding_date");

CREATE INDEX "idx_journal_entries_cinema_complex_id" ON "finance"."journal_entries"("cinema_complex_id");

CREATE INDEX "idx_journal_entries_cinema_complex_id_entry_date" ON "finance"."journal_entries"("cinema_complex_id", "entry_date");

CREATE INDEX "idx_journal_entries_entry_date" ON "finance"."journal_entries"("entry_date");

CREATE INDEX "idx_journal_entries_entry_type" ON "finance"."journal_entries"("entry_type");

CREATE INDEX "idx_journal_entries_origin_type_origin_id" ON "finance"."journal_entries"("origin_type", "origin_id");

CREATE INDEX "idx_journal_entries_status" ON "finance"."journal_entries"("status");

CREATE INDEX "idx_journal_entry_items_account_id" ON "finance"."journal_entry_items"("account_id");

CREATE INDEX "idx_journal_entry_items_journal_entry_id" ON "finance"."journal_entry_items"("journal_entry_id");

CREATE INDEX "idx_journal_entry_items_movement_type" ON "finance"."journal_entry_items"("movement_type");

CREATE INDEX "idx_journal_entry_status_company_id" ON "finance"."journal_entry_status"("company_id");

CREATE INDEX "idx_journal_entry_types_company_id" ON "finance"."journal_entry_types"("company_id");

CREATE INDEX "idx_journal_entry_types_nature" ON "finance"."journal_entry_types"("nature");

CREATE INDEX "idx_media_types_company_id" ON "catalog"."media_types"("company_id");

CREATE INDEX "idx_monthly_income_statement_cinema_complex_id" ON "finance"."monthly_income_statement"("cinema_complex_id");

CREATE INDEX "idx_monthly_income_statement_year_month" ON "finance"."monthly_income_statement"("year", "month");

CREATE INDEX "idx_monthly_tax_settlement_cinema_complex_id" ON "tax"."monthly_tax_settlement"("cinema_complex_id");

CREATE INDEX "idx_monthly_tax_settlement_settlement_date" ON "tax"."monthly_tax_settlement"("settlement_date");

CREATE INDEX "idx_monthly_tax_settlement_status" ON "tax"."monthly_tax_settlement"("status");

CREATE INDEX "idx_monthly_tax_settlement_year_month" ON "tax"."monthly_tax_settlement"("year", "month");

CREATE INDEX "idx_movie_cast_active" ON "catalog"."movie_cast"("active");

CREATE INDEX "idx_movie_cast_cast_type" ON "catalog"."movie_cast"("cast_type");

CREATE INDEX "idx_movie_cast_movie_id" ON "catalog"."movie_cast"("movie_id");

CREATE INDEX "idx_movie_categories_active" ON "catalog"."movie_categories"("active");

CREATE INDEX "idx_movie_categories_company_id" ON "catalog"."movie_categories"("company_id");

CREATE INDEX "idx_movie_media_active" ON "catalog"."movie_media"("active");

CREATE INDEX "idx_movie_media_media_type" ON "catalog"."movie_media"("media_type");

CREATE INDEX "idx_movie_media_movie_id" ON "catalog"."movie_media"("movie_id");

CREATE INDEX "idx_movies_active" ON "catalog"."movies"("active");

CREATE INDEX "idx_movies_age_rating_id" ON "catalog"."movies"("age_rating_id");

CREATE INDEX "idx_movies_company_id" ON "catalog"."movies"("company_id");

CREATE INDEX "idx_movies_distributor_id" ON "catalog"."movies"("distributor_id");

CREATE INDEX "idx_movies_imdb_id" ON "catalog"."movies"("imdb_id");

CREATE INDEX "idx_movies_national" ON "catalog"."movies"("national");

CREATE INDEX "idx_movies_on_categories_category_id" ON "catalog"."movies_on_categories"("category_id");

CREATE INDEX "idx_movies_slug" ON "catalog"."movies"("slug");

CREATE INDEX "idx_movies_tmdb_id" ON "catalog"."movies"("tmdb_id");

CREATE INDEX "idx_municipal_tax_parameters_active" ON "tax"."municipal_tax_parameters"("active");

CREATE INDEX "idx_municipal_tax_parameters_company_id" ON "tax"."municipal_tax_parameters"("company_id");

CREATE INDEX "idx_municipal_tax_parameters_ibge_municipality_code" ON "tax"."municipal_tax_parameters"("ibge_municipality_code");

CREATE INDEX "idx_municipal_tax_parameters_validity_start_validity_end" ON "tax"."municipal_tax_parameters"("validity_start", "validity_end");

CREATE INDEX "idx_permissions_active" ON "identity"."permissions"("active");

CREATE INDEX "idx_permissions_company_id_resource_action" ON "identity"."permissions"("company_id", "resource", "action");

CREATE INDEX "idx_permissions_module_active" ON "identity"."permissions"("module", "active");

CREATE INDEX "idx_persons_cpf" ON "identity"."persons"("cpf");

CREATE INDEX "idx_persons_full_name" ON "identity"."persons"("full_name");

CREATE INDEX "idx_pis_cofins_credits_cinema_complex_id" ON "tax"."pis_cofins_credits"("cinema_complex_id");

CREATE INDEX "idx_pis_cofins_credits_competence_date" ON "tax"."pis_cofins_credits"("competence_date");

CREATE INDEX "idx_pis_cofins_credits_credit_type" ON "tax"."pis_cofins_credits"("credit_type");

CREATE INDEX "idx_pis_cofins_credits_processed" ON "tax"."pis_cofins_credits"("processed");

CREATE INDEX "idx_positions_active" ON "hr"."positions"("active");

CREATE INDEX "idx_positions_company_id" ON "hr"."positions"("company_id");

CREATE INDEX "idx_positions_department_id" ON "hr"."positions"("department_id");

CREATE INDEX "idx_product_categories_company_id" ON "catalog"."product_categories"("company_id");

CREATE INDEX "idx_product_prices_company_id" ON "catalog"."product_prices"("company_id");

CREATE INDEX "idx_product_prices_product_id" ON "catalog"."product_prices"("product_id");

CREATE INDEX "idx_product_prices_product_id_complex_id_active" ON "catalog"."product_prices"("product_id", "complex_id", "active");

CREATE INDEX "idx_product_prices_valid_from_valid_to" ON "catalog"."product_prices"("valid_from", "valid_to");

CREATE INDEX "idx_product_stock_active" ON "stock"."product_stock"("active");

CREATE INDEX "idx_product_stock_complex_id" ON "stock"."product_stock"("complex_id");

CREATE INDEX "idx_product_stock_current_quantity" ON "stock"."product_stock"("current_quantity");

CREATE INDEX "idx_product_stock_current_quantity_minimum_quantity" ON "stock"."product_stock"("current_quantity", "minimum_quantity");

CREATE INDEX "idx_product_stock_product_id" ON "stock"."product_stock"("product_id");

CREATE INDEX "idx_products_active" ON "catalog"."products"("active");

CREATE INDEX "idx_products_barcode" ON "catalog"."products"("barcode");

CREATE INDEX "idx_products_category_id" ON "catalog"."products"("category_id");

CREATE INDEX "idx_products_company_id" ON "catalog"."products"("company_id");

CREATE INDEX "idx_products_product_code" ON "catalog"."products"("product_code");

CREATE INDEX "idx_projection_types_company_id" ON "operations"."projection_types"("company_id");

CREATE INDEX "idx_promotion_types_active" ON "marketing"."promotion_types"("active");

CREATE INDEX "idx_promotion_types_company_id" ON "marketing"."promotion_types"("company_id");

CREATE INDEX "idx_promotional_campaigns_active" ON "marketing"."promotional_campaigns"("active");

CREATE INDEX "idx_promotional_campaigns_campaign_code" ON "marketing"."promotional_campaigns"("campaign_code");

CREATE INDEX "idx_promotional_campaigns_company_id" ON "marketing"."promotional_campaigns"("company_id");

CREATE INDEX "idx_promotional_campaigns_promotion_type_id" ON "marketing"."promotional_campaigns"("promotion_type_id");

CREATE INDEX "idx_promotional_campaigns_start_date_end_date" ON "marketing"."promotional_campaigns"("start_date", "end_date");

CREATE INDEX "idx_promotional_coupons_active_used" ON "marketing"."promotional_coupons"("active", "used");

CREATE INDEX "idx_promotional_coupons_campaign_id" ON "marketing"."promotional_coupons"("campaign_id");

CREATE INDEX "idx_promotional_coupons_coupon_code" ON "marketing"."promotional_coupons"("coupon_code");

CREATE INDEX "idx_promotional_coupons_customer_id" ON "marketing"."promotional_coupons"("customer_id");

CREATE INDEX "idx_promotional_coupons_start_date_end_date" ON "marketing"."promotional_coupons"("start_date", "end_date");

CREATE INDEX "idx_promotions_used_campaign_id" ON "sales"."promotions_used"("campaign_id");

CREATE INDEX "idx_promotions_used_coupon_id" ON "sales"."promotions_used"("coupon_id");

CREATE INDEX "idx_promotions_used_customer_id" ON "sales"."promotions_used"("customer_id");

CREATE INDEX "idx_promotions_used_sale_id" ON "sales"."promotions_used"("sale_id");

CREATE INDEX "idx_promotions_used_usage_date" ON "sales"."promotions_used"("usage_date");

CREATE INDEX "idx_recine_acquisition_types_company_id" ON "projects"."recine_acquisition_types"("company_id");

CREATE INDEX "idx_recine_acquisitions_acquisition_date" ON "projects"."recine_acquisitions"("acquisition_date");

CREATE INDEX "idx_recine_acquisitions_acquisition_type" ON "projects"."recine_acquisitions"("acquisition_type");

CREATE INDEX "idx_recine_acquisitions_item_type" ON "projects"."recine_acquisitions"("item_type");

CREATE INDEX "idx_recine_acquisitions_recine_project_id" ON "projects"."recine_acquisitions"("recine_project_id");

CREATE INDEX "idx_recine_acquisitions_supplier" ON "projects"."recine_acquisitions"("supplier");

CREATE INDEX "idx_recine_deadline_types_company_id" ON "projects"."recine_deadline_types"("company_id");

CREATE INDEX "idx_recine_deadlines_completion_date" ON "projects"."recine_deadlines"("completion_date");

CREATE INDEX "idx_recine_deadlines_deadline_type" ON "projects"."recine_deadlines"("deadline_type");

CREATE INDEX "idx_recine_deadlines_due_date" ON "projects"."recine_deadlines"("due_date");

CREATE INDEX "idx_recine_deadlines_project_id" ON "projects"."recine_deadlines"("project_id");

CREATE INDEX "idx_recine_item_types_company_id" ON "projects"."recine_item_types"("company_id");

CREATE INDEX "idx_recine_project_status_company_id" ON "projects"."recine_project_status"("company_id");

CREATE INDEX "idx_recine_project_types_company_id" ON "projects"."recine_project_types"("company_id");

CREATE INDEX "idx_recine_projects_cinema_complex_id" ON "projects"."recine_projects"("cinema_complex_id");

CREATE INDEX "idx_recine_projects_expected_completion_date" ON "projects"."recine_projects"("expected_completion_date");

CREATE INDEX "idx_recine_projects_project_type" ON "projects"."recine_projects"("project_type");

CREATE INDEX "idx_recine_projects_start_date" ON "projects"."recine_projects"("start_date");

CREATE INDEX "idx_recine_projects_status" ON "projects"."recine_projects"("status");

CREATE INDEX "idx_revenue_types_company_id" ON "tax"."revenue_types"("company_id");

CREATE INDEX "idx_role_permissions_permission_id" ON "identity"."role_permissions"("permission_id");

CREATE INDEX "idx_role_permissions_role_id" ON "identity"."role_permissions"("role_id");

CREATE INDEX "idx_rooms_active" ON "operations"."rooms"("active");

CREATE INDEX "idx_rooms_audio_type" ON "operations"."rooms"("audio_type");

CREATE INDEX "idx_rooms_cinema_complex_id" ON "operations"."rooms"("cinema_complex_id");

CREATE INDEX "idx_rooms_projection_type" ON "operations"."rooms"("projection_type");

CREATE INDEX "idx_sales_cinema_complex_id" ON "sales"."sales"("cinema_complex_id");

CREATE INDEX "idx_sales_customer_id" ON "sales"."sales"("customer_id");

CREATE INDEX "idx_sales_sale_date" ON "sales"."sales"("sale_date");

CREATE INDEX "idx_sales_sale_date_status" ON "sales"."sales"("sale_date", "status");

CREATE INDEX "idx_sales_status" ON "sales"."sales"("status");

CREATE INDEX "idx_sales_user_id" ON "sales"."sales"("user_id");

CREATE INDEX "idx_seat_status_company_id" ON "operations"."seat_status"("company_id");

CREATE INDEX "idx_seat_status_company_id_is_default" ON "operations"."seat_status"("company_id", "is_default");

CREATE INDEX "idx_seat_types_company_id" ON "operations"."seat_types"("company_id");

CREATE INDEX "idx_seats_accessible" ON "operations"."seats"("accessible");

CREATE INDEX "idx_seats_active" ON "operations"."seats"("active");

CREATE INDEX "idx_seats_room_id" ON "operations"."seats"("room_id");

CREATE INDEX "idx_seats_seat_type" ON "operations"."seats"("seat_type");

CREATE INDEX "idx_session_languages_company_id" ON "operations"."session_languages"("company_id");

CREATE INDEX "idx_session_seat_status_expiration_date" ON "operations"."session_seat_status"("expiration_date");

CREATE INDEX "idx_session_seat_status_expiration_date_reservation_uuid" ON "operations"."session_seat_status"("expiration_date", "reservation_uuid");

CREATE INDEX "idx_session_seat_status_reservation_uuid_expiration_date_sale_id" ON "operations"."session_seat_status"("reservation_uuid", "expiration_date", "sale_id");

CREATE INDEX "idx_session_seat_status_reservation_uuid_showtime_id" ON "operations"."session_seat_status"("reservation_uuid", "showtime_id");

CREATE INDEX "idx_session_seat_status_sale_id" ON "operations"."session_seat_status"("sale_id");

CREATE INDEX "idx_session_seat_status_seat_id" ON "operations"."session_seat_status"("seat_id");

CREATE INDEX "idx_session_seat_status_showtime_id" ON "operations"."session_seat_status"("showtime_id");

CREATE INDEX "idx_session_seat_status_showtime_id_sale_id_reservation_uuid" ON "operations"."session_seat_status"("showtime_id", "sale_id", "reservation_uuid", "expiration_date");

CREATE INDEX "idx_session_seat_status_showtime_id_status_sale_id" ON "operations"."session_seat_status"("showtime_id", "status", "sale_id", "reservation_uuid");

CREATE INDEX "idx_session_seat_status_status" ON "operations"."session_seat_status"("status");

CREATE INDEX "idx_session_status_company_id" ON "operations"."session_status"("company_id");

CREATE INDEX "idx_settlement_bases_company_id" ON "finance"."settlement_bases"("company_id");

CREATE INDEX "idx_settlement_status_company_id" ON "tax"."settlement_status"("company_id");

CREATE INDEX "idx_showtime_schedule_audio_type" ON "operations"."showtime_schedule"("audio_type");

CREATE INDEX "idx_showtime_schedule_cinema_complex_id" ON "operations"."showtime_schedule"("cinema_complex_id");

CREATE INDEX "idx_showtime_schedule_movie_id" ON "operations"."showtime_schedule"("movie_id");

CREATE INDEX "idx_showtime_schedule_projection_type" ON "operations"."showtime_schedule"("projection_type");

CREATE INDEX "idx_showtime_schedule_room_id" ON "operations"."showtime_schedule"("room_id");

CREATE INDEX "idx_showtime_schedule_session_language" ON "operations"."showtime_schedule"("session_language");

CREATE INDEX "idx_showtime_schedule_start_time_end_time" ON "operations"."showtime_schedule"("start_time", "end_time");

CREATE INDEX "idx_showtime_schedule_status" ON "operations"."showtime_schedule"("status");

CREATE INDEX "idx_simple_national_brackets_active" ON "tax"."simple_national_brackets"("active");

CREATE INDEX "idx_simple_national_brackets_annex" ON "tax"."simple_national_brackets"("annex");

CREATE INDEX "idx_simple_national_brackets_company_id" ON "tax"."simple_national_brackets"("company_id");

CREATE INDEX "idx_simple_national_brackets_validity_start_validity_end" ON "tax"."simple_national_brackets"("validity_start", "validity_end");

CREATE INDEX "idx_state_icms_parameters_active" ON "tax"."state_icms_parameters"("active");

CREATE INDEX "idx_state_icms_parameters_company_id" ON "tax"."state_icms_parameters"("company_id");

CREATE INDEX "idx_state_icms_parameters_state" ON "tax"."state_icms_parameters"("state");

CREATE INDEX "idx_state_icms_parameters_validity_start_validity_end" ON "tax"."state_icms_parameters"("validity_start", "validity_end");

CREATE INDEX "idx_stock_movement_types_affects_stock" ON "stock"."stock_movement_types"("affects_stock");

CREATE INDEX "idx_stock_movement_types_company_id" ON "stock"."stock_movement_types"("company_id");

CREATE INDEX "idx_stock_movements_complex_id" ON "stock"."stock_movements"("complex_id");

CREATE INDEX "idx_stock_movements_movement_date" ON "stock"."stock_movements"("movement_date");

CREATE INDEX "idx_stock_movements_movement_type" ON "stock"."stock_movements"("movement_type");

CREATE INDEX "idx_stock_movements_origin_type_origin_id" ON "stock"."stock_movements"("origin_type", "origin_id");

CREATE INDEX "idx_stock_movements_product_id" ON "stock"."stock_movements"("product_id");

CREATE INDEX "idx_stock_movements_user_id" ON "stock"."stock_movements"("user_id");

CREATE INDEX "idx_suppliers_active" ON "inventory"."suppliers"("active");

CREATE INDEX "idx_suppliers_company_id" ON "inventory"."suppliers"("company_id");

CREATE INDEX "idx_suppliers_is_film_distributor" ON "inventory"."suppliers"("is_film_distributor");

CREATE INDEX "idx_suppliers_supplier_type_id" ON "inventory"."suppliers"("supplier_type_id");

CREATE INDEX "idx_tax_compensations_cinema_complex_id" ON "tax"."tax_compensations"("cinema_complex_id");

CREATE INDEX "idx_tax_compensations_credit_competence_date" ON "tax"."tax_compensations"("credit_competence_date");

CREATE INDEX "idx_tax_compensations_tax_type" ON "tax"."tax_compensations"("tax_type");

CREATE INDEX "idx_tax_entries_cinema_complex_id" ON "tax"."tax_entries"("cinema_complex_id");

CREATE INDEX "idx_tax_entries_competence_date" ON "tax"."tax_entries"("competence_date");

CREATE INDEX "idx_tax_entries_pis_cofins_regime" ON "tax"."tax_entries"("pis_cofins_regime");

CREATE INDEX "idx_tax_entries_processed" ON "tax"."tax_entries"("processed");

CREATE INDEX "idx_tax_entries_source_type_source_id" ON "tax"."tax_entries"("source_type", "source_id");

CREATE INDEX "idx_tax_types_company_id" ON "tax"."tax_types"("company_id");

CREATE INDEX "idx_tickets_sale_id" ON "sales"."tickets"("sale_id");

CREATE INDEX "idx_tickets_seat_id" ON "sales"."tickets"("seat_id");

CREATE INDEX "idx_tickets_showtime_id" ON "sales"."tickets"("showtime_id");

CREATE INDEX "idx_tickets_ticket_type" ON "sales"."tickets"("ticket_type");

CREATE INDEX "idx_tickets_used" ON "sales"."tickets"("used");

CREATE INDEX "idx_user_attributes_key_value" ON "identity"."user_attributes"("key", "value");

CREATE INDEX "idx_user_attributes_user_id" ON "identity"."user_attributes"("user_id");

CREATE INDEX "idx_user_sessions_expires_at_active" ON "identity"."user_sessions"("expires_at", "active");

CREATE INDEX "idx_user_sessions_identity_id" ON "identity"."user_sessions"("identity_id");

CREATE INDEX "idx_user_sessions_last_activity" ON "identity"."user_sessions"("last_activity");

CREATE INDEX "idx_user_sessions_revoked" ON "identity"."user_sessions"("revoked");

CREATE INDEX "idx_user_sessions_session_id" ON "identity"."user_sessions"("session_id");

CREATE UNIQUE INDEX "uq_account_natures_company_id_name" ON "finance"."account_natures"("company_id", "name");

CREATE UNIQUE INDEX "uq_account_types_company_id_name" ON "finance"."account_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_accounting_movement_types_company_id_name" ON "finance"."accounting_movement_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_age_ratings_company_id_code" ON "catalog"."age_ratings"("company_id", "code");

CREATE UNIQUE INDEX "uq_audio_types_company_id_name" ON "operations"."audio_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_bank_reconciliations_bank_account_id_reference_month" ON "finance"."bank_reconciliations"("bank_account_id", "reference_month");

CREATE UNIQUE INDEX "uq_campaign_categories_campaign_id_category_id" ON "marketing"."campaign_categories"("campaign_id", "category_id");

CREATE UNIQUE INDEX "uq_campaign_complexes_campaign_id_complex_id" ON "marketing"."campaign_complexes"("campaign_id", "complex_id");

CREATE UNIQUE INDEX "uq_campaign_movies_campaign_id_movie_id" ON "marketing"."campaign_movies"("campaign_id", "movie_id");

CREATE UNIQUE INDEX "uq_campaign_rooms_campaign_id_room_id" ON "marketing"."campaign_rooms"("campaign_id", "room_id");

CREATE UNIQUE INDEX "uq_campaign_session_types_campaign_id_projection_type_id" ON "marketing"."campaign_session_types"("campaign_id", "projection_type_id");

CREATE UNIQUE INDEX "uq_campaign_weekdays_campaign_id_weekday" ON "marketing"."campaign_weekdays"("campaign_id", "weekday");

CREATE UNIQUE INDEX "uq_cast_types_company_id_name" ON "catalog"."cast_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_chart_of_accounts_company_id_account_code" ON "finance"."chart_of_accounts"("company_id", "account_code");

CREATE UNIQUE INDEX "uq_cinema_complexes_code" ON "operations"."cinema_complexes"("code");

CREATE UNIQUE INDEX "uq_combo_products_combo_id_product_id" ON "catalog"."combo_products"("combo_id", "product_id");

CREATE UNIQUE INDEX "uq_combos_combo_code" ON "catalog"."combos"("combo_code");

CREATE UNIQUE INDEX "uq_companies_cnpj" ON "identity"."companies"("cnpj");

CREATE UNIQUE INDEX "uq_companies_tenant_slug" ON "identity"."companies"("tenant_slug");

CREATE UNIQUE INDEX "uq_company_customers_company_id_customer_id" ON "crm"."company_customers"("company_id", "customer_id");

CREATE UNIQUE INDEX "uq_company_users_company_id_identity_id" ON "identity"."company_users"("company_id", "identity_id");

CREATE UNIQUE INDEX "uq_concession_status_company_id_name" ON "sales"."concession_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_contingency_status_company_id_name" ON "finance"."contingency_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_contingency_types_company_id_name" ON "finance"."contingency_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_contract_types_company_id_name" ON "contracts"."contract_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_credit_types_company_id_name" ON "tax"."credit_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_custom_roles_company_id_name" ON "identity"."custom_roles"("company_id", "name");

CREATE UNIQUE INDEX "uq_customer_favorite_combos_company_customer_id_combo_id" ON "crm"."customer_favorite_combos"("company_customer_id", "combo_id");

CREATE UNIQUE INDEX "uq_customer_favorite_genres_company_customer_id_genre" ON "crm"."customer_favorite_genres"("company_customer_id", "genre");

CREATE UNIQUE INDEX "uq_customer_favorite_products_company_customer_id_product_id" ON "crm"."customer_favorite_products"("company_customer_id", "product_id");

CREATE UNIQUE INDEX "uq_customer_preferred_rows_company_customer_id_row_code" ON "crm"."customer_preferred_rows"("company_customer_id", "row_code");

CREATE UNIQUE INDEX "uq_customer_preferred_times_company_customer_id_time_slot" ON "crm"."customer_preferred_times"("company_customer_id", "time_slot");

CREATE UNIQUE INDEX "uq_customer_preferred_weekdays_company_customer_id_weekday" ON "crm"."customer_preferred_weekdays"("company_customer_id", "weekday");

CREATE UNIQUE INDEX "uq_customers_cpf" ON "crm"."customers"("cpf");

CREATE UNIQUE INDEX "uq_customers_identity_id" ON "crm"."customers"("identity_id");

CREATE UNIQUE INDEX "uq_distributor_settlement_status_company_id_name" ON "finance"."distributor_settlement_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_employees_employee_number" ON "hr"."employees"("employee_number");

CREATE UNIQUE INDEX "uq_employment_contract_types_company_id_name" ON "hr"."employment_contract_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_exhibition_contract_sliding_scales_contract_id_week_number" ON "contracts"."exhibition_contract_sliding_scales"("contract_id", "week_number");

CREATE UNIQUE INDEX "uq_exhibition_contracts_contract_number" ON "contracts"."exhibition_contracts"("contract_number");

CREATE UNIQUE INDEX "uq_identities_email_identity_type" ON "identity"."identities"("email", "identity_type");

CREATE UNIQUE INDEX "uq_journal_entries_entry_number" ON "finance"."journal_entries"("entry_number");

CREATE UNIQUE INDEX "uq_journal_entry_status_company_id_name" ON "finance"."journal_entry_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_journal_entry_types_company_id_name" ON "finance"."journal_entry_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_media_types_company_id_name" ON "catalog"."media_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_monthly_income_statement_cinema_complex_id_year_month" ON "finance"."monthly_income_statement"("cinema_complex_id", "year", "month");

CREATE UNIQUE INDEX "uq_monthly_tax_settlement_cinema_complex_id_year_month" ON "tax"."monthly_tax_settlement"("cinema_complex_id", "year", "month");

CREATE UNIQUE INDEX "uq_movie_categories_company_id_name" ON "catalog"."movie_categories"("company_id", "name");

CREATE UNIQUE INDEX "uq_movie_categories_company_id_slug" ON "catalog"."movie_categories"("company_id", "slug");

CREATE UNIQUE INDEX "uq_movies_slug" ON "catalog"."movies"("slug");

CREATE UNIQUE INDEX "uq_municipal_tax_parameters_company_id_ibge_municipality_code_validity_start" ON "tax"."municipal_tax_parameters"("company_id", "ibge_municipality_code", "validity_start");

CREATE UNIQUE INDEX "uq_payment_methods_company_id_name" ON "sales"."payment_methods"("company_id", "name");

CREATE UNIQUE INDEX "uq_permissions_company_id_code" ON "identity"."permissions"("company_id", "code");

CREATE UNIQUE INDEX "uq_persons_cpf" ON "identity"."persons"("cpf");

CREATE UNIQUE INDEX "uq_persons_passport_number" ON "identity"."persons"("passport_number");

CREATE UNIQUE INDEX "uq_product_categories_company_id_name" ON "catalog"."product_categories"("company_id", "name");

CREATE UNIQUE INDEX "uq_product_prices_product_id_complex_id_valid_from" ON "catalog"."product_prices"("product_id", "complex_id", "valid_from");

CREATE UNIQUE INDEX "uq_product_stock_product_id_complex_id" ON "stock"."product_stock"("product_id", "complex_id");

CREATE UNIQUE INDEX "uq_products_barcode" ON "catalog"."products"("barcode");

CREATE UNIQUE INDEX "uq_products_company_id_product_code" ON "catalog"."products"("company_id", "product_code");

CREATE UNIQUE INDEX "uq_products_product_code" ON "catalog"."products"("product_code");

CREATE UNIQUE INDEX "uq_projection_types_company_id_name" ON "operations"."projection_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_promotion_types_company_id_code" ON "marketing"."promotion_types"("company_id", "code");

CREATE UNIQUE INDEX "uq_promotional_campaigns_campaign_code" ON "marketing"."promotional_campaigns"("campaign_code");

CREATE UNIQUE INDEX "uq_promotional_coupons_coupon_code" ON "marketing"."promotional_coupons"("coupon_code");

CREATE UNIQUE INDEX "uq_recine_acquisition_types_company_id_name" ON "projects"."recine_acquisition_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_recine_deadline_types_company_id_name" ON "projects"."recine_deadline_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_recine_item_types_company_id_name" ON "projects"."recine_item_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_recine_project_status_company_id_name" ON "projects"."recine_project_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_recine_project_types_company_id_name" ON "projects"."recine_project_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_recine_projects_project_number" ON "projects"."recine_projects"("project_number");

CREATE UNIQUE INDEX "uq_revenue_types_company_id_name" ON "tax"."revenue_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_role_permissions_role_id_permission_id" ON "identity"."role_permissions"("role_id", "permission_id");

CREATE UNIQUE INDEX "uq_rooms_cinema_complex_id_room_number" ON "operations"."rooms"("cinema_complex_id", "room_number");

CREATE UNIQUE INDEX "uq_sale_status_company_id_name" ON "sales"."sale_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_sale_types_company_id_name" ON "sales"."sale_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_sales_sale_number" ON "sales"."sales"("sale_number");

CREATE UNIQUE INDEX "uq_seat_status_company_id_name" ON "operations"."seat_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_seat_types_company_id_name" ON "operations"."seat_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_seats_room_id_seat_code" ON "operations"."seats"("room_id", "seat_code");

CREATE UNIQUE INDEX "uq_session_languages_company_id_name" ON "operations"."session_languages"("company_id", "name");

CREATE UNIQUE INDEX "uq_session_seat_status_showtime_id_seat_id" ON "operations"."session_seat_status"("showtime_id", "seat_id");

CREATE UNIQUE INDEX "uq_session_status_company_id_name" ON "operations"."session_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_settlement_bases_company_id_name" ON "finance"."settlement_bases"("company_id", "name");

CREATE UNIQUE INDEX "uq_settlement_status_company_id_name" ON "tax"."settlement_status"("company_id", "name");

CREATE UNIQUE INDEX "uq_simple_national_brackets_company_id_annex_bracket" ON "tax"."simple_national_brackets"("company_id", "annex", "bracket", "validity_start");

CREATE UNIQUE INDEX "uq_state_icms_parameters_company_id_state_validity_start" ON "tax"."state_icms_parameters"("company_id", "state", "validity_start");

CREATE UNIQUE INDEX "uq_stock_movement_types_company_id_name" ON "stock"."stock_movement_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_supplier_types_company_id_name" ON "inventory"."supplier_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_suppliers_company_id_cnpj" ON "inventory"."suppliers"("company_id", "cnpj");

CREATE UNIQUE INDEX "uq_tax_types_company_id_name" ON "tax"."tax_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_ticket_types_company_id_name" ON "sales"."ticket_types"("company_id", "name");

CREATE UNIQUE INDEX "uq_tickets_ticket_number" ON "sales"."tickets"("ticket_number");

CREATE UNIQUE INDEX "uq_user_attributes_user_id_key" ON "identity"."user_attributes"("user_id", "key");

CREATE UNIQUE INDEX "uq_user_sessions_session_id" ON "identity"."user_sessions"("session_id");

