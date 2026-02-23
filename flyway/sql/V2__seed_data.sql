-- COUNTRY
INSERT INTO country (name)
VALUES ('USA');
INSERT INTO country (name)
VALUES ('Germany');
INSERT INTO country (name)
VALUES ('Poland');
INSERT INTO country (name)
VALUES ('Japan');
INSERT INTO country (name)
VALUES ('Brazil');

-- CITY
INSERT INTO city (name, country_id)
VALUES ('New York', 1);
INSERT INTO city (name, country_id)
VALUES ('Berlin', 2);
INSERT INTO city (name, country_id)
VALUES ('Warsaw', 3);
INSERT INTO city (name, country_id)
VALUES ('Tokyo', 4);
INSERT INTO city (name, country_id)
VALUES ('Sao Paulo', 5);

-- ADDRESS
INSERT INTO address (street, number, city_id)
VALUES ('5th Avenue', 101, 1);
INSERT INTO address (street, number, city_id)
VALUES ('Unter den Linden', 45, 2);
INSERT INTO address (street, number, city_id)
VALUES ('Nowy Świat', 15, 3);
INSERT INTO address (street, number, city_id)
VALUES ('Shibuya Street', 23, 4);
INSERT INTO address (street, number, city_id)
VALUES ('Paulista Ave', 321, 5);


-- PERSON
INSERT INTO person (first_name, last_name, email, password, role)
VALUES ( 'John', 'Doe', 'john.doe@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT');
INSERT INTO person (first_name, last_name, email, password, role)
VALUES ( 'Anna', 'Smith', 'anna.smith@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT');
INSERT INTO person (first_name, last_name, email, password, role)
VALUES ( 'Piotr', 'Nowak', 'piotr.nowak@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT');
INSERT INTO person (first_name, last_name, email, password, role)
VALUES ( 'Taro', 'Yamada', 'taro.yamada@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT');
INSERT INTO person ( first_name, last_name, email, password, role)
VALUES ( 'Maria', 'Ferreira', 'maria.ferreira@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'PATIENT');
INSERT INTO person ( first_name, last_name, email, password, role)
VALUES ( 'Rafael', 'Garcia', 'rafael.garcia@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR');
INSERT INTO person ( first_name, last_name, email, password, role)
VALUES ( 'Laura', 'Johnson', 'laura.johnson@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR');
INSERT INTO person ( first_name, last_name, email, password, role)
VALUES ( 'Tom', 'Adams', 'tom.adams@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR');
INSERT INTO person ( first_name, last_name, email, password, role)
VALUES ( 'Hannah', 'Brown', 'hannah.brown@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR');
INSERT INTO person (first_name, last_name, email, password, role)
VALUES ( 'Kevin', 'Lee', 'kevin.lee@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'DOCTOR');
INSERT INTO person (first_name, last_name, email, password, role)
VALUES ( 'Adam', 'Test', 'admin@example.com', '$2a$10$vLrzSWN6uhaiMxMaiKwG4u77Dzu81A4/V.vL.hU0Ns2Gsz56HnIKG', 'ADMIN');
-- pass 111

-- PATIENT
INSERT INTO patient (id, birth_date, phone_number, address_id)
VALUES (1, '1990-05-15', '+123456789', 1);
INSERT INTO patient (id, birth_date, phone_number, address_id)
VALUES (2, '1985-07-20', '+49123456789', 2);
INSERT INTO patient (id, birth_date, phone_number, address_id)
VALUES (3, '1993-03-10', '+48123456789', 3);
INSERT INTO patient (id, birth_date, phone_number, address_id)
VALUES (4, '1978-11-05', '+81123456789', 4);
INSERT INTO patient (id, birth_date, phone_number, address_id)
VALUES (5, '1980-09-25', '+55123456789', 5);

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
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-01', '10:30:00', 'SCHEDULED', 1, 6);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-02', '11:00:00', 'SCHEDULED', 2, 7);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-06', '09:00:00', 'COMPLETED', 3, 8);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-05', '11:00:00', 'CANCELLED', 4, 9);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-07', '13:00:00', 'CANCELLED', 5, 10);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-02', '13:00:00', 'SCHEDULED', 1, 6);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-05', '11:00:00', 'CANCELLED', 1, 6);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-06', '12:00:00', 'SCHEDULED', 1, 6);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-06', '09:00:00', 'COMPLETED', 3, 9);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-08', '09:30:00', 'COMPLETED', 2, 8);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-06', '09:00:00', 'COMPLETED', 4, 6);
INSERT INTO appointment (date, time, status, patient_id, doctor_id)
VALUES ('2025-05-01', '09:30:00', 'COMPLETED', 1, 10);


-- MEDICAL RECORD
INSERT INTO record (diagnosis, summary, appointment_id)
VALUES ('Hypertension', 'Patient with elevated blood pressure', 3);
INSERT INTO record (diagnosis, summary, appointment_id)
VALUES ('Migraine', 'Recurring headache attacks', 9);
INSERT INTO record (diagnosis, summary, appointment_id)
VALUES ('Flu', 'High fever and coughing', 10);
INSERT INTO record (diagnosis, summary, appointment_id)
VALUES ('Diabetes', 'High blood sugar levels', 11);
INSERT INTO record (diagnosis, summary, appointment_id)
VALUES ('High cholesterol', 'Elevated LDL levels', 12);

-- PRESCRIPTION
INSERT INTO prescription (medicine, dosage, start_date, end_date, frequency, record_id)
VALUES ('Aspirin', '1 tablet', '2025-04-01', '2025-04-10', 'DAILY', 1);
INSERT INTO prescription (medicine, dosage, start_date, end_date, frequency, record_id)
VALUES ('Ibuprofen', '2 capsules', '2025-04-05', '2025-04-15', 'DAILY', 1);
INSERT INTO prescription (medicine, dosage, start_date, end_date, frequency, record_id)
VALUES ('Amoxicillin', '50 mg', '2025-04-08', NULL, 'WEEKLY', 3);
INSERT INTO prescription (medicine, dosage, start_date, end_date, frequency, record_id)
VALUES ('Metformin', '100 mg', '2025-04-01', '2025-05-01', 'MONTHLY', 2);
INSERT INTO prescription (medicine, dosage, start_date, end_date, frequency, record_id)
VALUES ('Atorvastatin', '10 drops', '2025-04-12', NULL, 'NEEDED', 4);

-- CONSULTATION
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('MONDAY', '08:00:00', '12:00:00', 6);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('TUESDAY', '09:00:00', '13:00:00', 6);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('WEDNESDAY', '10:00:00', '14:00:00', 6);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('THURSDAY', '08:30:00', '12:30:00', 6);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('FRIDAY', '10:00:00', '17:00:00', 6);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('MONDAY', '08:00:00', '12:00:00', 7);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('TUESDAY', '08:00:00', '13:00:00', 8);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('WEDNESDAY', '10:00:00', '14:00:00', 7);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('THURSDAY', '08:30:00', '12:30:00', 8);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('FRIDAY', '11:00:00', '19:00:00', 7);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('MONDAY', '08:00:00', '12:00:00', 9);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('TUESDAY', '09:00:00', '13:00:00', 9);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('WEDNESDAY', '10:00:00', '14:00:00', 10);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('THURSDAY', '08:30:00', '12:30:00', 10);
INSERT INTO consultation (day, start_work_time, end_work_time, doctor_id)
VALUES ('FRIDAY', '10:00:00', '16:00:00', 10);
