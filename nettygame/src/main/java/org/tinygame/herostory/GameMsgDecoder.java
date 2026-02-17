package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket binary frame decoder for game protocol messages.
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (ctx == null || msg == null) {
            return;
        }

        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        try {
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = inputFrame.content();

            byteBuf.readShort();
            int msgCode = byteBuf.readShort();

            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            Message.Builder msgBuilder = GameMsgRecognizer.getMsgBuilderByMsgCode(msgCode);
            if (msgBuilder == null) {
                LOGGER.error("No message builder found for msgCode={}", msgCode);
                return;
            }

            msgBuilder.clear();
            msgBuilder.mergeFrom(msgBody);
            Message cmd = msgBuilder.build();

            if (cmd != null) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to decode incoming message", ex);
        }
    }
}
