package org.delcom.app.services;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.dto.WishlistStats;
import org.delcom.app.entities.Status;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
import org.delcom.app.repositories.WishlistItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistItemRepository repository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private WishlistService wishlistService;

    private User testUser;
    private WishlistItem testItem;
    private WishlistForm testForm;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());

        testItem = new WishlistItem();
        testItem.setId(UUID.randomUUID());
        testItem.setUser(testUser);
        testItem.setName("Test Item");
        testItem.setPrice(new BigDecimal("100.00"));
        testItem.setSavedAmount(new BigDecimal("50.00"));
        testItem.setCategory("Electronics");
        testItem.setStatus(Status.PENDING);

        testForm = new WishlistForm();
        testForm.setName("Test Item");
        testForm.setPrice(new BigDecimal("100.00"));
        testForm.setSavedAmount(new BigDecimal("50.00"));
        testForm.setCategory("Electronics");
        testForm.setTargetDate(LocalDate.now().plusDays(30));
        testForm.setShopUrl("https://shop.com");
        testForm.setDescription("Test description");
    }

    @Test
    void testGetAllItems() {
        // Arrange
        List<WishlistItem> items = Arrays.asList(testItem);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        List<WishlistItem> result = wishlistService.getAllItems(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testItem, result.get(0));
        verify(repository, times(1)).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    void testFindById() {
        // Arrange
        UUID id = testItem.getId();
        when(repository.findById(id)).thenReturn(Optional.of(testItem));

        // Act
        WishlistItem result = wishlistService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(testItem, result);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testFindByIdNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        WishlistItem result = wishlistService.findById(id);

        // Assert
        assertNull(result);
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testAddItemWithoutImage() throws IOException {
        // Arrange
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
        verify(fileStorageService, never()).storeFile(any(), any());
    }

    @Test
    void testAddItemWithImage() throws IOException {
        // Arrange
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "test.jpg", "image/jpeg", "test image content".getBytes()
        );
        testForm.setImageFile(imageFile);
        
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);
        when(fileStorageService.storeFile(any(), any())).thenReturn("test-image.jpg");

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(2)).save(any(WishlistItem.class));
        verify(fileStorageService, times(1)).storeFile(imageFile, testItem.getId());
    }

    @Test
    void testAddItemWithNullSavedAmount() throws IOException {
        // Arrange
        testForm.setSavedAmount(null);
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testAddItemWithAutoStatusBought() throws IOException {
        // Arrange
        testForm.setSavedAmount(new BigDecimal("100.00"));
        testForm.setPrice(new BigDecimal("100.00"));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testAddItemWithSavedAmountGreaterThanPrice() throws IOException {
        // Arrange
        testForm.setSavedAmount(new BigDecimal("150.00"));
        testForm.setPrice(new BigDecimal("100.00"));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testAddItemWithNullPrice() throws IOException {
        // Arrange - Branch: form.getPrice() == null
        testForm.setPrice(null);
        testForm.setSavedAmount(new BigDecimal("50.00"));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testAddItemWithSavedAmountLessThanPrice() throws IOException {
        // Arrange - Branch: savedAmount < price (tidak auto bought)
        testForm.setSavedAmount(new BigDecimal("30.00"));
        testForm.setPrice(new BigDecimal("100.00"));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testUpdateItemWithoutImage() throws IOException {
        // Arrange
        testForm.setId(testItem.getId());
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.updateItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).findById(testItem.getId());
        verify(repository, times(1)).save(any(WishlistItem.class));
        verify(fileStorageService, never()).storeFile(any(), any());
    }

    @Test
    void testUpdateItemWithImage() throws IOException {
        // Arrange
        testForm.setId(testItem.getId());
        MockMultipartFile imageFile = new MockMultipartFile(
            "image", "updated.jpg", "image/jpeg", "updated image".getBytes()
        );
        testForm.setImageFile(imageFile);
        
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);
        when(fileStorageService.storeFile(any(), any())).thenReturn("updated-image.jpg");

        // Act
        wishlistService.updateItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).findById(testItem.getId());
        verify(repository, times(1)).save(any(WishlistItem.class));
        verify(fileStorageService, times(1)).storeFile(imageFile, testItem.getId());
    }

    @Test
    void testUpdateItemNotFound() {
        // Arrange
        testForm.setId(UUID.randomUUID());
        when(repository.findById(testForm.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            wishlistService.updateItem(testUser, testForm);
        });
        verify(repository, times(1)).findById(testForm.getId());
        verify(repository, never()).save(any(WishlistItem.class));
    }

    @Test
    void testUpdateItemWithNullPrice() throws IOException {
        // Arrange - Branch: form.getPrice() == null di update
        testForm.setId(testItem.getId());
        testForm.setPrice(null);
        testForm.setSavedAmount(new BigDecimal("50.00"));
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.updateItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testUpdateItemWithAutoStatusBought() throws IOException {
        // Arrange - Test auto status change di update
        testForm.setId(testItem.getId());
        testForm.setSavedAmount(new BigDecimal("100.00"));
        testForm.setPrice(new BigDecimal("100.00"));
        testItem.setStatus(Status.PENDING); // Status awal PENDING
        
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.updateItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testUpdateItemAlreadyBought() throws IOException {
        // Arrange - Branch: item.getStatus() != Status.PENDING
        testForm.setId(testItem.getId());
        testForm.setSavedAmount(new BigDecimal("100.00"));
        testForm.setPrice(new BigDecimal("100.00"));
        testItem.setStatus(Status.BOUGHT); // Status sudah BOUGHT
        
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.updateItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testChangeStatusPendingToBought() {
        // Arrange
        testItem.setStatus(Status.PENDING);
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            return item;
        });

        // Act
        wishlistService.changeStatus(testItem.getId());

        // Assert
        verify(repository, times(1)).findById(testItem.getId());
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testChangeStatusBoughtToPending() {
        // Arrange
        testItem.setStatus(Status.BOUGHT);
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem item = invocation.getArgument(0);
            return item;
        });

        // Act
        wishlistService.changeStatus(testItem.getId());

        // Assert
        verify(repository, times(1)).findById(testItem.getId());
        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testDeleteItemWithImage() {
        // Arrange
        testItem.setImageUrl("test-image.jpg");
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        doNothing().when(repository).deleteById(any(UUID.class));

        // Act
        wishlistService.deleteItem(testItem.getId());

        // Assert
        verify(repository, times(1)).findById(testItem.getId());
        verify(fileStorageService, times(1)).deleteFile("test-image.jpg");
        verify(repository, times(1)).deleteById(testItem.getId());
    }

    @Test
    void testDeleteItemWithoutImage() {
        // Arrange
        testItem.setImageUrl(null);
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        doNothing().when(repository).deleteById(any(UUID.class));

        // Act
        wishlistService.deleteItem(testItem.getId());

        // Assert
        verify(repository, times(1)).findById(testItem.getId());
        verify(fileStorageService, never()).deleteFile(any());
        verify(repository, times(1)).deleteById(testItem.getId());
    }

    @Test
    void testDeleteItemNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        wishlistService.deleteItem(id);

        // Assert
        verify(repository, times(1)).findById(id);
        verify(fileStorageService, never()).deleteFile(any());
        verify(repository, never()).deleteById(any());
    }

    @Test
    void testCountPending() {
        // Arrange
        when(repository.countByUserAndStatus(testUser, Status.PENDING)).thenReturn(5L);

        // Act
        long result = wishlistService.countPending(testUser);

        // Assert
        assertEquals(5L, result);
        verify(repository, times(1)).countByUserAndStatus(testUser, Status.PENDING);
    }

    @Test
    void testCountBought() {
        // Arrange
        when(repository.countByUserAndStatus(testUser, Status.BOUGHT)).thenReturn(3L);

        // Act
        long result = wishlistService.countBought(testUser);

        // Assert
        assertEquals(3L, result);
        verify(repository, times(1)).countByUserAndStatus(testUser, Status.BOUGHT);
    }

    @Test
    void testGetStats() {
        // Arrange
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.PENDING);
        item1.setPrice(new BigDecimal("100.00"));
        item1.setCategory("Electronics");

        WishlistItem item2 = new WishlistItem();
        item2.setStatus(Status.BOUGHT);
        item2.setPrice(new BigDecimal("200.00"));
        item2.setCategory("Books");

        WishlistItem item3 = new WishlistItem();
        item3.setStatus(Status.PENDING);
        item3.setPrice(new BigDecimal("150.00"));
        item3.setCategory("Electronics");

        List<WishlistItem> items = Arrays.asList(item1, item2, item3);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalWishlist());
        assertEquals(1L, result.getTotalPurchased());
        assertEquals(new BigDecimal("250.00"), result.getTotalPriceWishlist());
        assertEquals(new BigDecimal("200.00"), result.getTotalPricePurchased());
        assertEquals(2L, result.getCountByCategory().get("Electronics"));
        assertEquals(1L, result.getCountByCategory().get("Books"));
        assertEquals(new BigDecimal("250.00"), result.getPriceByCategory().get("Electronics"));
        assertEquals(new BigDecimal("200.00"), result.getPriceByCategory().get("Books"));
    }

    @Test
    void testGetStatsWithNullPrice() {
        // Arrange
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.PENDING);
        item1.setPrice(null);
        item1.setCategory("Electronics");

        List<WishlistItem> items = Arrays.asList(item1);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotalWishlist());
        assertEquals(BigDecimal.ZERO, result.getTotalPriceWishlist());
    }

    @Test
    void testGetStatsWithNullCategory() {
        // Arrange
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.PENDING);
        item1.setPrice(new BigDecimal("100.00"));
        item1.setCategory(null);

        List<WishlistItem> items = Arrays.asList(item1);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotalWishlist());
        assertTrue(result.getCountByCategory().isEmpty());
        assertTrue(result.getPriceByCategory().isEmpty());
    }

    @Test
    void testGetStatsEmptyList() {
        // Arrange
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(new ArrayList<>());

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotalWishlist());
        assertEquals(0L, result.getTotalPurchased());
        assertEquals(BigDecimal.ZERO, result.getTotalPriceWishlist());
        assertEquals(BigDecimal.ZERO, result.getTotalPricePurchased());
        assertTrue(result.getCountByCategory().isEmpty());
        assertTrue(result.getPriceByCategory().isEmpty());
    }

    @Test
    void testGetStatsWithMixedNullValues() {
        // Arrange - Test untuk coverage lambdaGetStats$3 (75% -> 100%)
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.PENDING);
        item1.setPrice(new BigDecimal("100.00"));
        item1.setCategory("Electronics");

        WishlistItem item2 = new WishlistItem();
        item2.setStatus(Status.PENDING);
        item2.setPrice(null); // price null tapi category ada
        item2.setCategory("Electronics");

        WishlistItem item3 = new WishlistItem();
        item3.setStatus(Status.BOUGHT);
        item3.setPrice(new BigDecimal("200.00"));
        item3.setCategory(null); // category null

        WishlistItem item4 = new WishlistItem();
        item4.setStatus(Status.PENDING);
        item4.setPrice(new BigDecimal("50.00"));
        item4.setCategory("Books");

        List<WishlistItem> items = Arrays.asList(item1, item2, item3, item4);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getTotalWishlist());
        assertEquals(1L, result.getTotalPurchased());
        
        // Check category counts - item2 has null price but counted
        assertEquals(2L, result.getCountByCategory().get("Electronics"));
        assertEquals(1L, result.getCountByCategory().get("Books"));
        
        // Check category prices - item2 excluded (null price)
        assertEquals(new BigDecimal("100.00"), result.getPriceByCategory().get("Electronics"));
        assertEquals(new BigDecimal("50.00"), result.getPriceByCategory().get("Books"));
    }

    @Test
    void testGetStatsWithCategoryNullAndPriceNotNull() {
        // Arrange - Branch: category != null tapi price == null di priceByCategory
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.PENDING);
        item1.setPrice(null);
        item1.setCategory("Electronics");

        WishlistItem item2 = new WishlistItem();
        item2.setStatus(Status.PENDING);
        item2.setPrice(new BigDecimal("100.00"));
        item2.setCategory("Books");

        List<WishlistItem> items = Arrays.asList(item1, item2);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalWishlist());
        assertEquals(1L, result.getCountByCategory().get("Electronics"));
        assertEquals(1L, result.getCountByCategory().get("Books"));
        // Electronics tidak masuk di priceByCategory karena price null
        assertNull(result.getPriceByCategory().get("Electronics"));
        assertEquals(new BigDecimal("100.00"), result.getPriceByCategory().get("Books"));
    }

    @Test
    void testGetStatsWithOnlyCategoryNull() {
        // Arrange - Test untuk menutup branch: category == null (skip filter pertama)
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.PENDING);
        item1.setPrice(new BigDecimal("100.00"));
        item1.setCategory(null); // category null, tidak masuk grouping

        WishlistItem item2 = new WishlistItem();
        item2.setStatus(Status.PENDING);
        item2.setPrice(new BigDecimal("50.00"));
        item2.setCategory(null); // category null, tidak masuk grouping

        List<WishlistItem> items = Arrays.asList(item1, item2);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalWishlist());
        assertEquals(new BigDecimal("150.00"), result.getTotalPriceWishlist());
        assertTrue(result.getCountByCategory().isEmpty());
        assertTrue(result.getPriceByCategory().isEmpty());
    }

    @Test
    void testGetStatsWithOnlyPriceNull() {
        // Arrange - Test untuk branch: price == null di filter kedua
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.PENDING);
        item1.setPrice(null); // price null
        item1.setCategory("Electronics");

        WishlistItem item2 = new WishlistItem();
        item2.setStatus(Status.PENDING);
        item2.setPrice(null); // price null
        item2.setCategory("Books");

        List<WishlistItem> items = Arrays.asList(item1, item2);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotalWishlist());
        assertEquals(BigDecimal.ZERO, result.getTotalPriceWishlist());
        // countByCategory masih ada karena hanya check category != null
        assertEquals(1L, result.getCountByCategory().get("Electronics"));
        assertEquals(1L, result.getCountByCategory().get("Books"));
        // priceByCategory kosong karena semua price null
        assertTrue(result.getPriceByCategory().isEmpty());
    }

    @Test
    void testGetStatsWithBoughtItemButNullPrice() {
        // Arrange - Test untuk branch: status == BOUGHT && price == null (line 119)
        WishlistItem item1 = new WishlistItem();
        item1.setStatus(Status.BOUGHT);
        item1.setPrice(null); // BOUGHT tapi price null
        item1.setCategory("Electronics");

        WishlistItem item2 = new WishlistItem();
        item2.setStatus(Status.BOUGHT);
        item2.setPrice(new BigDecimal("200.00"));
        item2.setCategory("Books");

        WishlistItem item3 = new WishlistItem();
        item3.setStatus(Status.PENDING);
        item3.setPrice(new BigDecimal("100.00"));
        item3.setCategory("Electronics");

        List<WishlistItem> items = Arrays.asList(item1, item2, item3);
        when(repository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(items);

        // Act
        WishlistStats result = wishlistService.getStats(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotalWishlist()); // item3
        assertEquals(2L, result.getTotalPurchased()); // item1 dan item2
        assertEquals(new BigDecimal("100.00"), result.getTotalPriceWishlist()); // item3
        assertEquals(new BigDecimal("200.00"), result.getTotalPricePurchased()); // hanya item2 (item1 price null)
    }

    @Test
    void testAddItemWithEmptyImageFile() throws IOException {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image", "", "image/jpeg", new byte[0]
        );
        testForm.setImageFile(emptyFile);
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.addItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
        verify(fileStorageService, never()).storeFile(any(), any());
    }

    @Test
    void testUpdateItemWithEmptyImageFile() throws IOException {
        // Arrange
        testForm.setId(testItem.getId());
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image", "", "image/jpeg", new byte[0]
        );
        testForm.setImageFile(emptyFile);
        when(repository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(repository.save(any(WishlistItem.class))).thenReturn(testItem);

        // Act
        wishlistService.updateItem(testUser, testForm);

        // Assert
        verify(repository, times(1)).save(any(WishlistItem.class));
        verify(fileStorageService, never()).storeFile(any(), any());
    }
}