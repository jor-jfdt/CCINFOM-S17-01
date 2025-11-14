import java.sql.*;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
public class Driver {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        AppGUI app = new AppGUI("Health Maintenance Organization");
        AppModel model = new AppModel();
		
		/* Sample SQL query pass with table test
		DefaultTableModel dtm1 = model.makeTableFromStatement("SELECT * FROM employee");
		
		JFrame jf = new JFrame("Sample table");
		JTable jt = new JTable(dtm1);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new JScrollPane(jt), BorderLayout.CENTER);
		jf.pack();
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		*/
		
		AppController controller = new AppController(app, model);
    }
}
