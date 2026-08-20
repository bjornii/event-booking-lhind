package com.lufthansa.internship.controller;

import com.lufthansa.internship.dto.request.CategoryRequestDTO;
import com.lufthansa.internship.dto.request.VenueRequestDTO;
import com.lufthansa.internship.dto.response.CategoryResponseDTO;
import com.lufthansa.internship.dto.response.VenueResponseDTO;
import com.lufthansa.internship.service.VenueService;
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
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> createCategory(@Valid @RequestBody VenueRequestDTO request){
        VenueResponseDTO created = venueService.createVenue(request);
        URI location = URI.create("/api/venues/" + created.getId());

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<VenueResponseDTO>> getAllCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        Page<VenueResponseDTO> users = venueService.getAllCategories(page, size);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VenueResponseDTO> updateCategory (@PathVariable Integer venueId, @Valid @RequestBody VenueRequestDTO request){
        VenueResponseDTO updated = venueService.updateVenue(venueId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeCategory (@Valid @PathVariable Integer venueId){
        venueService.removeVenue(venueId);
        return ResponseEntity.noContent().build();
    }

}
