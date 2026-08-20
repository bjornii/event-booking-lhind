package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.EventRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.entity.*;
import com.lufthansa.internship.exception.*;
import com.lufthansa.internship.mapper.EventMapper;
import com.lufthansa.internship.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Transactional
    public void validateEventRequest(Integer organizerId, EventRequestDTO request){

        if (request.getEndDateTime().isBefore(request.getStartDateTime())){
            throw new IllegalDateTimeExcpetion("Start date should be before End date");
        }
        if(request.getAvailableSeats() > request.getTotalSeats()){
            throw new IllegalSeatsException("Available seats can NOT exceed total number of seats");
        }
        if (request.getPrice() < 0){
            throw new IllegalPriceException("Price can NOT be negative");
        }
        if (request.getVenueId() <= 0 || organizerId <= 0){
            throw new IllegalIdException("Venue ID and Organizer ID must be positive");
        }
        if (request.getCategoryIds().isEmpty()) {
            throw new IllegalCategoryException("Category Id list cannot be empty");
        }

        for (Integer categoryId : request.getCategoryIds()){
            if(categoryRepository.findStatusById(categoryId).equals(CategoryStatus.INACTIVE)) {
                throw new IllegalCategoryException("Select available categories");
            }
        }

        if(venueRepository.findStatusById(request.getVenueId()).equals(VenueStatus.INACTIVE)) {
            throw new IllegalVenueException("Select available venues");
        }

    }

    @Transactional
    public EventResponseDTO createEvent(Integer organizerId, EventRequestDTO request){
        Event e = eventRepository.findByTitle(request.getTitle());
        if (e != null) throw new DuplicateEventTitleException("Event already exists!");
        User organizer = userRepository.findById(organizerId).orElseThrow(() -> new UserNotFoundException("User not found!"));
        validateEventRequest(organizerId, request);

        Event event = eventMapper.toEntity(request);
        event.setOrganizer(organizer);
        event.setVisibility(EventVisibility.UNLISTED);

        Event saved = eventRepository.save(event);

        return eventMapper.toDTO(saved);
    }

    @Transactional
    public Page<EventResponseDTO> getAllEvents() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("startDateTime"));
        Page<Event> events = eventRepository.findAll(pageable);

        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByCategory(List<String> categories, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByCategoriesIn(categories, pageable);

        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByCity(String city, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        List<Venue> cityVenues = venueRepository.findByCity(city);
        Page<Event> events = eventRepository.findByVenueIn(cityVenues, pageable);

        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByDateRange(LocalDateTime fromDate, LocalDateTime toDate, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByStartDateTimeBetween(fromDate, toDate, pageable);

        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByPriceRange(Double startPrice, Double endPrice, String sortVariable) {
        if (sortVariable.isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByPriceBetween(startPrice, endPrice, pageable);

        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public EventResponseDTO getSingleEvent(Integer eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event does NOT exist!"));
        return eventMapper.toDTO(event);
    }

    @Transactional
    public EventResponseDTO updateEvent(Integer organizerId, Integer eventId, EventRequestDTO request){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event does NOT exist!"));
        if (!organizerId.equals(event.getOrganizer().getId())) throw new UnauthorizedUpdateEventException("You can only update your own events");
        if (eventRepository.existsEventByTitle(request.getTitle())) throw new DuplicateEventTitleException("Event already exists!");
        Venue venue = venueRepository.findById(request.getVenueId()).orElseThrow(() -> new VenueNotFoundException("Venue not found"));
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds()).stream().toList();

        validateEventRequest(organizerId, request);

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getStartDateTime() != null) event.setStartDateTime(request.getStartDateTime());
        if (request.getEndDateTime() != null) event.setEndDateTime(request.getEndDateTime());
        if (request.getPrice() != null) event.setPrice(request.getPrice());
        if (request.getTotalSeats() != null) event.setTotalSeats(request.getTotalSeats());
        if (request.getAvailableSeats() != null) event.setAvailableSeats(request.getAvailableSeats());
        if (request.getVenueId() != null) event.setVenue(venue);
        if (request.getCategoryIds() != null) event.setCategories(categories);

        eventMapper.updateEntityFromDTO(request, event);
        event.setUpdatedAt(LocalDateTime.now());
        event.setOrganizer(event.getOrganizer());

        Event saved = eventRepository.save(event);
        return eventMapper.toDTO(saved);
    }

    @Transactional
    public EventResponseDTO publishEvent(Integer organizerId, Integer eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event does NOT exist!"));
        if (!organizerId.equals(event.getOrganizer().getId())) throw new UnauthorizedUpdateEventException("You can only update your own events");
        event.setVisibility(EventVisibility.PUBLISHED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setOrganizer(event.getOrganizer());
        Event saved = eventRepository.save(event);
        return eventMapper.toDTO(saved);
    }

    @Transactional
    public EventResponseDTO unlistEvent(Integer organizerId, Integer eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event does NOT exist!"));
        if (!organizerId.equals(event.getOrganizer().getId())) throw new UnauthorizedUpdateEventException("You can only update your own events");
        event.setVisibility(EventVisibility.UNLISTED);
        event.setUpdatedAt(LocalDateTime.now());
        event.setOrganizer(event.getOrganizer());
        Event saved = eventRepository.save(event);
        return eventMapper.toDTO(saved);
    }

    @Transactional
    public void cancelEvent(Integer organizerId, Integer eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event does NOT exist!"));
        if (!organizerId.equals(event.getOrganizer().getId())) throw new UnauthorizedUpdateEventException("You can only cancel your own events");
        if (event.getStartDateTime().isBefore(LocalDateTime.now())) throw new InvalidCancellationRequestException("Cannot cancel past events");
        if (!event.getStatus().equals(EventStatus.UPCOMING)) throw new InvalidCancellationRequestException("Can only cancel upcoming events");

        event.setStatus(EventStatus.CANCELLED);

        for(Booking booking : event.getBookings()){
            if (booking.getStatus().equals(BookingStatus.CONFIRMED)) booking.setStatus(BookingStatus.REFUNDED);
            if (booking.getStatus().equals(BookingStatus.WAITLIST)) booking.setStatus(BookingStatus.CANCELLED);
        }
        bookingRepository.saveAll(event.getBookings());
        eventRepository.save(event);

    }



}
