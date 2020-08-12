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
 * 登陆服务
 */
public final class LoginService {
    /**
     * 日志服务
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    /**
     * 单例对象
     */
    static private final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆
     *
     * @param userName 用户名称
     * @param password 密码
     * @param callback 回调函数
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName ||
            null == password) {
            return;
        }

        AsyncOperationProcessor.getInstance().process(new AsyncGetUserEntity(userName, password) {
            @Override
            public void doFinish() {
                if (null != callback) {
                    callback.apply(this.getUserEntity());
                }
            }
        });
    }

    /**
     * 更新 Redis 中的用户基本信息
     *
     * @param userEntity 用户实体
     */
    private void updateBasicInfoInRedis(UserEntity userEntity) {
        if (null == userEntity) {
            return;
        }

        try (Jedis redis = RedisUtil.getJedis()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userName", userEntity.userName);
            jsonObj.put("heroAvatar", userEntity.heroAvatar);

            redis.hset("User_" + userEntity.userId, "BasicInfo", jsonObj.toJSONString());
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 异步方式获取用户实体
     */
    static private class AsyncGetUserEntity implements IAsyncOperation {
        /**
         * 用户名称
         */
        private final String _userName;

        /**
         * 密码
         */
        private final String _password;

        /**
         * 用户实体
         */
        private UserEntity _userEntity;

        /**
         * 类参数构造器
         *
         * @param userName 用户名称
         * @param password 密码
         */
        AsyncGetUserEntity(String userName, String password) {
            _userName = userName;
            _password = password;
        }

        /**
         * 获取用户实体
         *
         * @return 用户实体
         */
        UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public int getBindId() {
            if (null == _userName) {
                return 0;
            } else {
                return _userName.charAt(_userName.length() - 1);
            }
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                // 获取 DAO
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);
                // 获取用户实体
                UserEntity userEntity = dao.getByUserName(_userName);

                if (null != userEntity) {
                    if (!_password.equals(userEntity.password)) {
                        throw new RuntimeException("密码错误");
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman";

                    dao.insertInto(userEntity);
                }

                LoginService.getInstance().updateBasicInfoInRedis(userEntity);
                _userEntity = userEntity;
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
