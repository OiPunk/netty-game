package org.tinygame.valorlegend.login.db;

/**
 * User persistence DAO.
 */
public interface IUserDao {
    UserEntity getByUserName(String userName);

    void insertInto(UserEntity newEntity);
}
