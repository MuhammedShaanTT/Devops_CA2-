package com.smartnotes.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides JDBC connections to the MySQL database.
 * Configuration is read from environment variables with sensible defaults.
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private static final String DB_HOST     = getEnv("DB_HOST", "localhost");
    private static final String DB_PORT     = getEnv("DB_PORT", "3306");
    private static final String DB_NAME     = getEnv("DB_NAME", "smartnotes");
    private static final String DB_USER     = getEnv("DB_USER", "root");
    private static final String DB_PASSWORD = getEnv("DB_PASSWORD", "root123");

    private static final int MAX_RETRIES   = 3;
    private static final int RETRY_DELAY_MS = 2000;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Obtains a JDBC connection to the configured MySQL database.
     * Retries up to {@value MAX_RETRIES} times with a {@value RETRY_DELAY_MS}ms
     * delay between attempts to handle Docker container startup timing.
     *
     * @return a live {@link Connection}
     * @throws SQLException if a connection cannot be established after all retries
     */
    public static Connection getConnection() throws SQLException {
        String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                DB_HOST, DB_PORT, DB_NAME
        );

        SQLException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                Connection connection = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
                LOGGER.info("Database connection established successfully (attempt " + attempt + ")");
                return connection;
            } catch (SQLException e) {
                lastException = e;
                LOGGER.log(Level.WARNING,
                        "Database connection attempt " + attempt + " of " + MAX_RETRIES + " failed: " + e.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Connection retry interrupted", ie);
                    }
                }
            }
        }

        LOGGER.log(Level.SEVERE, "Failed to connect to database after " + MAX_RETRIES + " attempts");
        throw lastException;
    }

    /**
     * Reads an environment variable, returning a default value when the
     * variable is not set or is blank.
     */
    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
