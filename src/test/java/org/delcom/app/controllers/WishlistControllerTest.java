package org.delcom.app.controllers;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.dto.WishlistStats;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistController Tests")
class WishlistControllerTests {

    @Mock
    private WishlistService wishlistService;

    @Mock
    private AuthService authService;

    @Mock
    private Model model;

    @InjectMocks
    private WishlistController wishlistController;

    private User testUser;
    private WishlistItem testItem;
    private List<WishlistItem> testItems;
    private WishlistForm testForm;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        // Setup test wishlist item
        testItem = new WishlistItem();
        testItem.setId(UUID.randomUUID());
        testItem.setName("PlayStation 5");
        testItem.setPrice(new BigDecimal("7999000"));
        testItem.setSavedAmount(new BigDecimal("2000000"));
        testItem.setCategory("Gaming");
        testItem.setTargetDate(LocalDate.now().plusMonths(6));
        testItem.setShopUrl("https://example.com/ps5");
        testItem.setDescription("Next-gen gaming console");
        testItem.setUser(testUser);

        // Setup test items list
        testItems = new ArrayList<>();
        testItems.add(testItem);

        // Setup test form
        testForm = new WishlistForm();
        testForm.setName("Nintendo Switch");
        testForm.setPrice(new BigDecimal("4500000"));
        testForm.setSavedAmount(new BigDecimal("1000000"));
        testForm.setCategory("Gaming");
        testForm.setTargetDate(LocalDate.now().plusMonths(3));
        testForm.setShopUrl("https://example.com/switch");
        testForm.setDescription("Portable gaming console");
    }

    // ========== TESTS FOR MISSED METHODS (RED - Need Coverage) ==========

    @Test
    @DisplayName("Test viewDetail() with valid user and item")
    void testViewDetailSuccess() {
        // Arrange
        UUID itemId = testItem.getId();
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getAllItems(testUser)).thenReturn(testItems);

        // Act
        String result = wishlistController.viewDetail(itemId, model);

        // Assert
        assertEquals("wishlist/detail", result);
        verify(authService).getCurrentUser();
        verify(wishlistService).getAllItems(testUser);
        verify(model).addAttribute("item", testItem);
    }

    @Test
    @DisplayName("Test viewDetail() with no authenticated user - should redirect to login")
    void testViewDetailNoUser() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(authService.getCurrentUser()).thenReturn(null);

        // Act
        String result = wishlistController.viewDetail(itemId, model);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(authService).getCurrentUser();
        verify(wishlistService, never()).getAllItems(any());
    }

    @Test
    @DisplayName("Test viewDetail() with non-existent item - should redirect to wishlist")
    void testViewDetailItemNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getAllItems(testUser)).thenReturn(testItems);

        // Act
        String result = wishlistController.viewDetail(nonExistentId, model);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(authService).getCurrentUser();
        verify(wishlistService).getAllItems(testUser);
        verify(model, never()).addAttribute(eq("item"), any());
    }

    @Test
    @DisplayName("Test statistics() with valid user")
    void testStatisticsSuccess() {
        // Arrange
        // Tidak perlu instance WishlistStats yang real, cukup mock return value
        WishlistStats mockStats = mock(WishlistStats.class);
        
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getStats(testUser)).thenReturn(mockStats);
        when(wishlistService.getAllItems(testUser)).thenReturn(testItems);

        // Act
        String result = wishlistController.statistics(model);

        // Assert
        assertEquals("wishlist/stats", result);
        verify(authService).getCurrentUser();
        verify(wishlistService).getStats(testUser);
        verify(wishlistService).getAllItems(testUser);
        verify(model).addAttribute("stats", mockStats);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute("items", testItems);
    }

    @Test
    @DisplayName("Test statistics() with no authenticated user - should redirect to login")
    void testStatisticsNoUser() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(null);

        // Act
        String result = wishlistController.statistics(model);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(authService).getCurrentUser();
        verify(wishlistService, never()).getStats(any());
    }

    // ========== TESTS FOR COVERED METHODS (GREEN - Already Covered) ==========

    @Test
    @DisplayName("Test dashboard() with authenticated user")
    void testDashboardSuccess() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getAllItems(testUser)).thenReturn(testItems);
        when(wishlistService.countPending(testUser)).thenReturn(5L);
        when(wishlistService.countBought(testUser)).thenReturn(3L);

        // Act
        String result = wishlistController.dashboard(model);

        // Assert
        assertEquals("wishlist/dashboard", result);
        verify(authService).getCurrentUser();
        verify(wishlistService).getAllItems(testUser);
        verify(wishlistService).countPending(testUser);
        verify(wishlistService).countBought(testUser);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute("items", testItems);
        verify(model).addAttribute("totalPending", 5L);
        verify(model).addAttribute("totalBought", 3L);
    }

    @Test
    @DisplayName("Test dashboard() with no authenticated user - should redirect to login")
    void testDashboardNoUser() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(null);

        // Act
        String result = wishlistController.dashboard(model);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(authService).getCurrentUser();
        verify(wishlistService, never()).getAllItems(any());
    }

    @Test
    @DisplayName("Test showAddForm()")
    void testShowAddForm() {
        // Act
        String result = wishlistController.showAddForm(model);

        // Assert
        assertEquals("wishlist/form", result);
        verify(model).addAttribute(eq("wishlistForm"), any(WishlistForm.class));
        verify(model).addAttribute("isEdit", false);
    }

    @Test
    @DisplayName("Test processAdd() with authenticated user")
    void testProcessAddSuccess() throws IOException {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(wishlistService).addItem(testUser, testForm);

        // Act
        String result = wishlistController.processAdd(testForm);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(authService).getCurrentUser();
        verify(wishlistService).addItem(testUser, testForm);
    }

    @Test
    @DisplayName("Test processAdd() with no authenticated user - should redirect to login")
    void testProcessAddNoUser() throws IOException {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(null);

        // Act
        String result = wishlistController.processAdd(testForm);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(authService).getCurrentUser();
        verify(wishlistService, never()).addItem(any(), any());
    }

    @Test
    @DisplayName("Test showEditForm() with existing item")
    void testShowEditFormSuccess() {
        // Arrange
        UUID itemId = testItem.getId();
        when(wishlistService.findById(itemId)).thenReturn(testItem);

        // Act
        String result = wishlistController.showEditForm(itemId, model);

        // Assert
        assertEquals("wishlist/form", result);
        verify(wishlistService).findById(itemId);
        verify(model).addAttribute(eq("wishlistForm"), any(WishlistForm.class));
        verify(model).addAttribute("isEdit", true);
    }

    @Test
    @DisplayName("Test showEditForm() with non-existent item - should redirect to wishlist")
    void testShowEditFormItemNotFound() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(wishlistService.findById(itemId)).thenReturn(null);

        // Act
        String result = wishlistController.showEditForm(itemId, model);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(wishlistService).findById(itemId);
        verify(model, never()).addAttribute(eq("wishlistForm"), any());
    }

    @Test
    @DisplayName("Test processUpdate() with authenticated user")
    void testProcessUpdateSuccess() throws IOException {
        // Arrange
        testForm.setId(testItem.getId());
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(wishlistService).updateItem(testUser, testForm);

        // Act
        String result = wishlistController.processUpdate(testForm);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(authService).getCurrentUser();
        verify(wishlistService).updateItem(testUser, testForm);
    }

    @Test
    @DisplayName("Test processUpdate() with no authenticated user - should redirect to login")
    void testProcessUpdateNoUser() throws IOException {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(null);

        // Act
        String result = wishlistController.processUpdate(testForm);

        // Assert
        assertEquals("redirect:/auth/login", result);
        verify(authService).getCurrentUser();
        verify(wishlistService, never()).updateItem(any(), any());
    }

    @Test
    @DisplayName("Test toggleStatus()")
    void testToggleStatus() {
        // Arrange
        UUID itemId = testItem.getId();
        doNothing().when(wishlistService).changeStatus(itemId);

        // Act
        String result = wishlistController.toggleStatus(itemId);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(wishlistService).changeStatus(itemId);
    }

    @Test
    @DisplayName("Test deleteItem()")
    void testDeleteItem() {
        // Arrange
        UUID itemId = testItem.getId();
        doNothing().when(wishlistService).deleteItem(itemId);

        // Act
        String result = wishlistController.deleteItem(itemId);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(wishlistService).deleteItem(itemId);
    }

    // ========== ADDITIONAL INTEGRATION & EDGE CASE TESTS ==========

    @Test
    @DisplayName("Test showEditForm() loads all form fields correctly")
    void testShowEditFormLoadsAllFields() {
        // Arrange
        UUID itemId = testItem.getId();
        when(wishlistService.findById(itemId)).thenReturn(testItem);

        // Act
        String result = wishlistController.showEditForm(itemId, model);

        // Assert
        assertEquals("wishlist/form", result);
        verify(model).addAttribute(eq("wishlistForm"), argThat(form -> {
            WishlistForm f = (WishlistForm) form;
            return f.getId().equals(testItem.getId()) &&
                   f.getName().equals(testItem.getName()) &&
                   f.getPrice().equals(testItem.getPrice()) &&
                   f.getSavedAmount().equals(testItem.getSavedAmount()) &&
                   f.getCategory().equals(testItem.getCategory()) &&
                   f.getTargetDate().equals(testItem.getTargetDate()) &&
                   f.getShopUrl().equals(testItem.getShopUrl()) &&
                   f.getDescription().equals(testItem.getDescription());
        }));
    }

    @Test
    @DisplayName("Test processAdd() with IOException - should propagate exception")
    void testProcessAddWithIOException() throws IOException {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doThrow(new IOException("File upload failed")).when(wishlistService).addItem(testUser, testForm);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            wishlistController.processAdd(testForm);
        });
        verify(authService).getCurrentUser();
        verify(wishlistService).addItem(testUser, testForm);
    }

    @Test
    @DisplayName("Test processUpdate() with IOException - should propagate exception")
    void testProcessUpdateWithIOException() throws IOException {
        // Arrange
        testForm.setId(testItem.getId());
        when(authService.getCurrentUser()).thenReturn(testUser);
        doThrow(new IOException("File upload failed")).when(wishlistService).updateItem(testUser, testForm);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            wishlistController.processUpdate(testForm);
        });
        verify(authService).getCurrentUser();
        verify(wishlistService).updateItem(testUser, testForm);
    }

    @Test
    @DisplayName("Test dashboard() with empty wishlist")
    void testDashboardWithEmptyWishlist() {
        // Arrange
        List<WishlistItem> emptyList = new ArrayList<>();
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getAllItems(testUser)).thenReturn(emptyList);
        when(wishlistService.countPending(testUser)).thenReturn(0L);
        when(wishlistService.countBought(testUser)).thenReturn(0L);

        // Act
        String result = wishlistController.dashboard(model);

        // Assert
        assertEquals("wishlist/dashboard", result);
        verify(model).addAttribute("items", emptyList);
        verify(model).addAttribute("totalPending", 0L);
        verify(model).addAttribute("totalBought", 0L);
    }

    @Test
    @DisplayName("Test statistics() returns correct view name")
    void testStatisticsReturnsCorrectView() {
        // Arrange
        WishlistStats mockStats = mock(WishlistStats.class);
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getStats(testUser)).thenReturn(mockStats);
        when(wishlistService.getAllItems(testUser)).thenReturn(new ArrayList<>());

        // Act
        String result = wishlistController.statistics(model);

        // Assert
        assertEquals("wishlist/stats", result);
        verify(model).addAttribute(eq("stats"), any());
    }

    @Test
    @DisplayName("Test viewDetail() with empty wishlist - should redirect")
    void testViewDetailWithEmptyWishlist() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getAllItems(testUser)).thenReturn(new ArrayList<>());

        // Act
        String result = wishlistController.viewDetail(itemId, model);

        // Assert
        assertEquals("redirect:/wishlist", result);
        verify(model, never()).addAttribute(eq("item"), any());
    }

    @Test
    @DisplayName("Test lambda in viewDetail() filters correctly with multiple items")
    void testViewDetailLambdaFilteringMultipleItems() {
        // Arrange
        WishlistItem item2 = new WishlistItem();
        item2.setId(UUID.randomUUID());
        item2.setName("Xbox Series X");
        
        WishlistItem item3 = new WishlistItem();
        item3.setId(UUID.randomUUID());
        item3.setName("Nintendo Switch");
        
        List<WishlistItem> multipleItems = new ArrayList<>();
        multipleItems.add(testItem);
        multipleItems.add(item2);
        multipleItems.add(item3);

        UUID targetId = testItem.getId();
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(wishlistService.getAllItems(testUser)).thenReturn(multipleItems);

        // Act
        String result = wishlistController.viewDetail(targetId, model);

        // Assert
        assertEquals("wishlist/detail", result);
        verify(model).addAttribute("item", testItem);
    }

    @Test
    @DisplayName("Test WishlistController default constructor")
    void testWishlistControllerConstructor() {
        // Act
        WishlistController controller = new WishlistController();

        // Assert
        assertNotNull(controller);
    }

    @Test
    @DisplayName("Test processAdd() ensures redirect path is correct")
    void testProcessAddRedirectPath() throws IOException {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(wishlistService).addItem(any(), any());

        // Act
        String result = wishlistController.processAdd(testForm);

        // Assert
        assertTrue(result.startsWith("redirect:"));
        assertEquals("redirect:/wishlist", result);
    }

    @Test
    @DisplayName("Test processUpdate() ensures redirect path is correct")
    void testProcessUpdateRedirectPath() throws IOException {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(testUser);
        doNothing().when(wishlistService).updateItem(any(), any());

        // Act
        String result = wishlistController.processUpdate(testForm);

        // Assert
        assertTrue(result.startsWith("redirect:"));
        assertEquals("redirect:/wishlist", result);
    }

    @Test
    @DisplayName("Test toggleStatus() ensures redirect path is correct")
    void testToggleStatusRedirectPath() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        doNothing().when(wishlistService).changeStatus(itemId);

        // Act
        String result = wishlistController.toggleStatus(itemId);

        // Assert
        assertTrue(result.startsWith("redirect:"));
        assertEquals("redirect:/wishlist", result);
    }

    @Test
    @DisplayName("Test deleteItem() ensures redirect path is correct")
    void testDeleteItemRedirectPath() {
        // Arrange
        UUID itemId = UUID.randomUUID();
        doNothing().when(wishlistService).deleteItem(itemId);

        // Act
        String result = wishlistController.deleteItem(itemId);

        // Assert
        assertTrue(result.startsWith("redirect:"));
        assertEquals("redirect:/wishlist", result);
    }
}