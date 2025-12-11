package org.delcom.app.services;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceBoostTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    void testUpdateUser_WithStringId_Success() {
        UUID id = UUID.randomUUID();
        User existing = new User();
        existing.setId(id);
        
        User updateData = new User();
        updateData.setName("New Name");
        updateData.setEmail("new@mail.com");
        updateData.setPassword("newpass");
        updateData.setRole("ADMIN");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("hashed");

        // Panggil method yang overload (String ID)
        userService.updateUser(id.toString(), updateData);

        verify(userRepository).save(existing);
    }

    @Test
    void testUpdateUser_InvalidUUID() {
        // Test ID ngawur, harusnya gak error tapi nge-print log
        userService.updateUser("id-palsu", new User());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        userService.findById(id);
        verify(userRepository).findById(id);
    }
}