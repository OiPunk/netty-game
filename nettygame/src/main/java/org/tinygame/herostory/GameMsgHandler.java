package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Dispatches decoded messages to the main-thread processor.
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);
    private static final AttributeKey<Integer> USER_ID_KEY = AttributeKey.valueOf("userId");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }

        try {
            super.channelActive(ctx);
            Broadcaster.addChannel(ctx.channel());
        } catch (Exception ex) {
            LOGGER.error("Failed on channelActive", ex);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }

        try {
            super.handlerRemoved(ctx);
            Broadcaster.removeChannel(ctx.channel());

            Integer userId = ctx.channel().attr(USER_ID_KEY).get();
            if (userId == null) {
                return;
            }

            UserManager.removeByUserId(userId);

            GameMsgProtocol.UserQuitResult result = GameMsgProtocol.UserQuitResult
                .newBuilder()
                .setQuitUserId(userId)
                .build();

            Broadcaster.broadcast(result);
        } catch (Exception ex) {
            LOGGER.error("Failed on handlerRemoved", ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (ctx == null || msg == null) {
            return;
        }

        MainThreadProcessor.getInstance().process(ctx, msg);
    }
}
