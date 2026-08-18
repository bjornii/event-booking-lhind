package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.EventRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.entity.Category;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.Venue;
import com.lufthansa.internship.exception.*;
import com.lufthansa.internship.mapper.EventMapper;
import com.lufthansa.internship.repository.EventRepository;
import com.lufthansa.internship.repository.VenueRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final VenueRepository venueRepository;


    public void validateEventRequest(EventRequestDTO request){

        if (request.getEndDateTime().isBefore(request.getStartDateTime())){
            throw new IllegalDateTimeExcpetion("Start date should be before End date");
        }
        if(request.getAvailableSeats() > request.getTotalSeats()){
            throw new IllegalSeatsException("Available seats can NOT exceed total number of seats");
        }
        if (request.getPrice() < 0){
            throw new IllegalPriceException("Price can NOT be negative");
        }
        if (request.getVenueId() <= 0 || request.getOrganizerId() <= 0){
            throw new IllegalIdException("Venue ID and Organizer ID must be positive");
        }
        if (request.getCategoryIds().isEmpty()) {
            throw new IllegalCategoryException("Category Id list cannot be empty");
        }

    }

    public EventResponseDTO createEvent(EventRequestDTO request){

        Event e = eventRepository.findByTitle(request.getTitle());
        if (e != null) throw new DuplicateEventTitleException("Event already exists!");

        validateEventRequest(request);

        Event event = eventMapper.toEntity(request);

        Event saved = eventRepository.save(event);

        return eventMapper.toDTO(saved);
    }

    public Page<EventResponseDTO> getAllEvents() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDateTime"));
        Page<Event> events = eventRepository.findAll(pageable);

        return events.map(eventMapper::toDTO);
    }

    public Page<EventResponseDTO> getEventsByCategory(List<String> categories, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByCategoriesIn(categories, pageable);

        return events.map(eventMapper::toDTO);
    }

    public Page<EventResponseDTO> getEventsByCity(String city, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        List<Venue> cityVenues = venueRepository.findByCity(city);
        Page<Event> events = eventRepository.findByVenueIn(cityVenues, pageable);

        return events.map(eventMapper::toDTO);
    }

    public Page<EventResponseDTO> getEventsByDateRange(LocalDate fromDate, LocalDate toDate, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByStartDateTimeBetween(fromDate, toDate, pageable);

        return events.map(eventMapper::toDTO);
    }

    public Page<EventResponseDTO> getEventsByPriceRange(Double startPrice, Double endPrice, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByPriceBetween(startPrice, endPrice, pageable);

        return events.map(eventMapper::toDTO);
    }


}
