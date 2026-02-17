package org.tinygame.valorlegend.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory online user registry.
 */
public final class UserManager {
    private static final Map<Integer, User> USER_MAP = new ConcurrentHashMap<>();

    private UserManager() {
    }

    public static void addUser(User user) {
        if (user != null) {
            USER_MAP.putIfAbsent(user.userId, user);
        }
    }

    public static void removeByUserId(int userId) {
        USER_MAP.remove(userId);
    }

    public static Collection<User> listUser() {
        return USER_MAP.values();
    }

    public static User getByUserId(int userId) {
        return USER_MAP.get(userId);
    }

    public static void clear() {
        USER_MAP.clear();
    }
}
