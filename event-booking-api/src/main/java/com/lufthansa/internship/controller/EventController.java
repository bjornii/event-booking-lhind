package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.EventRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.entity.EventVisibility;
import com.lufthansa.internship.security.AppUserPrincipal;
import com.lufthansa.internship.service.EventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
