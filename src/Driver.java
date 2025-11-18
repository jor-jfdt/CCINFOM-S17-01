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

//        model.insertIntoTable("clients", "KURT ANJO", "AZUCENA", "LAGUERTA", LocalDate.of(2000, 12, 21), false, "M", LocalDate.of(2019, 5, 29), true, true);
//        model.insertIntoTable("clients", "MARIA", "DELA", "CRUZ", LocalDate.of(1995, 5, 15), true, "F", LocalDate.of(2020, 3, 6), true, true);
//        model.insertIntoTable("clients", "JOSE", "PROTACIO", "RIZAL", LocalDate.of(1988, 6, 19), false, "M", LocalDate.of(2020, 11, 5), true, false);
//        model.insertIntoTable("clients", "GABRIELA", "DIZON", "SILANG", LocalDate.of(2001, 3, 10), true, "F", LocalDate.of(2020, 11, 5), false, true);
//        model.insertIntoTable("clients", "ANDRES", "DE CASTRO", "BONIFACIO", LocalDate.of(1992, 11, 30), false, "M", LocalDate.of(2020, 11, 7), true, false);
//        model.insertIntoTable("clients", "MELCHORA", "AQUINO", "RAMOS", LocalDate.of(1978, 1, 6), true, "F", LocalDate.of(2020, 12, 11), true, true);
//        model.insertIntoTable("clients", "EMILIO", "GARCIA", "AGUINALDO", LocalDate.of(2003, 8, 22), false, "M", LocalDate.of(2020, 12, 19), false, false);
//        model.insertIntoTable("clients", "LEONOR", "RIVERA", "BAUTISTA", LocalDate.of(1999, 4, 12), false, "F", LocalDate.of(2021, 3, 17), true, true);
//        model.insertIntoTable("clients", "ANTONIO", "PEREZ", "LUNA", LocalDate.of(1985, 10, 29), true, "M", LocalDate.of(2021, 6, 1), true, true);
//        model.insertIntoTable("clients", "TERESA", "HERNANDEZ", "MAGBANUA", LocalDate.of(1990, 7, 4), false, "F", LocalDate.of(2021, 8, 7), true, true);
//        model.insertIntoTable("clients", "JUAN", "SANTOS", "LUNA", LocalDate.of(2000, 2, 14), true, "M", LocalDate.of(2019, 8, 29), false, true);
//		//testRecordPush(model);
		//testSQLQuery(model);
    }
	/*
	public static void testSQLQuery(AppModel model) throws SQLException {
		// Sample SQL query pass with table test
		DefaultTableModel dtm1 = model.makeTableFromStatement("SELECT * FROM client_record");
		JFrame jf = new JFrame("Sample table");
		JTable jt = new JTable(dtm1);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new JScrollPane(jt), BorderLayout.CENTER);
		jf.pack();
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		
	}
	
	public static void testRecordPush(AppModel model) throws SQLException {
		// Sample entry
		AppModel.ClientRecord.createRecord(
			"KURT ANJO",
			"LAGUERTA",
			"A",
			LocalDate.of(2005, 12, 21),
			false,
			'M',
			LocalDate.of(2025, 11, 14),
			true
		);
	}
	*/

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
}
