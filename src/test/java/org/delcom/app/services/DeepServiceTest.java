package org.delcom.app.services;

import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeepServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks AuthService authService;

    @Test
    void testLoadUserByUsername_NotFound() {
        // Skenario: Login dengan email yang TIDAK ADA di database
        // Harapannya: Error UsernameNotFoundException
        String email = "hantu@del.ac.id";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(email);
        });
    }
}