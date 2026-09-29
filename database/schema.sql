-- Reference schema for jewelry_billing (JPA also creates/updates tables)


CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS daily_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metal_type VARCHAR(20) NOT NULL,
    purity VARCHAR(10) NOT NULL,
    rate_per_gram DECIMAL(12,4) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source VARCHAR(50),
    created_by BIGINT,
    INDEX idx_daily_rates_metal_purity (metal_type, purity, updated_at)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    metal_type VARCHAR(20) NOT NULL,
    purity VARCHAR(10) NOT NULL,
    making_charge_per_gram DECIMAL(10,4) NOT NULL,
    wastage_percent DECIMAL(5,2),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(120),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    invoice_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    rate_snapshot_id BIGINT,
    subtotal DECIMAL(14,2) NOT NULL,
    tax_amount DECIMAL(14,2) NOT NULL,
    total_amount DECIMAL(14,2) NOT NULL,
    gst_percent DECIMAL(5,2) NOT NULL,
    created_by BIGINT,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS invoice_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT,
    product_name VARCHAR(150) NOT NULL,
    weight_grams DECIMAL(10,4) NOT NULL,
    rate_applied DECIMAL(12,4) NOT NULL,
    making_applied DECIMAL(12,4) NOT NULL,
    metal_amount DECIMAL(14,2) NOT NULL,
    making_amount DECIMAL(14,2) NOT NULL,
    line_subtotal DECIMAL(14,2) NOT NULL,
    tax_amount DECIMAL(14,2) NOT NULL,
    line_total DECIMAL(14,2) NOT NULL,
    daily_rate_id BIGINT,
    CONSTRAINT fk_item_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);
