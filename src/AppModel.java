import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import javax.swing.table.DefaultTableModel;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
public class AppModel {
	// MySQL port
	static final String JDBC_MAIN_ADDRESS = "jdbc:mysql://localhost:3306/";
	static enum AM_SMSG { AMS_MAKECONNECTION, AMS_PROCSTATEMENT, AMS_PROCSTATEMENT_EMPTY }
	static enum AM_EMSG { AME_MAKECONNECTION, AME_PROCSTATEMENT, AME_JCONNECTOR }
	
	AppModel() throws SQLException, ClassNotFoundException, IOException {
		enterDatabase();
	}
	
	static class SQLUtils {
		static String makeSQLTemplateUpdateSetWhere(String table_name, String set_name, String where_name) {
			if (null == table_name || null == set_name || null == where_name)
				throw new IllegalArgumentException("makeSQLTemplateUpdateSetWhere: please check your parameters");
			return "UPDATE " + table_name + " SET " + set_name + " = ? WHERE " + where_name + " = ?;";
		}
		static String makeSQLTemplateInsertIntoValues(String table_name, String... column_names) {
			if (null == table_name || null == column_names)
				throw new IllegalArgumentException("makeSQLTemplateInsertIntoValues: please check your parameters");
			String column_body = "(" + String.join(",", column_names) + ")";
			String value_body = "(" + Stream.generate(() -> "?").limit(column_names.length).collect(Collectors.joining(",")) + ")";
			return "INSERT INTO " + table_name + " " + column_body + " VALUES " + value_body + ";";
		}
		static String toSQLDate(LocalDate ld) {
			return ld.format(DateTimeFormatter.ofPattern(DATE_FORMATTING));
		}
		static LocalDate stringToDate(String str) {
			return str != null ? LocalDate.parse(str, DateTimeFormatter.ofPattern(DATE_FORMATTING)) : null;
		}
		static boolean stringFitsLong(String str) {
			return str != null && str.length() > 0 && str.length() <= LONG_STRING_LENGTH && str.matches(REGEX_LATIN1);
		}
		static boolean stringFitsShort(String str) {
			return str != null && str.length() > 0 && str.length() <= SHORT_STRING_LENGTH && str.matches(REGEX_LATIN1);
		}
		static boolean stringFitsShorter(String str) {
			return str != null && str.length() > 0 && str.length() <= SHORTER_STRING_LENGTH && str.matches(REGEX_LATIN1);
		}
		static boolean emailIsValid(String str) {
			return str != null && stringFitsLong(str) && str.matches(REGEX_EMAIL);
		}
		static boolean dateIsValid(String str) {
			try {
				return str != null && LocalDate.parse(str, DateTimeFormatter.ofPattern(DATE_FORMATTING)) != null;
			} catch (DateTimeParseException dtpe) {
				return false;
			}
		}
		static boolean genderIsValid(char c) {
			return c == 'M' || c == 'F';
		}
	}
	
	static Object[] readSQLFile(String sql_path) throws SQLException, IOException, InvalidPathException {
		Path fp = null;
		String content = null;
		String[] statementSet;
		Object[] result;
		int i;
		try {
			fp = Paths.get(sql_path);
		} catch (InvalidPathException ipe) {
			ipe.printStackTrace();
			return null;
		}
		try {
			content = Files.readString(fp);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
		statementSet = content
			.replaceAll("(--.*)|((?s)\\/\\*.*?\\*\\/)", "") // Tanggalin lahat ng comments
			.replaceAll("\\s+", " ") // Paltan lahat ng extra whitespaces ng isang space lang
			.trim() // Trim trailing whitespaces sa unahan saka hulihan
			.split("(?<=;)"); // Split statements before each ; (so kasama ung ;)
		result = new Object[statementSet.length];
		i = 0;
		if (statementSet.length > 0) {
			for (String s : statementSet) {
				System.out.println("[read] " + s);
				if (!s.isEmpty())
					if (s.toLowerCase().trim().startsWith("SELECT"))
						result[i++] = processQuery(s);
					else
						result[i++] = processNonQuery(s);
			}
		}
		return result;
	}
	
	static ResultSet processQuery(String queryTemplate, Object... params) throws SQLException {
		PreparedStatement ps = null;
		ResultSet r = null;
		int i;
		if (null == modelConnection)
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		try {
			ps = modelConnection.prepareStatement(queryTemplate,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			i = 1;
			if (params != null)
				for (Object o : params)
					ps.setObject(i++, o);
			r = ps.executeQuery();
			open_queries.put(r, ps);
			printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT);
			if (!r.next())
				printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT_EMPTY);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_PROCSTATEMENT);
		}
		return r;
	}
	
	static int processNonQuery(String dmlTemplate, Object... params) throws SQLException {
		PreparedStatement ps = null;
		int i, r = 0;
		if (null == modelConnection)
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		try {
			ps = modelConnection.prepareStatement(dmlTemplate);
			i = 1;
			if (params != null)
				for (Object o : params)
					ps.setObject(i++, o);
			r = ps.executeUpdate();
			printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_PROCSTATEMENT);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
		return r;
	}
	
	static DefaultTableModel makeTableModel(ResultSet rs, boolean releaseOnReturn) throws SQLException {
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
	
	static DefaultTableModel makeTableFromStatement(String query) throws SQLException {
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

	static void releaseResultSet(ResultSet rs) throws SQLException {
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
	
	static void releaseAllResultSets() throws SQLException {
		for (Map.Entry<ResultSet, Statement> e : open_queries.entrySet()) {
			try {
				releaseResultSet(e.getKey());
			} catch (SQLException se) {
				System.out.printf("[%s] INFO(release_all): ResultSet@%x cannot be removed from memory.\n",
					DATABASE_NAME, e.getKey().hashCode());
			}
		}
	}
	
	private static void enterDatabase() throws SQLException, IOException, ClassNotFoundException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			modelConnection = DriverManager.getConnection(JDBC_MAIN_ADDRESS, MYSQL_USERNAME, MYSQL_PASSWORD);
			/*
			processNonQuery("CREATE DATABASE IF NOT EXISTS `" + DATABASE_NAME + "`;");
			processNonQuery("USE `" + DATABASE_NAME + "`;");
			processNonQuery("CREATE TABLE IF NOT EXISTS `client_record` (" +
				"member_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"first_name VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"last_name VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"middle_initial VARCHAR(" + SHORTER_STRING_LENGTH + "), " +
				"birth_date DATE, " +
				"is_employee BOOLEAN, " +
				"sex VARCHAR(" + CHAR_LENGTH + "), " +
				"enrollment_date DATE, " +
				"is_active BOOLEAN" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `treatment_summary` (" +
				"treatment_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"treatment_details VARCHAR(" + LONG_STRING_LENGTH + ")" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `illness` (" +
				"illness_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"illness_name VARCHAR(" + SHORT_STRING_LENGTH + "), " +
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
				"plan_name VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"coverage_type VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"coverage_limit FLOAT, " +
				"premium_amount FLOAT, " +
				"payment_period VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"inclusion VARCHAR(" + SHORT_STRING_LENGTH + ")" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `hospital_record` (" +
				"hospital_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"hospital_name VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"address VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"city VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"zipcode INT, " +
				"contact_no INT, " +
				"email VARCHAR(" + LONG_STRING_LENGTH + ")" +
				");"
			);
			processNonQuery("CREATE TABLE IF NOT EXISTS `doctor_record` (" +
				"doctor_id INT PRIMARY KEY AUTO_INCREMENT, " +
				"first_name VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"last_name VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"middle_initial VARCHAR(" + SHORTER_STRING_LENGTH + "), " +
				"doctor_type VARCHAR(" + SHORT_STRING_LENGTH + "), " +
				"contact_no INT, " +
				"email VARCHAR(" + LONG_STRING_LENGTH + ")" +
				");"
			);
			*/
			readSQLFile("example_query.sql");
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
	
	static final int LONG_STRING_LENGTH = 254;
	static final int SHORT_STRING_LENGTH = 127;
	static final int SHORTER_STRING_LENGTH = 15;
	static final int CHAR_LENGTH = 1;
	static final String DATE_FORMATTING = "yyyy-MM-dd";
	static final String REGEX_LATIN1 = "\\A[\\u0000-\\u00FF]*\\z";
	static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
