package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.delcom.app.entities.User;
import org.delcom.app.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setPassword("plainPass");

        // Mocking
        when(passwordEncoder.encode("plainPass")).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Panggil method yang sesuai dengan Service baru
        User result = userService.registerUser(user); 

        // Verifikasi
        // Harapan: password berubah jadi hashedPass (karena mock diatas mengembalikan apa yang disave)
        // Tapi karena ini unit test mock, kita cek interaksinya
        verify(passwordEncoder).encode("plainPass");
        verify(userRepository).save(user);
    }

    @Test
    void testFindByEmail() {
        User user = new User();
        user.setEmail("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@test.com");
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }
}