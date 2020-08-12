package org.tinygame.herostory;

/**
 * Oi! Mom,this is for U!
 * Anarchism
 * 20.5.3
 * 11:38
 **/

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 主消息处理器 (单例模式)
 */
public class MainMsgProcessor {

    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);

    /**
     * 单例对象
     */
    static private final MainMsgProcessor _instance = new MainMsgProcessor();

    /**
     * 创建一个单线程的线程池
     *
     * ExecutorService 装饰者模式
     * newSingleThreadExecutor  创建一个大小为1的线程池
     * new ThreadPoolExecutor(1, 1, // 核心池(最小)大小, 最大池大小
     *                        0L, TimeUnit.MILLISECONDS, // 保持活跃, 单位毫秒
     *                        new LinkedBlockingQueue<Runnable>(), // 阻塞队列, 取数为空, 阻塞, 等待放入数
     *                        threadFactory)
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((newRunable) -> {
        Thread newThread = new Thread(newRunable);
        newThread.setName("MainMsgProcessor");
        return newThread;
    });
    /*private final ExecutorService _es = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return null;
        }
    });*/

    /**
     * 私有化默认构造器
     */
    private MainMsgProcessor() {

    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public MainMsgProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理消息
     *
     * @param ctx
     * @param msg
     */
    public void process(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx ||
                null == msg) {
            return;
        }

        final Class<?> msgClazz = msg.getClass();

        LOGGER.info(
                "收到客户端消息, msgClazz = {}, msgBody = {}",
                msg.getClass().getSimpleName(),
                msg
        );

        // 提交到单线程执行
        _es.submit(() -> {
            try {
                // 工厂模式 : 加业务逻辑, 不用修改消息处理器
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msgClazz);

                if (null != cmdHandler) {
                    cmdHandler.handle(ctx, cast(msg));
                }


            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    /**
     * 处理 Runnable 实例
     * @param r
     */
    public void process(Runnable r) {
        if (null == r) {
            return;
        }

        _es.submit(r);
    }

    /**
     * 转型为命令对象
     *
     * @param msg
     * @param <TCmd>
     * @return
     */
    // 处理转型问题 写一个函数, 骗一下编译器
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }


}
