package org.tinygame.herostory;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.mq.MqConsumer;
import org.tinygame.herostory.util.RedisUtil;

/**
 * 排行榜应用程序
 */
public class RankApp {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RankApp.class);

    /**
     * 应用主函数
     *
     * @param argvArray 参数数组
     */
    static public void main(String[] argvArray) {
        // 设置 log4j 属性文件
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 初始化 Redis
        RedisUtil.init();
        // 初始化消息队列
        MqConsumer.init();

        LOGGER.info(">>> 排行榜应用启动成功! <<<");
    }
}
