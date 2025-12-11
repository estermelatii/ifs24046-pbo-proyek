package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthFormsTest {

    @Test
    void testLoginForm() {
        LoginForm form = new LoginForm();
        form.setEmail("test@email.com");
        form.setPassword("rahasia");

        assertEquals("test@email.com", form.getEmail());
        assertEquals("rahasia", form.getPassword());
    }

    @Test
    void testRegisterForm() {
        RegisterForm form = new RegisterForm();
        form.setName("Budi");
        form.setEmail("budi@email.com");
        form.setPassword("123456");

        assertEquals("Budi", form.getName());
        assertEquals("budi@email.com", form.getEmail());
        assertEquals("123456", form.getPassword());
    }
}