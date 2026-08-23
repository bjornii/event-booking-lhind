package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.EventRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.EventService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Browse, create, and manage events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new event", description = "Organizer-only. Creates a new event in UNLISTED visibility until published.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data (dates, seats, price, category, or venue)"),
            @ApiResponse(responseCode = "403", description = "Caller is not an organizer"),
            @ApiResponse(responseCode = "404", description = "Organizer user not found"),
            @ApiResponse(responseCode = "409", description = "An event with this title already exists")
    })
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO request,
                                                        @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        logger.info("Organizer {} creating event '{}'", authenticatedUser.getId(), request.getTitle());
        EventResponseDTO created = eventService.createEvent(authenticatedUser.getId(), request);
        logger.info("Event '{}' created with id {}", created.getTitle(), created.getId());
        URI location = URI.create("/api/events/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List all published events", description = "Paginated, sortable list of publicly visible events")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "startDateTime") String sort,
            @Parameter(description = "Zero-indexed page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size){
        Page<EventResponseDTO> events = eventService.getAllEvents(sort, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get a published event by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found or not published")
    })
    public ResponseEntity<EventResponseDTO> getEventById(
            @Parameter(description = "ID of the event to retrieve") @PathVariable Integer eventId) {
        EventResponseDTO event = eventService.getSingleEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/category")
    @Operation(summary = "Filter published events by category")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCategory(
            @Parameter(description = "One or more category names") @RequestParam List<String> category,
            @RequestParam(defaultValue = "startDateTime") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponseDTO> events = eventService.getEventsByCategory(category, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/city")
    @Operation(summary = "Filter published events by city")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCity(
            @Parameter(description = "City name") @RequestParam String city,
            @RequestParam(defaultValue = "startDateTime") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponseDTO> events = eventService.getEventsByCity(city, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/dateRange")
    @Operation(summary = "Filter published events by date range")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByDateRange(
            @Parameter(description = "Start date (inclusive), ISO format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End date (inclusive), ISO format yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "startDateTime") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        Page<EventResponseDTO> events = eventService.getEventsByDateRange(fromDateTime, toDateTime, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/priceRange")
    @Operation(summary = "Filter published events by price range")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByPriceRange(
            @Parameter(description = "Minimum price") @RequestParam Double start,
            @Parameter(description = "Maximum price") @RequestParam Double end,
            @RequestParam(defaultValue = "startDateTime") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EventResponseDTO> events = eventService.getEventsByPriceRange(start, end, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/publish/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Publish an event", description = "Organizer-only. Makes the event publicly visible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event published"),
            @ApiResponse(responseCode = "403", description = "Caller does not own this event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> publishEvent(@PathVariable Integer eventId,
                                                         @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        logger.info("Organizer {} publishing event {}", authenticatedUser.getId(), eventId);
        EventResponseDTO event = eventService.publishEvent(authenticatedUser.getId(), eventId);
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/unlist/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Unlist an event", description = "Organizer-only. Hides the event from public listings.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event unlisted"),
            @ApiResponse(responseCode = "403", description = "Caller does not own this event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> unlistEvent(@PathVariable Integer eventId,
                                                        @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        logger.info("Organizer {} unlisting event {}", authenticatedUser.getId(), eventId);
        EventResponseDTO event = eventService.unlistEvent(authenticatedUser.getId(), eventId);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update an event", description = "Organizer-only. Updates fields on an event you own.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event updated"),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "403", description = "Caller does not own this event"),
            @ApiResponse(responseCode = "404", description = "Event or referenced venue not found"),
            @ApiResponse(responseCode = "409", description = "An event with this title already exists")
    })
    public ResponseEntity<EventResponseDTO> updateEvent(@Valid @RequestBody EventRequestDTO request,
                                                        @AuthenticationPrincipal AppUserPrincipal authenticatedUser,
                                                        @PathVariable Integer eventId){
        logger.info("Organizer {} updating event {}", authenticatedUser.getId(), eventId);
        EventResponseDTO updated = eventService.updateEvent(authenticatedUser.getId(), eventId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel an event", description = "Organizer-only. Cancels an upcoming event and refunds/cancels its bookings.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Event cancelled"),
            @ApiResponse(responseCode = "400", description = "Event already started or is not upcoming"),
            @ApiResponse(responseCode = "403", description = "Caller does not own this event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<Void> cancelEvent(@PathVariable Integer eventId,
                                            @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        logger.info("Organizer {} cancelling event {}", authenticatedUser.getId(), eventId);
        eventService.cancelEvent(authenticatedUser.getId(), eventId);
        logger.info("Event {} cancelled, associated bookings refunded/cancelled", eventId);
        return ResponseEntity.noContent().build();
    }
}


