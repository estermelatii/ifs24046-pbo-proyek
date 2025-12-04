package org.delcom.app.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WishlistForm {
    private String name;
    private BigDecimal price;
    private String category;
    private LocalDate targetDate;
    private String shopUrl;
    private String description;
    
    // Ini PENTING untuk upload gambar
    private MultipartFile imageFile; 
}