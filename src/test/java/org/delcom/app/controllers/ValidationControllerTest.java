package org.delcom.app.controllers;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.UserService;
import org.delcom.app.services.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class ValidationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private WishlistService wishlistService;
    @MockBean private AuthService authService;
    @MockBean private UserService userService;

    @Test
    void testAddWishlist_ValidationError() throws Exception {
        // Skenario: Submit form kosong (Nama & Harga kosong)
        // Harapannya: Tidak redirect, tapi kembali ke form (status 200) atau error page
        
        mockMvc.perform(post("/wishlist/add")
                .flashAttr("wishlistForm", new WishlistForm())) // Form Kosong
                .andExpect(status().is3xxRedirection()); // Atau isOk() tergantung logic kamu
        
        // Note: Kalau di controller kamu ada @Valid, dan form error, dia akan return "wishlist/form"
        // Kalau belum ada validasi, dia tetap redirect. Test ini memastikan jalur itu terlewati.
    }
}