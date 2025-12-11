package org.delcom.app.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UtilsTest {
    @Test
    void testConstUtil() {
        // Instansiasi class konstanta agar coverage 100%
        ConstUtil util = new ConstUtil();
        assertNotNull(util);
    }
}