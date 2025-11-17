import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
		/*
		model.insertIntoTable("Client", "KURT ANJO", "AZUCENA", "LAGUERTA", LocalDate.of(2000, 12, 21), false, "M", true, "EXAMPLE");
		model.insertIntoTable("client", "MARIA", "DELA", "CRUZ", LocalDate.of(1995, 5, 15), true, "F", true, "EXAMPLE");
		model.insertIntoTable("client", "JOSE", "PROTACIO", "RIZAL", LocalDate.of(1988, 6, 19), false, "M", true, "EXAMPLE");
		model.insertIntoTable("client", "GABRIELA", "DIZON", "SILANG", LocalDate.of(2001, 3, 10), true, "F", false, "EXAMPLE");
		model.insertIntoTable("client", "ANDRES", "DE CASTRO", "BONIFACIO", LocalDate.of(1992, 11, 30), false, "M", true, "EXAMPLE");
		model.insertIntoTable("client", "MELCHORA", "AQUINO", "RAMOS", LocalDate.of(1978, 1, 6), true, "F", true, "EXAMPLE");
		model.insertIntoTable("client", "EMILIO", "GARCIA", "AGUINALDO", LocalDate.of(2003, 8, 22), false, "M", false, "EXAMPLE");
		model.insertIntoTable("client", "LEONOR", "RIVERA", "BAUTISTA", LocalDate.of(1999, 4, 12), false, "F", true, "EXAMPLE");
		model.insertIntoTable("client", "ANTONIO", "PEREZ", "LUNA", LocalDate.of(1985, 10, 29), true, "M", true, "EXAMPLE");
		model.insertIntoTable("client", "TERESA", "HERNANDEZ", "MAGBANUA", LocalDate.of(1990, 7, 4), false, "F", true, "EXAMPLE");
		model.insertIntoTable("client", "JUAN", "SANTOS", "LUNA", LocalDate.of(2000, 2, 14), true, "M", false, "EXAMPLE");
		*/
		//model.updateColumnValueOfId("client", "birth_date", 1, LocalDate.of(2005, 1, 1));
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
		DefaultTableModel dtm = model.makeTableModel(model.getTableEntries("client", "*"));
		TableRowSorter<DefaultTableModel> trs = model.filterOnTableRowSorter(dtm, "@!6,7,8 f");
		model.readSQLFile("generate_reports.sql");
		model.closeDatabase();
		JFrame jf = new JFrame("Sample table");
		JTable jt = new JTable(dtm);
		jt.setRowSorter(trs);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().add(new JScrollPane(jt), BorderLayout.CENTER);
		jf.pack();
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}
}