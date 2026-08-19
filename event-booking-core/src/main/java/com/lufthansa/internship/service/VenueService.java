package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.CategoryRequestDTO;
import com.lufthansa.internship.dto.request.VenueRequestDTO;
import com.lufthansa.internship.dto.response.CategoryResponseDTO;
import com.lufthansa.internship.dto.response.VenueResponseDTO;
import com.lufthansa.internship.entity.Category;
import com.lufthansa.internship.entity.CategoryStatus;
import com.lufthansa.internship.entity.Venue;
import com.lufthansa.internship.entity.VenueStatus;
import com.lufthansa.internship.exception.CategoryNotFoundException;
import com.lufthansa.internship.exception.DuplicateCategoryException;
import com.lufthansa.internship.exception.DuplicateVenueException;
import com.lufthansa.internship.exception.VenueNotFoundException;
import com.lufthansa.internship.mapper.VenueMapper;
import com.lufthansa.internship.repository.VenueRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
        Venue saved = venueRepository.save(venue);
        return venueMapper.toDTO(saved);
    }

    @Transactional
    public VenueResponseDTO updateVenue(Integer venueId, VenueRequestDTO request){
        Venue existing = venueRepository.findById(venueId).orElseThrow(() -> new VenueNotFoundException("Venue does not exist"));
        Venue dupe = venueRepository.findByName(request.getName());
        if (dupe != null) throw new DuplicateVenueException("Venue with this name already exists");
        venueMapper.updateEntityFromDTO(request, existing);

        Venue saved = venueRepository.save(existing);
        return venueMapper.toDTO(saved);
    }

    @Transactional
    public VenueResponseDTO removeVenue(Integer venueId){
        Venue venue = venueRepository.findById(venueId).orElseThrow(() -> new VenueNotFoundException("Venue does not exist"));
        venue.setStatus(VenueStatus.INACTIVE);
        Venue saved = venueRepository.save(venue);
        return venueMapper.toDTO(saved);
    }

}
