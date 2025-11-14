import java.sql.*;
public class Driver {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        AppGUI app = new AppGUI("Health Maintenance Organization");
        AppModel model = new AppModel();
		
		// Sample SQL query pass
		//ResultSet ex = model.processStatement("SELECT * FROM employee");
		//model.releaseResultSet(ex);
		
        AppController controller = new AppController(app, model);
    }
}
