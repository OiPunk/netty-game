/**
 * 多线程测试
 */
public class MultiThreadTest {
    /**
     * 入口函数
     *
     * @param argvArray 命令行参数
     */
    static public void main(String[] argvArray) {
        for (int i = 0; i < 10000; i++) {
            System.out.println("第 " + i + "次测试");
            (new MultiThreadTest()).test3();
        }
    }

    /**
     * 两条线程修改同一数值
     */
    private void test1() {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;

        Thread t0 = new Thread(() -> newUser.currHp = newUser.currHp - 1);
        Thread t1 = new Thread(() -> newUser.currHp = newUser.currHp - 1);

        t0.start();
        t1.start();

        try {
            t0.join();
            t1.join();
        } catch (Exception ex) {
            // 打印错误日志
            ex.printStackTrace();
        }

        if (newUser.currHp != 98) {
            throw new RuntimeException("当前血量错误, currHp = " + newUser.currHp);
        } else {
            System.out.println("当前血量正确");
        }
    }

    /**
     * 利用 synchronized 同步数据
     */
    private void test2() {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;

        Thread t0 = new Thread(() -> newUser.subtractHp(1));
        Thread t1 = new Thread(() -> newUser.subtractHp(1));

        t0.start();
        t1.start();

        try {
            t0.join();
            t1.join();
        } catch (Exception ex) {
            // 打印错误日志
            ex.printStackTrace();
        }

        if (newUser.currHp != 98) {
            throw new RuntimeException("当前血量错误, currHp = " + newUser.currHp);
        } else {
            System.out.println("当前血量正确");
        }
    }

    /**
     * 死锁
     */
    private void test3() {
        TestUser user1 = new TestUser();
        user1.currHp = 100;
        TestUser user2 = new TestUser();
        user2.currHp = 100;

        Thread t0 = new Thread(() -> user1.attkUser(user2));
        Thread t1 = new Thread(() -> user2.attkUser(user1));

        t0.start();
        t1.start();

        try {
            t0.join();
            t1.join();
        } catch (Exception ex) {
            // 打印错误日志
            ex.printStackTrace();
        }
    }
}
