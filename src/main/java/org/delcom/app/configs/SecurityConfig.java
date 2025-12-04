package org.delcom.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Matikan CSRF sementara biar tidak ribet saat testing form
            .csrf(csrf -> csrf.disable())

            // 2. Atur Izin Akses URL
            .authorizeHttpRequests(auth -> auth
                // HALAMAN YANG BOLEH DIAKSES TANPA LOGIN (PENTING!)
                .requestMatchers(
                    "/", 
                    "/auth/**",       // Login & Register Controller
                    "/login", 
                    "/register", 
                    "/css/**",        // File CSS
                    "/js/**",         // File Javascript
                    "/images/**",     // Gambar Aset
                    "/uploads/**"     // Gambar Upload User
                ).permitAll()
                
                // SISANYA WAJIB LOGIN
                .anyRequest().authenticated()
            )

            // 3. Konfigurasi Form Login
            .formLogin(login -> login
                .loginPage("/auth/login")       // URL Halaman Login Custom
                .loginProcessingUrl("/login")   // URL tujuan saat tombol "Submit" ditekan
                .defaultSuccessUrl("/wishlist", true) // Kalau sukses, pergi ke Dashboard Wishlist
                .permitAll()
            )

            // 4. Konfigurasi Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            );

        return http.build();
    }
    // ... kode filterChain yang tadi ...

    // TAMBAHKAN INI: Alat pengaman password
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

} // Tutup class SecurityConfig
