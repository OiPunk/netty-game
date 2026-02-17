package org.tinygame.herostory;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.config.RuntimeConfig;
import org.tinygame.herostory.mq.MqConsumer;
import org.tinygame.herostory.util.RedisUtil;

/**
 * Ranking worker bootstrap.
 */
public final class RankApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(RankApp.class);

    private RankApp() {
    }

    public static void main(String[] argvArray) {
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        if (RuntimeConfig.redisEnabled()) {
            RedisUtil.init();
        } else {
            LOGGER.info("Redis integration is disabled");
        }

        if (RuntimeConfig.rocketMqEnabled()) {
            MqConsumer.init();
            LOGGER.info("Ranking worker started");
        } else {
            LOGGER.info("RocketMQ consumer is disabled. Ranking worker is idle.");
        }
    }
}
