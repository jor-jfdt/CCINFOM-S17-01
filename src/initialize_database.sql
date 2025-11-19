CREATE DATABASE IF NOT EXISTS insurance_database;
USE insurance_database;

/*
	static final int LONG_STRING_LENGTH = 254; // VARCHAR(254)
	static final int SHORT_STRING_LENGTH = 127; // VARCHAR(127)
	static final int SHORTER_STRING_LENGTH = 15; // VARCHAR(15)
	static final int CHAR_LENGTH = 1; // VARCHAR(1)
*/

CREATE TABLE IF NOT EXISTS clients (
	member_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(127) NOT NULL,
    last_name VARCHAR(127) NOT NULL,
    middle_name VARCHAR(127),
    birth_date DATE NOT NULL,
    is_employee BOOLEAN NOT NULL,
    sex CHAR(1) NOT NULL,
    enrollment_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL,
	data_status BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS illness (
	illness_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    illness_name VARCHAR(127) NOT NULL,
    icd10_code VARCHAR(7) UNIQUE NOT NULL,
	data_status BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS policy (
	plan_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    plan_name VARCHAR(127) NOT NULL,
    coverage_type VARCHAR(127) NOT NULL,
	coverage_limit FLOAT NOT NULL,
    premium_amount FLOAT NOT NULL,
    payment_period VARCHAR(127) NOT NULL, #Monthly, Weekly, Yearly, etc.check
	inclusion VARCHAR(127) NOT NULL,
	data_status BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS hospital (
	hospital_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    hospital_name VARCHAR(127),
    address VARCHAR(127),
    city VARCHAR(127),
    zipcode INT,
    contact_no VARCHAR(15),
    email VARCHAR(254),
	data_status BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS doctor (
	doctor_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(127) NOT NULL,
    last_name VARCHAR(127) NOT NULL,
    middle_name VARCHAR(127),
    doctor_type VARCHAR(127) NOT NULL,
    contact_no VARCHAR(15),
    email VARCHAR(254),
	data_status BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS client_policy (
	client_plan_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    member_id INT NOT NULL,
    plan_id INT NOT NULL,
    preexisting_illnesses VARCHAR(127) NOT NULL,
    effective_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    policy_status VARCHAR(15) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES clients(member_id),
    FOREIGN KEY (plan_id) REFERENCES policy(plan_id)
);

CREATE TABLE IF NOT EXISTS client_payment (
	payment_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    client_plan_id INT,
    amount FLOAT,
    payment_date DATE,
    payment_method VARCHAR(127),
    premium_payment_status VARCHAR(127),
    FOREIGN KEY (client_plan_id) REFERENCES client_policy(policy_id)
);

CREATE TABLE IF NOT EXISTS payout (
	payout_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	claim_id INT NOT NULL,
    payout_date DATE,
    payout_amount FLOAT NOT NULL,
    payout_status VARCHAR(127) NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES hospital(claim_id),
);

CREATE TABLE IF NOT EXISTS claim (
	claim_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	client_plan_id INT NOT NULL,
    illness_id INT NOT NULL,
    hospital_id INT NOT NULL,
    doctor_id INT NOT NULL,
    service_date DATE NOT NULL,
    service_type VARCHAR(127) NOT NULL,
    service_amount FLOAT NOT NULL,
    covered_amount FLOAT NOT NULL,
    claim_status VARCHAR(15),
    FOREIGN KEY (client_plan_id) REFERENCES client_policy(client_plan_id),
	FOREIGN KEY (illness_id) REFERENCES illness(illness_id),
    FOREIGN KEY (hospital_id) REFERENCES hospital(hospital_id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id)
);

CREATE TABLE IF NOT EXISTS loa (
	request_id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	client_plan_id INT NOT NULL,
	hospital_id INT NOT NULL,
	doctor_id INT NOT NULL,
	illness_id INT NOT NULL,
	service_type VARCHAR(127) NOT NULL,
	loa_status VARCHAR(15) NOT NULL,
	FOREIGN KEY (client_plan_id) REFERENCES client_policy(policy_id),
	FOREIGN KEY (hospital_id) REFERENCES hospital(hospital_id),
	FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),
	FOREIGN KEY (illness_id) REFERENCES illness(illness_id)
);