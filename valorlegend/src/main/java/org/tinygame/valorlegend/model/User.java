package org.tinygame.valorlegend.model;

/**
 * Online user session model.
 */
public class User {
    public int userId;
    public String userName;
    public String heroAvatar;
    public int currHp;
    public final MoveState moveState = new MoveState();
}
