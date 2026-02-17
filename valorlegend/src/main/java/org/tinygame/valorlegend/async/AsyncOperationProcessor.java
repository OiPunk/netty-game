package org.tinygame.valorlegend.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.valorlegend.MainThreadProcessor;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executes async operations and dispatches finish callbacks to the main thread.
 */
public final class AsyncOperationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);
    private static final AsyncOperationProcessor INSTANCE = new AsyncOperationProcessor();

    private final ExecutorService[] executorArray = new ExecutorService[8];

    private AsyncOperationProcessor() {
        for (int i = 0; i < executorArray.length; i++) {
            final String threadName = MessageFormat.format("AsyncOperationProcessor[{0}]", i);
            executorArray[i] = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r);
                t.setName(threadName);
                t.setDaemon(true);
                return t;
            });
        }
    }

    public static AsyncOperationProcessor getInstance() {
        return INSTANCE;
    }

    public void process(IAsyncOperation op) {
        if (op == null) {
            return;
        }

        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % executorArray.length;

        executorArray[esIndex].submit(() -> {
            try {
                op.doAsync();
                MainThreadProcessor.getInstance().process(op::doFinish);
            } catch (Exception ex) {
                LOGGER.error("Async operation failed", ex);
            }
        });
    }
}
