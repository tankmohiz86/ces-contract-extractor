CREATE TABLE IF NOT EXISTS contract_records (
    id                    BIGSERIAL PRIMARY KEY,
    uuid                  UUID NOT NULL DEFAULT gen_random_uuid(),
    contract_number       VARCHAR(100),
    work_order_number     VARCHAR(100),
    airline_operator      VARCHAR(200),
    aircraft_type         VARCHAR(100),
    engine_model          VARCHAR(100),
    engine_serial_number  VARCHAR(100),
    shop_visit_date       VARCHAR(50),
    customer_name         VARCHAR(200),
    station_location      VARCHAR(200),
    total_cost            VARCHAR(50),
    currency              VARCHAR(10) DEFAULT 'USD',
    work_scope            TEXT,
    return_to_service_date VARCHAR(50),
    authorized_signatory  VARCHAR(200),
    original_filename     VARCHAR(500),
    extraction_status     VARCHAR(20) DEFAULT 'SUCCESS',
    raw_llm_response      TEXT,
    created_at            TIMESTAMPTZ DEFAULT now(),
    CONSTRAINT uq_uuid UNIQUE (uuid)
);

CREATE INDEX idx_contract_number ON contract_records(contract_number);
CREATE INDEX idx_created_at ON contract_records(created_at DESC);
