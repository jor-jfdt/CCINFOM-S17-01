import java.sql.*;
public class Driver {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        AppGUI app = new AppGUI("Health Maintenance Organization");
        AppModel model = new AppModel();
        AppController controller = new AppController(app, model);
    }
}
