package org.tinygame.herostory.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeConfigTest {
    @AfterEach
    void tearDown() {
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
        System.setProperty("herostory.server.port", "19090");
        System.setProperty("herostory.mysql.jdbc-url", "jdbc:mysql://mysql:3306/hero_story");

        assertEquals(19090, RuntimeConfig.serverPort());
        assertEquals("jdbc:mysql://mysql:3306/hero_story", RuntimeConfig.mysqlJdbcUrl());
    }

    @Test
    void shouldFallbackToDefaultForInvalidInt() {
        System.setProperty("herostory.redis.port", "invalid");

        assertEquals(6379, RuntimeConfig.redisPort());
    }

    @Test
    void shouldParseBooleanAliases() {
        System.setProperty("herostory.mysql.enabled", "yes");
        System.setProperty("herostory.redis.enabled", "0");
        System.setProperty("herostory.rocketmq.enabled", "off");

        assertTrue(RuntimeConfig.mysqlEnabled());
        assertFalse(RuntimeConfig.redisEnabled());
        assertFalse(RuntimeConfig.rocketMqEnabled());
    }

    @Test
    void shouldFallbackToDefaultsWhenPropertiesAreBlank() {
        System.setProperty("herostory.mysql.jdbc-url", "   ");
        System.setProperty("herostory.mysql.username", "\t");

        assertTrue(RuntimeConfig.mysqlJdbcUrl().startsWith("jdbc:mysql://127.0.0.1:3306/hero_story"));
        assertEquals("root", RuntimeConfig.mysqlUsername());
    }

    @Test
    void shouldFallbackToDefaultForUnrecognizedBooleanValue() {
        System.setProperty("herostory.mysql.enabled", "sometimes");

        assertTrue(RuntimeConfig.mysqlEnabled());
    }
}
