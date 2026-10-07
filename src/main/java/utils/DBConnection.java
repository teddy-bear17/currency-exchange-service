package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DBConnection implements ServletContextListener {

    private static HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:C:\\Users\\Redmi\\Desktop\\currency-exchange-backend\\src\\main\\resources\\currency.db");
            config.setDriverClassName("org.sqlite.JDBC");

            config.setMaximumPoolSize(1);
            config.setMinimumIdle(0);

            config.setConnectionTimeout(10000);
            config.setIdleTimeout(10000);
            config.setMaxLifetime(30000);
            config.setLeakDetectionThreshold(5000);

            config.setConnectionInitSql("PRAGMA foreign_keys=ON");

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка инициализации приложения", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}