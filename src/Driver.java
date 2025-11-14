import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
public class Driver {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("Button.background", Color.LIGHT_GRAY);
            UIManager.put("Button.foreground", Color.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
		AppGUI app = new AppGUI("Health Maintenance Organization");
        AppModel model = new AppModel();
		AppController controller = new AppController(app, model);
		//testRecordPush(model);
		//testSQLQuery(model);
    }
	
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
}
