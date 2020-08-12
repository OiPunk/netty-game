package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户登陆
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx ||
            null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        if (null == userName ||
            null == password) {
            return;
        }

        // 获取用户实体
        LoginService.getInstance().userLogin(userName, password, (userEntity) -> {
            GameMsgProtocol.UserLoginResult.Builder
                resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

            if (null == userEntity) {
                resultBuilder.setUserId(-1);
                resultBuilder.setUserName("");
                resultBuilder.setHeroAvatar("");
            } else {
                User newUser = new User();
                newUser.userId = userEntity.userId;
                newUser.userName = userEntity.userName;
                newUser.heroAvatar = userEntity.heroAvatar;
                newUser.currHp = 100;
                UserManager.addUser(newUser);

                // 将用户 Id 保存至 Session
                ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.userId);

                resultBuilder.setUserId(userEntity.userId);
                resultBuilder.setUserName(userEntity.userName);
                resultBuilder.setHeroAvatar(userEntity.heroAvatar);
            }

            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);

            return null;
        });
    }
}
