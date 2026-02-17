package org.tinygame.valorlegend.model;

/**
 * Movement snapshot for interpolation on clients.
 */
public class MoveState {
    public float fromPosX;
    public float fromPosY;
    public float toPosX;
    public float toPosY;
    public long startTime;
}
