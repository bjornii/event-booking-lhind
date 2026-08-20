package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.ReviewRequestDTO;
import com.lufthansa.internship.dto.response.ReviewResponseDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    //pass userAppUserPrincipal as default for user id

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDTO> leaveReview(@AuthenticationPrincipal AppUserPrincipal authenticatedUser, @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO created = reviewService.leaveReview(authenticatedUser.getId(), reviewRequestDTO);
        URI location = URI.create("/api/review/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@Valid @PathVariable Integer reviewId){
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

}
