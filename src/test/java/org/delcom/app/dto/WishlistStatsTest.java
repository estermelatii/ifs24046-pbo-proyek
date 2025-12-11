package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class WishlistStatsTest {

    @Test
    void testStatsLogic() {
        WishlistStats stats = new WishlistStats();
        Map<String, Long> mapCount = new HashMap<>();
        mapCount.put("Elektronik", 5L);

        stats.setTotalWishlist(10);
        stats.setTotalPurchased(5);
        stats.setTotalPriceWishlist(BigDecimal.TEN);
        stats.setCountByCategory(mapCount);

        assertEquals(10, stats.getTotalWishlist());
        assertEquals(5, stats.getTotalPurchased());
        assertEquals(BigDecimal.TEN, stats.getTotalPriceWishlist());
        assertEquals(5L, stats.getCountByCategory().get("Elektronik"));
    }
}