package org.tinygame.herostory.login;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

/**
 * User login service.
 */
public final class LoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
    private static final LoginService INSTANCE = new LoginService();

    private LoginService() {
    }

    public static LoginService getInstance() {
        return INSTANCE;
    }

    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (userName == null || userName.isEmpty() || password == null || password.isEmpty()) {
            return;
        }

        AsyncOperationProcessor.getInstance().process(new AsyncGetUserEntity(userName, password) {
            @Override
            public void doFinish() {
                if (callback != null) {
                    callback.apply(this.getUserEntity());
                }
            }
        });
    }

    private void updateBasicInfoInRedis(UserEntity userEntity) {
        if (userEntity == null) {
            return;
        }

        try (Jedis redis = RedisUtil.getJedis()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userName", userEntity.userName);
            jsonObj.put("heroAvatar", userEntity.heroAvatar);

            redis.hset("User_" + userEntity.userId, "BasicInfo", jsonObj.toJSONString());
        } catch (Exception ex) {
            LOGGER.error("Failed to cache user basic info", ex);
        }
    }

    private static class AsyncGetUserEntity implements IAsyncOperation {
        private final String userName;
        private final String password;

        private UserEntity userEntity;

        AsyncGetUserEntity(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        UserEntity getUserEntity() {
            return userEntity;
        }

        @Override
        public int getBindId() {
            if (userName == null || userName.isEmpty()) {
                return 0;
            }

            return userName.charAt(userName.length() - 1);
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);
                UserEntity loadedUser = dao.getByUserName(userName);

                if (loadedUser != null) {
                    if (!password.equals(loadedUser.password)) {
                        throw new RuntimeException("Invalid password");
                    }
                } else {
                    loadedUser = new UserEntity();
                    loadedUser.userName = userName;
                    loadedUser.password = password;
                    loadedUser.heroAvatar = "Hero_Shaman";

                    dao.insertInto(loadedUser);
                }

                LoginService.getInstance().updateBasicInfoInRedis(loadedUser);
                userEntity = loadedUser;
            } catch (Exception ex) {
                LOGGER.error("Failed to fetch user entity", ex);
            }
        }
    }
}
