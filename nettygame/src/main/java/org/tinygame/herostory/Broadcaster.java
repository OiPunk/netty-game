package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播员
 */
public final class Broadcaster {
    /**
     * 信道组, 注意这里一定要用 static,
     * 否则无法实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化类默认构造器
     */
    private Broadcaster() {
    }

    /**
     * 添加客户端信道
     *
     * @param ch 客户端信道
     */
    static public void addChannel(Channel ch) {
        if (null != ch) {
            _channelGroup.add(ch);
        }
    }

    /**
     * 移除客户端信道
     *
     * @param ch 客户端信道
     */
    static public void removeChannel(Channel ch) {
        if (null != ch) {
            _channelGroup.remove(ch);
        }
    }

    /**
     * 广播消息
     *
     * @param msg 消息对象
     */
    static public void broadcast(Object msg) {
        if (null != msg) {
            _channelGroup.writeAndFlush(msg);
        }
    }
}
