CREATE TABLE credit_applications (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    term INTEGER NOT NULL,
    income NUMERIC(19, 2) NOT NULL,
    current_debt NUMERIC(19, 2) NOT NULL,
    credit_rating INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_applications_status ON credit_applications(status);