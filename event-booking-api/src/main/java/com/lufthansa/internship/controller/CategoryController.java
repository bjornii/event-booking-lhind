package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.CategoryRequestDTO;
import com.lufthansa.internship.dto.response.CategoryResponseDTO;
import com.lufthansa.internship.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Admin-managed event categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a category", description = "Admin-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "409", description = "Category with this name already exists")
    })
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO request){
        CategoryResponseDTO created = categoryService.createCategory(request);
        URI location = URI.create("/api/categories/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all categories", description = "Admin-only. Paginated.")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        Page<CategoryResponseDTO> users = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a category", description = "Admin-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category with this name already exists")
    })
    public ResponseEntity<CategoryResponseDTO> updateCategory(@Valid @PathVariable Integer categoryId, @Valid @RequestBody CategoryRequestDTO request){
        CategoryResponseDTO updated = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deactivate a category", description = "Admin-only. Soft-deletes by marking the category INACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deactivated"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> removeCategory(@Valid @PathVariable Integer categoryId){
        categoryService.removeCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}