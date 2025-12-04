package org.delcom.app.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wishlist_items")
@Data // Lombok ini wajib agar setUser, setPrice, dll otomatis dibuat
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // --- BAGIAN INI YANG SEPERTINYA HILANG DI KODEMU ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Ini memperbaiki error: setUser undefined

    @Column(nullable = false)
    private String name;

    // --- BAGIAN INI HARUS BigDecimal (Bukan Double) ---
    private BigDecimal price; // Ini memperbaiki error: setPrice mismatch

    private String category;

    // --- BAGIAN INI HARUS LocalDate (Bukan LocalDateTime) ---
    @Column(name = "target_date")
    private LocalDate targetDate; // Ini memperbaiki error: setTargetDate mismatch

    // --- BAGIAN INI MUNGKIN HILANG ---
    @Column(name = "shop_url")
    private String shopUrl; // Ini memperbaiki error: setShopUrl undefined

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}