package org.tinygame.herostory.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class UserManagerTest {
    @AfterEach
    void tearDown() {
        UserManager.clear();
    }

    @Test
    void shouldAddAndLookupUser() {
        User user = new User();
        user.userId = 7;
        user.userName = "alice";

        UserManager.addUser(user);

        assertSame(user, UserManager.getByUserId(7));
    }

    @Test
    void shouldIgnoreNullInput() {
        UserManager.addUser(null);

        Collection<User> users = UserManager.listUser();
        assertEquals(0, users.size());
    }

    @Test
    void shouldRemoveUser() {
        User user = new User();
        user.userId = 8;
        user.userName = "bob";

        UserManager.addUser(user);
        UserManager.removeByUserId(8);

        assertNull(UserManager.getByUserId(8));
    }
}
