package org.tinygame.valorlegend.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.valorlegend.model.User;
import org.tinygame.valorlegend.model.UserManager;
import org.tinygame.valorlegend.msg.GameMsgProtocol;

import java.util.Collection;

/**
 * Returns currently online users.
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
        Collection<User> userList = UserManager.listUser();

        for (User currUser : userList) {
            if (currUser == null) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState moveState = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState
                .newBuilder()
                .setFromPosX(currUser.moveState.fromPosX)
                .setFromPosY(currUser.moveState.fromPosY)
                .setToPosX(currUser.moveState.toPosX)
                .setToPosY(currUser.moveState.toPosY)
                .setStartTime(currUser.moveState.startTime)
                .build();

            GameMsgProtocol.WhoElseIsHereResult.UserInfo userInfo = GameMsgProtocol.WhoElseIsHereResult.UserInfo
                .newBuilder()
                .setUserId(currUser.userId)
                .setHeroAvatar(currUser.heroAvatar)
                .setMoveState(moveState)
                .build();

            resultBuilder.addUserInfo(userInfo);
        }

        ctx.writeAndFlush(resultBuilder.build());
    }
}
