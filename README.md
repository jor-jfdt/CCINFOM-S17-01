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



Database Applications Project Specifications
CCINFOM – Fundamentals of Database Systems AY 2024-2025
Developing a Database System requires the group to submit a project proposal for approval by Week 3 of the trimester. Approval of the proposal will span from Week 03 to Week 04
in preparation for the official start of the project development period from Week 09-12, although student groups are highly encouraged to make early preparations before Week 09-12
to avoid piling up requirements typical during Week 10-12. This document is a proposal guideline and not the project's full specification (expectations).
Deadline of Submission: January 24, 2025, 9:00 PM
Late Submissions will result in a deduction in the project grade of 5 points per day of late submission; a fraction of a
day is considered a whole day.
Proposal Document: Created in Google Docs, Arial 10in all texts, default
margin Document Sections: Section 1.0 Group Composition
Section 2.0 Why is this Database System important to be developed
Section 3.0 Records Management
Section 4.0 Transactions
Section 5.0 Reports to be Generated
Key components of the Database System Development project
1. Design of an appropriate and correct database to meet the records management and report requirements of the project
2. Design and develop the application in Java that correctly and completely implements the project's records management and
report requirements.
Proposal Requirements
1. The proposal must have a background (one paragraph, maximum of 5 lines) that explains why this Database System is needed and
that using Excel will not be enough to help users with their data requirements.
2. There should be four (4) records that should be managed in the Database System. Each record management must be assigned to a
group member. Record management means adding a new record, updating an existing record, deleting an existing record, viewing a
record, listing records (that can be filtered using a set of fields), and viewing a record & the related other records. In the proposal, the
itemized data within each record will have to be enumerated.
Example: Product Record Management (product code, product name, description, quantity in stock, discontinued) assigned to group member 1
Customer Record Management (customer number, last name, first name, first engagement date) assigned to group member 2
Sales Representative Record Management (sales rep number, last name, first name, branch code, quota, active) assigned to group member 3
Branch Record Management (branch code, branch name, address) assigned to group member 4
For “viewing a record & the related other records,” examples could be
• Viewing of a specific product record and the list of customers who bought the product
• Viewing a customer record and the list of products they bought
• Viewing a branch record and the list of active sales
representative In the proposal, it should be indicated as:
Product Record Management (product code, product name, description, quantity in stock, discontinued) assigned to group member 1
Including viewing of a specific product record and the list of customers who bought the product
Customer Record Management (customer number, last name, first name, first engagement date) assigned to group member 2
Including viewing a customer and the list of products they bought
Branch Record Management (branch code, branch name, address) assigned to group member 4
Including viewing a branch record and the list of active sales representative
3. There should be four (4) transactions that can be performed in the Database System. Transactions differ from records management
because they combine data operations on several records.
Example: Selling of Product as a Transaction will involve the following data, operations assigned to group member 2
a. Reading the record of the customer buying to check if the customer is still allowed to buy products
b. Reading the records of products that can be sold (e.g., products that are not yet zeroed in quantity)
c. Recording the sales record with the total amount sold, the sales representative that facilitated the sales, among others
d. Recording the products sold in the sale with the quantity sold and price given
e. Updating the product record to deduct the quantity sold
In the proposal, the itemized operations within the transaction will have to be enumerated, and the group member assigned to develop the transaction.
4. Four (4) reports should be generated, combining and aggregating data from at least two records. Each report must be assigned to each
group member. Imperatively, reports always have a time dimension (e.g., Report per Month and Year, Report per Year, Report per Day)
that will be asked from the user before the report is generated. Reports are different from listings. Data for listing are operating on
records managed, and data for Reports are extracted from transactions.
Example: Sales Report (total and average sales amount) per day, for a given Year and Month, assigned to group member 3
Customer Engagement (number and total amount of sales transactions) Report per customer for a given Year and Month, assigned to group member 4
Submission of DBApplication Project
In submitting the DB Application Project, a ZIP File (filename as CCINFOM Section-GROUP NUMBER-DBAPP.zip) containing the following must be submitted in
Canvas
1. SQL Script to create the Database used in the project with sample Data (.SQL). Use the filename as – CCINFOM Section-GROUP NUMBERDBAPPSCRIPT.sql
2. Application Folder of the DB should use the project name as – CCINFOM Section-GROUP NUMBER-DBAPP
Not following any of the submission requirement rules constitute a -5 in the project grade per rule violation
