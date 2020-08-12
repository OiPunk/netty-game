package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户入场
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (null == ctx ||
            null == cmd) {
            return;
        }

        // 获取用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userId) {
            return;
        }

        User existUser = UserManager.getByUserId(userId);

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setUserName(existUser.userName);
        resultBuilder.setHeroAvatar(existUser.heroAvatar);

        // 构建结果并广播
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
