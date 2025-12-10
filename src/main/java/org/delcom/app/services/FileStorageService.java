package org.delcom.app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    // Default ke folder static agar gambar langsung tampil di browser
    @Value("${app.upload.dir:src/main/resources/static/uploads/}")
    private String uploadDir;

    public String storeFile(MultipartFile file, UUID entityId) throws IOException {
        // Buat directory jika belum ada
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate nama file unik
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Ganti prefix "cover_" (Todo) menjadi "item_" (Wishlist)
        String filename = "item_" + entityId.toString() + fileExtension;

        // Simpan file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename; 
    }

    public boolean deleteFile(String filename) {
        try {
            if (filename == null) return false;
            Path filePath = Paths.get(uploadDir).resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
}