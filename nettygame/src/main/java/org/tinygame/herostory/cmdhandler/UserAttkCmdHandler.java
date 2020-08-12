package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.mq.MqProducer;
import org.tinygame.herostory.mq.VictorMsg;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户攻击命令处理器
 */
public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (null == ctx ||
            null == cmd) {
            return;
        }

        // 获取攻击用户 Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == attkUserId) {
            return;
        }

        // 获取目标用户 Id
        int targetUserId = cmd.getTargetUserId();
        // 获取目标用户
        User targetUser = UserManager.getByUserId(targetUserId);

        if (null == targetUser) {
            broadcastAttkResult(attkUserId, -1);
            return;
        }

        final int dmgPoint = 10;
        targetUser.currHp = targetUser.currHp - dmgPoint;

        // 广播攻击结果
        broadcastAttkResult(attkUserId, targetUserId);
        // 广播减血结果
        broadcastSubtractHpResult(targetUserId, dmgPoint);

        if (targetUser.currHp <= 0) {
            // 广播死亡结果
            broadcastDieResult(targetUserId);

            VictorMsg newMsg = new VictorMsg();
            newMsg.winnerId = attkUserId;
            newMsg.loserId = targetUserId;
            MqProducer.sendMsg("herostory_victor", newMsg);
        }
    }

    /**
     * 广播攻击结果
     *
     * @param attkUserId   攻击用户 Id
     * @param targetUserId 目标用户 Id
     */
    static private void broadcastAttkResult(int attkUserId, int targetUserId) {
        if (attkUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播减血结果
     *
     * @param targetUserId 目标用户 Id
     * @param subtractHp   减血量 ( 必须 > 0 )
     */
    static private void broadcastSubtractHpResult(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 ||
            subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播死亡结果
     *
     * @param targetUserId 目标用户 Id
     */
    static private void broadcastDieResult(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
