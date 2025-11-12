import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static void main(String[] args) {
        try{
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/insurance_database",
                    "root",
                    "123456");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


}
