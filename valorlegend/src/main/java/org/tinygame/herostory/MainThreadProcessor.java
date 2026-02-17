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
 * Single-threaded processor that serializes game message handling.
 */
public final class MainThreadProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);
    private static final MainThreadProcessor INSTANCE = new MainThreadProcessor();

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread newThread = new Thread(r);
        newThread.setName("MainMsgProcessor");
        newThread.setDaemon(true);
        return newThread;
    });

    private MainThreadProcessor() {
    }

    public static MainThreadProcessor getInstance() {
        return INSTANCE;
    }

    public void process(ChannelHandlerContext ctx, Object msg) {
        if (ctx == null || msg == null) {
            return;
        }

        final Class<?> msgClazz = msg.getClass();
        LOGGER.info("Incoming message class={}", msgClazz.getName());

        final ICmdHandler<? extends GeneratedMessageV3> handlerImpl = CmdHandlerFactory.create(msgClazz);
        if (handlerImpl == null) {
            LOGGER.error("No command handler found for class={}", msgClazz.getName());
            return;
        }

        process(() -> handlerImpl.handle(ctx, cast(msg)));
    }

    public void process(Runnable runnable) {
        if (runnable != null) {
            executor.submit(new SafeRun(runnable));
        }
    }

    @SuppressWarnings("unchecked")
    private static <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (!(msg instanceof GeneratedMessageV3)) {
            return null;
        }

        return (TCmd) msg;
    }

    static final class SafeRun implements Runnable {
        private final Runnable innerRunnable;

        SafeRun(Runnable innerRunnable) {
            this.innerRunnable = innerRunnable;
        }

        @Override
        public void run() {
            if (innerRunnable == null) {
                return;
            }

            try {
                innerRunnable.run();
            } catch (Exception ex) {
                LOGGER.error("Main-thread task execution failed", ex);
            }
        }
    }
}
