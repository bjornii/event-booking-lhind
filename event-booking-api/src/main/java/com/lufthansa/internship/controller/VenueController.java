package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.VenueRequestDTO;
import com.lufthansa.internship.dto.response.VenueResponseDTO;
import com.lufthansa.internship.service.VenueService;
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
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Admin-managed event venues")
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a venue", description = "Admin-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venue created"),
            @ApiResponse(responseCode = "409", description = "Venue with this name already exists")
    })
    public ResponseEntity<VenueResponseDTO> createVenue(@Valid @RequestBody VenueRequestDTO request){
        VenueResponseDTO created = venueService.createVenue(request);
        URI location = URI.create("/api/venues/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all venues", description = "Admin-only.")
    @ApiResponse(responseCode = "200", description = "Venues retrieved successfully")
    public ResponseEntity<Page<VenueResponseDTO>> getAllVenues(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        Page<VenueResponseDTO> venues = venueService.getAllVenues(page, size);
        return ResponseEntity.ok(venues);
    }

    @PutMapping("/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a venue", description = "Admin-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venue updated"),
            @ApiResponse(responseCode = "404", description = "Venue not found"),
            @ApiResponse(responseCode = "409", description = "Venue with this name already exists")
    })
    public ResponseEntity<VenueResponseDTO> updateVenue(@PathVariable Integer venueId, @Valid @RequestBody VenueRequestDTO request){
        VenueResponseDTO updated = venueService.updateVenue(venueId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deactivate a venue", description = "Admin-only. Soft-deletes by marking the venue INACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Venue deactivated"),
            @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    public ResponseEntity<Void> removeVenue(@Valid @PathVariable Integer venueId){
        venueService.removeVenue(venueId);
        return ResponseEntity.noContent().build();
    }
}