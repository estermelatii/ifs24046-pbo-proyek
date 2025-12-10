package org.delcom.app.dto;

import java.math.BigDecimal;
import java.util.Map;

public class WishlistStats {
    private long totalWishlist;
    private long totalPurchased;
    private long totalCancelled; 
    public long getTotalWishlist() {
      return totalWishlist;
    }

    public void setTotalWishlist(long totalWishlist) {
      this.totalWishlist = totalWishlist;
    }

    public long getTotalPurchased() {
      return totalPurchased;
    }

    public void setTotalPurchased(long totalPurchased) {
      this.totalPurchased = totalPurchased;
    }

    public long getTotalCancelled() {
      return totalCancelled;
    }

    public void setTotalCancelled(long totalCancelled) {
      this.totalCancelled = totalCancelled;
    }

    public BigDecimal getTotalPriceWishlist() {
      return totalPriceWishlist;
    }

    public void setTotalPriceWishlist(BigDecimal totalPriceWishlist) {
      this.totalPriceWishlist = totalPriceWishlist;
    }

    public BigDecimal getTotalPricePurchased() {
      return totalPricePurchased;
    }

    public void setTotalPricePurchased(BigDecimal totalPricePurchased) {
      this.totalPricePurchased = totalPricePurchased;
    }

    public Map<String, Long> getCountByCategory() {
      return countByCategory;
    }

    public void setCountByCategory(Map<String, Long> countByCategory) {
      this.countByCategory = countByCategory;
    }

    public Map<String, BigDecimal> getPriceByCategory() {
      return priceByCategory;
    }

    public void setPriceByCategory(Map<String, BigDecimal> priceByCategory) {
      this.priceByCategory = priceByCategory;
    }

    private BigDecimal totalPriceWishlist;
    private BigDecimal totalPricePurchased;
    
    // Data untuk Chart
    private Map<String, Long> countByCategory;
    private Map<String, BigDecimal> priceByCategory;

    // Constructor default untuk inisialisasi nilai awal agar tidak null
    public WishlistStats() {
        this.totalWishlist = 0;
        this.totalPurchased = 0;
        this.totalCancelled = 0;
        this.totalPriceWishlist = BigDecimal.ZERO;
        this.totalPricePurchased = BigDecimal.ZERO;
    }
}