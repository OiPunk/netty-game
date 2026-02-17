package org.tinygame.valorlegend;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.valorlegend.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps protocol message classes and protocol message codes.
 */
public final class GameMsgRecognizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    private static final Map<Integer, GeneratedMessageV3> MSG_CODE_TO_DEFAULT_INSTANCE = new HashMap<>();
    private static final Map<Class<?>, Integer> MSG_CLASS_TO_CODE = new HashMap<>();

    private GameMsgRecognizer() {
    }

    public static void init() {
        MSG_CODE_TO_DEFAULT_INSTANCE.clear();
        MSG_CLASS_TO_CODE.clear();

        LOGGER.info("Building message code registry");

        for (Class<?> innerClazz : GameMsgProtocol.class.getDeclaredClasses()) {
            if (innerClazz == null || !GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }

            String clazzName = innerClazz.getSimpleName().toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                if (msgCode == null) {
                    continue;
                }

                String msgCodeName = msgCode.name().replace("_", "").toLowerCase();
                if (!msgCodeName.startsWith(clazzName)) {
                    continue;
                }

                try {
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);
                    GeneratedMessageV3 msgInstance = (GeneratedMessageV3) returnObj;
                    MSG_CODE_TO_DEFAULT_INSTANCE.put(msgCode.getNumber(), msgInstance);
                    MSG_CLASS_TO_CODE.put(innerClazz, msgCode.getNumber());

                    LOGGER.info("Bound {} -> {}", innerClazz.getName(), msgCode.getNumber());
                } catch (Exception ex) {
                    LOGGER.error("Failed to register message class {}", innerClazz.getName(), ex);
                }
            }
        }
    }

    public static Message.Builder getMsgBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 defaultMsg = MSG_CODE_TO_DEFAULT_INSTANCE.get(msgCode);
        return defaultMsg == null ? null : defaultMsg.newBuilderForType();
    }

    public static int getMsgCodeByMsgClazz(Class<?> msgClazz) {
        if (msgClazz == null) {
            return -1;
        }

        Integer msgCode = MSG_CLASS_TO_CODE.get(msgClazz);
        return msgCode == null ? -1 : msgCode;
    }
}
