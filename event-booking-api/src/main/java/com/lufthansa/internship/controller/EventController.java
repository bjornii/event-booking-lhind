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

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create a new event",
            description = "Organizer only. Create a new event listing"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "403", description = "Caller is not an organizer")
    })
    public ResponseEntity<EventResponseDTO> createEvent(
            @Valid @RequestBody EventRequestDTO request,
            @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {

        log.info("Organizer {} creating event '{}'",
                authenticatedUser.getId(), request.getTitle());

        EventResponseDTO created =
                eventService.createEvent(authenticatedUser.getId(), request);

        URI location = URI.create("/api/events/" + created.getId());

        log.info("Event {} created successfully by organizer {}",
                created.getId(), authenticatedUser.getId());

        return ResponseEntity.created(location).body(created);
    }


    @GetMapping
    @Operation(
            summary = "List all events",
            description = "Returns a paginated and sortable list of all events"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters")
    })
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "startDateTime") String sort,

            @Parameter(description = "Page number, zero-indexed")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of events per page")
            @RequestParam(defaultValue = "10") int size) {

        log.info("Getting all events - page: {}, size: {}, sort: {}", page, size, sort);

        Page<EventResponseDTO> events =
                eventService.getAllEvents(sort, page, size);

        log.info("Retrieved {} events for page {}", events.getNumberOfElements(), page);

        return ResponseEntity.ok(events);
    }


    @GetMapping("/{eventId}")
    @Operation(
            summary = "Get event by ID",
            description = "Returns the details of a single event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event found"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> getEventById(
            @Parameter(description = "ID of the event")
            @PathVariable Integer eventId) {

        log.info("Getting event with id: {}", eventId);

        EventResponseDTO event = eventService.getSingleEvent(eventId);

        return ResponseEntity.ok(event);
    }


    @GetMapping("/category")
    @Operation(
            summary = "List events filtered by category",
            description = "Returns a paginated list of events belonging to one or more categories"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category or pagination parameters")
    })
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCategory(
            @Parameter(
                    description = "Event category or categories",
                    example = "CONCERT"
            )
            @RequestParam List<String> category,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "startDateTime") String sort,

            @Parameter(description = "Page number, zero-indexed")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of events per page")
            @RequestParam(defaultValue = "10") int size) {

        log.info("Getting events by category: {}, page: {}, size: {}, sort: {}",
                category, page, size, sort);

        Page<EventResponseDTO> events =
                eventService.getEventsByCategory(category, sort, page, size);

        log.info("Retrieved {} events for categories {}",
                events.getNumberOfElements(), category);

        return ResponseEntity.ok(events);
    }


    @GetMapping("/city")
    @Operation(
            summary = "List events filtered by city",
            description = "Returns a paginated list of events taking place in the specified city"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid city or pagination parameters")
    })
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCity(
            @Parameter(description = "City where the events take place")
            @RequestParam String city,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "startDateTime") String sort,

            @Parameter(description = "Page number, zero-indexed")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of events per page")
            @RequestParam(defaultValue = "10") int size) {

        log.info("Getting events in city '{}', page: {}, size: {}, sort: {}",
                city, page, size, sort);

        Page<EventResponseDTO> events =
                eventService.getEventsByCity(city, sort, page, size);

        log.info("Retrieved {} events for city '{}'",
                events.getNumberOfElements(), city);

        return ResponseEntity.ok(events);
    }


    @GetMapping("/dateRange")
    @Operation(
            summary = "List events filtered by date range",
            description = "Returns events occurring between the specified start and end dates"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date range or pagination parameters")
    })
    public ResponseEntity<Page<EventResponseDTO>> getEventsByDateRange(
            @Parameter(description = "Start date of the range")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @Parameter(description = "End date of the range")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "startDateTime") String sort,

            @Parameter(description = "Page number, zero-indexed")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of events per page")
            @RequestParam(defaultValue = "10") int size) {

        log.info("Getting events between {} and {}, page: {}, size: {}, sort: {}",
                from, to, page, size, sort);

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        Page<EventResponseDTO> events =
                eventService.getEventsByDateRange(
                        fromDateTime,
                        toDateTime,
                        sort,
                        page,
                        size
                );

        log.info("Retrieved {} events between {} and {}",
                events.getNumberOfElements(), from, to);

        return ResponseEntity.ok(events);
    }


    @GetMapping("/priceRange")
    @Operation(
            summary = "List events filtered by price range",
            description = "Returns a paginated list of events whose price falls within the specified range"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid price range or pagination parameters")
    })
    public ResponseEntity<Page<EventResponseDTO>> getEventsByPriceRange(
            @Parameter(description = "Minimum event price")
            @RequestParam Double start,

            @Parameter(description = "Maximum event price")
            @RequestParam Double end,

            @Parameter(description = "Field to sort by")
            @RequestParam(defaultValue = "startDateTime") String sort,

            @Parameter(description = "Page number, zero-indexed")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of events per page")
            @RequestParam(defaultValue = "10") int size) {

        log.info("Getting events with price between {} and {}, page: {}, size: {}, sort: {}",
                start, end, page, size, sort);

        Page<EventResponseDTO> events =
                eventService.getEventsByPriceRange(start, end, sort, page, size);

        log.info("Retrieved {} events with price between {} and {}",
                events.getNumberOfElements(), start, end);

        return ResponseEntity.ok(events);
    }


    @PatchMapping("/publish/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Publish an event",
            description = "Organizer only. Publishes an event and makes it available to users"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event published successfully"),
            @ApiResponse(responseCode = "403", description = "Caller is not an organizer or does not own the event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> publishEvent(
            @Parameter(description = "ID of the event to publish")
            @PathVariable Integer eventId,

            @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {

        log.info("Organizer {} publishing event {}",
                authenticatedUser.getId(), eventId);

        EventResponseDTO event =
                eventService.publishEvent(authenticatedUser.getId(), eventId);

        log.info("Event {} published successfully by organizer {}",
                eventId, authenticatedUser.getId());

        return ResponseEntity.ok(event);
    }


    @PatchMapping("/unlist/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Unlist an event",
            description = "Organizer only. Removes an event from the publicly available event listings"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event unlisted successfully"),
            @ApiResponse(responseCode = "403", description = "Caller is not an organizer or does not own the event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> unlistEvent(
            @Parameter(description = "ID of the event to unlist")
            @PathVariable Integer eventId,

            @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {

        log.info("Organizer {} unlisting event {}",
                authenticatedUser.getId(), eventId);

        EventResponseDTO event =
                eventService.unlistEvent(authenticatedUser.getId(), eventId);

        log.info("Event {} unlisted successfully by organizer {}",
                eventId, authenticatedUser.getId());

        return ResponseEntity.ok(event);
    }


    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update an event",
            description = "Organizer only. Updates the details of an existing event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data"),
            @ApiResponse(responseCode = "403", description = "Caller is not an organizer or does not own the event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<EventResponseDTO> updateEvent(
            @Valid @RequestBody EventRequestDTO request,

            @AuthenticationPrincipal AppUserPrincipal authenticatedUser,

            @Parameter(description = "ID of the event to update")
            @PathVariable Integer eventId) {

        log.info("Organizer {} updating event {}",
                authenticatedUser.getId(), eventId);

        EventResponseDTO updated =
                eventService.updateEvent(
                        authenticatedUser.getId(),
                        eventId,
                        request
                );

        log.info("Event {} updated successfully by organizer {}",
                eventId, authenticatedUser.getId());

        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Cancel an event",
            description = "Organizer only. Cancels an existing event"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Event cancelled successfully"),
            @ApiResponse(responseCode = "403", description = "Caller is not an organizer or does not own the event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<Void> cancelEvent(
            @Parameter(description = "ID of the event to cancel")
            @PathVariable Integer eventId,

            @AuthenticationPrincipal AppUserPrincipal authenticatedUser) {

        log.info("Organizer {} cancelling event {}",
                authenticatedUser.getId(), eventId);

        eventService.cancelEvent(authenticatedUser.getId(), eventId);

        log.info("Event {} cancelled successfully by organizer {}",
                eventId, authenticatedUser.getId());

        return ResponseEntity.noContent().build();
    }
}


}
