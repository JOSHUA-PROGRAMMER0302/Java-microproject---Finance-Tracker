package com.financetracker.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfig {

    // Path to the config file inside src/main/resources
    private static final String CONFIG_FILE = "/config.properties";

    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream input = DBConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("⚠️ Cannot find " + CONFIG_FILE + " in classpath (check src/main/resources)");
            }

            // Load properties from file
            Properties props = new Properties();
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            // Validate properties
            if (url == null || user == null || password == null) {
                throw new RuntimeException("⚠x️ Missing one or more DB properties (db.url / db.user / db.password)");
            }

            // Load PostgreSQL driver explicitly (for safety)
            Class.forName("org.postgresql.Driver");

            System.out.println("✅ DBConfig loaded successfully!");
            System.out.println("→ Connected URL: " + url);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to initialize DBConfig: " + e.getMessage(), e);
        }
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Warning: failed to close connection - " + e.getMessage());
            }
        }
    }
}
