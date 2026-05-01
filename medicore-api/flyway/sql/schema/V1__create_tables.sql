-- Table: country
CREATE TABLE country
(
    id        BIGSERIAL PRIMARY KEY,
    public_id UUID        NOT NULL DEFAULT gen_random_uuid(),
    name      varchar(30) NOT NULL
);

-- Table: city
CREATE TABLE city
(
    id         BIGSERIAL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    name       varchar(30) NOT NULL,
    country_id BIGINT      NOT NULL,
    CONSTRAINT city_country
        FOREIGN KEY (country_id) REFERENCES country (id)
);

-- Table: address
CREATE TABLE address
(
    id      BIGSERIAL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    street  varchar(40) NOT NULL,
    number  integer     NOT NULL,
    city_id BIGINT      NOT NULL,
    CONSTRAINT address_city
        FOREIGN KEY (city_id) REFERENCES city (id)
);

-- Table: person
CREATE TABLE person
(
    id         BIGSERIAL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    password   varchar(255) NOT NULL,
    first_name varchar(20)  NOT NULL,
    last_name  varchar(20)  NOT NULL,
    email      varchar(60)  NOT NULL UNIQUE,
    role       varchar(20)  NOT NULL,
    status     varchar(40)  NOT NULL,
    gender     VARCHAR(20)  NOT NULL
);

-- Table: doctor (inherits ID from person)
CREATE TABLE doctor
(
    id              BIGINT PRIMARY KEY,
    experience      integer     NOT NULL,
    employment_date date        NOT NULL,
    specialization  varchar(40) NOT NULL,
    CONSTRAINT doctor_person
        FOREIGN KEY (id) REFERENCES person (id)
);

-- Table: patient (inherits ID from person)
CREATE TABLE patient
(
    id           BIGINT PRIMARY KEY,
    birth_date   date             NOT NULL,
    phone_number varchar(20)      NOT NULL,
    weight       DOUBLE PRECISION NOT NULL,
    height       DOUBLE PRECISION NOT NULL,
    pregnant     BOOLEAN          NOT NULL DEFAULT FALSE,
    address_id   BIGINT           NOT NULL,
    CONSTRAINT patient_person
        FOREIGN KEY (id) REFERENCES person (id),
    CONSTRAINT patient_address
        FOREIGN KEY (address_id) REFERENCES address (id)
);

-- Table: consultation
CREATE TABLE consultation
(
    id              BIGSERIAL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    day             varchar(20) NOT NULL,
    start_work_time time        NOT NULL,
    end_work_time   time        NOT NULL,
    doctor_id       BIGINT      NOT NULL,
    CONSTRAINT consultation_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctor (id)
);

-- Table: appointment
CREATE TABLE appointment
(
    id            BIGSERIAL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    date          date                  NOT NULL,
    time          time                  NOT NULL,
    patient_id    BIGINT                NOT NULL,
    doctor_id     BIGINT                NOT NULL,
    status        varchar(30)           NOT NULL,
    reminder_sent BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT appointment_patient
        FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT appointment_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctor (id)
);

-- Table: record
CREATE TABLE record
(
    id             BIGSERIAL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    diagnosis      varchar(100) NOT NULL,
    summary        varchar(255) NOT NULL,
    appointment_id BIGINT       NOT NULL,
    CONSTRAINT record_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointment (id)
);

-- Table: prescription
CREATE TABLE prescription
(
    id         BIGSERIAL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    medicine   varchar(60) NOT NULL,
    dosage     varchar(20) NOT NULL,
    start_date date        NOT NULL,
    end_date   date,
    frequency  varchar(50) NOT NULL,
    record_id  BIGINT      NOT NULL,
    CONSTRAINT prescription_record
        FOREIGN KEY (record_id) REFERENCES record (id)
);

-- Table: refresh_token
CREATE TABLE refresh_token
(
    id         BIGSERIAL PRIMARY KEY,
    token      varchar(512) NOT NULL,
    person_id  BIGINT       NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT refresh_token_person
        FOREIGN KEY (person_id) REFERENCES person (id)
);

-- Table: verification_token
CREATE TABLE verification_token
(
    id         BIGSERIAL PRIMARY KEY,
    token_hash TEXT         NOT NULL,
    token_type varchar(40)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table: test
CREATE TABLE test
(
    id             BIGSERIAL NOT NULL PRIMARY KEY,
    public_id  UUID NOT NULL DEFAULT gen_random_uuid(),
    performed_date DATE      NOT NULL,
    patient_id     BIGINT    NOT NULL,

    CONSTRAINT test_patient_fk
        FOREIGN KEY (patient_id)
            REFERENCES patient (id)
);

-- Table: lab_result
CREATE TABLE lab_result
(
    id        BIGSERIAL    NOT NULL PRIMARY KEY,
    parameter VARCHAR(100) NOT NULL,
    value     DOUBLE PRECISION,
    unit      VARCHAR(50)  NOT NULL,
    test_id   BIGINT       NOT NULL,

    CONSTRAINT lab_result_test_fk
        FOREIGN KEY (test_id)
            REFERENCES test (id)
);

-- Table: risk_result
CREATE TABLE risk_result
(
    id            BIGSERIAL    NOT NULL PRIMARY KEY,
    disease       VARCHAR(100) NOT NULL,
    risk_group    VARCHAR(50)  NOT NULL,
    risk_percent  DOUBLE PRECISION,
    calculated_at TIMESTAMP    NOT NULL,
    patient_id    BIGINT       NOT NULL,
    test_id       BIGINT       NOT NULL,

    CONSTRAINT risk_result_patient_fk
        FOREIGN KEY (patient_id)
            REFERENCES patient (id),

    CONSTRAINT risk_result_test_fk
        FOREIGN KEY (test_id)
            REFERENCES test (id)
);
