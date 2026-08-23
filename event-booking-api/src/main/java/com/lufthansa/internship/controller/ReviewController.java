package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.ReviewRequestDTO;
import com.lufthansa.internship.dto.response.ReviewResponseDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Leave and manage event reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Leave a review", description = "Requires a CONFIRMED booking for a FINISHED event; one review per user per event.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "No confirmed booking, event not finished, or already reviewed"),
            @ApiResponse(responseCode = "404", description = "User or event not found")
    })
    public ResponseEntity<ReviewResponseDTO> leaveReview(@AuthenticationPrincipal AppUserPrincipal authenticatedUser, @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO created = reviewService.leaveReview(authenticatedUser.getId(), reviewRequestDTO);
        URI location = URI.create("/api/review/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a review", description = "Admin-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> deleteReview(@Valid @PathVariable Integer reviewId){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}