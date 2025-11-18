import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
public class AppModel {
	// ---------------------------------------------------------------
	// ---------------------- Public Attributes ----------------------
	// ---------------------------------------------------------------
	// MySQL port
	static final String JDBC_MAIN_ADDRESS = "jdbc:mysql://localhost:3306/";
	static enum AM_SMSG { AMS_MAKECONNECTION, AMS_PROCSTATEMENT, AMS_PROCSTATEMENT_EMPTY }
	static enum AM_EMSG { AME_MAKECONNECTION, AME_CONNECTION_CLOSED, AME_PROCSTATEMENT, AME_JCONNECTOR }
	
	// ---------------------------------------------------------------
	// ------------------------- Constructor -------------------------
	// ---------------------------------------------------------------
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
	
	// ---------------------------------------------------------------
	// ---------------- Utilities for SQL processing -----------------
	// ---------------------------------------------------------------
	static class SQLUtils {
		// Creates a PreparedStatement template for processQuery
		// Returns "UPDATE <table_name> SET <set_name> = ? WHERE <where_name> = ?;"
		static String makeSQLTemplateUpdateSetWhere(String table_name, String set_name, String where_name) {
			if (null == table_name || null == set_name || null == where_name || !assessAllIdentifiers(table_name, set_name, where_name))
				throw new IllegalArgumentException("makeSQLTemplateUpdateSetWhere: please check your parameters");
			return "UPDATE " + table_name + " SET " + set_name + " = ? WHERE " + where_name + " = ?;";
		}
		// Creates a PreparedStatement template for processQuery
		// Returns "INSERT INTO <table_name> (<column_name1>,<column_name2>,...) VALUES (?,?,...);"
		static String makeSQLTemplateInsertIntoValues(String table_name, String... column_names) {
			String[] identifiers = Arrays.copyOf(column_names, column_names.length + 1);
			identifiers[identifiers.length - 1] = table_name;
			if (null == table_name || null == column_names || !assessAllIdentifiers(identifiers))
				throw new IllegalArgumentException("makeSQLTemplateInsertIntoValues: please check your parameters");
			String column_body = "(" + String.join(",", column_names) + ")";
			String value_body = "(" + Stream.generate(() -> "?").limit(column_names.length).collect(Collectors.joining(",")) + ")";
			return "INSERT INTO " + table_name + " " + column_body + " VALUES " + value_body + ";";
		}
		// Creates a PreparedStatement template for processQuery
		// Returns "SELECT <column_name1>,<column_name2>,... FROM <table_name>;"
		static String makeSQLTemplateSelectFrom(String table_name, String... ordered_column_names) {
			String[] identifiers = Arrays.copyOf(ordered_column_names, ordered_column_names.length + 1);
			identifiers[identifiers.length - 1] = table_name;
			if (null == table_name || null == ordered_column_names || !assessAllIdentifiers(identifiers))
				throw new IllegalArgumentException("makeSQLTemplateSelectFrom: please check your parameters");
			return "SELECT " + String.join(",", ordered_column_names) + " FROM " + table_name + ";";
		}
		// Creates a PreparedStatement template for processQuery
		// Returns "SELECT <column_name1>,<column_name2>,... FROM <table_name> WHERE <where_name> = ?;"
		static String makeSQLTemplateSelectFromWhere(String table_name, String where_name, String... ordered_column_names) {
			String[] identifiers = Arrays.copyOf(ordered_column_names, ordered_column_names.length + 2);
			identifiers[identifiers.length - 2] = where_name;
			identifiers[identifiers.length - 1] = table_name;
			if (null == table_name || null == where_name || null == ordered_column_names || !assessAllIdentifiers(identifiers))
				throw new IllegalArgumentException("makeSQLTemplateSelectFromWhere: please check your parameters");
			return "SELECT " + String.join(",", ordered_column_names) + " FROM " + table_name + " WHERE " + where_name + " = ?;";
		}
		// Creates a PreparedStatement template for processQuery
		// Returns "DELETE FROM <table_name> WHERE <where_key_name> = ?;"
		static String makeSQLTemplateDeleteFromWhere(String table_name, String where_key_name) {
			if (null == table_name || null == where_key_name || !assessAllIdentifiers(table_name, where_key_name))
				throw new IllegalArgumentException("makeSQLTemplateDeleteFromWhere: please check your parameters");
			return "DELETE FROM " + table_name + " WHERE " + where_key_name + " = ?;";
		}
		// Creates a DATE in form of a string that can be understood by SQL
		static String toSQLDate(LocalDate ld) {
			return ld.format(DateTimeFormatter.ofPattern(DATE_FORMATTING));
		}
		// Creates a DATETIME in form of a string that can be understood by SQL
		static String toSQLDatetime(LocalDateTime ldt) {
			return ldt.format(DateTimeFormatter.ofPattern(DATETIME_FORMATTING));
		}
		// Converts a string to Java's LocalDate; null if inconvertible
		static LocalDate stringToDate(String str) {
			return str != null && dateIsValid(str) ? LocalDate.parse(str, DateTimeFormatter.ofPattern(DATE_FORMATTING)) : null;
		}
		// Converts a string to Java's LocalDateTime; null if inconvertible
		static LocalDateTime stringToDatetime(String str) {
			return str != null && datetimeIsValid(str) ? LocalDateTime.parse(str, DateTimeFormatter.ofPattern(DATE_FORMATTING)) : null;
		}
		// Check if String fits the requirements for a VARCHAR(254)
		static boolean stringFitsLong(String str) {
			return str != null && str.length() > 0 && str.length() <= LONG_STRING_LENGTH && str.matches(REGEX_LATIN1);
		}
		// Check if String fits the requirements for a VARCHAR(127)
		static boolean stringFitsShort(String str) {
			return str != null && str.length() > 0 && str.length() <= SHORT_STRING_LENGTH && str.matches(REGEX_LATIN1);
		}
		// Check if String fits the requirements for a VARCHAR(15)
		static boolean stringFitsShorter(String str) {
			return str != null && str.length() > 0 && str.length() <= SHORTER_STRING_LENGTH && str.matches(REGEX_LATIN1);
		}
		// Check if String fits the requirements for an ICD10, i.e. 3-7 chars only
		static boolean stringFitsICD10(String str) {
			return str != null && str.length() >= ICD10_STRING_MIN_LENGTH &&
				str.length() <= ICD10_STRING_MAX_LENGTH && str.matches(REGEX_LATIN1);
		}
		// Check if String fits the requirements for an email address
		static boolean emailIsValid(String str) {
			return str != null && stringFitsLong(str) && str.matches(REGEX_EMAIL);
		}
		// Check if String fits the requirements for a DATE type
		static boolean dateIsValid(String str) {
			try {
				return str != null && LocalDate.parse(str, DateTimeFormatter.ofPattern(DATE_FORMATTING)) != null;
			} catch (DateTimeParseException dtpe) {
				return false;
			}
		}
		// Check if String fits the requirements for a DATETIME type
		static boolean datetimeIsValid(String str) {
			try {
				return str != null && LocalDateTime.parse(str, DateTimeFormatter.ofPattern(DATETIME_FORMATTING)) != null;
			} catch (DateTimeParseException dtpe) {
				return false;
			}
		}
		// Check if String fits the requirements for gender assigned at birth
		static boolean genderIsValid(char c) {
			return c == 'M' || c == 'F';
		}
		// Checks if identifier name is valid
		private static boolean assessIdentifier(String identifier) {
			return identifier != null && identifier.trim().length() > 0 &&
				identifier.matches(REGEX_SQL_IDENTIFIER);
		}
		// Checks if all identifier names listed are valid
		private static boolean assessAllIdentifiers(String... identifierSet) {
			boolean assessment = true;
			for (int i = 0; assessment && i < identifierSet.length; i++)
				assessment = assessIdentifier(identifierSet[i]);
			return assessment;
		}
		static final int LONG_STRING_LENGTH = 254;
		static final int SHORT_STRING_LENGTH = 127;
		static final int SHORTER_STRING_LENGTH = 15;
		static final int ICD10_STRING_MIN_LENGTH = 3;
		static final int ICD10_STRING_MAX_LENGTH = 3;
		static final int CHAR_LENGTH = 1;
		static final String DATE_FORMATTING = "yyyy-MM-dd";
		static final String DATETIME_FORMATTING = "yyyy-MM-dd HH:mm:ss";
		static final String REGEX_LATIN1 = "\\A[\\u0000-\\u00FF]*\\z";
		static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		private static final String REGEX_SQL_IDENTIFIER = "\\*|^[a-zA-Z_]\\w*$";
	}
	
	// ---------------------------------------------------------------
	// ------------------------- For JTables -------------------------
	// ---------------------------------------------------------------
	
	// Creates a DefaultTableModel for a JTable to be used for .setModel()
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
	
	// Creates a TableRowSorter<> for a JTable to be used for .setRowSorter()
	TableRowSorter<DefaultTableModel> makeTableRowSorter(DefaultTableModel dtm) {
		TableRowSorter<DefaultTableModel> trs = new TableRowSorter<>(dtm);
		for (int i = 0; i < dtm.getColumnCount(); i++)
			trs.setComparator(i, DEFAULT_COMPARATOR);
		return trs;
	}
	
	// Creates a TableRowSorter<> for a JTable to be used for .setRowSorter()
	// with a filtering based on a keyword across the given columns
	TableRowSorter<DefaultTableModel> filterOnTableRowSorter(DefaultTableModel dtm, String keyword) {
		TableRowSorter<DefaultTableModel> trs = null;
		String[] keyword_split, target_split;
		String actual_keyword;
		Integer t;
		List<Integer> targets;
		int[] varargable_targets;
		boolean inverted = false;
		// Walang dtm walang sorter
		if (null != dtm) {
			trs = makeTableRowSorter(dtm);
			// Walang keyword walang filter
			if (keyword != null && keyword.length() > 0) {
				// Split the keyword into two parts para maisolate natin ung @@ or @! tag sa unahan
				keyword_split = keyword.trim().replaceAll("\\s+", " ").split(" ", 2);
				// Walang @@ or @!a,b,c,.. sa unahan, walang specific column na pagsesearchan ng keyword,
				// so the user must be asking for the whole keyword to be searched across all columns
				if (keyword_split.length <= 1 || !(keyword_split[0].startsWith("@@") || keyword_split[0].startsWith("@!"))) {
					actual_keyword = keyword;
					targets = IntStream.rangeClosed(0, dtm.getColumnCount() - 1).boxed().collect(Collectors.toList());
				} else {
					inverted = keyword_split[0].startsWith("@!");
					// Found @@ or @! tag, baka may gusto ung user na specific column na hanapan ng keywords
					// Forcefully clean the @@ or @! tag at itransform to into a comma-separated number string
					// Which means tatanggalin ko ung mga non-numeric symbols except for commas
					// Tapos just in case lang, ung mga napasobra o nadobleng spaces icompress na rin into one
					target_split = keyword_split[0].replaceAll("[^0-9,]+", " ").replaceAll("\\s+", " ")
						.replaceAll("[, ]+", ",").split(",");
					// Kung walang matinong number na laman ung tag, then false alarm, wala palang gustong column
					// ang user na hanapan, so just search for the whole keyword again across all columns
					if (0 == target_split.length) {
						actual_keyword = keyword;
						targets = IntStream.rangeClosed(0, dtm.getColumnCount() - 1).boxed().collect(Collectors.toList());
					} else {
						// May @@ or @! tag sa unahan containing actual indices
						// Wag isama sa search to at gamitin lang ung other part ng keyword as search query
						actual_keyword = keyword_split[1];
						// Kung inverted, then ang sistema is wag isasama ung mga nasabing columns,
						// kaya magsisimula tayo sa list na kasama lahat ng columns then we take away
						// Otherwise, start with an empty array then magdadagdag tayo
						targets = inverted ?
							IntStream.rangeClosed(0, dtm.getColumnCount() - 1).boxed().collect(Collectors.toList()) :
							new ArrayList<>();
						// Convert all numeric strings into Integers, pero iiignore ung mga out of bound indices
						// Dahil nareplace rin ung @@ or @! with , then most likely ang first index ng target_split ay empty string
						// Skip it by starting at i = 1
						for (int i = 1; i < target_split.length; i++) {
							if (inverted && targets.isEmpty())
								break;
							if (!inverted && targets.size() < dtm.getColumnCount())
								break;
							t = Integer.valueOf(target_split[i]);
							if (t > 0 && t <= dtm.getColumnCount())
								// Pag inverted, remove (t - 1), otherwise add
								if (inverted)
									// Cast to Object kasi hindi yan supposed to be
									// "remove the (t - 1)th value of the ArrayList", kundi
									// "remove value: t - 1 inside the ArrayList"
									targets.remove((Object)(t - 1));
								else
									targets.add(t - 1);
						}
						// Pag walang nadagdag, then lahat ng columns dapat hahanapan natin
						if (!inverted && targets.isEmpty())
							targets = IntStream.rangeClosed(0, dtm.getColumnCount() - 1).boxed().collect(Collectors.toList());
					}
				}
				actual_keyword = Pattern.quote(actual_keyword);
				varargable_targets = Arrays.stream(targets.toArray(new Integer[0])).mapToInt(Integer::intValue).toArray();
				trs.setRowFilter(RowFilter.regexFilter("(?i)" + actual_keyword, varargable_targets));
			}
		}
		keyword_split = null;
		target_split = null;
		actual_keyword = null;
		t = null;
		targets = null;
		varargable_targets = null;
		return trs;
	}
	
	// ---------------------------------------------------------------
	// --------------------------- C R U D ---------------------------
	// ---------------------------------------------------------------
	
	// Parses a .SQL file whole
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
			.replaceAll("(--.*)|(#.*)|((?s)\\/\\*.*?\\*\\/)", "") // Tanggalin lahat ng comments
			.replaceAll("\\s+", " ") // Paltan lahat ng extra whitespaces ng isang space lang
			.trim() // Trim trailing whitespaces sa unahan saka hulihan
			.split("(?<=;)"); // Split statements before each ; (so kasama ung ;)
		result = new Object[statementSet.length];
		i = 0;
		if (statementSet.length > 0) {
			for (String s : statementSet) {
				//System.out.println("[read] " + s);
				if (!s.isEmpty())
					if (s.toUpperCase().trim().startsWith("SELECT"))
						result[i++] = processQuery(s);
					else
						result[i++] = processNonQuery(s);
			}
		}
		return result;
	}
	
	// Processes a single query with a given query template. This is primarily for SELECT statements.
	// Returns a List<> of rows resulting from the query.
	// The first element of this List is the "basis row", with the String as the attribute name
	// and the Object (castable to String) as the SQL datatype name
	// The succeeding elements then represent each entry that matches the given query, with the String
	// as the attribute name and the Object as the value of the entry for that attribute.
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
	
	// Processes a single DML statement with a given DML template.
	// This is used for UPDATE, INSERT, DELETE, etc., which alters the database and
	// does not try to match entries.
	//
	// Returns an int representing the number of rows affected after executing the statement.
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
	
	// C. Creates an entry on the specified table with its corresponding values.
	void insertIntoTable(String table_name, Object... values_in_order) throws SQLException {
		Map<String, String> attributes = tables.get(table_name.toLowerCase());
		Set<String> column_names = attributes.keySet();
		String[] pk_excluded_column_names = Arrays.copyOfRange(column_names.toArray(new String[0]), 1, column_names.size());
		processNonQuery(AppModel.SQLUtils.makeSQLTemplateInsertIntoValues(table_name, pk_excluded_column_names), values_in_order);
	}
	
	// R. Reads all entries from a table.
	// Set of attributes to include in the result can be listed in requested_columns_in_order.
	// Attribute values of entries unlisted in requested_columns_in_order will not be shown in the result.
	List<Map<String, Object>> getTableEntries(String table_name, String... requested_columns_in_order) throws SQLException {
		// SELECT <requested_columns_in_order> FROM <table_name>
		return processQuery(AppModel.SQLUtils.makeSQLTemplateSelectFrom(table_name, requested_columns_in_order));
	}
	
	List<Map<String, Object>> getTableEntries(String table_name, String where_name, Object where_value, String... requested_columns_in_order) throws SQLException {
		// SELECT <requested_columns_in_order> FROM <table_name> WHERE <where_name> = <where_value>
		if (null == where_name)
			return getTableEntries(table_name, requested_columns_in_order);
		return processQuery(AppModel.SQLUtils.makeSQLTemplateSelectFromWhere(table_name, where_name, requested_columns_in_order), where_value);
	}
	
	List<Map<String, Object>> getTableEntriesInverted(String table_name, String... excluded_columns) throws SQLException {
		List<String> available_columns = new ArrayList<>(tables.get(table_name).keySet());
		String[] included_columns;
		for (String e : excluded_columns)
			available_columns.remove(e);
		included_columns = available_columns.toArray(new String[0]);
		return getTableEntries(table_name, included_columns);
	}
	
	List<Map<String, Object>> getTableEntriesInverted(String table_name, String where_name, Object where_value, String... excluded_columns) throws SQLException {	
		if (null == where_name)
			return getTableEntries(table_name, excluded_columns);
		List<String> available_columns = new ArrayList<>(tables.get(table_name).keySet());
		String[] included_columns;
		for (String e : excluded_columns)
			available_columns.remove(e);
		included_columns = available_columns.toArray(new String[0]);
		return getTableEntries(table_name, where_name, where_value, included_columns);
	}
	
	// U. Updates an attribute value of an entry within a table assigned to the target primary key.
	void updateColumnValueOfId(String table_name, String column_name, int table_primary_key_id, Object newValue) throws SQLException {
		Map<String, String> attributes = tables.get(table_name.toLowerCase());
		Set<String> column_names = attributes.keySet();
		String[] attribute_array = column_names.toArray(new String[0]);
		String primary_key_name = attribute_array[0];
		processNonQuery(AppModel.SQLUtils.makeSQLTemplateUpdateSetWhere(table_name, column_name, primary_key_name),
			newValue, table_primary_key_id);
	}
	
	// D. Deletes an entry from the table assigned to the target primary key.
	void deleteById(String table_name, int table_primary_key_id) throws SQLException {
		Map<String, String> attributes = tables.get(table_name.toLowerCase());
		Set<String> column_names = attributes.keySet();
		String primary_key_name = column_names.toArray(new String[0])[0];
		processNonQuery(AppModel.SQLUtils.makeSQLTemplateDeleteFromWhere(table_name, primary_key_name), table_primary_key_id);
	}
	
	// ---------------------------------------------------------------
	// ------------------------- Database ----------------------------
	// ---------------------------------------------------------------
	
	// Opens the connection to the database.
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
	
	// Closes the connection to the database.
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
	
	Map<String, String> getDatabaseTableAttributes(String table_name) {
		if (tables.getOrDefault(table_name, null) == null)
			throw new IllegalArgumentException("No such table named " + table_name + " in " + DATABASE_NAME);
		return tables.get(table_name);
	}
	
	// Prints success messages to the console.
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
	
	// Identifies the tables and its attributes into a Map<>
	// Map<  String,        Map<  String,       String    >
	//         ^                    ^             ^
	//         |                    |             |
	//     Table name         Attribute name   Data type
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
	
	// ---------------------------------------------------------------
	// --------------------- Private Attributes ----------------------
	// ---------------------------------------------------------------
	
	// Custom comparator used for TableRowSorter instances.
	// Specifically designed to fix errors on number sorting.
	private static final Comparator<Object> DEFAULT_COMPARATOR = new Comparator<>() {
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
	
	// Database variables
	private Map<String, Map<String, String>> tables;
	private Connection modelConnection;
	private final String DATABASE_NAME;
	private final String MYSQL_USERNAME;
	private final String MYSQL_PASSWORD;
}
