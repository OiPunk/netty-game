package org.tinygame.herostory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MySql 会话工厂
 */
public final class MySqlSessionFactory {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MySqlSessionFactory.class);

    /**
     * MyBatis Sql 会话工厂
     */
    static private SqlSessionFactory _sqlSessionFactory;

    /**
     * 私有化类默认构造器
     */
    private MySqlSessionFactory() {
    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            _sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(
                Resources.getResourceAsStream("MyBatisConfig.xml")
            );

            // 测试数据库连接
            SqlSession tempSession = openSession();

            tempSession.getConnection()
                .createStatement()
                .execute("SELECT -1");

            tempSession.close();

            LOGGER.error("MySql 数据库连接测试成功!");
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 创建 MySql 会话
     *
     * @return MySql 会话
     */
    static public SqlSession openSession() {
        if (null == _sqlSessionFactory) {
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }
}
