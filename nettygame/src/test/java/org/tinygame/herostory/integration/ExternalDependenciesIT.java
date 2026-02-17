package org.tinygame.herostory.integration;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
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
            "jdbc:mysql://127.0.0.1:3307/hero_story?useSSL=false&useUnicode=true&characterEncoding=UTF-8"
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
