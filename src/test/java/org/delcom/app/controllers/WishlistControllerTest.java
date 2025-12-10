package org.delcom.app.controllers;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class) // Hanya load Controller ini
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc; // Alat untuk menembak URL (Fake Browser)

    @MockBean
    private WishlistService wishlistService; // Service palsu

    @MockBean
    private AuthService authService; // Auth palsu

    private User dummyUser;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId(UUID.randomUUID());
        dummyUser.setName("Ester");
        dummyUser.setEmail("ester@del.ac.id");
    }

    @Test
    @WithMockUser(username = "ester", roles = "USER") // Pura-pura sudah login
    void testShowDashboard() throws Exception {
        // Skenario: User login dan buka dashboard
        when(authService.getCurrentUser()).thenReturn(dummyUser);
        when(wishlistService.getAllItems(dummyUser)).thenReturn(List.of(new WishlistItem()));

        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk()) // Kode 200 OK
                .andExpect(view().name("wishlist/dashboard")) // File HTML yang direturn
                .andExpect(model().attributeExists("items")) // Pastikan ada data items
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "ester", roles = "USER")
    void testAddWishlist_Success() throws Exception {
        // Skenario: Submit form tambah barang
        when(authService.getCurrentUser()).thenReturn(dummyUser);

        mockMvc.perform(post("/wishlist/add")
                .with(csrf()) // Wajib untuk form POST di Spring Security
                .flashAttr("wishlistForm", new WishlistForm()) // Kirim data form kosong
                .param("name", "Laptop")
                .param("price", "10000000"))
                .andExpect(status().is3xxRedirection()) // Harus redirect setelah save
                .andExpect(redirectedUrl("/wishlist"));
    }

    @Test
    void testAccessWithoutLogin() throws Exception {
        // Skenario: Belum login coba akses dashboard (harus gagal/redirect login)
        mockMvc.perform(get("/wishlist"))
                .andExpect(status().is3xxRedirection()) // Redirect ke login page
                .andExpect(redirectedUrl("http://localhost/login")); 
                // Note: URL redirect default Spring Security biasanya ke /login
    }
}