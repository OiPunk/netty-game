package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义的消息解码器
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx ||
            null == msg) {
            return;
        }

        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        try {
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = inputFrame.content();

            byteBuf.readShort(); // 读取消息的长度
            int msgCode = byteBuf.readShort(); // 读取消息编号

            // 拿到消息体
            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            // 获取消息构建器
            Message.Builder msgBuilder = GameMsgRecognizer.getMsgBuilderByMsgCode(msgCode);

            if (null == msgBuilder) {
                LOGGER.error(
                    "未找到消息构建器, msgCode = {}",
                    msgCode
                );
                return;
            }

            msgBuilder.clear();
            msgBuilder.mergeFrom(msgBody);

            // 构建消息实体
            Message cmd = msgBuilder.build();

            if (null != cmd) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
