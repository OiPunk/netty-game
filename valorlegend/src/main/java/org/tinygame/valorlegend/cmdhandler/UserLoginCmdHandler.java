package org.tinygame.valorlegend.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.valorlegend.login.LoginService;
import org.tinygame.valorlegend.model.User;
import org.tinygame.valorlegend.model.UserManager;
import org.tinygame.valorlegend.msg.GameMsgProtocol;

/**
 * Handles login commands.
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    private static final AttributeKey<Integer> USER_ID_KEY = AttributeKey.valueOf("userId");

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        if (userName == null || password == null) {
            return;
        }

        LoginService.getInstance().userLogin(userName, password, userEntity -> {
            GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

            if (userEntity == null) {
                resultBuilder.setUserId(-1).setUserName("").setHeroAvatar("");
            } else {
                User newUser = new User();
                newUser.userId = userEntity.userId;
                newUser.userName = userEntity.userName;
                newUser.heroAvatar = userEntity.heroAvatar;
                newUser.currHp = 100;
                UserManager.addUser(newUser);

                ctx.channel().attr(USER_ID_KEY).set(newUser.userId);

                resultBuilder
                    .setUserId(userEntity.userId)
                    .setUserName(userEntity.userName)
                    .setHeroAvatar(userEntity.heroAvatar);
            }

            ctx.writeAndFlush(resultBuilder.build());
            return null;
        });
    }
}
