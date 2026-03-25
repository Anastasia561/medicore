-- Table: test
CREATE TABLE test
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    performed_date       DATE      NOT NULL,
    patient_id BIGINT    NOT NULL,

    CONSTRAINT test_patient_fk
        FOREIGN KEY (patient_id)
            REFERENCES patient (id)
);

-- Table: lab_result
CREATE TABLE lab_result
(
    id        BIGSERIAL        NOT NULL PRIMARY KEY,
    parameter VARCHAR(100)     NOT NULL,
    value     DOUBLE PRECISION NOT NULL,
    unit      VARCHAR(50)      NOT NULL,
    test_id   BIGINT           NOT NULL,

    CONSTRAINT lab_result_test_fk
        FOREIGN KEY (test_id)
            REFERENCES test (id)
);

-- Table: risk_result
CREATE TABLE risk_result
(
    id            BIGSERIAL        NOT NULL PRIMARY KEY,
    disease       VARCHAR(100)     NOT NULL,
    risk_percent  DOUBLE PRECISION NOT NULL,
    calculated_at TIMESTAMP        NOT NULL,
    patient_id    BIGINT           NOT NULL,
    test_id       BIGINT           NOT NULL,

    CONSTRAINT risk_result_patient_fk
        FOREIGN KEY (patient_id)
            REFERENCES patient (id),

    CONSTRAINT risk_result_test_fk
        FOREIGN KEY (test_id)
            REFERENCES test (id)
);
