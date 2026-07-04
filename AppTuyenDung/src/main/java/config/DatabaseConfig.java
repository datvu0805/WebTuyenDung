package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    // Khối static này sẽ chạy 1 lần duy nhất khi ứng dụng nạp class DBConnection
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.postgresql.Driver"); // Khai báo Driver
            config.setJdbcUrl("jdbc:postgresql://103.216.117.40:15432/webtuyendung");
            config.setUsername("webtuyendung");
            config.setPassword("123456");

            // Cấu hình tối ưu cho Pool
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể khởi tạo HikariCP Connection Pool!");
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
