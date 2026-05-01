-- COUNTRY
INSERT INTO country (public_id, name)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'USA');
INSERT INTO country (name)
VALUES ('Germany');
INSERT INTO country (name)
VALUES ('Poland');
INSERT INTO country (name)
VALUES ('Japan');
INSERT INTO country (name)
VALUES ('Brazil');

-- CITY
INSERT INTO city (public_id, name, country_id)
VALUES ('11111111-1111-1111-1111-111111111111', 'New York', 1);
INSERT INTO city (name, country_id)
VALUES ('Berlin', 2);
INSERT INTO city (name, country_id)
VALUES ('Warsaw', 3);
INSERT INTO city (name, country_id)
VALUES ('Tokyo', 4);
INSERT INTO city (name, country_id)
VALUES ('Sao Paulo', 5);

-- ADDRESS
INSERT INTO address (public_id, street, number, city_id)
VALUES ('22222222-2222-2222-2222-222222222222', '5th Avenue', 101, 1);
INSERT INTO address (street, number, city_id)
VALUES ('Unter den Linden', 45, 2);
INSERT INTO address (street, number, city_id)
VALUES ('Nowy Świat', 15, 3);
INSERT INTO address (street, number, city_id)
VALUES ('Shibuya Street', 23, 4);
INSERT INTO address (street, number, city_id)
VALUES ('Paulista Ave', 321, 5);


-- PERSON
INSERT INTO person (public_id, first_name, last_name, email, password, role, status, gender)
VALUES ('00000000-0000-0000-0000-000000000001', 'John', 'Doe', 'john.doe@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT', 'ACTIVE', 'MALE'),

       ('00000000-0000-0000-0000-000000000002', 'Anna', 'Smith', 'anna.smith@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT', 'ACTIVE', 'FEMALE'),

       ('00000000-0000-0000-0000-000000000003', 'Piotr', 'Nowak', 'piotr.nowak@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT', 'ACTIVE', 'MALE'),

       ('00000000-0000-0000-0000-000000000004', 'Taro', 'Yamada', 'taro.yamada@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT', 'ACTIVE', 'MALE'),

       ('00000000-0000-0000-0000-000000000005', 'Maria', 'Ferreira', 'maria.ferreira@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT', 'ACTIVE', 'FEMALE'),

       ('00000000-0000-0000-0000-000000000006', 'Rafael', 'Garcia', 'rafael.garcia@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR', 'ACTIVE', 'MALE'),

       ('00000000-0000-0000-0000-000000000007', 'Laura', 'Johnson', 'laura.johnson@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR', 'ACTIVE', 'FEMALE'),

       ('00000000-0000-0000-0000-000000000008', 'Tom', 'Adams', 'tom.adams@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR', 'ACTIVE', 'MALE'),

       ('00000000-0000-0000-0000-000000000009', 'Hannah', 'Brown', 'hannah.brown@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR', 'ACTIVE', 'FEMALE'),

       ('00000000-0000-0000-0000-000000000010', 'Kevin', 'Lee', 'kevin.lee@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR', 'ACTIVE', 'MALE'),

       ('00000000-0000-0000-0000-000000000011', 'Adam', 'Test', 'admin@example.com',
        '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'ADMIN', 'ACTIVE', 'MALE');
-- pass 111

-- PATIENT
INSERT INTO patient (id, birth_date, phone_number, address_id, weight, height)
VALUES (1, '1990-05-15', '+123456789', 1, 70.5, 170.3);
INSERT INTO patient (id, birth_date, phone_number, address_id, weight, height, pregnant)
VALUES (2, '1985-07-20', '+49123456789', 2, 80.4, 190.0, true);
INSERT INTO patient (id, birth_date, phone_number, address_id, weight, height)
VALUES (3, '1993-03-10', '+48123456789', 3, 56.9, 169.8);
INSERT INTO patient (id, birth_date, phone_number, address_id, weight, height)
VALUES (4, '1978-11-05', '+81123456789', 4, 70.3, 184);
INSERT INTO patient (id, birth_date, phone_number, address_id, weight, height)
VALUES (5, '1980-09-25', '+55123456789', 5, 69.4, 179.4);

-- DOCTOR
INSERT INTO doctor (id, experience, employment_date, specialization)
VALUES (6, 10, '2015-06-01', 'CARDIOLOGIST');
INSERT INTO doctor (id, experience, employment_date, specialization)
VALUES (7, 5, '2018-09-15', 'DERMATOLOGIST');
INSERT INTO doctor (id, experience, employment_date, specialization)
VALUES (8, 15, '2010-03-20', 'NEUROLOGIST');
INSERT INTO doctor (id, experience, employment_date, specialization)
VALUES (9, 7, '2017-11-05', 'PEDIATRICIAN');
INSERT INTO doctor (id, experience, employment_date, specialization)
VALUES (10, 12, '2012-01-10', 'ONCOLOGIST');


-- APPOINTMENT
INSERT INTO appointment (public_id, date, time, status, patient_id, doctor_id)
VALUES ('10000000-0000-0000-0000-000000000001', '2026-04-06', '15:00:00', 'SCHEDULED', 1, 6),

       ('10000000-0000-0000-0000-000000000002', '2026-03-02', '11:00:00', 'SCHEDULED', 2, 7),

       ('10000000-0000-0000-0000-000000000003', '2026-02-06', '09:00:00', 'COMPLETED', 3, 8),

       ('10000000-0000-0000-0000-000000000004', '2026-03-05', '11:00:00', 'CANCELLED', 4, 9),

       ('10000000-0000-0000-0000-000000000005', '2026-02-07', '13:00:00', 'CANCELLED', 5, 10),

       ('10000000-0000-0000-0000-000000000006', '2026-05-02', '13:00:00', 'SCHEDULED', 1, 6),

       ('10000000-0000-0000-0000-000000000007', '2026-06-05', '11:00:00', 'CANCELLED', 1, 6),

       ('10000000-0000-0000-0000-000000000008', '2026-02-06', '12:00:00', 'SCHEDULED', 1, 6),

       ('10000000-0000-0000-0000-000000000009', '2026-01-06', '09:00:00', 'COMPLETED', 1, 9),

       ('10000000-0000-0000-0000-000000000010', '2026-03-08', '09:30:00', 'COMPLETED', 2, 6),

       ('10000000-0000-0000-0000-000000000011', '2026-04-06', '09:00:00', 'COMPLETED', 4, 6),

       ('10000000-0000-0000-0000-000000000012', '2026-02-01', '09:30:00', 'COMPLETED', 1, 10);


-- MEDICAL RECORD
INSERT INTO record (public_id, diagnosis, summary, appointment_id)
VALUES ('20000000-0000-0000-0000-000000000001', 'Hypertension', 'Patient with elevated blood pressure', 3),

       ('20000000-0000-0000-0000-000000000002', 'Migraine', 'Recurring headache attacks', 9),

       ('20000000-0000-0000-0000-000000000003', 'Flu', 'High fever and coughing', 10),

       ('20000000-0000-0000-0000-000000000004', 'Diabetes', 'High blood sugar levels', 11),

       ('20000000-0000-0000-0000-000000000005', 'High cholesterol', 'Elevated LDL levels', 12);

-- PRESCRIPTION
INSERT INTO prescription (public_id, medicine, dosage, start_date, end_date, frequency, record_id)
VALUES ('30000000-0000-0000-0000-000000000001', 'Aspirin', '1 tablet', '2025-04-01', '2025-04-10', 'DAILY', 1),

       ('30000000-0000-0000-0000-000000000002', 'Ibuprofen', '2 capsules', '2025-04-05', '2025-04-15', 'DAILY', 1),

       ('30000000-0000-0000-0000-000000000003', 'Amoxicillin', '50 mg', '2025-04-08', NULL, 'WEEKLY', 3),

       ('30000000-0000-0000-0000-000000000004', 'Metformin', '100 mg', '2025-04-01', '2025-05-01', 'MONTHLY', 2),

       ('30000000-0000-0000-0000-000000000005', 'Atorvastatin', '10 drops', '2025-04-12', NULL, 'NEEDED', 4);

-- CONSULTATION
INSERT INTO consultation (public_id, day, start_work_time, end_work_time, doctor_id)
VALUES ('40000000-0000-0000-0000-000000000001', 'MONDAY', '08:00:00', '12:00:00', 6),

       ('40000000-0000-0000-0000-000000000002', 'TUESDAY', '09:00:00', '13:00:00', 6),

       ('40000000-0000-0000-0000-000000000003', 'WEDNESDAY', '10:00:00', '14:00:00', 6),

       ('40000000-0000-0000-0000-000000000004', 'THURSDAY', '08:30:00', '12:30:00', 6),

       ('40000000-0000-0000-0000-000000000005', 'FRIDAY', '10:00:00', '17:00:00', 6),

       ('40000000-0000-0000-0000-000000000006', 'MONDAY', '08:00:00', '12:00:00', 7),

       ('40000000-0000-0000-0000-000000000007', 'TUESDAY', '08:00:00', '13:00:00', 8),

       ('40000000-0000-0000-0000-000000000008', 'WEDNESDAY', '10:00:00', '14:00:00', 7),

       ('40000000-0000-0000-0000-000000000009', 'THURSDAY', '08:30:00', '12:30:00', 8),

       ('40000000-0000-0000-0000-000000000010', 'FRIDAY', '11:00:00', '19:00:00', 7),

       ('40000000-0000-0000-0000-000000000011', 'MONDAY', '08:00:00', '12:00:00', 9),

       ('40000000-0000-0000-0000-000000000012', 'TUESDAY', '09:00:00', '13:00:00', 9),

       ('40000000-0000-0000-0000-000000000013', 'WEDNESDAY', '10:00:00', '14:00:00', 10),

       ('40000000-0000-0000-0000-000000000014', 'THURSDAY', '08:30:00', '12:30:00', 10),

       ('40000000-0000-0000-0000-000000000015', 'FRIDAY', '10:00:00', '16:00:00', 10);

-- TEST
INSERT INTO test (public_id, performed_date, patient_id)
VALUES ('11100000-0000-0000-0000-000000000000', '2025-01-11', 1);

-- LAB RESULT
INSERT INTO lab_result (parameter, value, unit, test_id)
VALUES ('RBC', 4.96, '10^12/L', 1),
       ('HGB', 149, 'g/L', 1),
       ('HCT', 45.8, '%', 1),
       ('CREATININE', NULL, 'mg/dL', 1),
       ('GLUCOSE', NULL, 'mg/dL', 1);

-- RISK RESULT

INSERT INTO risk_result (disease, risk_group, risk_percent, calculated_at, patient_id, test_id)
VALUES ('ANEMIA', 'NONE', 0, '2026-04-19 09:49:36.693880', 1, 1),
       ('DIABETES', 'UNKNOWN', NULL, '2026-04-19 09:49:36.697967', 1, 1),
       ('CKD', 'UNKNOWN', NULL, '2026-04-19 09:49:36.700109', 1, 1);
