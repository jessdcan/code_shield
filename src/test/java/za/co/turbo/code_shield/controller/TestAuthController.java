package za.co.turbo.code_shield.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class TestAuthController {

    @GetMapping("/test")
    public String test() {
        return "Authentication test endpoint";
    }
} 