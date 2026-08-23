package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.EventRequestDTO;
import com.lufthansa.internship.dto.response.EventResponseDTO;
import com.lufthansa.internship.entity.*;
import com.lufthansa.internship.exception.*;
import com.lufthansa.internship.mapper.EventMapper;
import com.lufthansa.internship.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void validateEventRequest(Integer organizerId, EventRequestDTO request) {
        log.debug("Validating event request for organizerId={} and title={}", organizerId, request.getTitle());

        if (request.getEndDateTime().isBefore(request.getStartDateTime())) {
            log.error("Invalid date range: start={} end={}", request.getStartDateTime(), request.getEndDateTime());
            throw new IllegalDateTimeExcpetion("Start date should be before End date");
        }
        if (request.getAvailableSeats() > request.getTotalSeats()) {
            log.error("Available seats {} exceed total seats {}", request.getAvailableSeats(), request.getTotalSeats());
            throw new IllegalSeatsException("Available seats can NOT exceed total number of seats");
        }
        if (request.getPrice() < 0) {
            log.error("Negative price detected: {}", request.getPrice());
            throw new IllegalPriceException("Price can NOT be negative");
        }
        if (request.getVenueId() <= 0 || organizerId <= 0) {
            log.error("Invalid IDs: venueId={} organizerId={}", request.getVenueId(), organizerId);
            throw new IllegalIdException("Venue ID and Organizer ID must be positive");
        }
        if (request.getCategoryIds().isEmpty()) {
            log.error("Empty category list for event {}", request.getTitle());
            throw new IllegalCategoryException("Category Id list cannot be empty");
        }

        for (Integer categoryId : request.getCategoryIds()) {
            if (categoryRepository.findStatusById(categoryId).equals(CategoryStatus.INACTIVE)) {
                log.error("Inactive category selected: {}", categoryId);
                throw new IllegalCategoryException("Select available categories");
            }
        }

        if (venueRepository.findStatusById(request.getVenueId()).equals(VenueStatus.INACTIVE)) {
            log.error("Inactive venue selected: {}", request.getVenueId());
            throw new IllegalVenueException("Select available venues");
        }

        log.debug("Validation passed for event {}", request.getTitle());
    }

    @Transactional
    public EventResponseDTO createEvent(Integer organizerId, EventRequestDTO request) {
        log.info("Creating event with title={} by organizerId={}", request.getTitle(), organizerId);

        Event e = eventRepository.findByTitle(request.getTitle());
        if (e != null) {
            log.error("Duplicate event title detected: {}", request.getTitle());
            throw new DuplicateEventTitleException("Event already exists!");
        }

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> {
                    log.error("Organizer not found with id={}", organizerId);
                    return new UserNotFoundException("User not found!");
                });

        validateEventRequest(organizerId, request);

        Event event = eventMapper.toEntity(request);
        event.setOrganizer(organizer);
        event.setCreatedAt(LocalDateTime.now());
        event.setStatus(EventStatus.UPCOMING);
        event.setVisibility(EventVisibility.UNLISTED);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            event.setCategories(categories);
        }

        Event saved = eventRepository.save(event);
        log.info("Event created successfully with id={} title={}", saved.getId(), saved.getTitle());

        return eventMapper.toDTO(saved);
    }

    @Transactional
    public Page<EventResponseDTO> getAllEvents(String sortVariable, int page, int size) {
        log.debug("Fetching all events with sort={} page={} size={}", sortVariable, page, size);
        if (sortVariable.trim().isEmpty()) sortVariable = "startDateTime";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findAllByVisibility(EventVisibility.PUBLISHED, pageable);

        log.info("Fetched {} events", events.getTotalElements());
        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByCategory(List<String> categories, String sortVariable, int page, int size) {
        log.debug("Fetching events by categories={} sort={} page={} size={}", categories, sortVariable, page, size);
        if (sortVariable.trim().isEmpty()) sortVariable = "startDateTime";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByVisibilityAndCategoryNamesIgnoreCase(EventVisibility.PUBLISHED, categories, pageable);

        log.info("Fetched {} events by category", events.getTotalElements());
        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByCity(String city, String sortVariable, int page, int size) {
        log.debug("Fetching events by city={} sort={} page={} size={}", city, sortVariable, page, size);
        if (sortVariable.trim().isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortVariable));
        List<Venue> cityVenues = venueRepository.findByCityIgnoreCase(city);
        Page<Event> events = eventRepository.findByVisibilityAndVenueIn(EventVisibility.PUBLISHED, cityVenues, pageable);

        log.info("Fetched {} events in city={}", events.getTotalElements(), city);
        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByDateRange(LocalDateTime fromDate, LocalDateTime toDate, String sortVariable, int page, int size) {
        log.debug("Fetching events between {} and {}", fromDate, toDate);
        if (sortVariable.trim().isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByVisibilityAndStartDateTimeBetween(EventVisibility.PUBLISHED, fromDate, toDate, pageable);

        log.info("Fetched {} events in date range", events.getTotalElements());
        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public Page<EventResponseDTO> getEventsByPriceRange(Double startPrice, Double endPrice, String sortVariable, int page, int size) {
        log.debug("Fetching events with price between {} and {}", startPrice, endPrice);
        if (sortVariable.trim().isEmpty()) sortVariable = "startDateTime";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortVariable));
        Page<Event> events = eventRepository.findByVisibilityAndPriceBetween(EventVisibility.PUBLISHED, startPrice, endPrice, pageable);

        log.info("Fetched {} events in price range", events.getTotalElements());
        return events.map(eventMapper::toDTO);
    }

    @Transactional
    public EventResponseDTO getSingleEvent(Integer eventId) {
        log.debug("Fetching single event with id={}", eventId);
        Event event = eventRepository.findByVisibilityAndId(EventVisibility.PUBLISHED, eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id={}", eventId);
                    return new EventNotFoundException("Event does NOT exist!");
                });
        log.info("Fetched event with id={}", eventId);
        return eventMapper.toDTO(event);
    }

    @Transactional
    public EventResponseDTO updateEvent(Integer organizerId, Integer eventId, EventRequestDTO request) {
        log.info("Updating eventId={} by organizerId={}", eventId, organizerId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id={}", eventId);
                    return new EventNotFoundException("Event does NOT exist!");
                });
        if (!organizerId.equals(event.getOrganizer().getId())) {
            log.error("Unauthorized update attempt by organizerId={} for eventId={}", organizerId, eventId);
            throw new UnauthorizedUpdateEventException("You can only update your own events");
        }
        if (eventRepository.findByTitle(request.getTitle()) != null && eventRepository.findByTitle(request.getTitle()).getId().equals(eventId)) {
            log.error("Duplicate event title detected during update: {}", request.getTitle());
            throw new DuplicateEventTitleException("Event already exists!");
        }

        validateEventRequest(organizerId, request);

        eventMapper.updateEntityFromDTO(request, event);
        event.setUpdatedAt(LocalDateTime.now());
        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        event.getCategories().clear();
        event.getCategories().addAll(categories);
        Event saved = eventRepository.save(event);
        log.info("Event updated successfully with id={}", saved.getId());
        return eventMapper.toDTO(saved);
    }

    @Transactional
    public EventResponseDTO publishEvent(Integer organizerId, Integer eventId) {
        log.info("Publishing eventId={} by organizerId={}", eventId, organizerId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id={}", eventId);
                    return new EventNotFoundException("Event does NOT exist!");
                });

        if (!organizerId.equals(event.getOrganizer().getId())) {
            log.error("Unauthorized publish attempt by organizerId={} for eventId={}", organizerId, eventId);
            throw new UnauthorizedUpdateEventException("You can only update your own events");
        }

        event.setVisibility(EventVisibility.PUBLISHED);
        event.setUpdatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        log.info("Event published successfully with id={} title={}", saved.getId(), saved.getTitle());

        return eventMapper.toDTO(saved);
    }

    @Transactional
    public EventResponseDTO unlistEvent(Integer organizerId, Integer eventId){
        log.info("Unlisting eventId={} by organizerId={}", eventId, organizerId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id={}", eventId);
                    return new EventNotFoundException("Event does NOT exist!");
                });

        if (!organizerId.equals(event.getOrganizer().getId())) {
            log.error("Unauthorized unlist attempt by organizerId={} for eventId={}", organizerId, eventId);
            throw new UnauthorizedUpdateEventException("You can only update your own events");
        }

        event.setVisibility(EventVisibility.UNLISTED);
        event.setUpdatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        log.info("Event unlisted successfully with id={} title={}", saved.getId(), saved.getTitle());

        return eventMapper.toDTO(saved);
    }

    @Transactional
    public void cancelEvent(Integer organizerId, Integer eventId){
        log.warn("Cancelling eventId={} by organizerId={}", eventId, organizerId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event not found with id={}", eventId);
                    return new EventNotFoundException("Event does NOT exist!");
                });

        if (!organizerId.equals(event.getOrganizer().getId())) {
            log.error("Unauthorized cancel attempt by organizerId={} for eventId={}", organizerId, eventId);
            throw new UnauthorizedUpdateEventException("You can only cancel your own events");
        }
        if (event.getStartDateTime().isBefore(LocalDateTime.now())) {
            log.error("Cancellation denied: eventId={} has already started", eventId);
            throw new InvalidCancellationRequestException("Cannot cancel past events");
        }
        if (!event.getStatus().equals(EventStatus.UPCOMING)) {
            log.error("Cancellation denied: eventId={} is not upcoming (status={})", eventId, event.getStatus());
            throw new InvalidCancellationRequestException("Can only cancel upcoming events");
        }

        event.setStatus(EventStatus.CANCELLED);
        log.debug("Event status set to CANCELLED for eventId={}", eventId);

        for (Booking booking : event.getBookings()){
            if (booking.getStatus().equals(BookingStatus.CONFIRMED)) {
                booking.setStatus(BookingStatus.REFUNDED);
                log.debug("Booking {} refunded", booking.getId());
            }
            if (booking.getStatus().equals(BookingStatus.WAITLIST)) {
                booking.setStatus(BookingStatus.CANCELLED);
                log.debug("Booking {} cancelled from waitlist", booking.getId());
            }
        }

        bookingRepository.saveAll(event.getBookings());
        eventRepository.save(event);

        log.info("Event cancelled successfully with id={} and {} bookings updated", eventId, event.getBookings().size());
    }


}