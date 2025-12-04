package org.delcom.app.repositories;

import org.delcom.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Mencari user berdasarkan email (Penting untuk Login)
    Optional<User> findByEmail(String email);
}