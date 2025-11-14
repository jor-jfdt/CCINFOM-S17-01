import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
public class AppModel {
	// MySQL port
	public static final String JDBC_MAIN_ADDRESS = "jdbc:mysql://localhost:3306/";
	public static enum AM_SMSG { AMS_MAKECONNECTION, AMS_PROCSTATEMENT, AMS_PROCSTATEMENT_EMPTY }
	public static enum AM_EMSG { AME_MAKECONNECTION, AME_PROCSTATEMENT, AME_JCONNECTOR }
	
	AppModel() throws SQLException, ClassNotFoundException {
		enterDatabase();
	}
	
	static String toSQLDate(LocalDate ld) {
		return ld.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	static class ClientRecord {
		static void createRecord(String first_name, String last_name, String middle_initial,
			LocalDate birth_date, Boolean is_employee, Character sex, LocalDate enrollment_date, Boolean is_active) throws SQLException {
			//System.out.println(AppModel.toSQLDate(birth_date));
			processNonQuery(
				"INSERT INTO client_record " + //INSERT INTO client_record
				"VALUES (DEFAULT, '"
				+ first_name.toUpperCase() + "', '"
				+ last_name.toUpperCase() + "', ' "
				+ middle_initial.toUpperCase() + "', '"
				+ AppModel.toSQLDate(birth_date) + "', '"
				+ (is_employee ? 1 : 0) + "', '"
				+ Character.toUpperCase(sex) + "', '"
				+ AppModel.toSQLDate(enrollment_date) + "', '" +
				(is_active ? 1 : 0) +"');" // VALUES (DEFAULT, '$first_name', '$last_name', '$middle_initial', '$birth_date', '$is_employee', '$sex', '$is_active');
			);
		}
		
		static void updateFirstName(String first_name, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " +
				"SET first_name = '" + first_name.toUpperCase() + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
		
		static void updateLastName(String last_name, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " +
				"SET last_name = '" + last_name.toUpperCase() + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
		
		static void updateMiddleInitial(String middle_initial, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " +
				"SET middle_initial = '" + middle_initial.toUpperCase() + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
		
		static void updateBirthDate(LocalDate birth_date, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " +
				"SET birth_date = '" + AppModel.toSQLDate(birth_date) + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
		
		static void updateIsEmployee(Boolean is_employee, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " +
				"SET is_employee = '" + is_employee + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
		
		static void updateSex(Character sex, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " + 
				"SET sex = '" + Character.toUpperCase(sex) + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
		
		static void updateEnrollmentDate(LocalDate enrollment_date, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " +
				"SET enrollment_date = '" + AppModel.toSQLDate(enrollment_date) + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
		
		static void updateIsActive(Boolean is_active, int member_id) throws SQLException {
			processNonQuery(
				"UPDATE client_record " +
				"SET is_active = '" + is_active + "' " +
				"WHERE member_id = " + member_id + ";"
			);
		}
	}
	
	// For SELECT
	ResultSet processQuery(String query) throws SQLException {
		Statement s = null;
		ResultSet r = null;
		if (null == modelConnection)
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		try {
			s = modelConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			r = s.executeQuery(query);
			open_queries.put(r, s);
			printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT);
			if (!r.next())
				printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT_EMPTY);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_PROCSTATEMENT);
		}
		return r;
	}
	
	// For CREATE, ALTER, INSERT INTO, DELETE, DROP, etc.
	static int processNonQuery(String dml) throws SQLException {
		Statement s = null;
		int r = -1;
		if (null == modelConnection)
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		try {
			s = modelConnection.createStatement();
			r = s.executeUpdate(dml);
			printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_PROCSTATEMENT);
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
		return r;
	}
	
	DefaultTableModel makeTableModel(ResultSet rs, boolean releaseOnReturn) throws SQLException {
		if (null == rs)
			throw new NullPointerException("rs is null");
		ResultSetMetaData rsmd = null;
		DefaultTableModel dtm = null;
		Integer cols;
		Object[] rowSet;
		try {
			rsmd = rs.getMetaData();
			dtm = new DefaultTableModel();
			cols = rsmd.getColumnCount();
			// Columns
			for (int i = 0; i < cols; i++)
				dtm.addColumn(rsmd.getColumnLabel(i + 1));
			// Rows
			rs.beforeFirst();
			if (rs.next()) {
				rs.beforeFirst();
				while (rs.next()) {
					rowSet = new Object[cols];
					for (int i = 0; i < cols; i++)
						rowSet[i] = rs.getObject(i + 1);
					dtm.addRow(rowSet);
					rowSet = null;
				}
			}
			if (releaseOnReturn)
				releaseResultSet(rs);
		} catch (SQLException se) {
			se.printStackTrace();
			throw new SQLException(String.format("Unable to make a table model for ResultSet@%x.", rs.hashCode()));
		} finally {
			rsmd = null;
			cols = null;
			rowSet = null;
		}
		return dtm;
	}
	
	DefaultTableModel makeTableFromStatement(String query) throws SQLException {
		ResultSet proc = null;
		Integer procHash;
		DefaultTableModel dtm = null;
		try {
			proc = processQuery(query);
			procHash = proc.hashCode();
			dtm = makeTableModel(proc, true);
			System.out.printf("[%s] INFO(table): Table generated for ResultSet@%x.\n", DATABASE_NAME, procHash);
		} catch (SQLException se) {
			se.printStackTrace();
			throw new SQLException("An error occured generating a table from query.");
		} finally {
			proc = null;
			procHash = null;
		}
		return dtm;
	}

	void releaseResultSet(ResultSet rs) throws SQLException {
		if (open_queries.get(rs) != null) {
			try {
				rs.close();
				open_queries.get(rs).close();
				System.out.printf("[%s] INFO(release): Released ResultSet@%x from memory.\n", DATABASE_NAME, rs.hashCode());
				open_queries.remove(rs);
				rs = null;
			} catch (SQLException se) {
				se.printStackTrace();
				throw new SQLException(String.format("Unable to release ResultSet@%x from memory.", rs.hashCode()));
			}
		}
	}
	
	void releaseAllResultSets() throws SQLException {
		for (Map.Entry<ResultSet, Statement> e : open_queries.entrySet()) {
			try {
				releaseResultSet(e.getKey());
			} catch (SQLException se) {
				System.out.printf("[%s] INFO(release_all): ResultSet@%x cannot be removed from memory.\n",
					DATABASE_NAME, e.getKey().hashCode());
			}
		}
	}
	
	private void enterDatabase() throws SQLException, ClassNotFoundException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			modelConnection = DriverManager.getConnection(JDBC_MAIN_ADDRESS, MYSQL_USERNAME, MYSQL_PASSWORD);
			processNonQuery("CREATE DATABASE IF NOT EXISTS `" + DATABASE_NAME + "`;");
			processNonQuery("USE `" + DATABASE_NAME + "`;");
			processNonQuery("CREATE TABLE IF NOT EXISTS `client_record` (" +
				"member_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"first_name VARCHAR(50), " +
				"last_name VARCHAR(50), " +
				"middle_initial VARCHAR(10), " +
				"birth_date DATE, " +
				"is_employee BOOLEAN, " +
				"sex VARCHAR(1), " +
				"enrollment_date DATE, " +
				"is_active BOOLEAN" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `treatment_summary` (" +
				"treatment_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"treatment_details VARCHAR(100)" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `illness` (" +
				"illness_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"illness_name VARCHAR(50), " +
				"icd10_code INT" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `illness_record` (" +
				"illness_id INT, " +
				"treatment_id INT, " +
				"PRIMARY KEY (illness_id, treatment_id), " +
				"FOREIGN KEY (illness_id) REFERENCES illness(illness_id), " +
				"FOREIGN KEY (treatment_id) REFERENCES treatment_summary(treatment_id)" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `company_policy_record` (" +
				"plan_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"plan_name VARCHAR(50), " +
				"coverage_type VARCHAR(50), " +
				"coverage_limit FLOAT, " +
				"premium_amount FLOAT, " +
				"payment_period VARCHAR(50), " +
				"inclusion VARCHAR(50)" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `hospital_record` (" +
				"hospital_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"hospital_name VARCHAR(50), " +
				"address VARCHAR(50), " +
				"city VARCHAR(50), " +
				"zipcode INT, " +
				"contact_no INT, " +
				"email VARCHAR(50)" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `doctor_record` (" +
				"doctor_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"first_name VARCHAR(50), " +
				"last_name VARCHAR(50), " +
				"middle_initial VARCHAR(10), " +
				"doctor_type VARCHAR(50), " +
				"contact_no INT, " +
				"email VARCHAR(50)" +
				");"
			);
			printSuccessLog(AM_SMSG.AMS_MAKECONNECTION);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			modelThrowError(AM_EMSG.AME_JCONNECTOR);
		}
	}
	
	private static void printSuccessLog(AM_SMSG msgtype) {
		switch (msgtype) {
			case AMS_MAKECONNECTION:
				System.out.println("[" + DATABASE_NAME + "] INFO: Connection established.");
				break;
			case AMS_PROCSTATEMENT:
				System.out.println("[" + DATABASE_NAME + "] INFO: Statement executed.");
				break;
			case AMS_PROCSTATEMENT_EMPTY:
				System.out.println("[" + DATABASE_NAME + "] INFO: Statement executed contains no rows.");
				break;
		}
	}
	
	private static void modelThrowError(AM_EMSG errtype) throws SQLException {
		switch (errtype) {
			case AME_MAKECONNECTION:
				throw new SQLException("Unable to establish connection with the database.");
			case AME_PROCSTATEMENT:
				throw new SQLException("Unable to execute statement.");
			case AME_JCONNECTOR:
				throw new SQLException("MySQL J Connector is malconfigured.");
		}
	}
	
	private static ConcurrentHashMap<ResultSet, Statement> open_queries = new ConcurrentHashMap<>();
	
	private static Connection modelConnection;
	private static final String DATABASE_NAME = "insurance_database";
	private static final String DATABASE_ADDRESS = JDBC_MAIN_ADDRESS + "/" + DATABASE_NAME;
	private static final String MYSQL_USERNAME = "root";
	private static final String MYSQL_PASSWORD = "hajtubtyacty1Bgmail.com";
}
