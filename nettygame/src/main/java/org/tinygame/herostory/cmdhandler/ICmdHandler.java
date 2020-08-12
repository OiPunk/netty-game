package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 命令处理器接口
 *
 * @param <TCmd>
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理命令
     *
     * @param ctx
     * @param cmd
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
