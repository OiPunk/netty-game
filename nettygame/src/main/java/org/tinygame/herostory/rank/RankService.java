package org.tinygame.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Ranking service backed by Redis.
 */
public final class RankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RankService.class);
    private static final RankService INSTANCE = new RankService();

    private RankService() {
    }

    public static RankService getInstance() {
        return INSTANCE;
    }

    public void getRank(Function<List<RankItem>, Void> callback) {
        if (callback == null) {
            return;
        }

        AsyncOperationProcessor.getInstance().process(new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(getRankItemList());
            }
        });
    }

    public void refreshRank(int winnerId, int loserId) {
        if (winnerId <= 0 || loserId <= 0) {
            return;
        }

        try (Jedis redis = RedisUtil.getJedis()) {
            redis.hincrBy("User_" + winnerId, "Win", 1);
            redis.hincrBy("User_" + loserId, "Lose", 1);

            String winStr = redis.hget("User_" + winnerId, "Win");
            int winNum = Integer.parseInt(winStr);

            redis.zadd("Rank", winNum, String.valueOf(winnerId));
        } catch (Exception ex) {
            LOGGER.error("Failed to refresh rank", ex);
        }
    }

    private static class AsyncGetRank implements IAsyncOperation {
        private List<RankItem> rankItemList;

        List<RankItem> getRankItemList() {
            return rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getJedis()) {
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);
                List<RankItem> newRankItemList = new ArrayList<>();
                int i = 0;

                for (Tuple tuple : valSet) {
                    if (tuple == null) {
                        continue;
                    }

                    int userId = Integer.parseInt(tuple.getElement());
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (jsonStr == null) {
                        continue;
                    }

                    RankItem newItem = new RankItem();
                    newItem.rankId = ++i;
                    newItem.userId = userId;
                    newItem.win = (int) tuple.getScore();

                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                    newItem.userName = jsonObj.getString("userName");
                    newItem.heroAvatar = jsonObj.getString("heroAvatar");

                    newRankItemList.add(newItem);
                }

                rankItemList = newRankItemList;
            } catch (Exception ex) {
                LOGGER.error("Failed to load ranking data", ex);
            }
        }
    }
}
