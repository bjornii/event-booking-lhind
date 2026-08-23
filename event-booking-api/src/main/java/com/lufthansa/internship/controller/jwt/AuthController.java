package com.lufthansa.internship.controller.jwt;

import com.lufthansa.internship.dto.request.LoginRequestDTO;
import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.security.AuthService;
import com.lufthansa.internship.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration and login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates with email/password and returns a JWT bearer token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "403", description = "Account is deactivated")
    })
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        logger.info("Login attempt for email {}", request.getEmail());
        TokenResponse tokenResponse = authService.login(request);
        logger.info("Login successful for email {}", request.getEmail());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new account", description = "Creates a new account with the ATTENDEE role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Email or username already in use")
    })
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRegisterRequestDTO registerRequest) {
        logger.info("Registration attempt for email {}", registerRequest.getEmail());
        UserResponseDTO user = authService.register(registerRequest);
        logger.info("User registered: {} ({})", user.getUsername(), user.getEmail());
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
        ));
    }
}