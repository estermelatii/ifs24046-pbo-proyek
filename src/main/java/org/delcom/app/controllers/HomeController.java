package org.delcom.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Saat buka localhost:8080, langsung lempar ke halaman login
        return "redirect:/auth/login";
    }
}