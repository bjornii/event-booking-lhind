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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Transactional
    public VenueResponseDTO createVenue (VenueRequestDTO request){

        Venue existing = venueRepository.findByName(request.getName());
        if (existing != null) throw new DuplicateVenueException("Venue with this name already exists");
        Venue venue = venueMapper.toEntity(request);
        venue.setStatus(VenueStatus.ACTIVE);
        venue.setCreatedAt(LocalDateTime.now());
        Venue saved = venueRepository.save(venue);
        return venueMapper.toDTO(saved);
    }

    @Transactional
    public Page<VenueResponseDTO> getAllCategories(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Venue> venues = venueRepository.findAll(pageable);
        return venues.map(venueMapper::toDTO);
    }

    @Transactional
    public VenueResponseDTO updateVenue(Integer venueId, VenueRequestDTO request){
        Venue existing = venueRepository.findByIdAndStatus(venueId, VenueStatus.ACTIVE).orElseThrow(() -> new VenueNotFoundException("Venue does not exist"));
        Venue dupe = venueRepository.findByName(request.getName());
        if (dupe != null) throw new DuplicateVenueException("Venue with this name already exists");
        venueMapper.updateEntityFromDTO(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        Venue saved = venueRepository.save(existing);
        return venueMapper.toDTO(saved);
    }

    @Transactional
    public void removeVenue(Integer venueId){
        Venue venue = venueRepository.findByIdAndStatus(venueId, VenueStatus.ACTIVE).orElseThrow(() -> new VenueNotFoundException("Venue does not exist"));
        venue.setStatus(VenueStatus.INACTIVE);
        venueRepository.save(venue);
    }

}
