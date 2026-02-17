package org.tinygame.valorlegend.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.valorlegend.Broadcaster;
import org.tinygame.valorlegend.model.User;
import org.tinygame.valorlegend.model.UserManager;
import org.tinygame.valorlegend.msg.GameMsgProtocol;

/**
 * Handles movement commands.
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    private static final AttributeKey<Integer> USER_ID_KEY = AttributeKey.valueOf("userId");

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        Integer userId = ctx.channel().attr(USER_ID_KEY).get();
        if (userId == null) {
            return;
        }

        User existUser = UserManager.getByUserId(userId);
        if (existUser == null) {
            return;
        }

        long nowTime = System.currentTimeMillis();

        existUser.moveState.fromPosX = cmd.getMoveFromPosX();
        existUser.moveState.fromPosY = cmd.getMoveFromPosY();
        existUser.moveState.toPosX = cmd.getMoveToPosX();
        existUser.moveState.toPosY = cmd.getMoveToPosY();
        existUser.moveState.startTime = nowTime;

        GameMsgProtocol.UserMoveToResult result = GameMsgProtocol.UserMoveToResult
            .newBuilder()
            .setMoveUserId(userId)
            .setMoveFromPosX(cmd.getMoveFromPosX())
            .setMoveFromPosY(cmd.getMoveFromPosY())
            .setMoveToPosX(cmd.getMoveToPosX())
            .setMoveToPosY(cmd.getMoveToPosY())
            .setMoveStartTime(nowTime)
            .build();

        Broadcaster.broadcast(result);
    }
}
