import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.io.IOException;
import java.time.LocalDate;

public class Driver {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        configureLookAndFeel();
        // Follow Model-View-Controller Design Pattern
        AppModel model = new AppModel("insurance_database", "root", "hatdogAngPassword");
		AppGUI app = new AppGUI("Health Maintenance Organization");
		AppController controller = new AppController(app, model);
        controller.connectToDatabase();
		
		//populateDatabase(model);
    }

	private static void configureLookAndFeel() {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}

			Font headerFont = new Font("Tahoma", Font.BOLD, 16);
			Font titleFont = new Font("Tahoma", Font.BOLD, 24);
			Font labelFont = new Font("Tahoma", Font.PLAIN, 13);
			Font buttonFont = new Font("Tahoma", Font.BOLD, 13);
			Font textFont = new Font("Tahoma", Font.PLAIN, 13);

			Color buttonGreen = new Color(0, 105, 55);

			UIManager.put("Button.background", Color.LIGHT_GRAY);
			UIManager.put("Button.foreground", buttonGreen);
			UIManager.put("Button.font", buttonFont);
			UIManager.put("Button[Enabled].textForeground", buttonGreen);
			UIManager.put("Button[MouseOver].textForeground", buttonGreen);
			UIManager.put("Button[Pressed].textForeground", buttonGreen);
			UIManager.put("Button[Focused].textForeground", buttonGreen);

			UIManager.put("ScrollBar.thumb", new Color(102, 187, 106));
			UIManager.put("ScrollBar.track", new Color(220, 220, 220));

			UIManager.put("Panel.background", new Color(245, 245, 245));
			UIManager.put("Panel.font", labelFont);

			UIManager.put("Label.font", labelFont);
			UIManager.put("Label.foreground", new Color(0, 105, 55));

			UIManager.put("TextField.font", textFont);
			UIManager.put("TextField.background", Color.WHITE);
			UIManager.put("TextField.foreground", new Color(0, 105, 55));

			UIManager.put("Table.font", textFont);
			UIManager.put("Table.foreground", new Color(0, 105, 55));
			UIManager.put("TableHeader.font", headerFont);
			UIManager.put("TableHeader.foreground", new Color(0, 105, 55));

			UIManager.put("TitledBorder.font", headerFont);
			UIManager.put("TitledBorder.titleColor", new Color(0, 105, 55));

			UIManager.put("nimbusBase", new Color(0, 105, 55));
			UIManager.put("nimbusBlueGrey", new Color(190, 190, 190));
			UIManager.put("control", Color.LIGHT_GRAY);

		} catch (ClassNotFoundException | InstantiationException |
				 IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.err.println("Failed to initialize Nimbus Look and Feel: " + e.getMessage());
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void populateDatabase(AppModel model) throws SQLException {
		//Client Records
		model.insertIntoTable("clients", "KURT ANJO", "AZUCENA", "LAGUERTA", LocalDate.of(2000, 12, 21), false, "M", LocalDate.of(2019, 5, 29), true, true);
        model.insertIntoTable("clients", "MARIA", "DELA", "CRUZ", LocalDate.of(1995, 5, 15), true, "F", LocalDate.of(2020, 3, 6), true, true);
        model.insertIntoTable("clients", "JOSE", "PROTACIO", "RIZAL", LocalDate.of(1988, 6, 19), false, "M", LocalDate.of(2020, 11, 5), true, false);
        model.insertIntoTable("clients", "GABRIELA", "DIZON", "SILANG", LocalDate.of(2001, 3, 10), true, "F", LocalDate.of(2020, 11, 5), false, true);
        model.insertIntoTable("clients", "ANDRES", "DE CASTRO", "BONIFACIO", LocalDate.of(1992, 11, 30), false, "M", LocalDate.of(2020, 11, 7), true, false);
        model.insertIntoTable("clients", "MELCHORA", "AQUINO", "RAMOS", LocalDate.of(1978, 1, 6), true, "F", LocalDate.of(2020, 12, 11), true, true);
        model.insertIntoTable("clients", "EMILIO", "GARCIA", "AGUINALDO", LocalDate.of(2003, 8, 22), false, "M", LocalDate.of(2020, 12, 19), false, false);
        model.insertIntoTable("clients", "LEONOR", "RIVERA", "BAUTISTA", LocalDate.of(1999, 4, 12), false, "F", LocalDate.of(2021, 3, 17), true, true);
        model.insertIntoTable("clients", "ANTONIO", "PEREZ", "LUNA", LocalDate.of(1985, 10, 29), true, "M", LocalDate.of(2021, 6, 1), true, true);
        model.insertIntoTable("clients", "TERESA", "HERNANDEZ", "MAGBANUA", LocalDate.of(1990, 7, 4), false, "F", LocalDate.of(2021, 8, 7), true, true);
        model.insertIntoTable("clients", "JUAN", "SANTOS", "LUNA", LocalDate.of(2000, 2, 14), true, "M", LocalDate.of(2019, 8, 29), false, true);

		model.insertIntoTable("illness", "Acute Upper Respiratory Infection", "J06.9", true);
		model.insertIntoTable("illness", "Essential Hypertension", "I10", true);
		model.insertIntoTable("illness", "Type 2 Diabetes Mellitus", "E11.9", true);
		model.insertIntoTable("illness", "Dengue Fever", "A90", true);
		model.insertIntoTable("illness", "Acute Gastroenteritis", "A09", true);
		model.insertIntoTable("illness", "Pneumonia, Unspecified", "J18.9", true);
		model.insertIntoTable("illness", "Bronchial Asthma", "J45.9", true);
		model.insertIntoTable("illness", "Migraine without Aura", "G43.0", true);
		model.insertIntoTable("illness", "Urinary Tract Infection", "N39.0", true);
		model.insertIntoTable("illness", "COVID-19, Virus Identified", "U07.1", true);

		// 2. POLICY TABLE (10 Entries)
		// Schema: plan_name, coverage_type, coverage_limit, premium_amount, payment_period, inclusion, data_status
		model.insertIntoTable("policy", "Basic Starter", "In-Patient", 100000.0f, 12000.0f, "Yearly", "Room and Board", true);
		model.insertIntoTable("policy", "Silver Shield", "Comprehensive", 250000.0f, 25000.0f, "Yearly", "Consultations included", true);
		model.insertIntoTable("policy", "Gold Health", "Comprehensive", 500000.0f, 45000.0f, "Yearly", "Dental + Optical", true);
		model.insertIntoTable("policy", "Platinum Plus", "All-Access", 1000000.0f, 80000.0f, "Yearly", "Executive Checkup", true);
		model.insertIntoTable("policy", "Senior Care A", "In-Patient", 150000.0f, 35000.0f, "Yearly", "Critical Care", true);
		model.insertIntoTable("policy", "Kiddie Protect", "Out-Patient", 50000.0f, 8000.0f, "Yearly", "Vaccinations", true);
		model.insertIntoTable("policy", "Corporate Standard", "Comprehensive", 200000.0f, 18000.0f, "Monthly", "Maternity", true);
		model.insertIntoTable("policy", "Corporate Executive", "All-Access", 800000.0f, 60000.0f, "Monthly", "Travel Insurance", true);
		model.insertIntoTable("policy", "Emergency Saver", "Emergency Only", 50000.0f, 5000.0f, "Yearly", "Ambulance", true);
		model.insertIntoTable("policy", "Family Bundle", "Comprehensive", 400000.0f, 70000.0f, "Yearly", "Dependents included", true);

		// 3. HOSPITAL TABLE (10 Entries)
		// Schema: hospital_name, address, city, zipcode, contact_no, email, data_status
		model.insertIntoTable("hospital", "Makati Medical Center", "2 Amorsolo Street", "Makati", 1229, "02-8888-8999", "info@makatimed.net.ph", true);
		model.insertIntoTable("hospital", "St. Luke's Medical Center", "279 E Rodriguez Sr. Ave", "Quezon City", 1112, "02-8723-0101", "info@stlukes.com.ph", true);
		model.insertIntoTable("hospital", "Philippine General Hospital", "Taft Avenue", "Manila", 1000, "02-8554-8400", "pgh.admin@up.edu.ph", true);
		model.insertIntoTable("hospital", "The Medical City", "Ortigas Avenue", "Pasig", 1605, "02-8988-1000", "mail@themedicalcity.com", true);
		model.insertIntoTable("hospital", "Asian Hospital", "2205 Civic Dr", "Muntinlupa", 1780, "02-8771-9000", "info@asianhospital.com", true);
		model.insertIntoTable("hospital", "Cardinal Santos Medical Center", "10 Wilson St", "San Juan", 1500, "02-8727-0001", "csmc@cardinalsantos.com.ph", true);
		model.insertIntoTable("hospital", "Lung Center of the Philippines", "Quezon Avenue", "Quezon City", 1100, "02-8924-6101", "lcp@doh.gov.ph", true);
		model.insertIntoTable("hospital", "Philippine Heart Center", "East Avenue", "Quezon City", 1100, "02-8925-2401", "phc@doh.gov.ph", true);
		model.insertIntoTable("hospital", "VRP Medical Center", "163 EDSA", "Mandaluyong", 1501, "02-8464-9999", "care@vrp.com.ph", true);
		model.insertIntoTable("hospital", "Manila Doctors Hospital", "667 United Nations Ave", "Manila", 1000, "02-8558-0888", "info@maniladoctors.com.ph", true);

		// 4. DOCTOR TABLE (10 Entries)
		// Schema: first_name, last_name, middle_name, doctor_type, contact_no, email, data_status
		model.insertIntoTable("doctor", "Gregory", "House", "Land", "Diagnostician", "09171001001", "house@med.com", true);
		model.insertIntoTable("doctor", "Meredith", "Grey", "Ellis", "General Surgeon", "09171001002", "grey@hospital.com", true);
		model.insertIntoTable("doctor", "Shaun", "Murphy", "Lee", "Surgeon", "09171001003", "murphy@stbonaventure.com", true);
		model.insertIntoTable("doctor", "Stephen", "Strange", "Vincent", "Neurosurgeon", "09171001004", "strange@ny.com", true);
		model.insertIntoTable("doctor", "Leonard", "McCoy", "H", "General Practitioner", "09171001005", "bones@starfleet.com", true);
		model.insertIntoTable("doctor", "Doogie", "Howser", "K", "Pediatrician", "09171001006", "doogie@la.com", true);
		model.insertIntoTable("doctor", "John", "Watson", "Hamish", "General Practitioner", "09171001007", "watson@bakerst.com", true);
		model.insertIntoTable("doctor", "Michaela", "Quinn", "E", "Family Medicine", "09171001008", "dr.mike@colorado.com", true);
		model.insertIntoTable("doctor", "Beverly", "Crusher", "C", "General Practitioner", "09171001009", "crusher@enterprise.com", true);
		model.insertIntoTable("doctor", "Drake", "Ramoray", "F", "Neurosurgeon", "09171001010", "drake@days.com", true);

		// 5. CLIENT_POLICY TABLE (10 Entries)
		// Note: Assumes Member IDs 1-10 and Plan IDs 1-10 exist.
		// Schema: client_plan_id, member_id, plan_id, preexisting_illnesses, effective_date, expiry_date, policy_status
		model.insertIntoTable("client_policy", 1, 1, "None", LocalDate.of(2023, 1, 15), LocalDate.of(2024, 1, 15), "Active");
		model.insertIntoTable("client_policy", 2, 2, "Asthma", LocalDate.of(2023, 2, 1), LocalDate.of(2024, 2, 1), "Active");
		model.insertIntoTable("client_policy", 3, 5, "None", LocalDate.of(2023, 3, 10), LocalDate.of(2024, 3, 10), "Active");
		model.insertIntoTable("client_policy", 4, 2, "Diabetes", LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), "Active");
		model.insertIntoTable("client_policy", 5, 7, "Hypertension", LocalDate.of(2022, 12, 1), LocalDate.of(2023, 12, 1), "Expired");
		model.insertIntoTable("client_policy", 6, 8, "None", LocalDate.of(2023, 6, 15), LocalDate.of(2024, 6, 15), "Active");
		model.insertIntoTable("client_policy", 7, 2, "None", LocalDate.of(2023, 7, 1), LocalDate.of(2024, 7, 1), "Active");
		model.insertIntoTable("client_policy", 8, 3, "Allergies", LocalDate.of(2023, 8, 20), LocalDate.of(2024, 8, 20), "Active");
		model.insertIntoTable("client_policy", 9, 8, "None", LocalDate.of(2023, 9, 5), LocalDate.of(2024, 9, 5), "Active");
		model.insertIntoTable("client_policy", 10, 1, "None", LocalDate.of(2023, 10, 1), LocalDate.of(2024, 10, 1), "Active");

		// 6. CLIENT_PAYMENT TABLE (10 Entries)
		// Schema: payment_id, client_plan_id, amount, payment_date, payment_method, status
		model.insertIntoTable("client_payment", 1, 12000.0f, LocalDate.of(2023, 1, 15), "Credit Card", "Complete");
		model.insertIntoTable("client_payment", 2, 25000.0f, LocalDate.of(2023, 2, 1), "Bank Transfer", "Complete");
		model.insertIntoTable("client_payment", 3, 45000.0f, LocalDate.of(2023, 3, 10), "Check", "Complete");
		model.insertIntoTable("client_payment", 4, 80000.0f, LocalDate.of(2023, 1, 1), "Credit Card", "Complete");
		model.insertIntoTable("client_payment", 1, 12000.0f, LocalDate.of(2022, 12, 1), "Cash", "Complete");
		model.insertIntoTable("client_payment", 5, 35000.0f, LocalDate.of(2023, 6, 15), "Bank Transfer", "Complete");
		model.insertIntoTable("client_payment", 6, 8000.0f, LocalDate.of(2023, 7, 1), "GCash", "Complete");
		model.insertIntoTable("client_payment", 2, 0.0f, LocalDate.of(2023, 8, 20), "N/A", "Overdue");
		model.insertIntoTable("client_payment", 3, 22500.0f, LocalDate.of(2023, 9, 5), "Check", "Partial");
		model.insertIntoTable("client_payment", 4, 80000.0f, LocalDate.of(2023, 10, 1), "Credit Card", "Complete");


		// 8. CLAIM TABLE (10 Entries)
		// Schema: claim_id, client_plan_id, illness_id, hospital_id, doctor_id, service_date, service_type, service_amount, covered_amount, claim_status
		// Schema: member_id, illness_id, hospital_id, doctor_id, service_date, service_type, service_amount, covered_amount, claim_status
		model.insertIntoTable("claim", 1, 1, 1, 1, LocalDate.of(2023, 4, 1), "Consultation", 1500.0f, 1500.0f, "Complete");
		model.insertIntoTable("claim", 2, 7, 2, 2, LocalDate.of(2023, 5, 10), "Surgery", 50000.0f, 45000.0f, "Complete");
		model.insertIntoTable("claim", 3, 4, 3, 3, LocalDate.of(2023, 6, 5), "Laboratory", 2500.0f, 2500.0f, "Complete");
		model.insertIntoTable("claim", 4, 3, 4, 4, LocalDate.of(2023, 2, 14), "Consultation", 2000.0f, 2000.0f, "Complete");
		model.insertIntoTable("claim", 5, 2, 5, 5, LocalDate.of(2023, 3, 3), "Emergency", 5000.0f, 5000.0f, "Complete");
		model.insertIntoTable("claim", 6, 6, 6, 6, LocalDate.of(2023, 7, 22), "Checkup", 1000.0f, 1000.0f, "Complete");
		model.insertIntoTable("claim", 7, 5, 7, 7, LocalDate.of(2023, 8, 30), "X-Ray", 1200.0f, 1200.0f, "Complete");
		model.insertIntoTable("claim", 8, 8, 8, 8, LocalDate.of(2023, 9, 12), "Consultation", 1500.0f, 1500.0f, "Complete");
		model.insertIntoTable("claim", 9, 9, 9, 9, LocalDate.of(2023, 10, 1), "Confinement", 25000.0f, 20000.0f, "In Progress");
		model.insertIntoTable("claim", 10, 10, 10, 10, LocalDate.of(2023, 11, 5), "Surgery", 80000.0f, 80000.0f, "In Progress");






		// 7. PAYOUT TABLE (10 Entries)
		// Schema: payout_id, claim_id, payout_date, payout_amount, payout_status
		model.insertIntoTable("payout", 1, LocalDate.of(2023, 4, 1), 1500.0f, "Complete");
		model.insertIntoTable("payout", 2, LocalDate.of(2023, 5, 10), 45000.0f, "Complete");
		model.insertIntoTable("payout", 3, LocalDate.of(2023, 6, 5), 2500.0f, "Complete");
		model.insertIntoTable("payout", 4, LocalDate.of(2023, 2, 14), 2000.0f, "Complete");
		model.insertIntoTable("payout", 5, LocalDate.of(2023, 3, 3), 5000.0f, "Complete");
		model.insertIntoTable("payout", 6, LocalDate.of(2023, 7, 22), 1000.0f, "Complete");
		model.insertIntoTable("payout", 7, LocalDate.of(2023, 8, 30), 1200.0f, "Complete");
		model.insertIntoTable("payout", 8, LocalDate.of(2023, 9, 12), 1500.0f, "Complete");
		model.insertIntoTable("payout", 9, LocalDate.of(2023, 10, 1), 20000.0f, "Pending");
		model.insertIntoTable("payout", 10, LocalDate.of(2023, 11, 5), 80000.0f, "Processing");




		// 9. LOA (Letter of Authorization) TABLE (10 Entries)
		// Schema: plan_id (Refers to ClientPolicy ID), hospital_id, doctor_id, illness_id, service_type, loa_status
		// Schema: request_id, client_plan_id, hospital_id, doctor_id, illness_id, service_type, loa_status
		// Note: plan_id here refers to the Client_Policy PK (1-10 generated above)
		model.insertIntoTable("loa", 1, 1, 1, 1, "Consultation", "Approved");
		model.insertIntoTable("loa", 2, 2, 2, 7, "Surgery", "Approved");
		model.insertIntoTable("loa", 3, 3, 3, 4, "Laboratory", "Approved");
		model.insertIntoTable("loa", 4, 4, 4, 3, "Consultation", "Approved");
		model.insertIntoTable("loa", 5, 5, 5, 2, "Emergency", "Approved");
		model.insertIntoTable("loa", 6, 6, 6, 6, "Checkup", "Approved");
		model.insertIntoTable("loa", 7, 7, 7, 5, "X-Ray", "Approved");
		model.insertIntoTable("loa", 8, 8, 8, 8, "Consultation", "Approved");
		model.insertIntoTable("loa", 9, 9, 9, 9, "Confinement", "Pending");
		model.insertIntoTable("loa", 10, 10, 10, 10, "Surgery", "Approved");
	}
}
