package org.delcom.app.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString() + "/");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test files
        if (Files.exists(tempDir)) {
            Files.walk(tempDir)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
        }
    }

    @Test
    void testStoreFile_Success() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        UUID entityId = UUID.randomUUID();
        String originalFilename = "test.jpg";
        byte[] content = "test content".getBytes();
        
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String filename = fileStorageService.storeFile(mockFile, entityId);

        // Assert
        assertNotNull(filename);
        assertTrue(filename.startsWith("item_"));
        assertTrue(filename.endsWith(".jpg"));
        assertTrue(filename.contains(entityId.toString()));
        
        Path savedFile = Paths.get(tempDir.toString(), filename);
        assertTrue(Files.exists(savedFile));
    }

    @Test
    void testStoreFile_WithoutExtension() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        UUID entityId = UUID.randomUUID();
        String originalFilename = "testfile";
        byte[] content = "test content".getBytes();
        
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String filename = fileStorageService.storeFile(mockFile, entityId);

        // Assert
        assertNotNull(filename);
        assertTrue(filename.startsWith("item_"));
        assertEquals("item_" + entityId.toString(), filename);
    }

    @Test
    void testStoreFile_NullOriginalFilename() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        UUID entityId = UUID.randomUUID();
        byte[] content = "test content".getBytes();
        
        when(mockFile.getOriginalFilename()).thenReturn(null);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String filename = fileStorageService.storeFile(mockFile, entityId);

        // Assert
        assertNotNull(filename);
        assertEquals("item_" + entityId.toString(), filename);
    }

    @Test
    void testStoreFile_FilenameWithoutDot() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        UUID entityId = UUID.randomUUID();
        String originalFilename = "testfile";
        byte[] content = "test content".getBytes();
        
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String filename = fileStorageService.storeFile(mockFile, entityId);

        // Assert
        assertNotNull(filename);
        assertFalse(filename.contains("."));
    }

    @Test
    void testStoreFile_ReplaceExisting() throws IOException {
        // Arrange
        MultipartFile mockFile1 = mock(MultipartFile.class);
        MultipartFile mockFile2 = mock(MultipartFile.class);
        UUID entityId = UUID.randomUUID();
        
        when(mockFile1.getOriginalFilename()).thenReturn("test1.jpg");
        when(mockFile1.getInputStream()).thenReturn(new ByteArrayInputStream("content1".getBytes()));
        
        when(mockFile2.getOriginalFilename()).thenReturn("test2.jpg");
        when(mockFile2.getInputStream()).thenReturn(new ByteArrayInputStream("content2".getBytes()));

        // Act
        String filename1 = fileStorageService.storeFile(mockFile1, entityId);
        String filename2 = fileStorageService.storeFile(mockFile2, entityId);

        // Assert
        assertEquals(filename1, filename2);
        Path savedFile = Paths.get(tempDir.toString(), filename2);
        assertTrue(Files.exists(savedFile));
        assertEquals("content2", Files.readString(savedFile));
    }

    @Test
    void testStoreFile_CreateDirectoryIfNotExists() throws IOException {
        // Arrange
        FileStorageService newService = new FileStorageService();
        Path newDir = tempDir.resolve("newsubdir");
        ReflectionTestUtils.setField(newService, "uploadDir", newDir.toString() + "/");
        
        MultipartFile mockFile = mock(MultipartFile.class);
        UUID entityId = UUID.randomUUID();
        
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));

        assertFalse(Files.exists(newDir));

        // Act
        String filename = newService.storeFile(mockFile, entityId);

        // Assert
        assertTrue(Files.exists(newDir));
        Path savedFile = Paths.get(newDir.toString(), filename);
        assertTrue(Files.exists(savedFile));
    }

    @Test
    void testStoreFile_IOException() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        UUID entityId = UUID.randomUUID();
        
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getInputStream()).thenThrow(new IOException("Test IO Exception"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fileStorageService.storeFile(mockFile, entityId);
        });
    }

    @Test
    void testDeleteFile_Success() throws IOException {
        // Arrange
        String filename = "item_test.jpg";
        Path filePath = Paths.get(tempDir.toString(), filename);
        Files.createFile(filePath);
        assertTrue(Files.exists(filePath));

        // Act
        boolean result = fileStorageService.deleteFile(filename);

        // Assert
        assertTrue(result);
        assertFalse(Files.exists(filePath));
    }

    @Test
    void testDeleteFile_FileNotExists() {
        // Arrange
        String filename = "nonexistent.jpg";

        // Act
        boolean result = fileStorageService.deleteFile(filename);

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteFile_NullFilename() {
        // Act
        boolean result = fileStorageService.deleteFile(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteFile_IOException() throws IOException {
        // Arrange - buat direktori sebagai file untuk memicu IOException
        String filename = "test.jpg";
        Path filePath = Paths.get(tempDir.toString(), filename);
        
        // Buat direktori dengan nama yang sama dengan file yang akan dihapus
        // Ini akan menyebabkan path resolution issue
        Files.createDirectory(filePath);
        
        // Buat subdirectory di dalamnya
        Path subPath = filePath.resolve("subdir");
        Files.createDirectory(subPath);
        
        // Sekarang coba delete dengan filename biasa (tapi sebenarnya itu directory dengan content)
        // Files.deleteIfExists akan throw DirectoryNotEmptyException (subclass of IOException)
        
        // Act
        boolean result = fileStorageService.deleteFile(filename);

        // Assert
        assertFalse(result); // Harus return false karena catch IOException
        
        // Clean up
        Files.deleteIfExists(subPath);
        Files.deleteIfExists(filePath);
    }
}