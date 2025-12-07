-- Make birth_date nullable in identity.persons
ALTER TABLE identity.persons ALTER COLUMN birth_date DROP NOT NULL;
