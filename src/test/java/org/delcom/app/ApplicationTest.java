package org.delcom.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTest {

	@Test
	void contextLoads() {
        // Test standar Spring Boot
	}
    
    @Test
    void main() {
        // Paksa jalankan main method
        Application.main(new String[] {});
    }
}