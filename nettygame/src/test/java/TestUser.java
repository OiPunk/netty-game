/**
 * 测试用户
 */
public class TestUser {
    /**
     * 当前血量
     */
    public int currHp;

    /**
     * 减血
     *
     * @param val
     */
    synchronized public void subtractHp(int val) {
        if (val <= 0) {
            return;
        }

        this.currHp = this.currHp - val;
    }

    /**
     * 攻击
     *
     * @param targetUser
     */
    public void attkUser(TestUser targetUser) {
        if (null == targetUser) {
            return;
        }

        synchronized (this) {
            final int dmgPoint = 10;
            targetUser.subtractHp(dmgPoint);
        }
    }
}
