package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "103.216.117.40";
    private static final String PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "15432";
    private static final String DB = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "webtuyendung";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "webtuyendung";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "123456";
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
