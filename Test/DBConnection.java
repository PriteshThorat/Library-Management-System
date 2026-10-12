package Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection()
            throws SQLException {

        String url =
            "jdbc:mysql://localhost:3306/student_db";

        String username = "root";
        String password = "YOUR_MYSQL_PASSWORD";

        return DriverManager.getConnection(
            url, username, password
        );
    }

    public static void main(String[] args) {
        try (Connection _ = getConnection()) {
            System.out.println("Database connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}