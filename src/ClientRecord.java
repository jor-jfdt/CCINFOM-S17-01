import java.sql.*;
import java.util.ArrayList;

public class ClientRecord {
    private static final String db_url = "jdbc:mysql://127.0.0.1:3306/insurance_database";
    private static final String user = "root";
    private static final String password = "123456";
    private static Connection connection = null;
    public static void main(String[] args) {

    }


    public static void connectDB() {
        try{
            connection = DriverManager.getConnection(db_url, user, password);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createRecord(String first_name,
                                    String last_name,
                                    String middle_initial,
                                    Date birth_date,
                                    Boolean is_employee,
                                    char sex,
                                    Date enrollment_date,
                                    Boolean is_active) {

        try {
            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO client_record"); //INSERT INTO client_record
            query.append("VALUES (DEFAULT, '"
                    + first_name + "' '"
                    + last_name + "' ' "
                    + middle_initial + "' '"
                    + birth_date + "' '"
                    + is_employee + "' '"
                    + sex + "' '"
                    + enrollment_date + "' '" +
                    is_active +"');"); // VALUES (DEFAULT, '$first_name', '$last_name', '$middle_initial', '$birth_date', '$is_employee', '$sex', '$is_active');

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to insert.");
        }
    }

    public static void updateFirstName(String first_name, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET first_name = '" + first_name + "'"); // SET first_name = '$first_name'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }

    public static void updateLastName(String last_name, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET first_name = '" + last_name + "'"); // SET last_name = '$last_name'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }

    public static void middle_initial(String middle_initial, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET first_name = '" + middle_initial + "'"); // SET middle_initial = '$middle_initial'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }

    public static void updateBirthDate(Date birthdate, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET birth_date = '" + birthdate + "'"); // SET birth_date = '$birthdate'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }

    public static void updateIsEmployee(Boolean isEmployee, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET is_employee = '" + isEmployee + "'"); // SET is_employee = '$is_employee'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }

    public static void updateSex(char sex, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET sex = '" + sex + "'"); // SET sex = '$sex'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }

    public static void updateEnrollmentDate(Date enrollmentDate, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET enrollment_date = '" + enrollmentDate + "'"); // SET enrollment_date = '$enrollment_date'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }

    public static void updateIsActive(Boolean isActive, int member_id) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE client_record"); //UPDATE client_record
            query.append("SET is_active = '" + isActive + "'"); // SET is_active = '$is_active'
            query.append("WHERE member_id = " + member_id + ";"); // WHERE member_id = $member_id;

            Statement statement = connection.createStatement();
            statement.execute(query.toString());
            statement.close();
        } catch(SQLException e) {
            System.out.println("Error. Unable to update.");
        }
    }





}
