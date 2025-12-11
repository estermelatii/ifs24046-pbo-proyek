package org.delcom.app.dto;

import java.math.BigDecimal;
import java.util.Map;

public class WishlistStats {
    private long totalWishlist;
    private long totalPurchased;
    private long totalCancelled;
    private BigDecimal totalPriceWishlist;
    private BigDecimal totalPricePurchased;
    
    private Map<String, Long> countByCategory;
    private Map<String, BigDecimal> priceByCategory;

    public WishlistStats() {
        this.totalWishlist = 0; this.totalPurchased = 0; this.totalCancelled = 0;
        this.totalPriceWishlist = BigDecimal.ZERO; this.totalPricePurchased = BigDecimal.ZERO;
    }

    public long getTotalWishlist() { return totalWishlist; }
    public void setTotalWishlist(long t) { this.totalWishlist = t; }

    public long getTotalPurchased() { return totalPurchased; }
    public void setTotalPurchased(long t) { this.totalPurchased = t; }

    public long getTotalCancelled() { return totalCancelled; }
    public void setTotalCancelled(long t) { this.totalCancelled = t; }

    public BigDecimal getTotalPriceWishlist() { return totalPriceWishlist; }
    public void setTotalPriceWishlist(BigDecimal t) { this.totalPriceWishlist = t; }

    public BigDecimal getTotalPricePurchased() { return totalPricePurchased; }
    public void setTotalPricePurchased(BigDecimal t) { this.totalPricePurchased = t; }

    public Map<String, Long> getCountByCategory() { return countByCategory; }
    public void setCountByCategory(Map<String, Long> m) { this.countByCategory = m; }

    public Map<String, BigDecimal> getPriceByCategory() { return priceByCategory; }
    public void setPriceByCategory(Map<String, BigDecimal> m) { this.priceByCategory = m; }
}