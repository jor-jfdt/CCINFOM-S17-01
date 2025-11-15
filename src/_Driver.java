import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
public class _Driver {
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		AppModel model = new AppModel("insurance_database", "root", "hajtubtyacty1Bgmail.com");
		// Sample SQL query pass with table test
		model.enterDatabase();
		/*
		model.processNonQuery(AppModel.SQLUtils.makeSQLTemplateInsertIntoValues(
			"Client", "first_name", "middle_name", "last_name", "birth_date",
			"is_employee", "sex", "is_active", "data_status"
		), "KURT ANJO", "AZUCENA", "LAGUERTA", LocalDate.of(2005, 12, 21), false, "M", true, "EXAMPLE");
		*/
		//model.insertIntoTable("Client", "KURT ANJO", "AZUCENA", "LAGUERTA", LocalDate.of(2005, 12, 21), false, "M", true, "EXAMPLE");
		model.updateColumnValueOfId("client", "birth_date", 1, LocalDate.of(2005, 1, 1));
		/*DefaultTableModel dtm1 = model.makeTableModel(model.processQuery(
			AppModel.SQLUtils.makeSQLTemplateSelectFrom("Client", "member_id", "first_name", "middle_name",
			"last_name", "birth_date", "is_employee", "sex", "is_active", "data_status")
		));*/
		/*
		DefaultTableModel dtm = model.makeTableModel(
			model.getTableEntries("client", "member_id", "first_name", "middle_name",
			"last_name", "birth_date", "is_employee", "sex", "is_active", "data_status")
		);
		*/
		DefaultTableModel dtm = model.makeTableModel(
			model.getTableEntries("client", "*")
		);
		model.closeDatabase();
		JFrame jf = new JFrame("Sample table");
		JTable jt = new JTable(dtm);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new JScrollPane(jt), BorderLayout.CENTER);
		jf.pack();
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}
}