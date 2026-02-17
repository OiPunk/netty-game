package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.config.RuntimeConfig;
import org.tinygame.herostory.rank.RankService;

import java.util.List;

/**
 * RocketMQ consumer helper.
 */
public final class MqConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqConsumer.class);

    private MqConsumer() {
    }

    public static void init() {
        if (!RuntimeConfig.rocketMqEnabled()) {
            LOGGER.info("RocketMQ consumer is disabled");
            return;
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");
        consumer.setNamesrvAddr(RuntimeConfig.rocketMqNameServer());

        try {
            consumer.subscribe("herostory_victor", "*");
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgExtList, ConsumeConcurrentlyContext ctx) {
                    for (MessageExt msgExt : msgExtList) {
                        if (msgExt == null) {
                            continue;
                        }

                        VictorMsg victorMsg = JSONObject.parseObject(msgExt.getBody(), VictorMsg.class);
                        LOGGER.info("Victory event received: winnerId={}, loserId={}", victorMsg.winnerId, victorMsg.loserId);

                        RankService.getInstance().refreshRank(victorMsg.winnerId, victorMsg.loserId);
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();
            LOGGER.info("RocketMQ consumer initialized");
        } catch (Exception ex) {
            LOGGER.error("Failed to initialize RocketMQ consumer", ex);
        }
    }
}
