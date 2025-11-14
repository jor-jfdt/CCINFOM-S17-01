import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.CopyOnWriteArrayList;
public class AppModel {
	// MySQL port
	public static final String JDBC_MAIN_ADDRESS = "jdbc:mysql://localhost:3306";
	public static enum AM_SMSG { AMS_MAKECONNECTION, AMS_PROCSTATEMENT, AMS_PROCSTATEMENT_EMPTY }
	public static enum AM_EMSG { AME_MAKECONNECTION, AME_PROCSTATEMENT, AME_JCONNECTOR }
	
	AppModel() throws SQLException, ClassNotFoundException {
		// Replace with database name
		// Eto nalang muna for now hahaha
		CONNECTION_NAME = "insurance_database";
		// Change credentials accordingly
		MYSQL_USERNAME = "root";
		MYSQL_PASSWORD = "123456";
		CONNECTION = makeConnection(CONNECTION_NAME, MYSQL_USERNAME, MYSQL_PASSWORD);
	}
	
	ResultSet processStatement(String sqlStatement) throws SQLException {
		Statement s = null;
		ResultSet r = null;
		if (null == CONNECTION)
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		try {
			s = CONNECTION.createStatement();
			r = s.executeQuery(sqlStatement);
			result_sets.add(r);
			printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT);
			if (!r.next())
				printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT_EMPTY);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_PROCSTATEMENT);
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
			do {
				rowSet = new Object[cols];
				for (int i = 0; i < cols; i++)
					rowSet[i] = rs.getObject(i + 1);
				dtm.addRow(rowSet);
				rowSet = null;
			} while (rs.next());
			if (releaseOnReturn)
				releaseResultSet(rs);
		} catch (SQLException se) {
			se.printStackTrace();
			throw new SQLException(String.format("Unable to make a table model for ResultSet@%x.", rs.hashCode()));
		}
		return dtm;
	}
	
	DefaultTableModel makeTableFromStatement(String query) throws SQLException {
		ResultSet proc = null;
		Integer procHash;
		DefaultTableModel dtm = null;
		try {
			proc = processStatement(query);
			procHash = proc.hashCode();
			dtm = makeTableModel(proc, true);
			System.out.printf("[%s] INFO(table): Table generated for ResultSet@%x.\n", CONNECTION_NAME, procHash);
		} catch (SQLException se) {
			se.printStackTrace();
			throw new SQLException("An error occured generating a table from query.");
		}
		return dtm;
	}
	
	void releaseResultSet(ResultSet rs) throws SQLException {
		if (result_sets.contains(rs)) {
			try {
				rs.close();
				System.out.printf("[%s] INFO(release): Released ResultSet@%x from memory.\n", CONNECTION_NAME, rs.hashCode());
				rs = null;
				result_sets.remove(rs);
			} catch (SQLException se) {
				se.printStackTrace();
				throw new SQLException(String.format("Unable to release ResultSet@%x from memory.", rs.hashCode()));
			}
		}
	}
	
	void releaseAllResultSets() throws SQLException {
		for (ResultSet r : result_sets) {
			try {
				releaseResultSet(r);
			} catch (SQLException se) {
				result_sets.remove(r);
				System.out.printf("[%s] INFO(release_all): ResultSet@%x cannot be removed from memory, but still removing it from list.\n",
					CONNECTION_NAME, r.hashCode());
			}
		}
	}
	
	private Connection makeConnection(String n, String u, String p) throws SQLException, ClassNotFoundException {
		Connection c = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			c = DriverManager.getConnection(JDBC_MAIN_ADDRESS + "/" + n, u, p);
			printSuccessLog(AM_SMSG.AMS_MAKECONNECTION);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			modelThrowError(AM_EMSG.AME_JCONNECTOR);
		}
		return c;
	}
	
	private void printSuccessLog(AM_SMSG msgtype) {
		switch (msgtype) {
			case AMS_MAKECONNECTION:
				System.out.println("[" + CONNECTION_NAME + "] INFO: Connection established.");
				break;
			case AMS_PROCSTATEMENT:
				System.out.println("[" + CONNECTION_NAME + "] INFO: Statement executed.");
				break;
			case AMS_PROCSTATEMENT_EMPTY:
				System.out.println("[" + CONNECTION_NAME + "] INFO: Statement executed contains no rows.");
				break;
		}
	}
	
	private void modelThrowError(AM_EMSG errtype) throws SQLException {
		switch (errtype) {
			case AME_MAKECONNECTION:
				throw new SQLException("Unable to establish connection with makeConnection() at this time.");
			case AME_PROCSTATEMENT:
				throw new SQLException("Unable to execute statement.");
			case AME_JCONNECTOR:
				throw new SQLException("MySQL J Connector is malconfigured.");
		}
	}
	
	private static CopyOnWriteArrayList<ResultSet> result_sets = new CopyOnWriteArrayList<>();
	
	private final Connection CONNECTION;
	private final String CONNECTION_NAME;
	private final String MYSQL_USERNAME;
	private final String MYSQL_PASSWORD;
}
