package org.delcom.app.entities;

import org.delcom.app.dto.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FullCoverageModelTest {

    @Test
    void testUserComplete() {
        User u = new User();
        UUID id = UUID.randomUUID();
        u.setId(id);
        u.setName("Test");
        u.setEmail("test@email.com");
        u.setPassword("pass");
        u.setRole("ADMIN");
        u.setCreatedAt(LocalDateTime.MIN);
        u.setUpdatedAt(LocalDateTime.MAX);

        assertEquals(id, u.getId());
        assertEquals("Test", u.getName());
        assertEquals("test@email.com", u.getEmail());
        assertEquals("pass", u.getPassword());
        assertEquals("ADMIN", u.getRole());
        assertEquals(LocalDateTime.MIN, u.getCreatedAt());
        assertEquals(LocalDateTime.MAX, u.getUpdatedAt());
    }

    @Test
    void testWishlistItemComplete() {
        WishlistItem w = new WishlistItem();
        User u = new User();
        UUID id = UUID.randomUUID();
        
        w.setId(id);
        w.setUser(u);
        w.setName("Item");
        w.setPrice(BigDecimal.TEN);
        w.setSavedAmount(BigDecimal.ONE);
        w.setCategory("Cat");
        w.setTargetDate(LocalDate.EPOCH);
        w.setShopUrl("url");
        w.setDescription("desc");
        w.setImageUrl("img.png");
        w.setStatus(Status.BOUGHT);
        w.setCreatedAt(LocalDateTime.MIN);
        w.setUpdatedAt(LocalDateTime.MAX);

        assertEquals(id, w.getId());
        assertEquals(u, w.getUser());
        assertEquals("Item", w.getName());
        assertEquals(BigDecimal.TEN, w.getPrice());
        assertEquals(BigDecimal.ONE, w.getSavedAmount());
        assertEquals("Cat", w.getCategory());
        assertEquals(LocalDate.EPOCH, w.getTargetDate());
        assertEquals("url", w.getShopUrl());
        assertEquals("desc", w.getDescription());
        assertEquals("img.png", w.getImageUrl());
        assertEquals(Status.BOUGHT, w.getStatus());
        assertEquals(LocalDateTime.MIN, w.getCreatedAt());
        assertEquals(LocalDateTime.MAX, w.getUpdatedAt());
    }

    @Test
    void testDTOs() {
        // LoginForm
        LoginForm login = new LoginForm();
        login.setEmail("e"); login.setPassword("p");
        assertEquals("e", login.getEmail());
        assertEquals("p", login.getPassword());

        // RegisterForm
        RegisterForm reg = new RegisterForm();
        reg.setName("n"); reg.setEmail("e"); reg.setPassword("p");
        assertEquals("n", reg.getName());
        assertEquals("e", reg.getEmail());
        assertEquals("p", reg.getPassword());

        // WishlistStats
        WishlistStats stats = new WishlistStats();
        stats.setTotalWishlist(1);
        stats.setTotalPurchased(2);
        stats.setTotalCancelled(3);
        stats.setTotalPriceWishlist(BigDecimal.TEN);
        stats.setTotalPricePurchased(BigDecimal.ONE);
        Map<String, Long> cat = new HashMap<>();
        Map<String, BigDecimal> price = new HashMap<>();
        stats.setCountByCategory(cat);
        stats.setPriceByCategory(price);

        assertEquals(1, stats.getTotalWishlist());
        assertEquals(2, stats.getTotalPurchased());
        assertEquals(3, stats.getTotalCancelled());
        assertEquals(BigDecimal.TEN, stats.getTotalPriceWishlist());
        assertEquals(BigDecimal.ONE, stats.getTotalPricePurchased());
        assertEquals(cat, stats.getCountByCategory());
        assertEquals(price, stats.getPriceByCategory());
    }
    
    @Test
    void testAuthToken() {
        AuthToken t = new AuthToken();
        UUID id = UUID.randomUUID();
        t.setId(id);
        t.setToken("tok");
        t.setUserId(id);
        t.setCreatedAt(LocalDateTime.MIN);
        
        assertEquals(id, t.getId());
        assertEquals("tok", t.getToken());
        assertEquals(id, t.getUserId());
        assertEquals(LocalDateTime.MIN, t.getCreatedAt());
    }
}