package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class WishlistFormTest {

    @Test
    void testGetterSetter() {
        // 1. Buat Object
        WishlistForm form = new WishlistForm();
        UUID id = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "content".getBytes());

        // 2. Set Data
        form.setId(id);
        form.setName("iPhone 15");
        form.setPrice(new BigDecimal("15000000"));
        form.setSavedAmount(new BigDecimal("5000000"));
        form.setCategory("Gadget");
        form.setTargetDate(date);
        form.setShopUrl("http://shopee.co.id");
        form.setDescription("Mau banget");
        form.setImageFile(file);

        // 3. Cek Data (Assert)
        assertEquals(id, form.getId());
        assertEquals("iPhone 15", form.getName());
        assertEquals(new BigDecimal("15000000"), form.getPrice());
        assertEquals(new BigDecimal("5000000"), form.getSavedAmount());
        assertEquals("Gadget", form.getCategory());
        assertEquals(date, form.getTargetDate());
        assertEquals("http://shopee.co.id", form.getShopUrl());
        assertEquals("Mau banget", form.getDescription());
        assertEquals(file, form.getImageFile());
    }
}