USE insurance_database;
# ==========================================
# 1. POPULATE CLIENTS
# ==========================================
INSERT INTO clients (first_name, last_name, middle_name, birth_date, is_employee, sex, enrollment_date, is_active, data_status) VALUES 
('KURT ANJO', 'AZUCENA', 'LAGUERTA', '2000-12-21', 0, 'M', '2019-05-29', 1, 1),
('MARIA', 'DELA', 'CRUZ', '1995-05-15', 1, 'F', '2020-03-06', 1, 1),
('JOSE', 'PROTACIO', 'RIZAL', '1988-06-19', 0, 'M', '2020-11-05', 1, 0),
('GABRIELA', 'DIZON', 'SILANG', '2001-03-10', 1, 'F', '2020-11-05', 0, 1),
('ANDRES', 'DE CASTRO', 'BONIFACIO', '1992-11-30', 0, 'M', '2020-11-07', 1, 0),
('MELCHORA', 'AQUINO', 'RAMOS', '1978-01-06', 1, 'F', '2020-12-11', 1, 1),
('EMILIO', 'GARCIA', 'AGUINALDO', '2003-08-22', 0, 'M', '2020-12-19', 0, 0),
('LEONOR', 'RIVERA', 'BAUTISTA', '1999-04-12', 0, 'F', '2021-03-17', 1, 1),
('ANTONIO', 'PEREZ', 'LUNA', '1985-10-29', 1, 'M', '2021-06-01', 1, 1),
('TERESA', 'HERNANDEZ', 'MAGBANUA', '1990-07-04', 0, 'F', '2021-08-07', 1, 1),
('JUAN', 'SANTOS', 'LUNA', '2000-02-14', 1, 'M', '2019-08-29', 0, 1);

# ==========================================
# 2. POPULATE ILLNESS
# ==========================================
INSERT INTO illness (illness_name, icd10_code, data_status) VALUES 
('Acute Upper Respiratory Infection', 'J06.9', 1),
('Essential Hypertension', 'I10', 1),
('Type 2 Diabetes Mellitus', 'E11.9', 1),
('Dengue Fever', 'A90', 1),
('Acute Gastroenteritis', 'A09', 1),
('Pneumonia, Unspecified', 'J18.9', 1),
('Bronchial Asthma', 'J45.9', 1),
('Migraine without Aura', 'G43.0', 1),
('Urinary Tract Infection', 'N39.0', 1),
('COVID-19, Virus Identified', 'U07.1', 1);

# ==========================================
# 3. POPULATE POLICY
# ==========================================
INSERT INTO policy (plan_name, coverage_type, coverage_limit, premium_amount, payment_period, inclusion, data_status) VALUES 
('Basic Starter', 'In-Patient', 100000.0, 12000.0, 'Yearly', 'Room and Board', 1),
('Silver Shield', 'Comprehensive', 250000.0, 25000.0, 'Yearly', 'Consultations included', 1),
('Gold Health', 'Comprehensive', 500000.0, 45000.0, 'Yearly', 'Dental + Optical', 1),
('Platinum Plus', 'All-Access', 1000000.0, 80000.0, 'Yearly', 'Executive Checkup', 1),
('Senior Care A', 'In-Patient', 150000.0, 35000.0, 'Yearly', 'Critical Care', 1),
('Kiddie Protect', 'Out-Patient', 50000.0, 8000.0, 'Yearly', 'Vaccinations', 1),
('Corporate Standard', 'Comprehensive', 200000.0, 18000.0, 'Monthly', 'Maternity', 1),
('Corporate Executive', 'All-Access', 800000.0, 60000.0, 'Monthly', 'Travel Insurance', 1),
('Emergency Saver', 'Emergency Only', 50000.0, 5000.0, 'Yearly', 'Ambulance', 1),
('Family Bundle', 'Comprehensive', 400000.0, 70000.0, 'Yearly', 'Dependents included', 1);

# ==========================================
# 4. POPULATE HOSPITAL
# ==========================================
INSERT INTO hospital (hospital_name, address, city, zipcode, contact_no, email, data_status) VALUES 
('Makati Medical Center', '2 Amorsolo Street', 'Makati', 1229, '02-8888-8999', 'info@makatimed.net.ph', 1),
('St. Luke''s Medical Center', '279 E Rodriguez Sr. Ave', 'Quezon City', 1112, '02-8723-0101', 'info@stlukes.com.ph', 1),
('Philippine General Hospital', 'Taft Avenue', 'Manila', 1000, '02-8554-8400', 'pgh.admin@up.edu.ph', 1),
('The Medical City', 'Ortigas Avenue', 'Pasig', 1605, '02-8988-1000', 'mail@themedicalcity.com', 1),
('Asian Hospital', '2205 Civic Dr', 'Muntinlupa', 1780, '02-8771-9000', 'info@asianhospital.com', 1),
('Cardinal Santos Medical Center', '10 Wilson St', 'San Juan', 1500, '02-8727-0001', 'csmc@cardinalsantos.com.ph', 1),
('Lung Center of the Philippines', 'Quezon Avenue', 'Quezon City', 1100, '02-8924-6101', 'lcp@doh.gov.ph', 1),
('Philippine Heart Center', 'East Avenue', 'Quezon City', 1100, '02-8925-2401', 'phc@doh.gov.ph', 1),
('VRP Medical Center', '163 EDSA', 'Mandaluyong', 1501, '02-8464-9999', 'care@vrp.com.ph', 1),
('Manila Doctors Hospital', '667 United Nations Ave', 'Manila', 1000, '02-8558-0888', 'info@maniladoctors.com.ph', 1);

# ==========================================
# 5. POPULATE DOCTOR
# ==========================================
INSERT INTO doctor (first_name, last_name, middle_name, doctor_type, contact_no, email, data_status) VALUES 
('Gregory', 'House', 'Land', 'Diagnostician', '09171001001', 'house@med.com', 1),
('Meredith', 'Grey', 'Ellis', 'General Surgeon', '09171001002', 'grey@hospital.com', 1),
('Shaun', 'Murphy', 'Lee', 'Surgeon', '09171001003', 'murphy@stbonaventure.com', 1),
('Stephen', 'Strange', 'Vincent', 'Neurosurgeon', '09171001004', 'strange@ny.com', 1),
('Leonard', 'McCoy', 'H', 'General Practitioner', '09171001005', 'bones@starfleet.com', 1),
('Doogie', 'Howser', 'K', 'Pediatrician', '09171001006', 'doogie@la.com', 1),
('John', 'Watson', 'Hamish', 'General Practitioner', '09171001007', 'watson@bakerst.com', 1),
('Michaela', 'Quinn', 'E', 'Family Medicine', '09171001008', 'dr.mike@colorado.com', 1),
('Beverly', 'Crusher', 'C', 'General Practitioner', '09171001009', 'crusher@enterprise.com', 1),
('Drake', 'Ramoray', 'F', 'Neurosurgeon', '09171001010', 'drake@days.com', 1);

# ==========================================
# 6. POPULATE CLIENT_POLICY
# ==========================================
# Note: Relying on Auto Increment to generate client_plan_id 1-10 sequentially
INSERT INTO client_policy (member_id, plan_id, preexisting_illnesses, effective_date, expiry_date, policy_status) VALUES 
(1, 1, 'None', '2023-01-15', '2024-01-15', 'Active'),
(2, 2, 'Asthma', '2023-02-01', '2024-02-01', 'Active'),
(5, 3, 'None', '2023-03-10', '2024-03-10', 'Active'),
(2, 4, 'Diabetes', '2023-01-01', '2024-01-01', 'Active'),
(7, 5, 'Hypertension', '2022-12-01', '2023-12-01', 'Expired'),
(8, 6, 'None', '2023-06-15', '2024-06-15', 'Active'),
(2, 7, 'None', '2023-07-01', '2024-07-01', 'Active'),
(3, 8, 'Allergies', '2023-08-20', '2024-08-20', 'Active'),
(8, 9, 'None', '2023-09-05', '2024-09-05', 'Active'),
(1, 10, 'None', '2023-10-01', '2024-10-01', 'Active');

# ==========================================
# 7. POPULATE CLIENT_PAYMENT
# ==========================================
INSERT INTO client_payment (client_plan_id, amount, payment_date, payment_method, premium_payment_status) VALUES 
(1, 12000.0, '2023-01-15', 'Credit Card', 'Complete'),
(2, 25000.0, '2023-02-01', 'Bank Transfer', 'Complete'),
(3, 45000.0, '2023-03-10', 'Check', 'Complete'),
(4, 80000.0, '2023-01-01', 'Credit Card', 'Complete'),
(1, 12000.0, '2022-12-01', 'Cash', 'Complete'),
(5, 35000.0, '2023-06-15', 'Bank Transfer', 'Complete'),
(6, 8000.0, '2023-07-01', 'GCash', 'Complete'),
(2, 0.0, '2023-08-20', 'N/A', 'Overdue'),
(3, 22500.0, '2023-09-05', 'Check', 'Partial'),
(4, 80000.0, '2023-10-01', 'Credit Card', 'Complete');

# ==========================================
# 8. POPULATE CLAIM
# ==========================================
INSERT INTO claim (client_plan_id, illness_id, hospital_id, doctor_id, service_date, service_type, service_amount, covered_amount, claim_status) VALUES 
(1, 1, 1, 1, '2023-04-01', 'Consultation', 1500.0, 1500.0, 'Complete'),
(2, 7, 2, 2, '2023-05-10', 'Surgery', 50000.0, 45000.0, 'Complete'),
(3, 4, 3, 3, '2023-06-05', 'Laboratory', 2500.0, 2500.0, 'Complete'),
(4, 3, 4, 4, '2023-02-14', 'Consultation', 2000.0, 2000.0, 'Complete'),
(5, 2, 5, 5, '2023-03-03', 'Emergency', 5000.0, 5000.0, 'Complete'),
(6, 6, 6, 6, '2023-07-22', 'Checkup', 1000.0, 1000.0, 'Complete'),
(7, 5, 7, 7, '2023-08-30', 'X-Ray', 1200.0, 1200.0, 'Complete'),
(8, 8, 8, 8, '2023-09-12', 'Consultation', 1500.0, 1500.0, 'Complete'),
(9, 9, 9, 9, '2023-10-01', 'Confinement', 25000.0, 20000.0, 'In Progress'),
(10, 10, 10, 10, '2023-11-05', 'Surgery', 80000.0, 80000.0, 'In Progress');

# ==========================================
# 9. POPULATE PAYOUT
# ==========================================
INSERT INTO payout (claim_id, payout_date, payout_amount, payout_status) VALUES 
(1, '2023-04-01', 1500.0, 'Complete'),
(2, '2023-05-10', 45000.0, 'Complete'),
(3, '2023-06-05', 2500.0, 'Complete'),
(4, '2023-02-14', 2000.0, 'Complete'),
(5, '2023-03-03', 5000.0, 'Complete'),
(6, '2023-07-22', 1000.0, 'Complete'),
(7, '2023-08-30', 1200.0, 'Complete'),
(8, '2023-09-12', 1500.0, 'Complete'),
(9, '2023-10-01', 20000.0, 'Pending'),
(10, '2023-11-05', 80000.0, 'Processing');

# ==========================================
# 10. POPULATE LOA
# ==========================================
INSERT INTO loa (client_plan_id, hospital_id, doctor_id, illness_id, service_type, loa_status) VALUES 
(1, 1, 1, 1, 'Consultation', 'Approved'),
(2, 2, 2, 7, 'Surgery', 'Approved'),
(3, 3, 3, 4, 'Laboratory', 'Approved'),
(4, 4, 4, 3, 'Consultation', 'Approved'),
(5, 5, 5, 2, 'Emergency', 'Approved'),
(6, 6, 6, 6, 'Checkup', 'Approved'),
(7, 7, 7, 5, 'X-Ray', 'Approved'),
(8, 8, 8, 8, 'Consultation', 'Approved'),
(9, 9, 9, 9, 'Confinement', 'Pending'),
(10, 10, 10, 10, 'Surgery', 'Approved');