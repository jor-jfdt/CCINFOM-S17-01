<a id = "readme-top"> </a>

<!---Quick Access Buttons--->
[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]
[![Pull Request][pullRequest-shield]][pullRequest-url]
[![DB Project Specs][Specs-shield]][Specs-url]
<!---Shield References--->
[contributors-shield]:
https://img.shields.io/github/contributors/jor-jfdt/CCINFOM-S17-01?style=for-the-badge
[contributors-url]:
https://github.com/jor-jfdt/CCINFOM-S17-01/graphs/contributors
[issues-shield]:
https://img.shields.io/github/issues/jor-jfdt/CCINFOM-S17-01?style=for-the-badge
[issues-url]:
https://github.com/jor-jfdt/CCINFOM-S17-01/issues
[pullRequest-shield]:
https://img.shields.io/github/issues-pr/jor-jfdt/CCINFOM-S17-01?style=for-the-badge
[pullRequest-url]:
https://github.com/jor-jfdt/CCINFOM-S17-01/pulls
[Specs-shield]:
https://img.shields.io/badge/DB-Specs-brightgreen?style=for-the-badge&color=brightgreen
[Specs-url]:
https://drive.google.com/file/d/1vTEbrqZcX2hyIaGHVvhlYiyKkR-RU3Oo/view?usp=sharing

# CCINFOM-S17-01
Data Base Application Project

Requirements for the Database Application Project
Software Applications and Tools Portfolio
The courses will be using several software applications and tools. You are expected to be responsible for installing these applications and tools in your own personal workstations and scout for online resources to install them properly. Most of these applications and tools only run in Windows; MAC Silicon users are expected to scout for necessary applications to execute these Windows applications and tools.
Please read the licensing agreements and limitations of using these applications and tools.

Microsoft OpenJDK 21.0.4Links to an external site.
MYSQL Server Community Edition 8.037Links to an external site.
MYSQL Workbench 8.038Links to an external site.
MYSQL J Connector (for connecting the Java Application to MYSQL) Links to an external site.
Eclipse Installer 2024‑06 RLinks to an external site.
You have to already install the required software by Week 2.

Database Applications Project
The project must be stored in a GitHub Repository named after your group code. Each group member must have an account in GitHub.Links to an external site. Have your group leader create the project and make all the members a collaborator. During the demo and defense, you will pull the latest project files from the GitHub Repository. Any updates in the repository after submission will make your project considered late or not submitted at all.

# HMO Monitoring Application

An application to track and encode entries of records related to a HMO Organization. The records include the following:
  - Client Record
    [member_ID, first_name, last_name, middle_initial, birthdate, is_employee, sex, enrollment_date, is_active]
    
  - Illness Record
    [illness_ID, illness_name, treatment_summary, icd10_code]

  - Hospital Record
    [hospital_ID, hospital_name, address, city, zipcode, contact_no, email]

  - Medical Doctor Record
    
    [doctor_ID, first_name, last_name, middle_initial, doctor_type, contact_no, email]
    
  - Company Policy Record
    [plan_ID, plan_name, coverage_type, coverage_limit, premium_amount, payment_period, inclusion]
    
  - Client Policy Record
    [policy_ID, member_ID, plan_ID company_ID, preexisting_illnesses, effective_date, expiry_date, status]

  - Payment of Premium Tracker Record
    [payment_ID, member_IDplan_ID, amount, payment_date, payment_method, status]
    
  - Claims Tracker Record
    [claim_ID, member_ID. illness_ID, hospital_ID, doctor_ID, service_dateservice_type, covered_amount, status]
    
  - Payout of Claims Tracker Record
    [payout_ID, hospital_ID, doctor_ID, service_date, service_type, service_amount, payout_amount, payout_date, status]
    
  - LOA Request Record
    [claim_IDmember_ID, illness_ID. hospital_ID, doctor_ID. service_type, status]

This application include the following transactions:
  - Buying of Company Policy Plan
  - Client Payment of Premiums
  - Consultation with Doctor
  - Hospitalization
  - Payout to Hospital
  - Payout to Doctor
