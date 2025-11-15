import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
public class Driver {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        configureLookAndFeel();
		AppGUI app = new AppGUI("Health Maintenance Organization");
        AppModel model = new AppModel();
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

			// Customize Nimbus colors
			UIManager.put("Button.background", new Color(200, 200, 255));
			UIManager.put("Button.foreground", Color.DARK_GRAY);
			UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));

			UIManager.put("ScrollBar.thumb", new Color(100, 150, 200));
			UIManager.put("ScrollBar.track", new Color(220, 220, 220));

			UIManager.put("Panel.background", new Color(240, 240, 240));

			UIManager.put("Label.font", new Font("Verdana", Font.PLAIN, 12));
			UIManager.put("TextField.font", new Font("Verdana", Font.PLAIN, 12));
			UIManager.put("Table.font", new Font("Verdana", Font.PLAIN, 12));

			UIManager.put("nimbusBase", new Color(80, 120, 180));
			UIManager.put("nimbusBlueGrey", new Color(190, 190, 190));
			UIManager.put("control", new Color(230, 230, 230));

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
