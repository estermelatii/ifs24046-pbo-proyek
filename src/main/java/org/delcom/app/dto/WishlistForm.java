package org.delcom.app.dto;

import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class WishlistForm {
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getSavedAmount() {
        return savedAmount;
    }
    public void setSavedAmount(BigDecimal savedAmount) {
        this.savedAmount = savedAmount;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public LocalDate getTargetDate() {
        return targetDate;
    }
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
    public String getShopUrl() {
        return shopUrl;
    }
    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public MultipartFile getImageFile() {
        return imageFile;
    }
    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
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