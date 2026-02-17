package org.tinygame.valorlegend.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.valorlegend.Broadcaster;
import org.tinygame.valorlegend.model.User;
import org.tinygame.valorlegend.model.UserManager;
import org.tinygame.valorlegend.mq.MqProducer;
import org.tinygame.valorlegend.mq.VictorMsg;
import org.tinygame.valorlegend.msg.GameMsgProtocol;

/**
 * Handles attack commands.
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    private static final AttributeKey<Integer> USER_ID_KEY = AttributeKey.valueOf("userId");

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        Integer attkUserId = ctx.channel().attr(USER_ID_KEY).get();
        if (attkUserId == null) {
            return;
        }

        int targetUserId = cmd.getTargetUserId();
        User targetUser = UserManager.getByUserId(targetUserId);

        if (targetUser == null) {
            broadcastAttkResult(attkUserId, -1);
            return;
        }

        final int dmgPoint = 10;
        targetUser.currHp = targetUser.currHp - dmgPoint;

        broadcastAttkResult(attkUserId, targetUserId);
        broadcastSubtractHpResult(targetUserId, dmgPoint);

        if (targetUser.currHp <= 0) {
            broadcastDieResult(targetUserId);

            VictorMsg newMsg = new VictorMsg();
            newMsg.winnerId = attkUserId;
            newMsg.loserId = targetUserId;
            MqProducer.sendMsg("valorlegend_victor", newMsg);
        }
    }

    private static void broadcastAttkResult(int attkUserId, int targetUserId) {
        if (attkUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserAttkResult result = GameMsgProtocol.UserAttkResult
            .newBuilder()
            .setAttkUserId(attkUserId)
            .setTargetUserId(targetUserId)
            .build();

        Broadcaster.broadcast(result);
    }

    private static void broadcastSubtractHpResult(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult result = GameMsgProtocol.UserSubtractHpResult
            .newBuilder()
            .setTargetUserId(targetUserId)
            .setSubtractHp(subtractHp)
            .build();

        Broadcaster.broadcast(result);
    }

    private static void broadcastDieResult(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserDieResult result = GameMsgProtocol.UserDieResult
            .newBuilder()
            .setTargetUserId(targetUserId)
            .build();

        Broadcaster.broadcast(result);
    }
}
