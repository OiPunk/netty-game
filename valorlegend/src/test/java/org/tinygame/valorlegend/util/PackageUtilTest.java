package org.tinygame.valorlegend.util;

import org.junit.jupiter.api.Test;
import org.tinygame.valorlegend.cmdhandler.ICmdHandler;
import org.tinygame.valorlegend.cmdhandler.UserMoveToCmdHandler;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackageUtilTest {
    @Test
    void shouldFindHandlerClassesInPackage() {
        Set<Class<?>> result = PackageUtil.listSubClazz(
            "org.tinygame.valorlegend.cmdhandler",
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
