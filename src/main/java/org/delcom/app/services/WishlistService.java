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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class WishlistService {

    @Autowired 
    private WishlistItemRepository repository;

    // Folder untuk menyimpan gambar (Path relatif)
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public List<WishlistItem> getAllItems(User user) {
        return repository.findByUserOrderByCreatedAtDesc(user);
    }

    public void addItem(User user, WishlistForm form) throws IOException {
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setName(form.getName());
        item.setPrice(form.getPrice());
        item.setCategory(form.getCategory());
        item.setTargetDate(form.getTargetDate());
        item.setShopUrl(form.getShopUrl());
        item.setDescription(form.getDescription());
        item.setStatus(Status.PENDING);

        // Logika Simpan Gambar
        if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
            String fileName = saveImage(form.getImageFile());
            item.setImageUrl(fileName); 
        }

        repository.save(item);
    }

    // Helper untuk save file fisik
    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return fileName;
    }

    public void changeStatus(UUID itemId) {
        WishlistItem item = repository.findById(itemId).orElseThrow();
        if (item.getStatus() == Status.PENDING) {
            item.setStatus(Status.BOUGHT);
        } else {
            item.setStatus(Status.PENDING);
        }
        repository.save(item);
    }
    
    public void deleteItem(UUID id) {
        repository.deleteById(id);
    }

    // Statistik
    public long countPending(User user) {
        return repository.countByUserAndStatus(user, Status.PENDING);
    }
    
    public long countBought(User user) {
        return repository.countByUserAndStatus(user, Status.BOUGHT);
    }
}