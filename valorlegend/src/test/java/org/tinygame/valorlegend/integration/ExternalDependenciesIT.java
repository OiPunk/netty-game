package org.tinygame.valorlegend.integration;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalDependenciesIT {
    @Test
    void mysqlShouldBeReachableAndSchemaReady() throws Exception {
        String jdbcUrl = readEnvOrProperty(
            "it.mysql.url",
            "IT_MYSQL_URL",
            "jdbc:mysql://127.0.0.1:3307/valor_legend?useSSL=false&useUnicode=true&characterEncoding=UTF-8"
        );
        String username = readEnvOrProperty("it.mysql.username", "IT_MYSQL_USERNAME", "hero");
        String password = readEnvOrProperty("it.mysql.password", "IT_MYSQL_PASSWORD", "hero_pass");

        Class.forName("com.mysql.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_user")) {
            assertTrue(rs.next());
            assertTrue(rs.getInt(1) >= 0);
        }
    }

    @Test
    void redisShouldBeReachable() {
        String host = readEnvOrProperty("it.redis.host", "IT_REDIS_HOST", "127.0.0.1");
        int port = Integer.parseInt(readEnvOrProperty("it.redis.port", "IT_REDIS_PORT", "6380"));

        try (Jedis jedis = new Jedis(host, port)) {
            assertEquals("PONG", jedis.ping());

            String key = "integration:test:key";
            jedis.set(key, "ok");
            assertEquals("ok", jedis.get(key));
            jedis.del(key);
        }
    }

    @Test
    void mysqlShouldSupportInsertAndDeleteRoundTrip() throws Exception {
        String jdbcUrl = readEnvOrProperty(
            "it.mysql.url",
            "IT_MYSQL_URL",
            "jdbc:mysql://127.0.0.1:3307/valor_legend?useSSL=false&useUnicode=true&characterEncoding=UTF-8"
        );
        String username = readEnvOrProperty("it.mysql.username", "IT_MYSQL_USERNAME", "hero");
        String password = readEnvOrProperty("it.mysql.password", "IT_MYSQL_PASSWORD", "hero_pass");
        String uniqueUserName = "it_user_" + System.currentTimeMillis();

        Class.forName("com.mysql.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            int insertedRows;

            try (PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO t_user (user_name, password, hero_avatar) VALUES (?, ?, ?)"
            )) {
                insertStmt.setString(1, uniqueUserName);
                insertStmt.setString(2, "integration_password");
                insertStmt.setString(3, "warrior");
                insertedRows = insertStmt.executeUpdate();
            }

            assertEquals(1, insertedRows);
            assertEquals(1, countUsersByName(conn, uniqueUserName));

            int deletedRows;
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                "DELETE FROM t_user WHERE user_name = ?"
            )) {
                deleteStmt.setString(1, uniqueUserName);
                deletedRows = deleteStmt.executeUpdate();
            }

            assertEquals(1, deletedRows);
            assertEquals(0, countUsersByName(conn, uniqueUserName));
        }
    }

    private static int countUsersByName(Connection conn, String userName) throws Exception {
        try (PreparedStatement queryStmt = conn.prepareStatement(
            "SELECT COUNT(*) FROM t_user WHERE user_name = ?"
        )) {
            queryStmt.setString(1, userName);

            try (ResultSet rs = queryStmt.executeQuery()) {
                assertTrue(rs.next());
                return rs.getInt(1);
            }
        }
    }

    private static String readEnvOrProperty(String propertyKey, String envKey, String defaultValue) {
        String fromProperty = System.getProperty(propertyKey);
        if (fromProperty != null && !fromProperty.trim().isEmpty()) {
            return fromProperty.trim();
        }

        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.trim().isEmpty()) {
            return fromEnv.trim();
        }

        return defaultValue;
    }
}
