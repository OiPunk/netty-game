package org.tinygame.herostory;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tinygame.herostory.msg.GameMsgProtocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameMsgCodecTest {
    @BeforeAll
    static void initRecognizer() {
        GameMsgRecognizer.init();
    }

    @Test
    void shouldEncodeProtobufMessageIntoBinaryFrame() {
        EmbeddedChannel channel = new EmbeddedChannel(new GameMsgEncoder());

        GameMsgProtocol.UserMoveToCmd message = GameMsgProtocol.UserMoveToCmd
            .newBuilder()
            .setMoveFromPosX(10.0f)
            .setMoveFromPosY(20.0f)
            .setMoveToPosX(30.0f)
            .setMoveToPosY(40.0f)
            .build();

        assertTrue(channel.writeOutbound(message));

        BinaryWebSocketFrame frame = channel.readOutbound();
        assertEquals(message.toByteArray().length, frame.content().readUnsignedShort());
        assertEquals(GameMsgRecognizer.getMsgCodeByMsgClazz(GameMsgProtocol.UserMoveToCmd.class), frame.content().readUnsignedShort());

        frame.release();
        channel.finishAndReleaseAll();
    }

    @Test
    void shouldDecodeBinaryFrameIntoProtobufMessage() {
        GameMsgProtocol.UserMoveToCmd original = GameMsgProtocol.UserMoveToCmd
            .newBuilder()
            .setMoveFromPosX(1.25f)
            .setMoveFromPosY(2.5f)
            .setMoveToPosX(3.75f)
            .setMoveToPosY(5.0f)
            .build();

        EmbeddedChannel encoder = new EmbeddedChannel(new GameMsgEncoder());
        assertTrue(encoder.writeOutbound(original));
        BinaryWebSocketFrame encodedFrame = encoder.readOutbound();

        EmbeddedChannel decoder = new EmbeddedChannel(new GameMsgDecoder());
        assertTrue(decoder.writeInbound(encodedFrame.retain()));

        Object decoded = decoder.readInbound();
        assertInstanceOf(GameMsgProtocol.UserMoveToCmd.class, decoded);
        assertEquals(original, decoded);

        encodedFrame.release();
        encoder.finishAndReleaseAll();
        decoder.finishAndReleaseAll();
    }

    @Test
    void shouldPassThroughNonProtocolOutboundMessage() {
        EmbeddedChannel channel = new EmbeddedChannel(new GameMsgEncoder());

        assertTrue(channel.writeOutbound("plain-message"));
        assertEquals("plain-message", channel.readOutbound());

        channel.finishAndReleaseAll();
    }

    @Test
    void shouldIgnoreNonBinaryInboundFrame() {
        EmbeddedChannel channel = new EmbeddedChannel(new GameMsgDecoder());

        assertFalse(channel.writeInbound(new TextWebSocketFrame("hello")));
        assertNull(channel.readInbound());

        channel.finishAndReleaseAll();
    }
}
