package com.lufthansa.internship.controller;


import com.lufthansa.internship.dto.request.BookingWithPaymentRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.dto.response.EventBookingsDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@AllArgsConstructor
@Tag(name = "Bookings", description = "Browse, create, and manage events")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingWithPaymentRequestDTO request){
        BookingResponseDTO created = bookingService.createBooking(request);
        URI location = URI.create("/api/booking/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(@Valid @RequestParam(defaultValue = "bookingDate") String sort,
                                                                   @Valid @RequestParam(defaultValue = "0") int page,
                                                                   @Valid @RequestParam(defaultValue = "20") int size){
        Page<BookingResponseDTO> bookings = bookingService.getAllBookings(sort, page, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/event")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookingsForEvent(@Valid @RequestParam(defaultValue = "bookingDate") Integer eventId,
                                                                           @Valid @RequestParam(defaultValue = "bookingDate") String sort,
                                                                           @Valid @RequestParam(defaultValue = "0") int page,
                                                                           @Valid @RequestParam(defaultValue = "20") int size){
        Page<BookingResponseDTO> bookings = bookingService.getAllBookingsForEvent(eventId, sort, page, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/myBookings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BookingResponseDTO>> getMyBookings(@AuthenticationPrincipal AppUserPrincipal authenticatedUser,
                                                                  @Valid @RequestParam(defaultValue = "bookingDate") String sortVariable) {
        log.info("User {} bookings", authenticatedUser.getId());
        return ResponseEntity.ok(bookingService.viewMyBookings(authenticatedUser.getId(), sortVariable));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<EventBookingsDTO>> viewMyBookingsAsOrganizer (@AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        log.info("Organizer {} booked events", authenticatedUser.getId());
        List<EventBookingsDTO> bookings = bookingService.getMyBookingsAsOrganizer(authenticatedUser.getId());
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelBooking(@PathVariable Integer bookingId,
                                              @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {
        log.info("User {} cancelling booking {}", authenticatedUser.getId(), bookingId);
        bookingService.cancelBookingByUser(bookingId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelBookingByAdmin(@PathVariable Integer bookingId,
                                                     @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {
        log.info("Admin {} cancelling booking {}", authenticatedUser.getId(), bookingId);
        bookingService.cancelBookingByAdmin(bookingId);
        return ResponseEntity.noContent().build();
    }



}
