package org.delcom.app.repositories;

import org.delcom.app.entities.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional; // Import penting
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {
    
    // Method untuk mencari token
    AuthToken findByToken(String token);

    // Method untuk menghapus token (Wajib ada @Transactional untuk delete)
    @Transactional
    void deleteByToken(String token);
}