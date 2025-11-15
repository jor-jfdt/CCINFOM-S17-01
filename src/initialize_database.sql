CREATE DATABASE IF NOT EXISTS insurance_database;
USE insurance_database;

/*
	static final int LONG_STRING_LENGTH = 254; // VARCHAR(254)
	static final int SHORT_STRING_LENGTH = 127; // VARCHAR(127)
	static final int SHORTER_STRING_LENGTH = 15; // VARCHAR(15)
	static final int CHAR_LENGTH = 1; // VARCHAR(1)
*/

CREATE TABLE IF NOT EXISTS Client (
	member_id INT PRIMARY KEY AUTO_INCREMENT,
	first_name VARCHAR(127),
	middle_initial VARCHAR(15),
	last_name VARCHAR(127),
	birth_date DATE,
	is_employee BOOLEAN,
	sex VARCHAR(1),
	is_active BOOLEAN,
	data_status VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS Doctor (
	doctor_id INT PRIMARY KEY AUTO_INCREMENT,
	first_name VARCHAR(127),
	middle_initial VARCHAR(15),
	last_name VARCHAR(127),
	doctor_type VARCHAR(127),
	contact_no INT,
	email VARCHAR(254),
	data_status VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS Hospital (
	hospital_id INT(10) PRIMARY KEY AUTO_INCREMENT,
	hospital_name VARCHAR(127),
	address VARCHAR(254),
	city VARCHAR(127),
	zip_code INT, -- 5 digit sya actually
	contact_no INT,
	email VARCHAR(254),
	data_status VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS Policy (
	policy_id INT PRIMARY KEY AUTO_INCREMENT,
	policy_name VARCHAR(127),
	coverage_type VARCHAR(127),
	coverage_limit FLOAT,
	payment_period VARCHAR(127),
	inclusion VARCHAR(127),
	data_status VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS Illness (
	illness_id INT PRIMARY KEY AUTO_INCREMENT,
	illness_name VARCHAR(127),
	icd10_code VARCHAR(7),
	data_status VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS ClientPolicy (
	client_plan_id INT PRIMARY KEY AUTO_INCREMENT,
	policy_id INT,
	member_id INT,
	preexisting_illness VARCHAR(254),
	effective_date DATE,
	expiry_date DATE,
	status VARCHAR(15),
	data_status VARCHAR(15),
	FOREIGN KEY (policy_id) REFERENCES Policy(policy_id),
	FOREIGN KEY (member_id) REFERENCES Client(member_id)
);

CREATE TABLE IF NOT EXISTS LOA (
	request_id INT PRIMARY KEY AUTO_INCREMENT,
	client_plan_id INT,
	hospital_id INT,
	doctor_id INT,
	illness_id INT,
	service_type VARCHAR(127),
	status VARCHAR(15),
	data_status VARCHAR(15),
	FOREIGN KEY (client_plan_id) REFERENCES ClientPolicy(client_plan_id),
	FOREIGN KEY (hospital_id) REFERENCES Hospital(hospital_id),
	FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
	FOREIGN KEY (illness_id) REFERENCES Illness(illness_id)
);

CREATE TABLE IF NOT EXISTS Payment (
	payment_id INT PRIMARY KEY AUTO_INCREMENT,
	client_plan_id INT,
	policy_id INT,
	amount FLOAT,
	payment_date DATETIME,
	payment_method VARCHAR(127),
	status VARCHAR(15),
	data_status VARCHAR(15),
	FOREIGN KEY (client_plan_id) REFERENCES ClientPolicy(client_plan_id),
	FOREIGN KEY (policy_id) REFERENCES Policy(policy_id)
);

CREATE TABLE IF NOT EXISTS Claim (
	claim_id INT PRIMARY KEY AUTO_INCREMENT,
	client_plan_id INT,
	illness_id INT,
	hospital_id INT,
	doctor_id INT,
	service_date DATETIME,
	service_type VARCHAR(127),
	covered_amount FLOAT,
	status VARCHAR(15),
	data_status VARCHAR(15),
	FOREIGN KEY (client_plan_id) REFERENCES ClientPolicy(client_plan_id),
	FOREIGN KEY (illness_id) REFERENCES Illness(illness_id),
	FOREIGN KEY (hospital_id) REFERENCES Hospital(hospital_id),
	FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

CREATE TABLE IF NOT EXISTS Payout (
	payout_id INT PRIMARY KEY AUTO_INCREMENT,
	claim_id INT,
	client_plan_id INT,
	service_date DATETIME,
	service_type VARCHAR(127),
	service_amount FLOAT,
	payout_amount FLOAT,
	payout_date DATETIME,
	status VARCHAR(15),
	data_status VARCHAR(15),
	FOREIGN KEY (claim_id) REFERENCES Claim(claim_id),
	FOREIGN KEY (client_plan_id) REFERENCES ClientPolicy(client_plan_id)
);