package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        user.setName("Test User");
    }

    @Test
    void testAddItem() throws IOException {
        WishlistForm form = new WishlistForm();
        form.setName("Test Item");
        form.setPrice(BigDecimal.TEN);
        form.setCategory("Elektronik");
        form.setTargetDate(LocalDate.now());
        
        // FIX: Tambahkan casting (WishlistItem) agar tidak error type mismatch
        when(repository.save(any(WishlistItem.class))).thenAnswer(i -> {
            WishlistItem item = (WishlistItem) i.getArguments()[0]; 
            item.setId(UUID.randomUUID()); 
            return item;
        });

        service.addItem(user, form);

        verify(repository, times(1)).save(any(WishlistItem.class));
    }

    @Test
    void testGetAllItems() {
        when(repository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(new WishlistItem(), new WishlistItem()));
        List<WishlistItem> items = service.getAllItems(user);
        assertEquals(2, items.size());
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

    @Test
    void testUpdateItem() throws IOException {
        UUID id = UUID.randomUUID();
        WishlistItem existing = new WishlistItem();
        existing.setId(id);
        existing.setName("Old Name");
        existing.setPrice(BigDecimal.TEN);
        existing.setSavedAmount(BigDecimal.ZERO);
        existing.setStatus(Status.PENDING);

        WishlistForm form = new WishlistForm();
        form.setId(id);
        form.setName("New Name");
        form.setPrice(new BigDecimal("100"));
        form.setSavedAmount(new BigDecimal("100")); 

        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.updateItem(user, form);

        assertEquals("New Name", existing.getName());
        assertEquals(Status.BOUGHT, existing.getStatus()); 
        verify(repository).save(existing);
    }

    @Test
    void testDeleteItem() {
        UUID id = UUID.randomUUID();
        WishlistItem item = new WishlistItem();
        item.setImageUrl("gambar.jpg");

        when(repository.findById(id)).thenReturn(Optional.of(item));

        service.deleteItem(id);

        verify(fileStorageService).deleteFile("gambar.jpg");
        verify(repository).deleteById(id);
    }
    
    @Test
    void testCountMethods() {
        service.countPending(user);
        service.countBought(user);
        verify(repository, times(1)).countByUserAndStatus(user, Status.PENDING);
        verify(repository, times(1)).countByUserAndStatus(user, Status.BOUGHT);
    }
}