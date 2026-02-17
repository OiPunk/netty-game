package org.tinygame.herostory.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeConfigTest {
    @AfterEach
    void tearDown() {
        System.clearProperty("valorlegend.server.port");
        System.clearProperty("valorlegend.redis.port");
        System.clearProperty("valorlegend.mysql.enabled");
        System.clearProperty("valorlegend.redis.enabled");
        System.clearProperty("valorlegend.rocketmq.enabled");
        System.clearProperty("valorlegend.mysql.jdbc-url");
        System.clearProperty("valorlegend.mysql.username");
        System.clearProperty("herostory.server.port");
        System.clearProperty("herostory.redis.port");
        System.clearProperty("herostory.mysql.enabled");
        System.clearProperty("herostory.redis.enabled");
        System.clearProperty("herostory.rocketmq.enabled");
        System.clearProperty("herostory.mysql.jdbc-url");
        System.clearProperty("herostory.mysql.username");
    }

    @Test
    void shouldPreferSystemPropertiesWhenPresent() {
        System.setProperty("valorlegend.server.port", "19090");
        System.setProperty("valorlegend.mysql.jdbc-url", "jdbc:mysql://mysql:3306/valor_legend");

        assertEquals(19090, RuntimeConfig.serverPort());
        assertEquals("jdbc:mysql://mysql:3306/valor_legend", RuntimeConfig.mysqlJdbcUrl());
    }

    @Test
    void shouldSupportLegacyPropertyKeys() {
        System.setProperty("herostory.server.port", "18080");
        System.setProperty("herostory.mysql.jdbc-url", "jdbc:mysql://mysql:3306/legacy_db");

        assertEquals(18080, RuntimeConfig.serverPort());
        assertEquals("jdbc:mysql://mysql:3306/legacy_db", RuntimeConfig.mysqlJdbcUrl());
    }

    @Test
    void shouldFallbackToDefaultForInvalidInt() {
        System.setProperty("valorlegend.redis.port", "invalid");

        assertEquals(6379, RuntimeConfig.redisPort());
    }

    @Test
    void shouldParseBooleanAliases() {
        System.setProperty("valorlegend.mysql.enabled", "yes");
        System.setProperty("valorlegend.redis.enabled", "0");
        System.setProperty("valorlegend.rocketmq.enabled", "off");

        assertTrue(RuntimeConfig.mysqlEnabled());
        assertFalse(RuntimeConfig.redisEnabled());
        assertFalse(RuntimeConfig.rocketMqEnabled());
    }

    @Test
    void shouldFallbackToDefaultsWhenPropertiesAreBlank() {
        System.setProperty("valorlegend.mysql.jdbc-url", "   ");
        System.setProperty("valorlegend.mysql.username", "\t");

        assertTrue(RuntimeConfig.mysqlJdbcUrl().startsWith("jdbc:mysql://127.0.0.1:3306/valor_legend"));
        assertEquals("root", RuntimeConfig.mysqlUsername());
    }

    @Test
    void shouldFallbackToDefaultForUnrecognizedBooleanValue() {
        System.setProperty("valorlegend.mysql.enabled", "sometimes");

        assertTrue(RuntimeConfig.mysqlEnabled());
    }
}
