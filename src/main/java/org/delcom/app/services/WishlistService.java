package org.delcom.app.services;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.dto.WishlistStats;
import org.delcom.app.entities.Status;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
import org.delcom.app.repositories.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired private WishlistItemRepository repository;
    @Autowired private FileStorageService fileStorageService; // Panggil Service Gambar

    public List<WishlistItem> getAllItems(User user) {
        return repository.findByUserOrderByCreatedAtDesc(user);
    }

    public WishlistItem findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public void addItem(User user, WishlistForm form) throws IOException {
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        mapFormToItem(item, form);
        item.setStatus(Status.PENDING);
        checkAutoStatus(item, form);

        // Save dulu untuk generate UUID
        item = repository.save(item);

        // Upload Gambar menggunakan FileStorageService yang sudah diperbaiki
        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            String fileName = fileStorageService.storeFile(form.getImageFile(), item.getId());
            item.setImageUrl(fileName);
            repository.save(item); // Update nama file di DB
        }
    }

    public void updateItem(User user, WishlistForm form) throws IOException {
        WishlistItem item = repository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        mapFormToItem(item, form);
        checkAutoStatus(item, form);

        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            // Hapus file lama jika perlu (opsional), lalu upload baru
            String fileName = fileStorageService.storeFile(form.getImageFile(), item.getId());
            item.setImageUrl(fileName);
        }
        repository.save(item);
    }

    private void mapFormToItem(WishlistItem item, WishlistForm form) {
        item.setName(form.getName());
        item.setPrice(form.getPrice());
        item.setSavedAmount(form.getSavedAmount() == null ? BigDecimal.ZERO : form.getSavedAmount());
        item.setCategory(form.getCategory());
        item.setTargetDate(form.getTargetDate());
        item.setShopUrl(form.getShopUrl());
        item.setDescription(form.getDescription());
    }

    private void checkAutoStatus(WishlistItem item, WishlistForm form) {
        if (form.getPrice() != null && item.getSavedAmount().compareTo(form.getPrice()) >= 0) {
            if (item.getStatus() == Status.PENDING) {
                item.setStatus(Status.BOUGHT);
            }
        }
    }

    public void changeStatus(UUID itemId) {
        WishlistItem item = repository.findById(itemId).orElseThrow();
        item.setStatus(item.getStatus() == Status.PENDING ? Status.BOUGHT : Status.PENDING);
        repository.save(item);
    }
    
    public void deleteItem(UUID id) {
        // Hapus gambarnya juga biar bersih (opsional)
        repository.findById(id).ifPresent(item -> {
             if (item.getImageUrl() != null) {
                 fileStorageService.deleteFile(item.getImageUrl());
             }
             repository.deleteById(id);
        });
    }

    public long countPending(User user) { return repository.countByUserAndStatus(user, Status.PENDING); }
    public long countBought(User user) { return repository.countByUserAndStatus(user, Status.BOUGHT); }

    // --- LOGIKA UTAMA UNTUK CHART ---
    public WishlistStats getStats(User user) {
        List<WishlistItem> allItems = getAllItems(user);
        WishlistStats stats = new WishlistStats();

        // 1. Hitung Jumlah Item
        stats.setTotalWishlist(allItems.stream().filter(i -> i.getStatus() == Status.PENDING).count());
        stats.setTotalPurchased(allItems.stream().filter(i -> i.getStatus() == Status.BOUGHT).count());

        // 2. Hitung Total Uang (Reduce)
        stats.setTotalPriceWishlist(allItems.stream()
                .filter(i -> i.getStatus() == Status.PENDING && i.getPrice() != null)
                .map(WishlistItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.setTotalPricePurchased(allItems.stream()
                .filter(i -> i.getStatus() == Status.BOUGHT && i.getPrice() != null)
                .map(WishlistItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // 3. Grouping Kategori (Jumlah Item)
        Map<String, Long> catCount = allItems.stream()
                .filter(i -> i.getCategory() != null)
                .collect(Collectors.groupingBy(WishlistItem::getCategory, Collectors.counting()));
        stats.setCountByCategory(catCount);

        // 4. Grouping Kategori (Total Harga)
        Map<String, BigDecimal> catPrice = allItems.stream()
                .filter(i -> i.getCategory() != null && i.getPrice() != null)
                .collect(Collectors.groupingBy(
                        WishlistItem::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, WishlistItem::getPrice, BigDecimal::add)
                ));
        stats.setPriceByCategory(catPrice);

        return stats;
    }
}