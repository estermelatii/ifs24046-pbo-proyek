package org.delcom.app.repositories;

import org.delcom.app.entities.WishlistItem;
import org.delcom.app.entities.User;
import org.delcom.app.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

// Perhatikan <WishlistItem, UUID> <- Ini harus UUID, bukan String/Long
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
    
    List<WishlistItem> findByUserOrderByCreatedAtDesc(User user);
    
    // Untuk Statistik
    long countByUserAndStatus(User user, Status status);
}