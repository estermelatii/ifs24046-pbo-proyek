package org.delcom.app.repositories;

import org.delcom.app.entities.Status;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest // Ini anotasi khusus buat ngetes Repository
class WishlistItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WishlistItemRepository repository;

    @Test
    void testFindByUser() {
        // 1. Siapkan User
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        user.setPassword("password");
        entityManager.persist(user); // Simpan ke DB sementara

        // 2. Siapkan Barang
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setName("Laptop");
        item.setPrice(BigDecimal.TEN);
        item.setStatus(Status.PENDING);
        entityManager.persist(item);

        // 3. Cek Repository
        List<WishlistItem> items = repository.findByUserOrderByCreatedAtDesc(user);
        
        assertEquals(1, items.size());
        assertEquals("Laptop", items.get(0).getName());
    }
}