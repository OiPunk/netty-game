package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.tinygame.herostory.rank.RankItem;
import org.tinygame.herostory.rank.RankService;

import java.util.Collections;

/**
 * Handles rank-list query commands.
 */
public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        RankService.getInstance().getRank(rankItemList -> {
            if (rankItemList == null) {
                rankItemList = Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder resultBuilder = GameMsgProtocol.GetRankResult.newBuilder();

            for (RankItem rankItem : rankItemList) {
                if (rankItem == null) {
                    continue;
                }

                GameMsgProtocol.GetRankResult.RankItem rankProto = GameMsgProtocol.GetRankResult.RankItem
                    .newBuilder()
                    .setRankId(rankItem.rankId)
                    .setUserId(rankItem.userId)
                    .setUserName(rankItem.userName)
                    .setHeroAvatar(rankItem.heroAvatar)
                    .setWin(rankItem.win)
                    .build();

                resultBuilder.addRankItem(rankProto);
            }

            ctx.writeAndFlush(resultBuilder.build());
            return null;
        });
    }
}
