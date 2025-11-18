import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
public class _Driver {
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		AppModel model = new AppModel("insurance_database", "root", "hatdogAngPassword");
		// Sample SQL query pass with table test
		model.enterDatabase();
		/*
		model.processNonQuery(AppModel.SQLUtils.makeSQLTemplateInsertIntoValues(
			"Client", "first_name", "middle_name", "last_name", "birth_date",
			"is_employee", "sex", "is_active", "data_status"
		), "KURT ANJO", "AZUCENA", "LAGUERTA", LocalDate.of(2005, 12, 21), false, "M", true, "EXAMPLE");
		*/
		/*
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
		DefaultTableModel dtm = model.makeTableModel(model.getTableEntriesInverted("clients", "last_name", "middle_name"));
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