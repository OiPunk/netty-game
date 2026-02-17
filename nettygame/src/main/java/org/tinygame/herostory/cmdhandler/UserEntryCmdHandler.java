package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * Handles user entry events.
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    private static final AttributeKey<Integer> USER_ID_KEY = AttributeKey.valueOf("userId");

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
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

        GameMsgProtocol.UserEntryResult result = GameMsgProtocol.UserEntryResult
            .newBuilder()
            .setUserId(userId)
            .setUserName(existUser.userName)
            .setHeroAvatar(existUser.heroAvatar)
            .build();

        Broadcaster.broadcast(result);
    }
}
