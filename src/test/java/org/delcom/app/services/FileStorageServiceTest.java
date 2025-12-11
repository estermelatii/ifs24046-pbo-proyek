package org.delcom.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir; // Folder sementara otomatis dihapus setelah test

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        // Set folder upload ke folder temp biar ga nyampah di laptop
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    @Test
    void testStoreFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "content".getBytes()
        );
        UUID id = UUID.randomUUID();

        String filename = fileStorageService.storeFile(file, id);

        assertNotNull(filename);
        assertTrue(filename.contains(id.toString()));
        assertTrue(Files.exists(tempDir.resolve(filename)));
    }

    @Test
    void testDeleteFile() throws IOException {
        // 1. Buat file dummy
        Path filePath = tempDir.resolve("delete-me.jpg");
        Files.createFile(filePath);

        // 2. Hapus
        boolean result = fileStorageService.deleteFile("delete-me.jpg");

        // 3. Cek
        assertTrue(result);
        assertFalse(Files.exists(filePath));
    }
    
    @Test
    void testDeleteFile_NotFound() {
        boolean result = fileStorageService.deleteFile("ga-ada.jpg");
        assertFalse(result);
    }
}