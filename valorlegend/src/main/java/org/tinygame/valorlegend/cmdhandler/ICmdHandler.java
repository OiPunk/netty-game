package org.tinygame.valorlegend.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * Generic command handler contract.
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * Handle one command message.
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
