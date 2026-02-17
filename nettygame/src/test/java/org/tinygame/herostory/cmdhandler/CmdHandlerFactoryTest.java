package org.tinygame.herostory.cmdhandler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.tinygame.herostory.msg.GameMsgProtocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CmdHandlerFactoryTest {
    @BeforeAll
    static void initFactory() {
        CmdHandlerFactory.init();
    }

    @Test
    void shouldResolveKnownCommandHandlers() {
        ICmdHandler<?> moveHandler = CmdHandlerFactory.create(GameMsgProtocol.UserMoveToCmd.class);
        ICmdHandler<?> rankHandler = CmdHandlerFactory.create(GameMsgProtocol.GetRankCmd.class);

        assertEquals(UserMoveToCmdHandler.class, moveHandler.getClass());
        assertEquals(GetRankCmdHandler.class, rankHandler.getClass());
    }

    @Test
    void shouldReturnNullForUnknownCommandClass() {
        assertNull(CmdHandlerFactory.create(String.class));
        assertNull(CmdHandlerFactory.create(null));
    }

    @Test
    void shouldResolveWhoElseHandler() {
        ICmdHandler<?> handler = CmdHandlerFactory.create(GameMsgProtocol.WhoElseIsHereCmd.class);

        assertTrue(handler instanceof WhoElseIsHereCmdHandler);
    }
}
