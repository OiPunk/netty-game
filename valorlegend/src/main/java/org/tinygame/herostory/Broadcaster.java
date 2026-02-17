package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Shared broadcaster over all connected channels.
 */
public final class Broadcaster {
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Broadcaster() {
    }

    public static void addChannel(Channel ch) {
        if (ch != null) {
            CHANNEL_GROUP.add(ch);
        }
    }

    public static void removeChannel(Channel ch) {
        if (ch != null) {
            CHANNEL_GROUP.remove(ch);
        }
    }

    public static void broadcast(Object msg) {
        if (msg != null) {
            CHANNEL_GROUP.writeAndFlush(msg);
        }
    }

    static int countChannels() {
        return CHANNEL_GROUP.size();
    }
}
