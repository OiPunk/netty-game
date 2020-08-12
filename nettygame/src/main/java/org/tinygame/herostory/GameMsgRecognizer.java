package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
public final class GameMsgRecognizer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    /**
     * 消息编号 -> 消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap = new HashMap<>();

    /**
     * 消息类 -> 消息编号字典
     */
    static private final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private GameMsgRecognizer() {
    }

    /**
     * 初始化
     */
    static public void init() {
        LOGGER.info("=== 完成消息类与消息编号的映射 ===");

        // 获取内部类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();

        for (Class<?> innerClazz : innerClazzArray) {
            if (null == innerClazz ||
                !GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                // 如果不是消息类,
                continue;
            }

            // 获取类名称并小写
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                if (null == msgCode) {
                    continue;
                }

                // 获取消息编码
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }

                try {
                    // 相当于调用 UserEntryCmd.getDefaultInstance();
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info(
                        "{} <==> {}",
                        innerClazz.getName(),
                        msgCode.getNumber()
                    );

                    _msgCodeAndMsgObjMap.put(
                        msgCode.getNumber(),
                        (GeneratedMessageV3) returnObj
                    );

                    _msgClazzAndMsgCodeMap.put(
                        innerClazz,
                        msgCode.getNumber()
                    );
                } catch (Exception ex) {
                    // 记录错误日志
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * 根据消息编号获取消息构建器
     *
     * @param msgCode 消息编码
     * @return 消息构建器
     */
    static public Message.Builder getMsgBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 defaultMsg = _msgCodeAndMsgObjMap.get(msgCode);

        if (null == defaultMsg) {
            return null;
        } else {
            return defaultMsg.newBuilderForType();
        }
    }

    /**
     * 根据消息类获取消息编号
     *
     * @param msgClazz 消息类
     * @return 消息编码
     */
    static public int getMsgCodeByMsgClazz(Class<?> msgClazz) {
        if (null == msgClazz) {
            return -1;
        }

        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);

        if (null == msgCode) {
            return -1;
        } else {
            return msgCode;
        }
    }
}
