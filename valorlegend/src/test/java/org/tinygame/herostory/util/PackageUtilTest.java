package org.tinygame.herostory.util;

import org.junit.jupiter.api.Test;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.cmdhandler.UserMoveToCmdHandler;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackageUtilTest {
    @Test
    void shouldFindHandlerClassesInPackage() {
        Set<Class<?>> result = PackageUtil.listSubClazz(
            "org.tinygame.herostory.cmdhandler",
            true,
            ICmdHandler.class
        );

        assertTrue(result.contains(UserMoveToCmdHandler.class));
    }

    @Test
    void shouldReturnEmptySetForBlankPackageName() {
        Set<Class<?>> result = PackageUtil.listClazz("", true, null);

        assertFalse(result.iterator().hasNext());
    }
}
