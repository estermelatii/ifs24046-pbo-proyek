package org.delcom.app.controllers;

import org.delcom.app.dto.LoginForm;
import org.delcom.app.dto.RegisterForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.AuthService;
import org.delcom.app.services.UserService;
import org.delcom.app.services.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private AuthService authService;
    @MockBean private UserService userService;
    @MockBean private WishlistService wishlistService;

    @Test
    void testShowPages() throws Exception {
        mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
        mockMvc.perform(get("/auth/register")).andExpect(status().isOk());
    }

    @Test
    void testProcessLogin_Success() throws Exception {
        when(authService.authenticateUser(any(), any())).thenReturn("dummy-token");

        mockMvc.perform(post("/auth/login")
                .flashAttr("loginForm", new LoginForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/wishlist"));
    }

    @Test
    void testProcessLogin_Failed() throws Exception {
        when(authService.authenticateUser(any(), any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/auth/login")
                .flashAttr("loginForm", new LoginForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=true"));
    }

    @Test
    void testProcessRegister_Success() throws Exception {
        mockMvc.perform(post("/auth/register")
                .flashAttr("registerForm", new RegisterForm()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
        
        verify(userService).registerNewUser(any(User.class));
    }
}