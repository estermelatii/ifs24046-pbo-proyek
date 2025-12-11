package org.delcom.app.controllers;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.dto.WishlistStats;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WishlistControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private WishlistService wishlistService;
    @MockBean private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        when(authService.getCurrentUser()).thenReturn(user);
    }

    @Test
    void testDashboard() throws Exception {
        when(wishlistService.getAllItems(user)).thenReturn(List.of(new WishlistItem()));
        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk())
                .andExpect(view().name("wishlist/dashboard"));
    }

    @Test
    void testStats() throws Exception {
        when(wishlistService.getStats(user)).thenReturn(new WishlistStats());
        mockMvc.perform(get("/wishlist/stats"))
                .andExpect(status().isOk())
                .andExpect(view().name("wishlist/stats"));
    }

    @Test
    void testShowAddForm() throws Exception {
        mockMvc.perform(get("/wishlist/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("wishlist/form"));
    }

    @Test
    void testProcessAdd() throws Exception {
        mockMvc.perform(post("/wishlist/add")
                .flashAttr("wishlistForm", new WishlistForm()))
                .andExpect(status().is3xxRedirection());
        verify(wishlistService).addItem(any(), any());
    }

    @Test
    void testShowEditForm() throws Exception {
        UUID id = UUID.randomUUID();
        WishlistItem item = new WishlistItem();
        item.setId(id);
        when(wishlistService.findById(id)).thenReturn(item);

        mockMvc.perform(get("/wishlist/edit/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("wishlist/form"));
    }
    
    @Test
    void testShowEditForm_NotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(wishlistService.findById(id)).thenReturn(null);

        mockMvc.perform(get("/wishlist/edit/" + id))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProcessUpdate() throws Exception {
        mockMvc.perform(post("/wishlist/update")
                .flashAttr("wishlistForm", new WishlistForm()))
                .andExpect(status().is3xxRedirection());
        verify(wishlistService).updateItem(any(), any());
    }

    @Test
    void testToggleStatus() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/wishlist/" + id + "/toggle"))
                .andExpect(status().is3xxRedirection());
        verify(wishlistService).changeStatus(id);
    }

    @Test
    void testDeleteItem() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/wishlist/" + id + "/delete"))
                .andExpect(status().is3xxRedirection());
        verify(wishlistService).deleteItem(id);
    }
    
    @Test
    void testViewDetail() throws Exception {
        UUID id = UUID.randomUUID();
        WishlistItem item = new WishlistItem();
        item.setId(id);
        // Mocking list yang berisi item yang dicari
        when(wishlistService.getAllItems(user)).thenReturn(List.of(item));

        mockMvc.perform(get("/wishlist/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("wishlist/detail"));
    }
}