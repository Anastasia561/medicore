CREATE TABLE verification_token
(
    id         BIGSERIAL PRIMARY KEY,

    token_hash TEXT       NOT NULL,

    token_type varchar(40) NOT NULL,

    email      VARCHAR(100) NOT NULL,

    expires_at TIMESTAMP  NOT NULL,

    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
