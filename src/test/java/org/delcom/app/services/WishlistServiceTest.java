package org.delcom.app.services;

import org.delcom.app.dto.WishlistForm;
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
import java.util.Optional;
import java.util.UUID;

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
    private WishlistService service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(UUID.randomUUID());
    }

    @Test
    void testAddItem() throws IOException {
        WishlistForm form = new WishlistForm();
        form.setName("Test Item");
        form.setPrice(BigDecimal.TEN);
        
        // Mock perilaku repository
        when(repository.save(any(WishlistItem.class))).thenAnswer(i -> i.getArguments()[0]);

        // Jalankan
        service.addItem(user, form);

        // Verifikasi repository dipanggil
        verify(repository, times(1)).save(any());
    }

    @Test
    void testChangeStatus() {
        UUID id = UUID.randomUUID();
        WishlistItem item = new WishlistItem();
        item.setId(id);
        item.setStatus(Status.PENDING);

        when(repository.findById(id)).thenReturn(Optional.of(item));

        service.changeStatus(id);

        assertEquals(Status.BOUGHT, item.getStatus());
        verify(repository).save(item);
    }
}