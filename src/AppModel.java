import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import javax.swing.table.DefaultTableModel;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
public class AppModel {
	// MySQL port
	static final String JDBC_MAIN_ADDRESS = "jdbc:mysql://localhost:3306/";
	static enum AM_SMSG { AMS_MAKECONNECTION, AMS_PROCSTATEMENT, AMS_PROCSTATEMENT_EMPTY }
	static enum AM_EMSG { AME_MAKECONNECTION, AME_PROCSTATEMENT, AME_JCONNECTOR }
	
	AppModel() throws SQLException, ClassNotFoundException {
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
	
	static class ClientRecord {
		static final String TABLE_NAME = "client_record";
		static final String PRIVATE_KEY_COLUMN_NAME = "member_id";
		// Returns a string message if there is an error, otherwise returns an empty string.
		static String createRecord(String first_name, String last_name, String middle_initial,
			LocalDate birth_date, boolean is_employee, char sex, LocalDate enrollment_date, boolean is_active) throws SQLException {
			if (!SQLUtils.stringFitsShort(first_name))
				return "Invalid field response: first_name. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShort(last_name))
				return "Invalid field response: last_name. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShorter(middle_initial))
				return "Invalid field response: middle_initial. Make sure it is " + SHORTER_STRING_LENGTH + " characters long.";
			if (!SQLUtils.genderIsValid(Character.toUpperCase(sex)))
				return "Invalid field response: sex. It can only be either 'M' or 'F'.";
			processNonQuery(String.format("INSERT INTO `%s` VALUES (DEFAULT, '%s', '%s', '%s', '%s', %d, '%c', '%s', %d);",
				TABLE_NAME, first_name.toUpperCase(), last_name.toUpperCase(), middle_initial.toUpperCase(), SQLUtils.toSQLDate(birth_date),
				(is_employee ? 1 : 0), Character.toUpperCase(sex), SQLUtils.toSQLDate(enrollment_date), (is_active ? 1 : 0))
			);
			return "";
		}
		// Returns a HashMap with the key being the column name, and the value
		// being an Object yet to be casted to the respective type during use.
		static HashMap<String, Object> getRecordById(int id) throws SQLException {
			HashMap<String, Object> result = null;
			ResultSet filter = null;
			ResultSetMetaData rsmd;
			int cols;
			try {
				filter = processQuery(
					"SELECT * FROM `" + TABLE_NAME + "` WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
				);
				if (filter.next()) {
					rsmd = filter.getMetaData();
					cols = rsmd.getColumnCount();
					result = new HashMap<>(cols);
					filter.absolute(1);
					for (int i = 1; i <= cols; i++)
						result.put(rsmd.getColumnName(i), filter.getObject(i));
				}
			} catch (SQLException se) {
				se.printStackTrace();
			} finally {
				if (filter != null) {
					try {
						releaseResultSet(filter);
					} catch (SQLException se) {
						se.printStackTrace();
					}
				}
			}
			return result;
		}
		static void updateFirstName(String first_name, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(first_name))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET first_name = '" + first_name.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateLastName(String last_name, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(last_name))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET last_name = '" + last_name.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateMiddleInitial(String middle_initial, int id) throws SQLException {
			if (!SQLUtils.stringFitsShorter(middle_initial))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET middle_initial = '" + middle_initial.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateBirthDate(LocalDate birth_date, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET birth_date = '" + SQLUtils.toSQLDate(birth_date) + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateIsEmployee(Boolean is_employee, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET is_employee = " + (is_employee ? 1 : 0) + " " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateSex(Character sex, int id) throws SQLException {
			if (!SQLUtils.genderIsValid(sex))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " + 
				"SET sex = '" + Character.toUpperCase(sex) + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateEnrollmentDate(LocalDate enrollment_date, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET enrollment_date = '" + SQLUtils.toSQLDate(enrollment_date) + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateIsActive(Boolean is_active, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET is_active = " + (is_active ? 1 : 0) + " " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
	}
	
	static class HospitalRecord {
		static final String TABLE_NAME = "hospital_record";
		static final String PRIVATE_KEY_COLUMN_NAME = "hospital_id";
		static String createRecord(String hospital_name, String address, String city, int zipcode,
			int contact_no, String email) throws SQLException {
			if (!SQLUtils.stringFitsShort(hospital_name))
				return "Invalid field response: hospital_name. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsLong(address))
				return "Invalid field response: address. Make sure it is " + LONG_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShort(city))
				return "Invalid field response: city. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.emailIsValid(email))
				return "Invalid field response: email. Make sure it is a valid email address.";
			processNonQuery(String.format("INSERT INTO `%s` VALUES (DEFAULT, '%s', '%s', '%s', %d, %d, '%s');",
				TABLE_NAME, hospital_name.toUpperCase(), address.toUpperCase(), city.toUpperCase(), zipcode, contact_no, email.toLowerCase()
			));
			return "";
		}
		static HashMap<String, Object> getRecordById(int hospital_id) throws SQLException {
			HashMap<String, Object> result = null;
			ResultSet filter = null;;
			ResultSetMetaData rsmd;
			int cols;
			try {
				filter = processQuery(
					"SELECT * FROM `" + TABLE_NAME + "` WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + hospital_id + ";"
				);
				if (filter.next()) {
					rsmd = filter.getMetaData();
					cols = rsmd.getColumnCount();
					result = new HashMap<>(cols);
					filter.absolute(1);
					for (int i = 1; i <= cols; i++)
						result.put(rsmd.getColumnName(i), filter.getObject(i));
				}
			} catch (SQLException se) {
				se.printStackTrace();
			} finally {
				if (filter != null) {
					try {
						releaseResultSet(filter);
					} catch (SQLException se) {
						se.printStackTrace();
					}
				}
			}
			return result;
		}
		static void updateHospitalName(String hospital_name, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(hospital_name))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET hospital_name = '" + hospital_name.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateAddress(String address, int id) throws SQLException {
			if (!SQLUtils.stringFitsLong(address))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET address = '" + address.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateCity(String city, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(city))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET city = '" + city.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateZipcode(int zipcode, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET zipcode = " + zipcode + " " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateContactNo(int contact_no, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET contact_no = " + contact_no + " " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateEmail(String email, int id) throws SQLException {
			if (!SQLUtils.emailIsValid(email))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET email = '" + email.toLowerCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
	}
	
	static class CompanyPolicyRecord {
		static final String TABLE_NAME = "company_policy_record";
		static final String PRIVATE_KEY_COLUMN_NAME = "plan_id";
		static String createRecord(String plan_name, String coverage_type, double coverage_limit, double premium_amount,
			String payment_period, String inclusion) throws SQLException {
			if (!SQLUtils.stringFitsShort(plan_name))
				return "Invalid field response: plan_name. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShort(coverage_type))
				return "Invalid field response: coverage_type. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShort(payment_period))
				return "Invalid field response: payment_period. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShort(inclusion))
				return "Invalid field response: inclusion. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			processNonQuery(String.format("INSERT INTO `%s` VALUES (DEFAULT, '%s', '%s', %f, %f, '%s', '%s');",
				TABLE_NAME, plan_name.toUpperCase(), coverage_type.toUpperCase(), coverage_limit, premium_amount,
				payment_period.toUpperCase(), inclusion.toUpperCase()
			));
			return "";
		}
		static HashMap<String, Object> getRecordById(int hospital_id) throws SQLException {
			HashMap<String, Object> result = null;
			ResultSet filter = null;;
			ResultSetMetaData rsmd;
			int cols;
			try {
				filter = processQuery(
					"SELECT * FROM `" + TABLE_NAME + "` WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + hospital_id + ";"
				);
				if (filter.next()) {
					rsmd = filter.getMetaData();
					cols = rsmd.getColumnCount();
					result = new HashMap<>(cols);
					filter.absolute(1);
					for (int i = 1; i <= cols; i++)
						result.put(rsmd.getColumnName(i), filter.getObject(i));
				}
			} catch (SQLException se) {
				se.printStackTrace();
			} finally {
				if (filter != null) {
					try {
						releaseResultSet(filter);
					} catch (SQLException se) {
						se.printStackTrace();
					}
				}
			}
			return result;
		}
		static void updatePlanName(String plan_name, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(plan_name))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET plan_name = '" + plan_name.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateCoverageType(String coverage_type, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(coverage_type))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET coverage_type = '" + coverage_type.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateCoverageLimit(float coverage_limit, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET coverage_limit = " + coverage_limit + " " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updatePremiumAmount(float premium_amount, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET premium_amount = " + premium_amount + " " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updatePaymentPeriod(String payment_period, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(payment_period))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET payment_period = '" + payment_period.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateInclusion(String inclusion, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(inclusion))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET inclusion = '" + inclusion.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
	}
	
	static class DoctorRecord {
		static final String TABLE_NAME = "doctor_record";
		static final String PRIVATE_KEY_COLUMN_NAME = "doctor_id";
		static String createRecord(String first_name, String last_name, String middle_initial, String doctor_type,
			int contact_no, String email) throws SQLException {
			if (!SQLUtils.stringFitsShort(first_name))
				return "Invalid field response: first_name. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShort(last_name))
				return "Invalid field response: last_name. Make sure it is " + SHORT_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShorter(middle_initial))
				return "Invalid field response: middle_initial. Make sure it is " + SHORTER_STRING_LENGTH + " characters long.";
			if (!SQLUtils.stringFitsShorter(doctor_type))
				return "Invalid field response: doctor_type. Make sure it is " + SHORTER_STRING_LENGTH + " characters long.";
			if (!SQLUtils.emailIsValid(email))
				return "Invalid field response: email. Make sure it is a valid email address.";
			processNonQuery(String.format("INSERT INTO `%s` VALUES (DEFAULT, '%s', '%s', '%s', '%s', %d, '%s');",
				TABLE_NAME, first_name.toUpperCase(), last_name.toUpperCase(), middle_initial.toUpperCase(), doctor_type.toUpperCase(),
				contact_no, email.toLowerCase()
			));
			return "";
		}
		static HashMap<String, Object> getRecordById(int hospital_id) throws SQLException {
			HashMap<String, Object> result = null;
			ResultSet filter = null;;
			ResultSetMetaData rsmd;
			int cols;
			try {
				filter = processQuery(
					"SELECT * FROM `" + TABLE_NAME + "` WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + hospital_id + ";"
				);
				if (filter.next()) {
					rsmd = filter.getMetaData();
					cols = rsmd.getColumnCount();
					result = new HashMap<>(cols);
					filter.absolute(1);
					for (int i = 1; i <= cols; i++)
						result.put(rsmd.getColumnName(i), filter.getObject(i));
				}
			} catch (SQLException se) {
				se.printStackTrace();
			} finally {
				if (filter != null) {
					try {
						releaseResultSet(filter);
					} catch (SQLException se) {
						se.printStackTrace();
					}
				}
			}
			return result;
		}
		static void updateFirstName(String first_name, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(first_name))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET first_name = '" + first_name.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateLastName(String last_name, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(last_name))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET last_name = '" + last_name.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateMiddleInitial(String middle_initial, int id) throws SQLException {
			if (!SQLUtils.stringFitsShorter(middle_initial))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET middle_initial = '" + middle_initial.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateDoctorType(String doctor_type, int id) throws SQLException {
			if (!SQLUtils.stringFitsShort(doctor_type))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET doctor_type = '" + doctor_type.toUpperCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateContactNo(int contact_no, int id) throws SQLException {
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET contact_no = " + contact_no + " " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
		static void updateEmail(String email, int id) throws SQLException {
			if (!SQLUtils.emailIsValid(email))
				return;
			processNonQuery(
				"UPDATE `" + TABLE_NAME + "` " +
				"SET email = '" + email.toLowerCase() + "' " +
				"WHERE " + PRIVATE_KEY_COLUMN_NAME + " = " + id + ";"
			);
		}
	}
	
	// For general SELECT statements
	static ResultSet processQuery(String query) throws SQLException {
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
	
	private static void enterDatabase() throws SQLException, ClassNotFoundException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			modelConnection = DriverManager.getConnection(JDBC_MAIN_ADDRESS, MYSQL_USERNAME, MYSQL_PASSWORD);
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
