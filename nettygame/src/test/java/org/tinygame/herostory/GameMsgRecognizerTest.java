package org.tinygame.herostory;

import com.google.protobuf.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tinygame.herostory.msg.GameMsgProtocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameMsgRecognizerTest {
    @BeforeAll
    static void initRecognizer() {
        GameMsgRecognizer.init();
    }

    @Test
    void shouldResolveMessageCodeByClass() {
        int msgCode = GameMsgRecognizer.getMsgCodeByMsgClazz(GameMsgProtocol.UserMoveToCmd.class);

        assertTrue(msgCode > 0);
    }

    @Test
    void shouldBuildMessageByCode() throws Exception {
        int msgCode = GameMsgRecognizer.getMsgCodeByMsgClazz(GameMsgProtocol.UserMoveToCmd.class);
        Message.Builder builder = GameMsgRecognizer.getMsgBuilderByMsgCode(msgCode);

        GameMsgProtocol.UserMoveToCmd source = GameMsgProtocol.UserMoveToCmd
            .newBuilder()
            .setMoveFromPosX(1.0f)
            .setMoveFromPosY(2.0f)
            .setMoveToPosX(3.0f)
            .setMoveToPosY(4.0f)
            .build();

        assertNotNull(builder);
        builder.mergeFrom(source.toByteArray());

        Message parsed = builder.build();
        assertInstanceOf(GameMsgProtocol.UserMoveToCmd.class, parsed);
        assertEquals(source, parsed);
    }

    @Test
    void shouldReturnDefaultsForUnknownInputs() {
        assertEquals(-1, GameMsgRecognizer.getMsgCodeByMsgClazz(null));
        assertNull(GameMsgRecognizer.getMsgBuilderByMsgCode(-1));
    }
}
