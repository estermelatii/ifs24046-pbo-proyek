package org.delcom.app.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartupInfoLoggerTest {

    private StartupInfoLogger startupInfoLogger;

    @Mock
    private ApplicationReadyEvent event;

    @Mock
    private ConfigurableApplicationContext applicationContext;

    @Mock
    private ConfigurableEnvironment environment;

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        startupInfoLogger = new StartupInfoLogger();

        // Setup mocks
        when(event.getApplicationContext()).thenReturn(applicationContext);
        when(applicationContext.getEnvironment()).thenReturn(environment);

        // Capture System.out
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testOnApplicationEventWithDefaultValues() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Application started successfully!"));
        assertTrue(output.contains("http://localhost:8080"));
        assertTrue(output.contains("LiveReload: DISABLED"));
    }

    @Test
    void testOnApplicationEventWithCustomPort() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("9090");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("http://localhost:9090"));
    }

    @Test
    void testOnApplicationEventWithContextPath() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/api");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("http://localhost:8080/api"));
    }

    @Test
    void testOnApplicationEventWithNullContextPath() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn(null);
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("http://localhost:8080"));
        assertFalse(output.contains("http://localhost:8080/"));
    }

    @Test
    void testOnApplicationEventWithSlashContextPath() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("http://localhost:8080"));
        // Context path should be empty when it's "/"
    }

    @Test
    void testOnApplicationEventWithLiveReloadEnabled() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(true);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("LiveReload: ENABLED"));
        assertTrue(output.contains("port 35729"));
    }

    @Test
    void testOnApplicationEventWithLiveReloadDisabled() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("LiveReload: DISABLED"));
    }

    @Test
    void testOnApplicationEventWithCustomLiveReloadPort() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(true);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("45729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("port 45729"));
    }

    @Test
    void testOnApplicationEventWithCustomHost() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("192.168.1.100");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("http://192.168.1.100:8080"));
    }

    @Test
    void testOnApplicationEventOutputFormat() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertTrue(output.contains("Application started successfully!"));
        assertTrue(output.contains("> URL:"));
        assertTrue(output.contains("> LiveReload:"));
    }

    @Test
    void testOnApplicationEventWithAllCustomValues() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("3000");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/myapp");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(true);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("40000");
        when(environment.getProperty("server.address", "localhost")).thenReturn("0.0.0.0");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("http://0.0.0.0:3000/myapp"));
        assertTrue(output.contains("LiveReload: ENABLED"));
        assertTrue(output.contains("port 40000"));
    }

    @Test
    void testOnApplicationEventCallsEnvironmentMethods() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert - verify all environment properties are accessed
        verify(environment, times(1)).getProperty("server.port", "8080");
        verify(environment, times(1)).getProperty("server.servlet.context-path", "/");
        verify(environment, times(1)).getProperty("spring.devtools.livereload.enabled", Boolean.class, false);
        verify(environment, times(1)).getProperty("spring.devtools.livereload.port", "35729");
        verify(environment, times(1)).getProperty("server.address", "localhost");
    }

    @Test
    void testOnApplicationEventWithEmptyContextPath() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("http://localhost:8080"));
    }

    @Test
    void testOnApplicationEventContextPathNotSlash() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/application");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("/application"));
    }

    @Test
    void testOnApplicationEventPrintsEmptyLines() {
        // Arrange
        when(environment.getProperty("server.port", "8080")).thenReturn("8080");
        when(environment.getProperty("server.servlet.context-path", "/")).thenReturn("/");
        when(environment.getProperty("spring.devtools.livereload.enabled", Boolean.class, false)).thenReturn(false);
        when(environment.getProperty("spring.devtools.livereload.port", "35729")).thenReturn("35729");
        when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");

        // Act
        startupInfoLogger.onApplicationEvent(event);

        // Assert
        String output = outputStream.toString();
        
        // Check that output contains required content
        assertNotNull(output, "Output should not be null");
        assertFalse(output.isEmpty(), "Output should not be empty");
        assertTrue(output.contains("Application started successfully!"), "Should contain success message");
        
        // Check for empty lines (newline characters at start and end)
        assertTrue(output.startsWith("\n") || output.startsWith("\r\n"), "Output should start with empty line");
        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"), "Output should end with empty line");
        
        // Count actual content lines (non-empty lines excluding ANSI codes)
        String cleanOutput = output.replaceAll("\u001B\\[[;\\d]*m", ""); // Remove ANSI codes
        String[] lines = cleanOutput.split("\\r?\\n");
        long contentLines = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                contentLines++;
            }
        }
        
        // Should have exactly 3 content lines: success message, URL, LiveReload status
        assertEquals(3, contentLines, "Should have exactly 3 content lines");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}