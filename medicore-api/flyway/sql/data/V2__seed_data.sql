-- COUNTRY
INSERT INTO country (name)
VALUES ('USA'),
       ('Germany'),
       ('Poland'),
       ('Japan'),
       ('Brazil');

-- CITY
INSERT INTO city (name, country_id)
VALUES ('New York', 1),
       ('Berlin', 2),
       ('Warsaw', 3),
       ('Tokyo', 4),
       ('Sao Paulo', 5);

-- ADDRESS
INSERT INTO address (street, number, city_id)
VALUES ('5th Avenue', 101, 1),
       ('Unter den Linden', 45, 2),
       ('Nowy Świat', 15, 3),
       ('Shibuya Street', 23, 4),
       ('Paulista Ave', 321, 5);


-- PERSON
INSERT INTO person (first_name, last_name, email, password, role, status, gender)
VALUES ('John', 'Doe', 'john.doe@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'PATIENT', 'ACTIVE', 'MALE'),
       ('Anna', 'Smith', 'anna.smith@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'PATIENT', 'ACTIVE', 'FEMALE'),
       ('Piotr', 'Nowak', 'piotr.nowak@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'PATIENT', 'ACTIVE', 'MALE'),
       ('Taro', 'Yamada', 'taro.yamada@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'PATIENT', 'ACTIVE', 'MALE'),
       ('Maria', 'Ferreira', 'maria.ferreira@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT', 'ACTIVE', 'FEMALE'),
       ('Rafael', 'Garcia', 'rafael.garcia@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'DOCTOR', 'ACTIVE', 'MALE'),
       ('Laura', 'Johnson', 'laura.johnson@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'DOCTOR', 'ACTIVE', 'FEMALE'),
       ('Tom', 'Adams', 'tom.adams@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'DOCTOR', 'ACTIVE', 'MALE'),
       ('Hannah', 'Brown', 'hannah.brown@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'DOCTOR', 'ACTIVE', 'FEMALE'),
       ('Kevin', 'Lee', 'kevin.lee@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG',
        'DOCTOR', 'ACTIVE', 'MALE'),
       ('Adam', 'Test', 'admin@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'ADMIN',
        'ACTIVE', 'MALE');
-- pass 111

-- PATIENT
INSERT INTO patient (id, birth_date, phone_number, address_id, weight, height, pregnant)
VALUES (1, '1990-05-15', '+123456789', 1, 70.5, 170.3, false),
       (2, '1985-07-20', '+49123456789', 2, 80.4, 190.0, true),
       (3, '1993-03-10', '+48123456789', 3, 56.9, 169.8, false),
       (4, '1978-11-05', '+81123456789', 4, 70.3, 184.0, false),
       (5, '1980-09-25', '+55123456789', 5, 69.4, 179.4, false);

-- DOCTOR
INSERT INTO doctor (id, experience, employment_date, specialization)
VALUES (6, 10, '2015-06-01', 'CARDIOLOGIST'),
       (7, 5, '2018-09-15', 'DERMATOLOGIST'),
       (8, 15, '2010-03-20', 'NEUROLOGIST'),
       (9, 7, '2017-11-05', 'PEDIATRICIAN'),
       (10, 12, '2012-01-10', 'ONCOLOGIST');

-- APPOINTMENT
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2026-04-06', '15:00:00', 'SCHEDULED', 1, 6),
       ('2026-03-02', '11:00:00', 'SCHEDULED', 2, 7),
       ('2026-02-06', '09:00:00', 'COMPLETED', 3, 8),
       ('2026-03-05', '11:00:00', 'CANCELLED', 4, 9),
       ('2026-02-07', '13:00:00', 'CANCELLED', 5, 10),
       ('2026-05-02', '13:00:00', 'SCHEDULED', 1, 6),
       ('2026-06-05', '11:00:00', 'CANCELLED', 1, 6),
       ('2026-02-06', '12:00:00', 'SCHEDULED', 1, 6),
       ('2026-01-06', '09:00:00', 'COMPLETED', 1, 9),
       ('2026-03-08', '09:30:00', 'COMPLETED', 2, 6),
       ('2026-04-06', '09:00:00', 'COMPLETED', 4, 6),
       ('2026-02-01', '09:30:00', 'COMPLETED', 1, 10);


-- MEDICAL RECORD
INSERT INTO record (diagnosis, summary, appointment_id)
VALUES ('Hypertension', 'Patient with elevated blood pressure', 3),
       ('Migraine', 'Recurring headache attacks', 9),
       ('Flu', 'High fever and coughing', 10),
       ('Diabetes', 'High blood sugar levels', 11),
       ('High cholesterol', 'Elevated LDL levels', 12);

-- PRESCRIPTION
INSERT INTO prescription (medicine, dosage, start_date, end_date, frequency, record_id)
VALUES ('Aspirin', '1 tablet', '2025-04-01', '2025-04-10', 'DAILY', 1),
       ('Ibuprofen', '2 capsules', '2025-04-05', '2025-04-15', 'DAILY', 1),
       ('Amoxicillin', '50 mg', '2025-04-08', NULL, 'WEEKLY', 3),
       ('Metformin', '100 mg', '2025-04-01', '2025-05-01', 'MONTHLY', 2),
       ('Atorvastatin', '10 drops', '2025-04-12', NULL, 'NEEDED', 4);

-- CONSULTATION
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('MONDAY', '08:00:00', '12:00:00', 6),
       ('TUESDAY', '09:00:00', '13:00:00', 6),
       ('WEDNESDAY', '10:00:00', '14:00:00', 6),
       ('THURSDAY', '08:30:00', '12:30:00', 6),
       ('FRIDAY', '10:00:00', '17:00:00', 6),
       ('MONDAY', '08:00:00', '12:00:00', 7),
       ('TUESDAY', '08:00:00', '13:00:00', 8),
       ('WEDNESDAY', '10:00:00', '14:00:00', 7),
       ('THURSDAY', '08:30:00', '12:30:00', 8),
       ('FRIDAY', '11:00:00', '19:00:00', 7),
       ('MONDAY', '08:00:00', '12:00:00', 9),
       ('TUESDAY', '09:00:00', '13:00:00', 9),
       ('WEDNESDAY', '10:00:00', '14:00:00', 10),
       ('THURSDAY', '08:30:00', '12:30:00', 10),
       ('FRIDAY', '10:00:00', '16:00:00', 10);
