package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.CategoryRequestDTO;
import com.lufthansa.internship.dto.request.UserRegisterRequestDTO;
import com.lufthansa.internship.dto.request.UserUpdateDTO;
import com.lufthansa.internship.dto.response.CategoryResponseDTO;
import com.lufthansa.internship.dto.response.UserResponseDTO;
import com.lufthansa.internship.entity.UserRole;
import com.lufthansa.internship.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO request){
        CategoryResponseDTO created = categoryService.createCategory(request);
        URI location = URI.create("/api/categories/" + created.getId());

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        Page<CategoryResponseDTO> users = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> updateCategory (@Valid @PathVariable Integer categoryId, @Valid @RequestBody CategoryRequestDTO request){
        CategoryResponseDTO updated = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeCategory (@Valid @PathVariable Integer categoryId){
        categoryService.removeCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
