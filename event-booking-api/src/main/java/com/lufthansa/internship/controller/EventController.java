package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.EventRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.EventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventRequestDTO request,
                                             @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        EventResponseDTO created = eventService.createEvent(authenticatedUser.getId(), request);
        URI location = URI.create("/api/events/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(@RequestParam(defaultValue = "startDateTime") String sort,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        Page<EventResponseDTO> events = eventService.getAllEvents(sort, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Integer eventId) {
        EventResponseDTO event = eventService.getSingleEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCategory(@RequestParam List<String> category,
                                                                      @RequestParam(defaultValue = "startDateTime") String sort,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Page<EventResponseDTO> events = eventService.getEventsByCategory(category, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/city")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByCity(@RequestParam String city,
                                                                      @RequestParam(defaultValue = "startDateTime") String sort,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Page<EventResponseDTO> events = eventService.getEventsByCity(city, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/dateRange")
    public ResponseEntity<Page<EventResponseDTO>> getEventsByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
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
    public ResponseEntity<Page<EventResponseDTO>> getEventsByPriceRange(@RequestParam Double start,
                                                                        @RequestParam Double end,
                                                                        @RequestParam(defaultValue = "startDateTime") String sort,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Page<EventResponseDTO> events = eventService.getEventsByPriceRange(start, end, sort, page, size);
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/publish/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponseDTO> publishEvent(@PathVariable Integer eventId,
                                                         @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        EventResponseDTO event = eventService.publishEvent(authenticatedUser.getId(), eventId);
        return ResponseEntity.ok(event);
    }

    @PatchMapping("/unlist/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponseDTO> unlistEvent(@PathVariable Integer eventId,
                                                         @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        EventResponseDTO event = eventService.unlistEvent(authenticatedUser.getId(), eventId);
        return ResponseEntity.ok(event);
    }


    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventResponseDTO> updateEvent(@Valid @RequestBody EventRequestDTO request,
                                                        @AuthenticationPrincipal AppUserPrincipal authenticatedUser,
                                                        @PathVariable Integer eventId){
        EventResponseDTO updated = eventService.updateEvent(authenticatedUser.getId(), eventId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Void> cancelEvent(@PathVariable Integer eventId,
                                            @AuthenticationPrincipal AppUserPrincipal authenticatedUser){
        eventService.cancelEvent(authenticatedUser.getId(), eventId);
        return ResponseEntity.noContent().build();
    }


}
