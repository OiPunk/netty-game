package org.tinygame.herostory.login.db;

/**
 * User persistence DAO.
 */
public interface IUserDao {
    UserEntity getByUserName(String userName);

    void insertInto(UserEntity newEntity);
}
