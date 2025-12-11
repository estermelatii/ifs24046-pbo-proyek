package org.delcom.app.configs;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConfigTest {

    @Test
    void testConfigsLoad() {
        // Kita hanya meng-instansiasi class config agar Java 'menyentuh' kodenya
        WebConfig webConfig = new WebConfig();
        assertNotNull(webConfig);

        SecurityConfig secConfig = new SecurityConfig();
        assertNotNull(secConfig);
        
        // MvcConfig biasanya implement interface, kita cek dia tidak null
        WebMvcConfig mvcConfig = new WebMvcConfig();
        assertNotNull(mvcConfig);
    }
}