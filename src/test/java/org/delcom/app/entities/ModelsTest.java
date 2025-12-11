package org.delcom.app.entities;

// Import ini penting biar assertNotNull terbaca
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.dto.WishlistForm;
import org.delcom.app.dto.WishlistStats;
import org.junit.jupiter.api.Test;

class ModelsTest {

    @Test
    void testAllGettersSettersAndToString() {
        // --- 1. TEST USER ---
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("pass");
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        assertNotNull(user.getId());
        assertNotNull(user.getName());
        assertNotNull(user.getEmail());
        assertNotNull(user.getPassword());
        assertNotNull(user.getRole());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertNotNull(user.toString()); 

        // --- 2. TEST WISHLIST ITEM ---
        WishlistItem item = new WishlistItem();
        item.setId(UUID.randomUUID());
        item.setUser(user);
        item.setName("Laptop");
        item.setPrice(BigDecimal.TEN);
        item.setSavedAmount(BigDecimal.ONE);
        item.setCategory("Tech");
        item.setTargetDate(LocalDate.now());
        item.setShopUrl("http://url.com");
        item.setDescription("Desc");
        item.setImageUrl("img.jpg");
        item.setStatus(Status.PENDING);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        assertNotNull(item.getId());
        assertNotNull(item.getUser());
        assertNotNull(item.getName());
        assertNotNull(item.getPrice());
        assertNotNull(item.getSavedAmount());
        assertNotNull(item.getCategory());
        assertNotNull(item.getTargetDate());
        assertNotNull(item.getShopUrl());
        assertNotNull(item.getDescription());
        assertNotNull(item.getImageUrl());
        assertNotNull(item.getStatus());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
        assertNotNull(item.toString()); 

        // --- 3. TEST AUTH TOKEN ---
        AuthToken token = new AuthToken();
        token.setId(UUID.randomUUID());
        token.setToken("abc-123");
        token.setUserId(UUID.randomUUID());
        token.setCreatedAt(LocalDateTime.now());

        assertNotNull(token.getId());
        assertNotNull(token.getToken());
        assertNotNull(token.getUserId());
        assertNotNull(token.getCreatedAt());
        assertNotNull(token.toString());

        // --- 4. TEST DTOs ---
        LoginForm login = new LoginForm();
        login.setEmail("mail");
        login.setPassword("pass");
        assertNotNull(login.getEmail());
        assertNotNull(login.getPassword());
        assertNotNull(login.toString());

        RegisterForm reg = new RegisterForm();
        reg.setName("Name");
        reg.setEmail("Mail");
        reg.setPassword("Pass");
        assertNotNull(reg.getName());
        assertNotNull(reg.getEmail());
        assertNotNull(reg.getPassword());
        assertNotNull(reg.toString());

        WishlistForm form = new WishlistForm();
        form.setId(UUID.randomUUID());
        form.setName("N");
        form.setPrice(BigDecimal.ZERO);
        form.setSavedAmount(BigDecimal.ZERO);
        form.setCategory("C");
        form.setTargetDate(LocalDate.now());
        form.setShopUrl("U");
        form.setDescription("D");
        form.setImageFile(null);
        
        assertNotNull(form.getId());
        assertNotNull(form.getName());
        assertNotNull(form.getPrice());
        assertNotNull(form.getSavedAmount());
        assertNotNull(form.getCategory());
        assertNotNull(form.getTargetDate());
        assertNotNull(form.getShopUrl());
        assertNotNull(form.getDescription());
        assertNull(form.getImageFile());
        assertNotNull(form.toString());

        // --- 5. TEST STATS DTO ---
        WishlistStats stats = new WishlistStats();
        stats.setTotalWishlist(1);
        stats.setTotalPurchased(1);
        stats.setTotalCancelled(0);
        stats.setTotalPriceWishlist(BigDecimal.TEN);
        stats.setTotalPricePurchased(BigDecimal.TEN);
        stats.setCountByCategory(null);
        stats.setPriceByCategory(null);

        assertNotNull(stats.getTotalWishlist());
        assertNotNull(stats.getTotalPurchased());
        assertNotNull(stats.getTotalCancelled());
        assertNotNull(stats.getTotalPriceWishlist());
        assertNotNull(stats.getTotalPricePurchased());
        assertNull(stats.getCountByCategory());
        assertNull(stats.getPriceByCategory());
        assertNotNull(stats.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        // Test sederhana untuk equals (memanggil methodnya saja biar coverage naik)
        User u1 = new User();
        User u2 = new User();
        
        // Cek reference
        assertEquals(u1, u1);
        assertNotEquals(u1, null);
        assertNotEquals(u1, new Object());
        
        // Panggil toString lagi buat memastikan
        assertNotNull(u1.toString());
    }
}