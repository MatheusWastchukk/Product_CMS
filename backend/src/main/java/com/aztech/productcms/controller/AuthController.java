package com.aztech.productcms.controller;

import com.aztech.productcms.dto.AuthRequestDTO;
import com.aztech.productcms.dto.AuthResponseDTO;
import com.aztech.productcms.dto.ProfileUpdateRequestDTO;
import com.aztech.productcms.dto.UserResponseDTO;
import com.aztech.productcms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
    }

    @GetMapping("/me")
    public UserResponseDTO me(@RequestHeader("Authorization") String authorization) {
        return authService.me(authorization);
    }

    @PutMapping("/me")
    public UserResponseDTO updateProfile(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody ProfileUpdateRequestDTO request
    ) {
        return authService.updateProfile(authorization, request);
    }
}
