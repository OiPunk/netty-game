package org.tinygame.valorlegend;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.valorlegend.config.RuntimeConfig;

import java.io.InputStream;
import java.util.Properties;

/**
 * Shared MyBatis session factory.
 */
public final class MySqlSessionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlSessionFactory.class);

    private static SqlSessionFactory sqlSessionFactory;

    private MySqlSessionFactory() {
    }

    public static void init() {
        if (!RuntimeConfig.mysqlEnabled()) {
            LOGGER.info("MySQL integration is disabled");
            return;
        }

        try (InputStream input = Resources.getResourceAsStream("MyBatisConfig.xml")) {
            Properties properties = new Properties();
            properties.setProperty("mysql.jdbc.url", RuntimeConfig.mysqlJdbcUrl());
            properties.setProperty("mysql.username", RuntimeConfig.mysqlUsername());
            properties.setProperty("mysql.password", RuntimeConfig.mysqlPassword());

            sqlSessionFactory = new SqlSessionFactoryBuilder().build(input, properties);

            try (SqlSession tempSession = openSession()) {
                tempSession.getConnection().createStatement().execute("SELECT 1");
            }

            LOGGER.info("MySQL connection check succeeded");
        } catch (Exception ex) {
            LOGGER.error("Failed to initialize MySQL session factory", ex);
        }
    }

    public static SqlSession openSession() {
        if (sqlSessionFactory == null) {
            throw new IllegalStateException("MySQL session factory has not been initialized");
        }

        return sqlSessionFactory.openSession(true);
    }
}
