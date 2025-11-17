CREATE DATABASE IF NOT EXISTS insurance_database;
USE insurance_database;

/*
	static final int LONG_STRING_LENGTH = 254; // VARCHAR(254)
	static final int SHORT_STRING_LENGTH = 127; // VARCHAR(127)
	static final int SHORTER_STRING_LENGTH = 15; // VARCHAR(15)
	static final int CHAR_LENGTH = 1; // VARCHAR(1)
*/

CREATE TABLE IF NOT EXISTS Client (
	member_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	first_name VARCHAR(127) NOT NULL,
	middle_name VARCHAR(127),
	last_name VARCHAR(127) NOT NULL,
	birth_date DATE NOT NULL,
	is_employee BOOLEAN NOT NULL,
	sex VARCHAR(1) NOT NULL,
	is_active BOOLEAN NOT NULL,
	data_status VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS Doctor (
	doctor_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	first_name VARCHAR(127) NOT NULL,
	middle_name VARCHAR(127),
	last_name VARCHAR(127) NOT NULL,
	doctor_type VARCHAR(127) NOT NULL,
	contact_no INT NOT NULL,
	email VARCHAR(254) NOT NULL,
	data_status VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS Hospital (
	hospital_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	hospital_name VARCHAR(127) NOT NULL,
	address VARCHAR(254) NOT NULL,
	city VARCHAR(127) NOT NULL,
	zip_code INT NOT NULL,
	contact_no INT NOT NULL,
	email VARCHAR(254) NOT NULL,
	data_status VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS Policy (
	policy_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	policy_name VARCHAR(127) NOT NULL,
	coverage_type VARCHAR(127) NOT NULL,
	coverage_limit FLOAT NOT NULL,
	payment_period VARCHAR(127) NOT NULL,
	inclusion VARCHAR(127) NOT NULL,
	data_status VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS Illness (
	illness_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	illness_name VARCHAR(127) NOT NULL,
	icd10_code VARCHAR(7) NOT NULL,
	data_status VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS ClientPolicy (
	client_plan_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	policy_id INT NOT NULL,
	member_id INT NOT NULL,
	preexisting_illness VARCHAR(254) NOT NULL,
	effective_date DATE NOT NULL,
	expiry_date DATE NOT NULL,
	status VARCHAR(15) NOT NULL,
	data_status VARCHAR(15) NOT NULL,
	FOREIGN KEY (policy_id) REFERENCES Policy(policy_id),
	FOREIGN KEY (member_id) REFERENCES Client(member_id)
);

CREATE TABLE IF NOT EXISTS LOA (
	request_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	client_plan_id INT NOT NULL,
	hospital_id INT NOT NULL,
	doctor_id INT NOT NULL,
	illness_id INT NOT NULL,
	service_type VARCHAR(127) NOT NULL,
	status VARCHAR(15) NOT NULL,
	data_status VARCHAR(15) NOT NULL,
	FOREIGN KEY (client_plan_id) REFERENCES ClientPolicy(client_plan_id),
	FOREIGN KEY (hospital_id) REFERENCES Hospital(hospital_id),
	FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
	FOREIGN KEY (illness_id) REFERENCES Illness(illness_id)
);

CREATE TABLE IF NOT EXISTS Claim (
	claim_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	client_plan_id INT NOT NULL,
	illness_id INT NOT NULL,
	hospital_id INT NOT NULL,
	doctor_id INT NOT NULL,
	service_date DATETIME NOT NULL,
	service_type VARCHAR(127) NOT NULL,
	covered_amount FLOAT NOT NULL,
	status VARCHAR(15) NOT NULL,
	data_status VARCHAR(15) NOT NULL,
	FOREIGN KEY (client_plan_id) REFERENCES ClientPolicy(client_plan_id),
	FOREIGN KEY (illness_id) REFERENCES Illness(illness_id),
	FOREIGN KEY (hospital_id) REFERENCES Hospital(hospital_id),
	FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

CREATE TABLE IF NOT EXISTS Payment (
	payment_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	claim_id INT NOT NULL,
	amount FLOAT NOT NULL,
	payment_date DATETIME NOT NULL,
	payment_method VARCHAR(127) NOT NULL,
	status VARCHAR(15) NOT NULL,
	data_status VARCHAR(15) NOT NULL,
	FOREIGN KEY (claim_id) REFERENCES Claim(claim_id)
);

CREATE TABLE IF NOT EXISTS Payout (
	payout_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	claim_id INT NOT NULL,
	service_date DATETIME NOT NULL,
	service_type VARCHAR(127) NOT NULL,
	service_amount FLOAT NOT NULL,
	payout_amount FLOAT NOT NULL,
	payout_date DATETIME NOT NULL,
	status VARCHAR(15) NOT NULL,
	data_status VARCHAR(15) NOT NULL,
	FOREIGN KEY (claim_id) REFERENCES Claim(claim_id)
);
