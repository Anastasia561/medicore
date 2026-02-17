-- COUNTRY
INSERT INTO country (id, name)
VALUES (1, 'USA');
INSERT INTO country (id, name)
VALUES (2, 'Germany');
INSERT INTO country (id, name)
VALUES (3, 'Poland');
INSERT INTO country (id, name)
VALUES (4, 'Japan');
INSERT INTO country (id, name)
VALUES (5, 'Brazil');

-- CITY
INSERT INTO city (id, name, country_id)
VALUES (1, 'New York', 1);
INSERT INTO city (id, name, country_id)
VALUES (2, 'Berlin', 2);
INSERT INTO city (id, name, country_id)
VALUES (3, 'Warsaw', 3);
INSERT INTO city (id, name, country_id)
VALUES (4, 'Tokyo', 4);
INSERT INTO city (id, name, country_id)
VALUES (5, 'Sao Paulo', 5);

-- ADDRESS
INSERT INTO address (id, street, number, city_id)
VALUES (1, '5th Avenue', 101, 1);
INSERT INTO address (id, street, number, city_id)
VALUES (2, 'Unter den Linden', 45, 2);
INSERT INTO address (id, street, number, city_id)
VALUES (3, 'Nowy Świat', 15, 3);
INSERT INTO address (id, street, number, city_id)
VALUES (4, 'Shibuya Street', 23, 4);
INSERT INTO address (id, street, number, city_id)
VALUES (5, 'Paulista Ave', 321, 5);


-- PERSON
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (1, 'jdoe', 'John', 'Doe', 'john.doe@example.com', '111', 'PATIENT');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (2, 'asmith', 'Anna', 'Smith', 'anna.smith@example.com', '111', 'PATIENT');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (3, 'pnowak', 'Piotr', 'Nowak', 'piotr.nowak@example.com', '111', 'PATIENT');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (4, 'yamada', 'Taro', 'Yamada', 'taro.yamada@example.com', '111', 'PATIENT');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (5, 'mferreira', 'Maria', 'Ferreira', 'maria.ferreira@example.com', '111', 'PATIENT');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (6, 'rgarcia', 'Rafael', 'Garcia', 'rafael.garcia@example.com', '111', 'DOCTOR');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (7, 'ljohnson', 'Laura', 'Johnson', 'laura.johnson@example.com', '111', 'DOCTOR');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (8, 'tadams', 'Tom', 'Adams', 'tom.adams@example.com', '111', 'DOCTOR');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (9, 'hbrown', 'Hannah', 'Brown', 'hannah.brown@example.com', '111', 'DOCTOR');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (10, 'klee', 'Kevin', 'Lee', 'kevin.lee@example.com', '111', 'DOCTOR');
INSERT INTO person (id, username, first_name, last_name, email, password, role)
VALUES (11, 'adam', 'Adam', 'Test', 'admin@example.com', '111', 'ADMIN');

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
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (1, '2025-05-01', '10:30:00', 'SCHEDULED', 1, 6);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (2, '2025-05-02', '11:00:00', 'SCHEDULED', 2, 7);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (3, '2025-05-06', '09:00:00', 'COMPLETED', 3, 8);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (4, '2025-05-05', '11:00:00', 'CANCELLED', 4, 9);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (5, '2025-05-07', '13:00:00', 'CANCELLED', 5, 10);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (6, '2025-05-02', '13:00:00', 'SCHEDULED', 1, 6);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (7, '2025-05-05', '11:00:00', 'CANCELLED', 1, 6);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (8, '2025-05-06', '12:00:00', 'SCHEDULED', 1, 6);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (9, '2025-05-06', '09:00:00', 'COMPLETED', 3, 9);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (10, '2025-05-08', '09:30:00', 'COMPLETED', 2, 8);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (11, '2025-05-06', '09:00:00', 'COMPLETED', 4, 6);
INSERT INTO appointment (id, date, time, status, patient_id, doctor_id)
VALUES (12, '2025-05-01', '09:30:00', 'COMPLETED', 1, 10);


-- MEDICAL RECORD
INSERT INTO record (id, diagnosis, summary, appointment_id)
VALUES (1, 'Hypertension', 'Patient with elevated blood pressure', 3);
INSERT INTO record (id, diagnosis, summary, appointment_id)
VALUES (2, 'Migraine', 'Recurring headache attacks', 9);
INSERT INTO record (id, diagnosis, summary, appointment_id)
VALUES (3, 'Flu', 'High fever and coughing', 10);
INSERT INTO record (id, diagnosis, summary, appointment_id)
VALUES (4, 'Diabetes', 'High blood sugar levels', 11);
INSERT INTO record (id, diagnosis, summary, appointment_id)
VALUES (5, 'High cholesterol', 'Elevated LDL levels', 12);

-- PRESCRIPTION
INSERT INTO prescription (id, medicine, dosage, start_date, end_date, frequency, record_id)
VALUES (1, 'Aspirin', '1 tablet', '2025-04-01', '2025-04-10', 'DAILY', 1);
INSERT INTO prescription (id, medicine, dosage, start_date, end_date, frequency, record_id)
VALUES (2, 'Ibuprofen', '2 capsules', '2025-04-05', '2025-04-15', 'DAILY', 1);
INSERT INTO prescription (id, medicine, dosage, start_date, end_date, frequency, record_id)
VALUES (3, 'Amoxicillin', '50 mg', '2025-04-08', NULL, 'WEEKLY', 3);
INSERT INTO prescription (id, medicine, dosage, start_date, end_date, frequency, record_id)
VALUES (4, 'Metformin', '100 mg', '2025-04-01', '2025-05-01', 'MONTHLY', 2);
INSERT INTO prescription (id, medicine, dosage, start_date, end_date, frequency, record_id)
VALUES (5, 'Atorvastatin', '10 drops', '2025-04-12', NULL, 'NEEDED', 4);

-- CONSULTATION
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (1, 'MONDAY', '08:00:00', '12:00:00', 6);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (2, 'TUESDAY', '09:00:00', '13:00:00', 6);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (3, 'WEDNESDAY', '10:00:00', '14:00:00', 6);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (4, 'THURSDAY', '08:30:00', '12:30:00', 6);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (5, 'FRIDAY', '10:00:00', '17:00:00', 6);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (6, 'MONDAY', '08:00:00', '12:00:00', 7);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (7, 'TUESDAY', '08:00:00', '13:00:00', 8);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (8, 'WEDNESDAY', '10:00:00', '14:00:00', 7);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (9, 'THURSDAY', '08:30:00', '12:30:00', 8);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (10, 'FRIDAY', '11:00:00', '19:00:00', 7);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (11, 'MONDAY', '08:00:00', '12:00:00', 9);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (12, 'TUESDAY', '09:00:00', '13:00:00', 9);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (13, 'WEDNESDAY', '10:00:00', '14:00:00', 10);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (14, 'THURSDAY', '08:30:00', '12:30:00', 10);
INSERT INTO consultation (id, day, start_work_time, end_work_time, doctor_id)
VALUES (15, 'FRIDAY', '10:00:00', '16:00:00', 10);
