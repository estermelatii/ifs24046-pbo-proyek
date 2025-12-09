package org.delcom.app.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class WishlistForm {
    private UUID id;
    private String name;
    private BigDecimal price;
    
    // --- FIELD BARU ---
    private BigDecimal savedAmount;
    // ------------------
    
    private String category;
    private LocalDate targetDate;
    private String shopUrl;
    private String description;
    private MultipartFile imageFile;
}