package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.request.UserUpdateDTO;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.entity.UserRole;
import com.lufthansa.internship.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Admin management of user accounts")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a user", description = "Admin-only. Creates a user with a specified role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Email or username already exists")
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRegisterRequestDTO request,
                                                      @Parameter(description = "Role to assign") @RequestParam UserRole role){
        UserResponseDTO created = userService.createUser(request, role);
        URI location = URI.create("/api/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all users", description = "Admin-only.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        Page<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a user", description = "Admin-only. Partial update — only non-null fields are applied.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Email or username already in use"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @PathVariable Integer userId, @Valid @RequestBody UserUpdateDTO request){
        UserResponseDTO updated = userService.updateUser(userId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deactivate a user", description = "Admin-only. Soft-deletes by marking the account DEACTIVATED.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deactivated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable Integer userId){
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}