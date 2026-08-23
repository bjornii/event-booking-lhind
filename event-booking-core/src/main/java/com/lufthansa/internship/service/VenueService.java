package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.VenueRequestDTO;
import com.lufthansa.internship.dto.response.VenueResponseDTO;
import com.lufthansa.internship.entity.Venue;
import com.lufthansa.internship.entity.VenueStatus;
import com.lufthansa.internship.exception.DuplicateVenueException;
import com.lufthansa.internship.exception.VenueNotFoundException;
import com.lufthansa.internship.mapper.VenueMapper;
import com.lufthansa.internship.repository.VenueRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class VenueService {

    private static final Logger log = LoggerFactory.getLogger(VenueService.class);

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    public VenueRequestDTO validateRequest(VenueRequestDTO request) {
        return new VenueRequestDTO(request.getName().trim(), request.getAddress().trim(), request.getCity().trim(), request.getCapacity());
    }

    @Transactional
    public VenueResponseDTO createVenue(VenueRequestDTO request){
        request = validateRequest(request);
        Venue existing = venueRepository.findByName(request.getName());
        if (existing != null) {
            log.warn("Venue creation rejected: '{}' already exists", request.getName());
            throw new DuplicateVenueException("Venue with this name already exists");
        }
        Venue venue = venueMapper.toEntity(request);
        venue.setStatus(VenueStatus.ACTIVE);
        venue.setCreatedAt(LocalDateTime.now());
        Venue saved = venueRepository.save(venue);
        log.info("Venue '{}' created with id {}", saved.getName(), saved.getId());
        return venueMapper.toDTO(saved);
    }

    @Transactional
    public Page<VenueResponseDTO> getAllVenues(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Venue> venues = venueRepository.findAll(pageable);
        return venues.map(venueMapper::toDTO);
    }

    @Transactional
    public VenueResponseDTO updateVenue(Integer venueId, VenueRequestDTO request){
        request = validateRequest(request);
        Venue existing = venueRepository.findByIdAndStatus(venueId, VenueStatus.ACTIVE)
                .orElseThrow(() -> new VenueNotFoundException("Venue does not exist"));
        Venue dupe = venueRepository.findByName(request.getName());
        if (dupe != null && dupe.getId().equals(venueId)) {
            log.warn("Venue update rejected: name '{}' already in use", request.getName());
            throw new DuplicateVenueException("Venue with this name already exists");
        }
        venueMapper.updateEntityFromDTO(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        Venue saved = venueRepository.save(existing);
        log.info("Venue {} updated", venueId);
        return venueMapper.toDTO(saved);
    }

    @Transactional
    public void removeVenue(Integer venueId){
        Venue venue = venueRepository.findByIdAndStatus(venueId, VenueStatus.ACTIVE)
                .orElseThrow(() -> new VenueNotFoundException("Venue does not exist"));
        venue.setStatus(VenueStatus.INACTIVE);
        venueRepository.save(venue);
        log.info("Venue {} deactivated", venueId);
    }
}