package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户移动到 ( 目标位置 )
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (null == ctx ||
            null == cmd) {
            return;
        }

        // 获取用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userId) {
            return;
        }

        // 获取已有用户
        User existUser = UserManager.getByUserId(userId);

        if (null == existUser) {
            return;
        }

        long nowTime = System.currentTimeMillis();

        existUser.moveState.fromPosX = cmd.getMoveFromPosX();
        existUser.moveState.fromPosY = cmd.getMoveFromPosY();
        existUser.moveState.toPosX = cmd.getMoveToPosX();
        existUser.moveState.toPosY = cmd.getMoveToPosY();
        existUser.moveState.startTime = nowTime;

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
        resultBuilder.setMoveFromPosY(cmd.getMoveFromPosY());
        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
        resultBuilder.setMoveStartTime(nowTime);

        // 构建结果并广播
        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
