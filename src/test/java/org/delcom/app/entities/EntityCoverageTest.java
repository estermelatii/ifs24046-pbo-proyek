package org.delcom.app.entities;

import org.delcom.app.dto.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntityCoverageTest {

    @Test
    void testUserFull() {
        User u1 = new User();
        u1.setId(UUID.randomUUID());
        u1.setName("Budi");
        u1.setEmail("budi@mail.com");
        u1.setPassword("pass");
        u1.setRole("USER");
        u1.setCreatedAt(LocalDateTime.now());
        u1.setUpdatedAt(LocalDateTime.now());

        assertNotNull(u1.getId());
        assertEquals("Budi", u1.getName());
        assertEquals("budi@mail.com", u1.getEmail());
        assertEquals("pass", u1.getPassword());
        assertEquals("USER", u1.getRole());
        assertNotNull(u1.getCreatedAt());
        assertNotNull(u1.getUpdatedAt());
        
        // Test Constructor (jika ada)
        User u2 = new User();
        assertNotNull(u2);
    }

    @Test
    void testWishlistItemFull() {
        WishlistItem w = new WishlistItem();
        User u = new User();
        w.setId(UUID.randomUUID());
        w.setUser(u);
        w.setName("Barang");
        w.setPrice(BigDecimal.TEN);
        w.setSavedAmount(BigDecimal.ONE);
        w.setCategory("Cat");
        w.setTargetDate(LocalDate.now());
        w.setShopUrl("url");
        w.setDescription("desc");
        w.setImageUrl("img.jpg");
        w.setStatus(Status.PENDING);
        w.setCreatedAt(LocalDateTime.now());
        w.setUpdatedAt(LocalDateTime.now());

        assertNotNull(w.getId());
        assertEquals(u, w.getUser());
        assertEquals("Barang", w.getName());
        assertEquals(BigDecimal.TEN, w.getPrice());
        assertEquals(BigDecimal.ONE, w.getSavedAmount());
        assertEquals("Cat", w.getCategory());
        assertNotNull(w.getTargetDate());
        assertEquals("url", w.getShopUrl());
        assertEquals("desc", w.getDescription());
        assertEquals("img.jpg", w.getImageUrl());
        assertEquals(Status.PENDING, w.getStatus());
        assertNotNull(w.getCreatedAt());
        assertNotNull(w.getUpdatedAt());
    }

    @Test
    void testAuthTokenFull() {
        AuthToken t = new AuthToken();
        t.setId(UUID.randomUUID());
        t.setToken("abc");
        t.setUserId(UUID.randomUUID());
        t.setCreatedAt(LocalDateTime.now());

        assertNotNull(t.getId());
        assertEquals("abc", t.getToken());
        assertNotNull(t.getUserId());
        assertNotNull(t.getCreatedAt());
    }

    @Test
    void testDTOs() {
        // LoginForm
        LoginForm login = new LoginForm();
        login.setEmail("a@a.com");
        login.setPassword("p");
        assertEquals("a@a.com", login.getEmail());
        assertEquals("p", login.getPassword());

        // RegisterForm
        RegisterForm reg = new RegisterForm();
        reg.setName("N");
        reg.setEmail("e");
        reg.setPassword("p");
        assertEquals("N", reg.getName());
        assertEquals("e", reg.getEmail());
        assertEquals("p", reg.getPassword());

        // WishlistForm (Coverage Getter Setter)
        WishlistForm form = new WishlistForm();
        form.setId(UUID.randomUUID());
        form.setName("N");
        form.setPrice(BigDecimal.TEN);
        form.setSavedAmount(BigDecimal.ZERO);
        form.setCategory("C");
        form.setTargetDate(LocalDate.now());
        form.setShopUrl("url");
        form.setDescription("d");
        form.setImageFile(null); // Multipart

        assertNotNull(form.getId());
        assertEquals("N", form.getName());
        assertEquals(BigDecimal.TEN, form.getPrice());
        assertEquals(BigDecimal.ZERO, form.getSavedAmount());
        assertEquals("C", form.getCategory());
        assertNotNull(form.getTargetDate());
        assertEquals("url", form.getShopUrl());
        assertEquals("d", form.getDescription());
        assertNull(form.getImageFile());
    }
    
    @Test
    void testEnums() {
        // Test values() dan valueOf() biar 100%
        Status[] statuses = Status.values();
        assertEquals(2, statuses.length);
        assertEquals(Status.PENDING, Status.valueOf("PENDING"));
        
        Priority[] priorities = Priority.values(); // Jika ada enum Priority
        assertNotNull(priorities);
    }
}