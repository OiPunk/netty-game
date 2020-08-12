package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主消息处理器
 */
public final class MainThreadProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    /**
     * 单例对象
     */
    static private final MainThreadProcessor _instance = new MainThreadProcessor();

    /**
     * 创建一个单线程的线程池
     */
    private final ExecutorService _es = Executors.newSingleThreadExecutor((r) -> {
        Thread newThread = new Thread(r);
        newThread.setName("MainMsgProcessor");
        return newThread;
    });

    /**
     * 私有化类默认构造器
     */
    private MainThreadProcessor() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理消息
     *
     * @param ctx 客户端信道上下文
     * @param msg 消息对象
     */
    public void process(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx ||
            null == msg) {
            return;
        }

        // 获取消息类
        final Class<?> msgClazz = msg.getClass();

        LOGGER.info(
            "收到客户端消息, msgClazz = {}",
            msgClazz.getName()
        );

        // 获取命令处理器
        final ICmdHandler<? extends GeneratedMessageV3> handlerImpl = CmdHandlerFactory.create(msgClazz);

        if (null == handlerImpl) {
            LOGGER.error(
                "未找到相对应的命令处理器, msgClazz = {}",
                msgClazz.getName()
            );
            return;
        }

        this.process(() -> handlerImpl.handle(
            ctx, cast(msg)
        ));
    }

    /**
     * 处理 Runnable 实例
     *
     * @param r Runnable 实例
     */
    public void process(Runnable r) {
        if (null != r) {
            _es.submit(new SafeRun(r));
        }
    }

    /**
     * 转型为命令对象
     *
     * @param msg    消息对象
     * @param <TCmd> 消息类
     * @return 命令对象
     */
    @SuppressWarnings("unchecked")
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (!(msg instanceof GeneratedMessageV3)) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }

    /**
     * 安全运行
     */
    static private class SafeRun implements Runnable {
        /**
         * 内置运行实例
         */
        private final Runnable _innerR;

        /**
         * 类参数构造器
         *
         * @param innerR 内置运行实例
         */
        SafeRun(Runnable innerR) {
            _innerR = innerR;
        }

        @Override
        public void run() {
            if (null == _innerR) {
                return;
            }

            try {
                // 运行
                _innerR.run();
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
