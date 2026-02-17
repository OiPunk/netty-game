package org.tinygame.valorlegend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.valorlegend.config.RuntimeConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis utility.
 */
public final class RedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    private static JedisPool jedisPool;

    private RedisUtil() {
    }

    public static void init() {
        if (!RuntimeConfig.redisEnabled()) {
            LOGGER.info("Redis integration is disabled");
            return;
        }

        try {
            jedisPool = new JedisPool(RuntimeConfig.redisHost(), RuntimeConfig.redisPort());
            LOGGER.info("Redis connection pool initialized");
        } catch (Exception ex) {
            LOGGER.error("Failed to initialize Redis connection pool", ex);
        }
    }

    public static Jedis getJedis() {
        if (jedisPool == null) {
            throw new IllegalStateException("Redis connection pool has not been initialized");
        }

        return jedisPool.getResource();
    }
}
