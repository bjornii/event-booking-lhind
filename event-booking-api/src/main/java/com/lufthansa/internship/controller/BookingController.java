package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.BookingWithPaymentRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.dto.response.EventBookingsDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Bookings", description = "Create, view, and cancel event bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a booking", description = "Books seats for an event and processes payment. Falls back to WAITLIST if no seats remain.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created (CONFIRMED, PENDING_PAYMENT, or WAITLIST)"),
            @ApiResponse(responseCode = "400", description = "Requested seats exceed the 6-seat limit"),
            @ApiResponse(responseCode = "401", description = "Caller is not authenticated")
    })
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingWithPaymentRequestDTO request){
        log.info("Booking request received for event {} by user {}", request.getEventId(), request.getUserId());
        BookingResponseDTO created = bookingService.createBooking(request);
        URI location = URI.create("/api/booking/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all bookings", description = "Admin-only. Paginated list of every booking in the system.")
    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully")
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(
            @Parameter(description = "Field to sort by") @Valid @RequestParam(defaultValue = "bookingDate") String sort,
            @Parameter(description = "Zero-indexed page number") @Valid @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @Valid @RequestParam(defaultValue = "20") int size){
        Page<BookingResponseDTO> bookings = bookingService.getAllBookings(sort, page, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/event")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List bookings for a specific event", description = "Admin-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookingsForEvent(
            @Parameter(description = "ID of the event") @Valid @RequestParam Integer eventId,
            @Valid @RequestParam(defaultValue = "bookingDate") String sort,
            @Valid @RequestParam(defaultValue = "0") int page,
            @Valid @RequestParam(defaultValue = "20") int size){
        Page<BookingResponseDTO> bookings = bookingService.getAllBookingsForEvent(eventId, sort, page, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/myBookings")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "View my bookings", description = "Returns the authenticated user's own bookings.")
    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully")
    public ResponseEntity<Page<BookingResponseDTO>> getMyBookings(@AuthenticationPrincipal AppUserPrincipal authenticatedUser,
                                                                  @Valid @RequestParam(defaultValue = "bookingDate") String sortVariable) {
        log.info("Fetching bookings for user {}", authenticatedUser.getId());
        return ResponseEntity.ok(bookingService.viewMyBookings(authenticatedUser.getId(), sortVariable));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "View bookings for my events", description = "Organizer-only. Returns bookings grouped by each event you organize.")
    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully")
    public ResponseEntity<List<EventBookingsDTO>> viewMyBookingsAsOrganizer(@AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        log.info("Fetching organizer bookings for user {}", authenticatedUser.getId());
        List<EventBookingsDTO> bookings = bookingService.getMyBookingsAsOrganizer(authenticatedUser.getId());
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel my booking", description = "Cancels a CONFIRMED booking you own, at least 24 hours before the event starts.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled"),
            @ApiResponse(responseCode = "400", description = "Booking is not cancellable or within the 24-hour cutoff"),
            @ApiResponse(responseCode = "403", description = "Caller does not own this booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<Void> cancelBooking(@PathVariable Integer bookingId,
                                              @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {
        log.info("User {} cancelling booking {}", authenticatedUser.getId(), bookingId);
        bookingService.cancelBookingByUser(bookingId, authenticatedUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel any booking", description = "Admin-only. Cancels any booking regardless of ownership or timing.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<Void> cancelBookingByAdmin(@PathVariable Integer bookingId,
                                                     @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {
        log.info("Admin {} cancelling booking {}", authenticatedUser.getId(), bookingId);
        bookingService.cancelBookingByAdmin(bookingId);
        return ResponseEntity.noContent().build();
    }
}