CREATE DATABASE IF NOT EXISTS insurance_database;
USE insurance_database;
/*
 abcdef
*/
CREATE TABLE IF NOT EXISTS client_record (
	member_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    middle_initial VARCHAR(10), -- abcdef
    birth_date DATE,
    is_employee BOOLEAN,
	/*
	 abcdef
	*/
    sex CHAR(1),
    enrollment_date DATE,
    is_active BOOLEAN
);

CREATE TABLE IF NOT EXISTS treatment_summary (
	treatment_id INT PRIMARY KEY AUTO_INCREMENT,
    treatment_details VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS illness (
	illness_id INT PRIMARY KEY AUTO_INCREMENT,
    illness_name VARCHAR(50),
    icd10_code INT
);

CREATE TABLE IF NOT EXISTS illness_record (
	illness_id INT,
    treatment_id INT,
    PRIMARY KEY(illness_id, treatment_id),
    FOREIGN KEY (illness_id) REFERENCES illness(illness_id),
    FOREIGN KEY (treatment_id) REFERENCES treatment_summary(treatment_id)
);

CREATE TABLE IF NOT EXISTS company_policy_record (
	plan_id INT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(50),
    coverage_type VARCHAR(50),
	coverage_limit FLOAT,
    premium_amount FLOAT,
    payment_period VARCHAR(50),
	inclusion VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS hospital_record (
	hospital_id INT PRIMARY KEY AUTO_INCREMENT,
    hospital_name VARCHAR(50),
    address VARCHAR(100),
    city VARCHAR(50),
    zipcode INT,
    contact_no INT,
    email VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS doctor_record (
	doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    middle_initial VARCHAR(10),
    doctor_type VARCHAR(50),
    contact_no INT,
    email VARCHAR(50)
);