CREATE TABLE credit_applications (
    id VARCHAR(36) PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    term INTEGER NOT NULL,
    income NUMERIC(19, 2) NOT NULL,
    current_debt NUMERIC(19, 2) NOT NULL,
    credit_rating INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_applications_status ON credit_applications(status);

-- ALTER TABLE credit_applications ALTER COLUMN id TYPE VARCHAR(36);

-- DROP TABLE IF EXISTS credit_applications;

-- DROP SCHEMA IF EXISTS public CASCADE;

-- DROP TABLE IF EXISTS public.flyway_schema_history;
