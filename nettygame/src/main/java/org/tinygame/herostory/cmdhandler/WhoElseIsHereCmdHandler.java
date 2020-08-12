package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.Collection;

/**
 * 还有谁在场
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (null == ctx ||
            null == cmd) {
            return;
        }

        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        // 获取用户列表
        Collection<User> userList = UserManager.listUser();

        for (User currUser : userList) {
            if (null == currUser) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder
                userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currUser.userId);
            userInfoBuilder.setHeroAvatar(currUser.heroAvatar);

            // 构建移动状态
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder
                mvStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(currUser.moveState.fromPosX);
            mvStateBuilder.setFromPosY(currUser.moveState.fromPosY);
            mvStateBuilder.setToPosX(currUser.moveState.toPosX);
            mvStateBuilder.setToPosY(currUser.moveState.toPosY);
            mvStateBuilder.setStartTime(currUser.moveState.startTime);
            userInfoBuilder.setMoveState(mvStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }

        // 构建结果并发送
        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
