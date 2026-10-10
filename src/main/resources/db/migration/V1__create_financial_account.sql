CREATE TABLE financial_account (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    account_type VARCHAR(32) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    data_source VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_financial_account_currency_upper
        CHECK (currency = UPPER(currency))
);

CREATE INDEX idx_financial_account_user_id
    ON financial_account (user_id);
