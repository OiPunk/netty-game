package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.config.RuntimeConfig;

/**
 * RocketMQ producer helper.
 */
public final class MqProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqProducer.class);

    private static DefaultMQProducer producer;

    private MqProducer() {
    }

    public static void init() {
        if (!RuntimeConfig.rocketMqEnabled()) {
            LOGGER.info("RocketMQ producer is disabled");
            return;
        }

        try {
            DefaultMQProducer newProducer = new DefaultMQProducer("herostory");
            newProducer.setNamesrvAddr(RuntimeConfig.rocketMqNameServer());
            newProducer.start();
            newProducer.setRetryTimesWhenSendAsyncFailed(3);
            producer = newProducer;

            LOGGER.info("RocketMQ producer initialized");
        } catch (Exception ex) {
            LOGGER.error("Failed to initialize RocketMQ producer", ex);
        }
    }

    public static void sendMsg(String topic, Object msg) {
        if (topic == null || msg == null || producer == null) {
            return;
        }

        Message newMsg = new Message();
        newMsg.setTopic(topic);
        newMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            producer.send(newMsg);
        } catch (Exception ex) {
            LOGGER.error("Failed to send MQ message", ex);
        }
    }
}
