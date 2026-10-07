PRAGMA foreign_keys = ON;

CREATE TABLE currencies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code VARCHAR(3) NOT NULL UNIQUE,
    full_name VARCHAR NOT NULL,
    sign VARCHAR NOT NULL
);

CREATE TABLE exchange_rates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id INTEGER NOT NULL,
    target_currency_id INTEGER NOT NULL,
    rate DECIMAL(6) NOT NULL,
    FOREIGN KEY (base_currency_id) REFERENCES currencies(id) ON DELETE CASCADE,
    FOREIGN KEY (target_currency_id) REFERENCES currencies(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_currency_pair ON exchange_rates (base_currency_id, target_currency_id);