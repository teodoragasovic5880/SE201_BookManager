package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainServer
{
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/books"; // replace with your DB name
        String user = "root"; // MySQL username
        String password = ""; // MySQL password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to the MySQL database!");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
