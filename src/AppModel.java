import java.sql.*;
public class AppModel {
	// MySQL port
	public static final String JDBC_MAIN_ADDRESS = "jdbc:mysql://localhost:3306";
	public static enum AM_SMSG { AMS_MAKECONNECTION, AMS_PROCSTATEMENT }
	public static enum AM_EMSG { AME_MAKECONNECTION, AME_PROCSTATEMENT, AME_JCONNECTOR }
	
	AppModel() throws SQLException, ClassNotFoundException {
		// Replace with database name
		// Eto nalang muna for now hahaha
		CONNECTION_NAME = "ccinfomdemo";
		// Change credentials accordingly
		MYSQL_USERNAME = "root";
		MYSQL_PASSWORD = "password";
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
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_PROCSTATEMENT);
		}
		printSuccessLog(AM_SMSG.AMS_PROCSTATEMENT);
		return r;
	}
	
	private Connection makeConnection(String n, String u, String p) throws SQLException, ClassNotFoundException {
		Connection c = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			c = DriverManager.getConnection(JDBC_MAIN_ADDRESS + "/" + n, u, p);
		} catch (SQLException se) {
			se.printStackTrace();
			modelThrowError(AM_EMSG.AME_MAKECONNECTION);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			modelThrowError(AM_EMSG.AME_JCONNECTOR);
		}
		printSuccessLog(AM_SMSG.AMS_MAKECONNECTION);
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
	
	private final Connection CONNECTION;
	private final String CONNECTION_NAME;
	private final String MYSQL_USERNAME;
	private final String MYSQL_PASSWORD;
}
