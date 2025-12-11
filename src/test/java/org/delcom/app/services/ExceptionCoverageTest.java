package org.delcom.app.services;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.entities.User;
import org.delcom.app.repositories.WishlistItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionCoverageTest {

    @Mock
    private WishlistItemRepository repository;

    @InjectMocks
    private WishlistService wishlistService;

    @Test
    void testUpdateItem_NotFound() {
        // Simulasi barang tidak ditemukan saat update
        UUID id = UUID.randomUUID();
        WishlistForm form = new WishlistForm();
        form.setId(id);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            wishlistService.updateItem(new User(), form);
        });
    }

    @Test
    void testChangeStatus_NotFound() {
        // Simulasi barang tidak ditemukan saat ganti status
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            wishlistService.changeStatus(id); // Harus error/exception
        });
    }
}