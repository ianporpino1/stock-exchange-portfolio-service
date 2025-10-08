CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS portfolio
(
    portfolio_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id      UUID           NOT NULL UNIQUE,
    cash_balance NUMERIC(38, 2) NOT NULL DEFAULT 100000.00
);

CREATE TABLE IF NOT EXISTS position
(
    position_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    portfolio_id  UUID  NOT NULL REFERENCES portfolio (portfolio_id) ON DELETE CASCADE,
    symbol VARCHAR(255)   NOT NULL,
    quantity  INTEGER  NOT NULL DEFAULT 0,
    average_price NUMERIC(38, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT uk_position_portfolio_symbol UNIQUE (portfolio_id, symbol)
);

CREATE TABLE IF NOT EXISTS transaction
(
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trade_id       UUID   NOT NULL,
    user_id        UUID  NOT NULL,
    symbol         VARCHAR(255)   NOT NULL,
    order_type     VARCHAR(255)   NOT NULL CHECK (order_type IN ('BUY', 'SELL')),
    quantity       INTEGER        NOT NULL,
    price          NUMERIC(38, 2) NOT NULL CHECK (price > 0),
    status         VARCHAR(255)   NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_position_portfolio_id ON position (portfolio_id);

CREATE INDEX IF NOT EXISTS idx_transaction_status ON transaction (status);