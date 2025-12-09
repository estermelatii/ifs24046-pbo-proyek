package org.delcom.app.services;

import org.delcom.app.dto.WishlistForm;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WishlistItem;
import org.delcom.app.entities.Status;
import org.delcom.app.repositories.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class WishlistService {

    @Autowired 
    private WishlistItemRepository repository;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public List<WishlistItem> getAllItems(User user) {
        return repository.findByUserOrderByCreatedAtDesc(user);
    }

    public WishlistItem findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public void addItem(User user, WishlistForm form) throws IOException {
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        mapFormToItem(item, form); // Pakai helper method
        item.setStatus(Status.PENDING);
        
        // Cek Auto Status saat Create (siapa tau langsung lunas)
        checkAutoStatus(item, form);

        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            item.setImageUrl(saveImage(form.getImageFile())); 
        }
        repository.save(item);
    }

    public void updateItem(User user, WishlistForm form) throws IOException {
        WishlistItem item = repository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        mapFormToItem(item, form);
        
        // Cek Auto Status saat Update
        checkAutoStatus(item, form);

        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            item.setImageUrl(saveImage(form.getImageFile()));
        }
        repository.save(item);
    }

    // --- LOGIKA MAPPING & CEK STATUS OTOMATIS ---
    private void mapFormToItem(WishlistItem item, WishlistForm form) {
        item.setName(form.getName());
        item.setPrice(form.getPrice());
        
        // Set Tabungan (Default 0 jika kosong)
        BigDecimal saved = (form.getSavedAmount() == null) ? BigDecimal.ZERO : form.getSavedAmount();
        item.setSavedAmount(saved);

        item.setCategory(form.getCategory());
        item.setTargetDate(form.getTargetDate());
        item.setShopUrl(form.getShopUrl());
        item.setDescription(form.getDescription());
    }

    private void checkAutoStatus(WishlistItem item, WishlistForm form) {
        // Jika harga ada & tabungan >= harga, otomatis BOUGHT
        if (form.getPrice() != null && item.getSavedAmount().compareTo(form.getPrice()) >= 0) {
            if (item.getStatus() == Status.PENDING) {
                item.setStatus(Status.BOUGHT);
            }
        }
    }
    // ---------------------------------------------

    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return fileName;
    }

    public void changeStatus(UUID itemId) {
        WishlistItem item = repository.findById(itemId).orElseThrow();
        item.setStatus(item.getStatus() == Status.PENDING ? Status.BOUGHT : Status.PENDING);
        repository.save(item);
    }
    
    public void deleteItem(UUID id) {
        repository.deleteById(id);
    }

    public long countPending(User user) { return repository.countByUserAndStatus(user, Status.PENDING); }
    public long countBought(User user) { return repository.countByUserAndStatus(user, Status.BOUGHT); }
}