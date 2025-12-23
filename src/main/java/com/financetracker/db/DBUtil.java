package com.financetracker.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBUtil {

    private static final String CONFIG_FILE = "/config.properties";

    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream input = DBUtil.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("⚠️ Cannot find " + CONFIG_FILE + " in classpath (check src/main/resources)");
            }

            Properties props = new Properties();
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new RuntimeException("⚠️ Missing db.url / db.user / db.password in config.properties");
            }

            // Load PostgreSQL JDBC driver (recommended for safety)
            Class.forName("org.postgresql.Driver");

            System.out.println("✅ DBUtil initialized successfully.");
            System.out.println("→ Using URL: " + url);

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load DBUtil configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Get a new connection to the PostgreSQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Close a connection safely (use in finally blocks or try-with-resources).
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Warning: failed to close connection - " + e.getMessage());
            }
        }
    }
}
