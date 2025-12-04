package org.delcom.app.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role; 

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- FITUR TAMBAHAN UNTUK MENGATASI ERROR ---

    // 1. Mengatasi error "The method toResponseMap() is undefined"
    public Map<String, Object> toResponseMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("name", this.name);
        map.put("email", this.email);
        map.put("role", this.role);
        map.put("createdAt", this.createdAt);
        return map;
    }

    // 2. Mengatasi error "setId(UUID) ... arguments (String)" di UserService
    // Kita buatkan setter khusus yang menerima String lalu mengubahnya jadi UUID
    public void setId(String id) {
        if (id != null) {
            this.id = UUID.fromString(id);
        }
    }
    
    // Setter asli untuk UUID (Overloading) biar JPA tetap jalan
    public void setId(UUID id) {
        this.id = id;
    }
}