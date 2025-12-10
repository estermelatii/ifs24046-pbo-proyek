package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WishlistItemTest {

    @Test
    void testWishlistItemGetterSetter() {
        // 1. Buat Object
        WishlistItem item = new WishlistItem();
        UUID id = UUID.randomUUID();
        User user = new User();
        
        // 2. Set Data
        item.setId(id);
        item.setUser(user);
        item.setName("Macbook Air");
        item.setPrice(new BigDecimal("15000000"));
        item.setSavedAmount(new BigDecimal("5000000"));
        item.setCategory("Elektronik");
        item.setStatus(Status.PENDING);
        item.setTargetDate(LocalDate.of(2025, 12, 12));

        // 3. Assert (Pastikan data yang diambil sama dengan yang diset)
        assertEquals(id, item.getId());
        assertEquals("Macbook Air", item.getName());
        assertEquals(new BigDecimal("15000000"), item.getPrice());
        assertEquals(Status.PENDING, item.getStatus());
        assertNotNull(item.getUser());
    }

    @Test
    void testDefaultValues() {
        // Test apakah default value (jika ada) berfungsi
        WishlistItem item = new WishlistItem();
        // Status biasanya null jika tidak di-set di constructor/field, 
        // tapi jika kamu set default = PENDING di class, cek di sini.
        // item.setStatus(Status.PENDING); 
        
        assertNull(item.getName()); // Harusnya null saat baru dibuat
    }
}