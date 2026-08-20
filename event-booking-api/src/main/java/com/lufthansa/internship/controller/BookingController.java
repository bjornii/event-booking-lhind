package com.lufthansa.internship.controller;


import com.lufthansa.internship.dto.request.BookingRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.BookingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/booking")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO request){
        BookingResponseDTO created = bookingService.createBooking(request);
        URI location = URI.create("/api/booking/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }



    @GetMapping("/myBookings")
    public ResponseEntity<Page<BookingResponseDTO>> getMyBookings(@AuthenticationPrincipal AppUserPrincipal authenticatedUser,
                                                                  @Valid @RequestParam(defaultValue = "bookingDate") String sortVariable) {

        return ResponseEntity.ok(bookingService.viewMyBookings(authenticatedUser.getId(), sortVariable));
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelBooking(@PathVariable Integer bookingId,
                                              @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {
        bookingService.cancelBookingByUser(bookingId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }



}
