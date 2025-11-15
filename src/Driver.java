import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        configureLookAndFeel();
		AppGUI app = new AppGUI("Health Maintenance Organization");
        AppModel model = new AppModel("insurance_database", "root", "hajtubtyacty1Bgmail.com");
		AppController controller = new AppController(app, model);
		//testRecordPush(model);
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
