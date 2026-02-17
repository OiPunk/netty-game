package org.tinygame.valorlegend.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.valorlegend.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for protocol command handlers.
 */
public final class CmdHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    private static final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> HANDLER_MAP = new ConcurrentHashMap<>();

    private CmdHandlerFactory() {
    }

    public static void init() {
        HANDLER_MAP.clear();

        final String packageName = CmdHandlerFactory.class.getPackage().getName();
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(packageName, true, ICmdHandler.class);

        LOGGER.info("Building command handler registry");

        for (Class<?> handlerClazz : clazzSet) {
            if (handlerClazz == null || 0 != (handlerClazz.getModifiers() & Modifier.ABSTRACT)) {
                continue;
            }

            Class<?> cmdClazz = extractCommandClass(handlerClazz);
            if (cmdClazz == null) {
                continue;
            }

            try {
                ICmdHandler<?> newHandler = (ICmdHandler<?>) handlerClazz.getDeclaredConstructor().newInstance();
                HANDLER_MAP.put(cmdClazz, newHandler);
                LOGGER.info("Bound {} -> {}", cmdClazz.getName(), handlerClazz.getName());
            } catch (Exception ex) {
                LOGGER.error("Failed to instantiate command handler: {}", handlerClazz.getName(), ex);
            }
        }
    }

    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> cmdClazz) {
        if (cmdClazz == null) {
            return null;
        }

        return HANDLER_MAP.get(cmdClazz);
    }

    private static Class<?> extractCommandClass(Class<?> handlerClazz) {
        Method[] methodArray = handlerClazz.getDeclaredMethods();

        for (Method currMethod : methodArray) {
            if (currMethod == null || !"handle".equals(currMethod.getName())) {
                continue;
            }

            Class<?>[] paramTypeArray = currMethod.getParameterTypes();
            if (paramTypeArray.length < 2) {
                continue;
            }

            Class<?> candidate = paramTypeArray[1];
            if (candidate == GeneratedMessageV3.class || !GeneratedMessageV3.class.isAssignableFrom(candidate)) {
                continue;
            }

            return candidate;
        }

        return null;
    }
}
