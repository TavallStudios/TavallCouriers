-- Enums (match your Java enums)
CREATE SCHEMA IF NOT EXISTS courier_schemas;
CREATE TYPE qr_type AS ENUM ('UUID', 'CUSTOM');
CREATE TYPE qr_state AS ENUM ('ACTIVE', 'INACTIVE', 'EXPIRED');
CREATE TYPE delivery_state AS ENUM (
    'LABEL_CREATED', 'DELIVERED', 'IN_TRANSIT', 'IN_HQ',
    'IN_MIDDLEMAN', 'CANCELLED', 'ON_HOLD', 'OUT_FOR_DELIVERY', 'RETRY'
);
CREATE TYPE camera_state AS ENUM ('SEARCHING', 'ANALYZING', 'FOUND', 'ERROR');

-- Tables
CREATE TABLE courier_schemas.hq_locations (
                                              id uuid PRIMARY KEY,

                                              name varchar(160),          -- e.g. "Google HQ Demo" or "Tavall HQ"
                                              address text,
                                              city varchar(120),
                                              state varchar(120),
                                              zip_code varchar(20),
                                              country varchar(120),
                                              phone_number varchar(30),

                                              created_at timestamptz,
                                              updated_at timestamptz,

    -- Optional: make one HQ the default
                                              is_default boolean
);



CREATE TABLE courier_schemas.qr_metadata (
                             uuid uuid PRIMARY KEY,
                             qr_data text NOT NULL,
                             created_at timestamptz NOT NULL,
                             qr_type qr_type NOT NULL,
                             qr_state qr_state NOT NULL);

CREATE TABLE courier_schemas.shipping_label_metadata (
                                         uuid varchar(36) PRIMARY KEY,
                                         tracking_number varchar(64) NOT NULL,
                                         recipient_name varchar(160) NOT NULL,
                                         phone_number varchar(30),
                                         address text NOT NULL,
                                         city varchar(120) NOT NULL,
                                         state varchar(120) NOT NULL,
                                         zip_code varchar(20) NOT NULL,
                                         country varchar(120) NOT NULL,
                                         priority boolean NOT NULL,
                                         deliver_by timestamptz,
                                         delivery_state delivery_state
);

CREATE TABLE courier_schemas.scan_response (
                               uuid varchar(36) PRIMARY KEY,
                               camera_state camera_state NOT NULL,
                               tracking_number varchar(64),
                               name varchar(160),
                               address text,
                               city varchar(120),
                               state varchar(120),
                               zip_code varchar(20),
                               country varchar(120),
                               phone_number varchar(30),
                               deadline timestamptz,
                               notes text
);

CREATE TABLE courier_schemas.delivery_routes (
                                                 route_id varchar(40) PRIMARY KEY,
                                                 status varchar(30) NOT NULL,
                                                 label_count integer NOT NULL,
                                                 created_at timestamptz NOT NULL,
                                                 updated_at timestamptz,
                                                 notes text,
                                                 assigned_drivers uuid,
                                                 deadline timestamptz,
                                                 route_link varchar(10000)
);

CREATE TABLE courier_schemas.delivery_route_stops (
                                                      id varchar(36) PRIMARY KEY,
                                                      route_id varchar(40) NOT NULL,
                                                      label_uuid varchar(36) NOT NULL,
                                                      stop_order integer NOT NULL,
                                                      created_at timestamptz NOT NULL,
                                                      CONSTRAINT fk_route_stops_route
                                                          FOREIGN KEY (route_id)
                                                              REFERENCES courier_schemas.delivery_routes (route_id)
                                                              ON DELETE CASCADE,
                                                      CONSTRAINT fk_route_stops_label
                                                          FOREIGN KEY (label_uuid)
                                                              REFERENCES courier_schemas.shipping_label_metadata (uuid)
                                                              ON DELETE CASCADE
);

CREATE TABLE courier_schemas.tracking_number_metadata (
                                          tracking_number varchar(64) PRIMARY KEY,
                                          qr_uuid uuid NOT NULL,
                                          delivery_state delivery_state
);
-- Relationships
ALTER TABLE courier_schemas.shipping_label_metadata
    ADD COLUMN hq_id uuid;

ALTER TABLE courier_schemas.shipping_label_metadata
    ADD CONSTRAINT fk_shipping_label_hq
        FOREIGN KEY (hq_id)
            REFERENCES courier_schemas.hq_locations (id)
            ON DELETE SET NULL;


-- Indexes
CREATE INDEX IF NOT EXISTS idx_hq_locations_is_default ON courier_schemas.hq_locations (is_default);

CREATE INDEX IF NOT EXISTS idx_hq_locations_name ON courier_schemas.hq_locations (name);
CREATE INDEX idx_qr_metadata_state ON qr_metadata (qr_state);
CREATE INDEX idx_qr_metadata_type ON qr_metadata (qr_type);

CREATE INDEX idx_shipping_label_tracking_number ON shipping_label_metadata (tracking_number);
CREATE INDEX idx_shipping_label_delivery_state ON shipping_label_metadata (delivery_state);
CREATE INDEX idx_shipping_label_priority ON shipping_label_metadata (priority);

CREATE INDEX idx_scan_response_tracking_number ON scan_response (tracking_number);
CREATE INDEX idx_scan_response_camera_state ON scan_response (camera_state);

CREATE INDEX idx_tracking_number_metadata_qr_uuid ON tracking_number_metadata (qr_uuid);

CREATE INDEX IF NOT EXISTS idx_shipping_label_hq_id ON courier_schemas.shipping_label_metadata (hq_id);

CREATE INDEX idx_delivery_routes_created_at ON courier_schemas.delivery_routes (created_at);
CREATE INDEX idx_delivery_route_stops_route_id ON courier_schemas.delivery_route_stops (route_id);
CREATE INDEX idx_delivery_route_stops_label_uuid ON courier_schemas.delivery_route_stops (label_uuid);

-- User accounts
CREATE TABLE courier_schemas.user_accounts (
                                               id uuid PRIMARY KEY,
                                               external_subject varchar(160) NOT NULL UNIQUE,
                                               username varchar(120) NOT NULL UNIQUE,
                                               enabled boolean NOT NULL,
                                               created_at timestamptz NOT NULL
);

CREATE TABLE courier_schemas.user_account_roles (
                                                   user_id uuid NOT NULL,
                                                   role varchar(40) NOT NULL,
                                                   PRIMARY KEY (user_id, role),
                                                   CONSTRAINT fk_user_account_roles_user
                                                       FOREIGN KEY (user_id)
                                                           REFERENCES courier_schemas.user_accounts (id)
                                                           ON DELETE CASCADE
);

CREATE INDEX idx_user_accounts_username ON courier_schemas.user_accounts (username);

-- Courier client contract intake
CREATE TABLE IF NOT EXISTS courier_schemas.courier_contract_templates (
    id uuid PRIMARY KEY,
    template_name varchar(120) NOT NULL,
    template_title varchar(160) NOT NULL,
    intro_text text NOT NULL,
    operations_text text NOT NULL,
    pricing_text text NOT NULL,
    review_notice text NOT NULL,
    binding_party_name varchar(160),
    binding_party_address text,
    active boolean NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL
);

CREATE TABLE IF NOT EXISTS courier_schemas.courier_contract_term_definitions (
    id uuid PRIMARY KEY,
    code varchar(80) NOT NULL UNIQUE,
    label varchar(140) NOT NULL,
    input_type varchar(40) NOT NULL,
    category varchar(60) NOT NULL,
    help_text text,
    options_text text,
    default_value text,
    required boolean NOT NULL,
    active boolean NOT NULL,
    display_order integer NOT NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL
);

CREATE TABLE IF NOT EXISTS courier_schemas.courier_contract_drafts (
    id uuid PRIMARY KEY,
    session_key varchar(120) NOT NULL,
    contact_name varchar(160),
    contact_email varchar(160),
    company_name varchar(160),
    phone_number varchar(60),
    selected_terms_json text NOT NULL,
    generated_contract_html text,
    status varchar(40) NOT NULL,
    linked_client_id uuid,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    CONSTRAINT fk_contract_drafts_client
        FOREIGN KEY (linked_client_id)
            REFERENCES courier_schemas.user_accounts (id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS courier_schemas.courier_client_contracts (
    id uuid PRIMARY KEY,
    draft_id uuid,
    client_user_id uuid NOT NULL,
    template_id uuid,
    contract_title varchar(160) NOT NULL,
    client_display_name varchar(160),
    client_company_name varchar(160),
    client_contact_email varchar(160),
    selected_terms_json text NOT NULL,
    generated_contract_html text NOT NULL,
    status varchar(40) NOT NULL,
    parcel_volume_summary varchar(120),
    service_radius_miles integer,
    service_zone varchar(120),
    pickup_zone varchar(120),
    pricing_summary text,
    signed_by_name varchar(160),
    approval_ip_address varchar(80),
    signed_at timestamptz,
    approved_at timestamptz,
    internal_reviewed_at timestamptz,
    internal_review_notes text,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    CONSTRAINT fk_client_contracts_draft
        FOREIGN KEY (draft_id)
            REFERENCES courier_schemas.courier_contract_drafts (id)
            ON DELETE SET NULL,
    CONSTRAINT fk_client_contracts_user
        FOREIGN KEY (client_user_id)
            REFERENCES courier_schemas.user_accounts (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_client_contracts_template
        FOREIGN KEY (template_id)
            REFERENCES courier_schemas.courier_contract_templates (id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS courier_schemas.courier_contract_status_history (
    id uuid PRIMARY KEY,
    contract_id uuid NOT NULL,
    status varchar(40) NOT NULL,
    note text,
    actor_name varchar(160),
    created_at timestamptz NOT NULL,
    CONSTRAINT fk_contract_status_history_contract
        FOREIGN KEY (contract_id)
            REFERENCES courier_schemas.courier_client_contracts (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS courier_schemas.courier_pickup_requests (
    id uuid PRIMARY KEY,
    contract_id uuid NOT NULL,
    client_user_id uuid NOT NULL,
    request_type varchar(40) NOT NULL,
    status varchar(40) NOT NULL,
    pickup_address text NOT NULL,
    pickup_zone varchar(120),
    notes text,
    scheduled_for timestamptz,
    recurring_rule text,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    CONSTRAINT fk_pickup_requests_contract
        FOREIGN KEY (contract_id)
            REFERENCES courier_schemas.courier_client_contracts (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_pickup_requests_client
        FOREIGN KEY (client_user_id)
            REFERENCES courier_schemas.user_accounts (id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_contract_templates_active ON courier_schemas.courier_contract_templates (active);
CREATE INDEX IF NOT EXISTS idx_contract_term_definitions_display_order ON courier_schemas.courier_contract_term_definitions (display_order);
CREATE INDEX IF NOT EXISTS idx_contract_term_definitions_active ON courier_schemas.courier_contract_term_definitions (active);
CREATE INDEX IF NOT EXISTS idx_contract_drafts_session_key ON courier_schemas.courier_contract_drafts (session_key);
CREATE INDEX IF NOT EXISTS idx_contract_drafts_contact_email ON courier_schemas.courier_contract_drafts (contact_email);
CREATE INDEX IF NOT EXISTS idx_contract_drafts_linked_client ON courier_schemas.courier_contract_drafts (linked_client_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_client_contracts_draft_id_unique ON courier_schemas.courier_client_contracts (draft_id) WHERE draft_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_client_contracts_client_user ON courier_schemas.courier_client_contracts (client_user_id);
CREATE INDEX IF NOT EXISTS idx_client_contracts_status ON courier_schemas.courier_client_contracts (status);
CREATE INDEX IF NOT EXISTS idx_client_contracts_updated_at ON courier_schemas.courier_client_contracts (updated_at);
CREATE INDEX IF NOT EXISTS idx_contract_status_history_contract_id ON courier_schemas.courier_contract_status_history (contract_id);
CREATE INDEX IF NOT EXISTS idx_contract_status_history_created_at ON courier_schemas.courier_contract_status_history (created_at);
CREATE INDEX IF NOT EXISTS idx_pickup_requests_client_user ON courier_schemas.courier_pickup_requests (client_user_id);
CREATE INDEX IF NOT EXISTS idx_pickup_requests_contract_id ON courier_schemas.courier_pickup_requests (contract_id);
CREATE INDEX IF NOT EXISTS idx_pickup_requests_status ON courier_schemas.courier_pickup_requests (status);
CREATE INDEX IF NOT EXISTS idx_pickup_requests_created_at ON courier_schemas.courier_pickup_requests (created_at);
