package org.tinygame.valorlegend.config;

/**
 * Runtime configuration sourced from JVM properties or environment variables.
 */
public final class RuntimeConfig {
    private RuntimeConfig() {
    }

    public static int serverPort() {
        return readInt(
            "valorlegend.server.port",
            "VALOR_LEGEND_SERVER_PORT",
            "herostory.server.port",
            "HERO_STORY_SERVER_PORT",
            12345
        );
    }

    public static boolean mysqlEnabled() {
        return readBoolean(
            "valorlegend.mysql.enabled",
            "VALOR_LEGEND_MYSQL_ENABLED",
            "herostory.mysql.enabled",
            "HERO_STORY_MYSQL_ENABLED",
            true
        );
    }

    public static String mysqlJdbcUrl() {
        return readString(
            "valorlegend.mysql.jdbc-url",
            "VALOR_LEGEND_MYSQL_JDBC_URL",
            "herostory.mysql.jdbc-url",
            "HERO_STORY_MYSQL_JDBC_URL",
            "jdbc:mysql://127.0.0.1:3306/valor_legend?useSSL=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&autoReconnectForPools=true"
        );
    }

    public static String mysqlUsername() {
        return readString(
            "valorlegend.mysql.username",
            "VALOR_LEGEND_MYSQL_USERNAME",
            "herostory.mysql.username",
            "HERO_STORY_MYSQL_USERNAME",
            "root"
        );
    }

    public static String mysqlPassword() {
        return readString(
            "valorlegend.mysql.password",
            "VALOR_LEGEND_MYSQL_PASSWORD",
            "herostory.mysql.password",
            "HERO_STORY_MYSQL_PASSWORD",
            "ROOT"
        );
    }

    public static boolean redisEnabled() {
        return readBoolean(
            "valorlegend.redis.enabled",
            "VALOR_LEGEND_REDIS_ENABLED",
            "herostory.redis.enabled",
            "HERO_STORY_REDIS_ENABLED",
            true
        );
    }

    public static String redisHost() {
        return readString(
            "valorlegend.redis.host",
            "VALOR_LEGEND_REDIS_HOST",
            "herostory.redis.host",
            "HERO_STORY_REDIS_HOST",
            "127.0.0.1"
        );
    }

    public static int redisPort() {
        return readInt(
            "valorlegend.redis.port",
            "VALOR_LEGEND_REDIS_PORT",
            "herostory.redis.port",
            "HERO_STORY_REDIS_PORT",
            6379
        );
    }

    public static boolean rocketMqEnabled() {
        return readBoolean(
            "valorlegend.rocketmq.enabled",
            "VALOR_LEGEND_ROCKETMQ_ENABLED",
            "herostory.rocketmq.enabled",
            "HERO_STORY_ROCKETMQ_ENABLED",
            true
        );
    }

    public static String rocketMqNameServer() {
        return readString(
            "valorlegend.rocketmq.namesrv",
            "VALOR_LEGEND_ROCKETMQ_NAMESRV",
            "herostory.rocketmq.namesrv",
            "HERO_STORY_ROCKETMQ_NAMESRV",
            "127.0.0.1:9876"
        );
    }

    private static String readString(String propertyKey, String envKey, String defaultValue) {
        return readString(propertyKey, envKey, null, null, defaultValue);
    }

    private static String readString(
        String propertyKey,
        String envKey,
        String legacyPropertyKey,
        String legacyEnvKey,
        String defaultValue
    ) {
        String fromProperty = readProperty(propertyKey);
        if (isNotBlank(fromProperty)) {
            return fromProperty.trim();
        }

        String fromLegacyProperty = readProperty(legacyPropertyKey);
        if (isNotBlank(fromLegacyProperty)) {
            return fromLegacyProperty.trim();
        }

        String fromEnv = readEnv(envKey);
        if (isNotBlank(fromEnv)) {
            return fromEnv.trim();
        }

        String fromLegacyEnv = readEnv(legacyEnvKey);
        if (isNotBlank(fromLegacyEnv)) {
            return fromLegacyEnv.trim();
        }

        return defaultValue;
    }

    private static int readInt(String propertyKey, String envKey, int defaultValue) {
        String raw = readString(propertyKey, envKey, String.valueOf(defaultValue));

        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static int readInt(
        String propertyKey,
        String envKey,
        String legacyPropertyKey,
        String legacyEnvKey,
        int defaultValue
    ) {
        String raw = readString(propertyKey, envKey, legacyPropertyKey, legacyEnvKey, String.valueOf(defaultValue));

        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static boolean readBoolean(String propertyKey, String envKey, boolean defaultValue) {
        String raw = readString(propertyKey, envKey, String.valueOf(defaultValue));
        return parseBooleanOrDefault(raw, defaultValue);
    }

    private static boolean readBoolean(
        String propertyKey,
        String envKey,
        String legacyPropertyKey,
        String legacyEnvKey,
        boolean defaultValue
    ) {
        String raw = readString(propertyKey, envKey, legacyPropertyKey, legacyEnvKey, String.valueOf(defaultValue));
        return parseBooleanOrDefault(raw, defaultValue);
    }

    private static boolean parseBooleanOrDefault(String raw, boolean defaultValue) {
        if ("1".equals(raw)) {
            return true;
        }

        if ("0".equals(raw)) {
            return false;
        }

        if ("true".equalsIgnoreCase(raw) || "yes".equalsIgnoreCase(raw) || "on".equalsIgnoreCase(raw)) {
            return true;
        }

        if ("false".equalsIgnoreCase(raw) || "no".equalsIgnoreCase(raw) || "off".equalsIgnoreCase(raw)) {
            return false;
        }

        return defaultValue;
    }

    private static String readProperty(String key) {
        if (!isNotBlank(key)) {
            return null;
        }

        return System.getProperty(key);
    }

    private static String readEnv(String key) {
        if (!isNotBlank(key)) {
            return null;
        }

        return System.getenv(key);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
