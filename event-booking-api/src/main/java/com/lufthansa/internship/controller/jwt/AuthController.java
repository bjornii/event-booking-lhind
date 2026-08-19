package com.lufthansa.internship.controller.jwt;

import com.lufthansa.internship.dto.request.LoginRequestDTO;
import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.security.AuthService;
import com.lufthansa.internship.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRegisterRequestDTO registerRequest) {
        UserResponseDTO user = authService.register(registerRequest);
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }
}
