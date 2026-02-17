package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket binary frame encoder for game protocol messages.
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (ctx == null || msg == null) {
            return;
        }

        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            int msgCode = GameMsgRecognizer.getMsgCodeByMsgClazz(msg.getClass());
            if (msgCode == -1) {
                LOGGER.error("Unsupported outbound message class={}", msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }

            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();
            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeShort((short) msgBody.length);
            byteBuf.writeShort((short) msgCode);
            byteBuf.writeBytes(msgBody);

            super.write(ctx, new BinaryWebSocketFrame(byteBuf), promise);
        } catch (Exception ex) {
            LOGGER.error("Failed to encode outgoing message", ex);
        }
    }
}
