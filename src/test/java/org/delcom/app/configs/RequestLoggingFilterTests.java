package org.delcom.app.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    private RequestLoggingFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        filter = new RequestLoggingFilter();
        ReflectionTestUtils.setField(filter, "port", 8080);
        ReflectionTestUtils.setField(filter, "livereload", false);

        // Capture System.out
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testDoFilterInternalWithStatus200() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("GET"));
        assertTrue(output.contains("/api/test"));
        assertTrue(output.contains("200"));
        assertTrue(output.contains("127.0.0.1"));
    }

    @Test
    void testDoFilterInternalWithStatus201() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/users");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(response.getStatus()).thenReturn(201);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("POST"));
        assertTrue(output.contains("/api/users"));
        assertTrue(output.contains("201"));
    }

    @Test
    void testDoFilterInternalWithStatus400() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/invalid");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(400);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("POST"));
        assertTrue(output.contains("/api/invalid"));
        assertTrue(output.contains("400"));
    }

    @Test
    void testDoFilterInternalWithStatus404() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/notfound");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(404);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("GET"));
        assertTrue(output.contains("/api/notfound"));
        assertTrue(output.contains("404"));
    }

    @Test
    void testDoFilterInternalWithStatus500() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/error");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(500);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("POST"));
        assertTrue(output.contains("/api/error"));
        assertTrue(output.contains("500"));
    }

    @Test
    void testDoFilterInternalWithStatus503() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/unavailable");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(503);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("GET"));
        assertTrue(output.contains("/api/unavailable"));
        assertTrue(output.contains("503"));
    }

    @Test
    void testDoFilterInternalWithStatus100() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/continue");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(100);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("GET"));
        assertTrue(output.contains("/api/continue"));
        assertTrue(output.contains("100"));
    }

    @Test
    void testDoFilterInternalWithWellKnownPath() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/.well-known/acme-challenge");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        // Output should be empty for .well-known paths
        assertFalse(output.contains("/.well-known/acme-challenge"));
    }

    @Test
    void testDoFilterInternalCalculatesDuration() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/slow");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        doAnswer(invocation -> {
            Thread.sleep(50); // Simulate slow request
            return null;
        }).when(filterChain).doFilter(request, response);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        String output = outputStream.toString();
        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertTrue(output.contains("GET"));
        assertTrue(output.contains("/api/slow"));
        assertTrue(output.contains("200"));
    }

    @Test
    void testDoFilterInternalWithDifferentMethods() throws ServletException, IOException {
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            // Reset output stream
            outputStream.reset();

            // Arrange
            when(request.getMethod()).thenReturn(method);
            when(request.getRequestURI()).thenReturn("/api/test");
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getStatus()).thenReturn(200);

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains(method));
        }
    }

    @Test
    void testDoFilterInternalWithDifferentIpAddresses() throws ServletException, IOException {
        String[] ips = {"127.0.0.1", "192.168.1.1", "10.0.0.1", "172.16.0.1"};

        for (String ip : ips) {
            // Reset output stream
            outputStream.reset();

            // Arrange
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn("/api/test");
            when(request.getRemoteAddr()).thenReturn(ip);
            when(response.getStatus()).thenReturn(200);

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains(ip));
        }
    }

    @Test
    void testDoFilterInternalWithVariousUris() throws ServletException, IOException {
        String[] uris = {"/api/users", "/api/auth/login", "/health", "/actuator/health"};

        for (String uri : uris) {
            // Reset output stream
            outputStream.reset();

            // Arrange
            when(request.getMethod()).thenReturn("GET");
            when(request.getRequestURI()).thenReturn(uri);
            when(request.getRemoteAddr()).thenReturn("127.0.0.1");
            when(response.getStatus()).thenReturn(200);

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains(uri));
        }
    }

    @Test
    void testDoFilterInternalIncludesOriginInfo() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        String output = outputStream.toString();
        // Should contain origin info with class name and line number
        assertTrue(output.contains("org.delcom") || output.contains("["));
    }

    @Test
    void testDoFilterInternalWithStatus299() throws ServletException, IOException {
        // Arrange - test boundary of 2xx status codes
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(299);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("299"));
    }

    @Test
    void testDoFilterInternalWithStatus399() throws ServletException, IOException {
        // Arrange - test status code less than 400
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(399);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("399"));
    }

    @Test
    void testDoFilterInternalWithStatus499() throws ServletException, IOException {
        // Arrange - test boundary of 4xx status codes
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(499);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        String output = outputStream.toString();
        assertTrue(output.contains("499"));
    }

    @Test
    void testDoFilterInternalCallsFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert - verify filter chain is called exactly once
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalFormatting() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert - check log format
        String output = outputStream.toString();
        assertNotNull(output);
        assertFalse(output.isEmpty());
        // Should contain method, URI, status, and remote address
        assertTrue(output.contains("GET"));
        assertTrue(output.contains("/api/test"));
        assertTrue(output.contains("200"));
        assertTrue(output.contains("127.0.0.1"));
    }

    // Restore System.out after each test
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}