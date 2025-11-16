import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
	static enum AM_EMSG { AME_MAKECONNECTION, AME_CONNECTION_CLOSED, AME_PROCSTATEMENT, AME_JCONNECTOR }
	
	AppModel(String database_name, String username, String password) throws SQLException, ClassNotFoundException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException cnfe) {
			throw new ClassNotFoundException("MySQL J Connector is malconfigured.", cnfe);
		}
		DATABASE_NAME = database_name;
		MYSQL_USERNAME = username;
		MYSQL_PASSWORD = password;
	}
	
	static class SQLUtils {
		static String makeSQLTemplateUpdateSetWhere(String table_name, String set_name, String where_name) {
			if (null == table_name || null == set_name || null == where_name || !assessAllIdentifiers(table_name, set_name, where_name))
				throw new IllegalArgumentException("makeSQLTemplateUpdateSetWhere: please check your parameters");
			return "UPDATE " + table_name + " SET " + set_name + " = ? WHERE " + where_name + " = ?;";
		}
		static String makeSQLTemplateInsertIntoValues(String table_name, String... column_names) {
			String[] identifiers = Arrays.copyOf(column_names, column_names.length + 1);
			identifiers[identifiers.length - 1] = table_name;
			if (null == table_name || null == column_names || !assessAllIdentifiers(identifiers))
				throw new IllegalArgumentException("makeSQLTemplateInsertIntoValues: please check your parameters");
			String column_body = "(" + String.join(",", column_names) + ")";
			String value_body = "(" + Stream.generate(() -> "?").limit(column_names.length).collect(Collectors.joining(",")) + ")";
			return "INSERT INTO " + table_name + " " + column_body + " VALUES " + value_body + ";";
		}
		static String makeSQLTemplateSelectFrom(String table_name, String... ordered_column_names) {
			String[] identifiers = Arrays.copyOf(ordered_column_names, ordered_column_names.length + 1);
			identifiers[identifiers.length - 1] = table_name;
			if (null == table_name || null == ordered_column_names || !assessAllIdentifiers(identifiers))
				throw new IllegalArgumentException("makeSQLTemplateSelectFrom: please check your parameters");
			return "SELECT " + String.join(",", ordered_column_names) + " FROM " + table_name + ";";
		}
		static String makeSQLTemplateDeleteFromWhere(String table_name, String where_key_name) {
			if (null == table_name || null == where_key_name || !assessAllIdentifiers(table_name, where_key_name))
				throw new IllegalArgumentException("makeSQLTemplateDeleteFromOneWhere: please check your parameters");
			return "DELETE FROM " + table_name + " WHERE " + where_key_name + " = ?";
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
		private static boolean assessIdentifier(String identifier) {
			return identifier != null && identifier.trim().length() > 0 &&
				identifier.matches(REGEX_SQL_IDENTIFIER);
		}
		private static boolean assessAllIdentifiers(String... identifierSet) {
			boolean assessment = true;
			for (int i = 0; assessment && i < identifierSet.length; i++)
				assessment = assessIdentifier(identifierSet[i]);
			return assessment;
		}
		static final int LONG_STRING_LENGTH = 254;
		static final int SHORT_STRING_LENGTH = 127;
		static final int SHORTER_STRING_LENGTH = 15;
		static final int CHAR_LENGTH = 1;
		static final String DATE_FORMATTING = "yyyy-MM-dd";
		static final String REGEX_LATIN1 = "\\A[\\u0000-\\u00FF]*\\z";
		static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		private static final String REGEX_SQL_IDENTIFIER = "\\*|^[a-zA-Z_]\\w*$";
		private static final String REGEX_SQL_NON_NESTED_FUNCTION = "^[A-Za-z_]*\\(\\s*([\\w.]+(\\s*,\\s*[\\w.]+)*)?\\s*\\)$";
	}
	
	Object[] readSQLFile(String sql_path) throws SQLException, IOException, InvalidPathException {
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
				//System.out.println("[read] " + s);
				if (!s.isEmpty())
					if (s.toLowerCase().trim().startsWith("SELECT"))
						result[i++] = processQuery(s);
					else
						result[i++] = processNonQuery(s);
			}
		}
		return result;
	}
	
	List<Map<String, Object>> processQuery(String queryTemplate, Object... params) throws SQLException {
		ResultSetMetaData rsmd = null;
		Map<String, Object> t = null;
		List<Map<String, Object>> out = new ArrayList<>();
		int cols, i;
		if (null == modelConnection || modelConnection.isClosed()) {
			throw new SQLException("The connection is null or closed. Please connect with enterDatabase().");
		} else {
			try (PreparedStatement ps = modelConnection.prepareStatement(queryTemplate,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				i = 1;
				if (params != null)
					for (Object o : params)
						ps.setObject(i++, o);
				try (ResultSet rs = ps.executeQuery()) {
					rsmd = rs.getMetaData();
					cols = rsmd.getColumnCount();
					// Column reference for future JTables
					// This must be a LinkedHashMap so that the column order is not lost
					t = new LinkedHashMap<>();
					for (i = 1; i <= cols; i++)
						t.put(rsmd.getColumnLabel(i), rsmd.getColumnTypeName(i));
					out.add(t);
					rs.beforeFirst();
					if (rs.next()) {
						rs.beforeFirst();
						while (rs.next()) {
							t = new HashMap<>();
							for (i = 1; i <= cols; i++)
								t.put(rsmd.getColumnLabel(i), rs.getObject(i));
							out.add(t);
						}
					}
				}
				printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT, queryTemplate);
				if (out.isEmpty())
					printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT_EMPTY);
			} catch (SQLException se) {
				throw new SQLException("Failed to execute query template; refer to stacktrace", se);
			}
		}
		return out;
	}
	
	int processNonQuery(String dmlTemplate, Object... params) throws SQLException {
		int i, r = -1;
		if (null == modelConnection || modelConnection.isClosed()) {
			throw new SQLException("The connection is null or closed. Please connect with enterDatabase().");
		} else {
			try (PreparedStatement ps = modelConnection.prepareStatement(dmlTemplate)) {
				i = 1;
				if (params != null)
					for (Object o : params)
						ps.setObject(i++, o);
				r = ps.executeUpdate();
				printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT, dmlTemplate);
			} catch (SQLException se) {
				throw new SQLException("Failed to execute DML template; refer to stacktrace", se);
			}
		}
		return r;
	}
	
	DefaultTableModel makeTableModel(List<Map<String, Object>> maprep) {
		DefaultTableModel dtm = new DefaultTableModel();
		Map<String, Object> basis = maprep.get(0);
		Object[] v;
		int i, j;
		for (String k : basis.keySet())
			dtm.addColumn(k);
		if (maprep.size() <= 1)
			return dtm;
		for (i = 1; i < maprep.size(); i++) {
			v = new Object[basis.size()];
			j = 0;
			for (String k : basis.keySet()) {
				//System.out.println(basis.get(k));
				v[j++] = maprep.get(i).get(k);
			}
			dtm.addRow(v);
			v = null;
		}
		basis = null;
		return dtm;
	}
	
	TableRowSorter makeTableRowSorter(DefaultTableModel dtm) {
		TableRowSorter trs = new TableRowSorter(dtm);
		for (int i = 0; i < dtm.getColumnCount(); i++)
			trs.setComparator(i, DEFAULT_COMPARATOR);
		return trs;
	}
	
	void insertIntoTable(String table_name, Object... values_in_order) throws SQLException {
		Map<String, String> attributes = tables.get(table_name.toLowerCase());
		Set<String> column_names = attributes.keySet();
		String[] pk_excluded_column_names = Arrays.copyOfRange(column_names.toArray(new String[0]), 1, column_names.size());
		processNonQuery(AppModel.SQLUtils.makeSQLTemplateInsertIntoValues(table_name, pk_excluded_column_names), values_in_order);
	}
	
	List<Map<String, Object>> getTableEntries(String table_name, String... requested_columns_in_order) throws SQLException {
		return processQuery(AppModel.SQLUtils.makeSQLTemplateSelectFrom(table_name, requested_columns_in_order));
	}
	
	void updateColumnValueOfId(String table_name, String column_name, int table_primary_key_id, Object newValue) throws SQLException {
		Map<String, String> attributes = tables.get(table_name.toLowerCase());
		Set<String> column_names = attributes.keySet();
		String[] attribute_array = column_names.toArray(new String[0]);
		String primary_key_name = attribute_array[0];
		processNonQuery(AppModel.SQLUtils.makeSQLTemplateUpdateSetWhere(table_name, column_name, primary_key_name),
			newValue, table_primary_key_id);
	}
	
	void deleteById(String table_name, int table_primary_key_id) throws SQLException {
		Map<String, String> attributes = tables.get(table_name.toLowerCase());
		Set<String> column_names = attributes.keySet();
		String primary_key_name = column_names.toArray(new String[0])[0];
		processNonQuery(AppModel.SQLUtils.makeSQLTemplateDeleteFromWhere(table_name, primary_key_name), table_primary_key_id);
	}
	
	void enterDatabase() throws SQLException, ClassNotFoundException, IOException {
		try {
			modelConnection = DriverManager.getConnection(JDBC_MAIN_ADDRESS, MYSQL_USERNAME, MYSQL_PASSWORD);
			printSuccessLog(AM_SMSG.AMS_MAKECONNECTION);
			readSQLFile("initialize_database.sql");
			tables = identifyDatabaseTables();
		} catch (SQLException se) {
			throw new SQLException("Unable to establish connection with the database.", se);
		}
	}
	
	void closeDatabase() throws SQLException {
		if (null == modelConnection || modelConnection.isClosed())
			return;
		try {
			modelConnection.close();
			modelConnection = null;
		} catch (SQLException se) {
			throw new SQLException("Unable to close database connection.", se);
		}
	}
	
	private void printSuccessLog(AM_SMSG msgtype, Object... params) {
		switch (msgtype) {
			case AMS_MAKECONNECTION:
				System.out.println("[" + DATABASE_NAME + "] INFO: Connection established.");
				break;
			case AMS_PROCSTATEMENT:
				System.out.println("[" + DATABASE_NAME + "] INFO: Statement executed: \"" + (String)params[0] + "\".");
				break;
			case AMS_PROCSTATEMENT_EMPTY:
				System.out.println("[" + DATABASE_NAME + "] INFO: Statement executed contains no rows.");
				break;
		}
	}
	
	private Map<String, Map<String, String>> identifyDatabaseTables() throws SQLException {
		Map<String, Map<String, String>> out = null;
		List<Map<String, Object>> table_names = processQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = DATABASE();");
		List<Map<String, Object>> column_names = null;
		Map<String, String> attributes = null;
		String table_name, column_name, column_type;
		if (table_names.size() > 1) {
			out = new HashMap<>();
			for (int i = 1; i < table_names.size(); i++) {
				table_name = (String)table_names.get(i).get("TABLE_NAME");
				//System.out.println(table_name);
				attributes = new LinkedHashMap<>();
				column_names = processQuery("SELECT COLUMN_NAME, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS" + 
					" WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?", table_name);
				for (int j = 1; j < column_names.size(); j++) {
					column_name = (String)column_names.get(j).get("COLUMN_NAME");
					column_type = (String)column_names.get(j).get("COLUMN_TYPE");
					//System.out.println(column_name);
					attributes.put(column_name, column_type);
				}
				out.put(table_name, attributes);
			}
		}
		return out;
	}
	
	static final Map<String, Class<?>> MYSQL_DATATYPES = Map.ofEntries(
        Map.entry("VARCHAR", String.class),
        Map.entry("INT", Integer.class),
        Map.entry("INTEGER", Integer.class),
        Map.entry("BIT", Boolean.class),
		Map.entry("BOOLEAN", Boolean.class),
        Map.entry("FLOAT", Double.class),
        Map.entry("DOUBLE", Double.class),
        Map.entry("DATE", java.sql.Date.class),
        Map.entry("TIME", java.sql.Time.class),
        Map.entry("DATETIME", java.sql.Timestamp.class)
    );
	
	static final Comparator<Object> DEFAULT_COMPARATOR = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			Number a, b;
			double da, db;
			if (null == o1 && null == o2)
				return 0;
			if (null == o1)
				return -1;
			if (null == o2)
				return 1;
			if (o1 instanceof Number && o2 instanceof Number) {
				a = (Number)o1;
				b = (Number)o2;
				da = a.doubleValue();
				db = b.doubleValue();
				return Double.compare(da, db);
			}
			return o1.toString().compareTo(o2.toString());
		}
	};
	
	private Map<String, Map<String, String>> tables;
	private Connection modelConnection;
	private final String DATABASE_NAME;
	private final String MYSQL_USERNAME;
	private final String MYSQL_PASSWORD;
}
