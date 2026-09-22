package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5433/bankAB";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123456";
    private static Connection connection = null;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {

        try {
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}