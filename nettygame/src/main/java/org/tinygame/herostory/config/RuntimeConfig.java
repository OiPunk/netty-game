package org.tinygame.herostory.config;

/**
 * Runtime configuration sourced from JVM properties or environment variables.
 */
public final class RuntimeConfig {
    private RuntimeConfig() {
    }

    public static int serverPort() {
        return readInt("herostory.server.port", "HERO_STORY_SERVER_PORT", 12345);
    }

    public static boolean mysqlEnabled() {
        return readBoolean("herostory.mysql.enabled", "HERO_STORY_MYSQL_ENABLED", true);
    }

    public static String mysqlJdbcUrl() {
        return readString(
            "herostory.mysql.jdbc-url",
            "HERO_STORY_MYSQL_JDBC_URL",
            "jdbc:mysql://127.0.0.1:3306/hero_story?useSSL=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&autoReconnectForPools=true"
        );
    }

    public static String mysqlUsername() {
        return readString("herostory.mysql.username", "HERO_STORY_MYSQL_USERNAME", "root");
    }

    public static String mysqlPassword() {
        return readString("herostory.mysql.password", "HERO_STORY_MYSQL_PASSWORD", "ROOT");
    }

    public static boolean redisEnabled() {
        return readBoolean("herostory.redis.enabled", "HERO_STORY_REDIS_ENABLED", true);
    }

    public static String redisHost() {
        return readString("herostory.redis.host", "HERO_STORY_REDIS_HOST", "127.0.0.1");
    }

    public static int redisPort() {
        return readInt("herostory.redis.port", "HERO_STORY_REDIS_PORT", 6379);
    }

    public static boolean rocketMqEnabled() {
        return readBoolean("herostory.rocketmq.enabled", "HERO_STORY_ROCKETMQ_ENABLED", true);
    }

    public static String rocketMqNameServer() {
        return readString("herostory.rocketmq.namesrv", "HERO_STORY_ROCKETMQ_NAMESRV", "127.0.0.1:9876");
    }

    private static String readString(String propertyKey, String envKey, String defaultValue) {
        String fromProperty = System.getProperty(propertyKey);
        if (isNotBlank(fromProperty)) {
            return fromProperty.trim();
        }

        String fromEnv = System.getenv(envKey);
        if (isNotBlank(fromEnv)) {
            return fromEnv.trim();
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

    private static boolean readBoolean(String propertyKey, String envKey, boolean defaultValue) {
        String raw = readString(propertyKey, envKey, String.valueOf(defaultValue));

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

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
