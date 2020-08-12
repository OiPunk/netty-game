package org.tinygame.herostory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 */
public final class AsyncOperationProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    /**
     * 单例对象
     */
    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 单线程数组
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];

    /**
     * 私有化类默认构造器
     */
    private AsyncOperationProcessor() {
        for (int i = 0; i < _esArray.length; i++) {
            // 线程名称
            final String threadName = MessageFormat.format("AsyncOperationProcessor[ {0} ]", i);
            // 创建单线程
            _esArray[i] = Executors.newSingleThreadExecutor((r) -> {
                Thread t = new Thread(r);
                t.setName(threadName);
                return t;
            });
        }
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 执行异步操作
     *
     * @param op 操作对象
     */
    public void process(IAsyncOperation op) {
        if (null == op) {
            return;
        }

        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % _esArray.length;

        _esArray[esIndex].submit(() -> {
            try {
                // 执行异步操作
                op.doAsync();
                // 回到主线程执行完成逻辑
                MainThreadProcessor.getInstance().process(op::doFinish);
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }
}
