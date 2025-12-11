package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnumTest {
    @Test
    void testStatus() {
        assertEquals("PENDING", Status.PENDING.name());
        assertEquals("BOUGHT", Status.BOUGHT.name());
    }
}