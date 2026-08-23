package com.lufthansa.internship.integration;

import com.lufthansa.internship.dto.request.BookingWithPaymentRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.entity.*;
import com.lufthansa.internship.exception.SeatLimitExceedingException;
import com.lufthansa.internship.repository.EventRepository;
import com.lufthansa.internship.repository.UserRepository;
import com.lufthansa.internship.repository.VenueRepository;
import com.lufthansa.internship.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingFlowIntegrationTest {

    @Autowired private BookingService bookingService;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VenueRepository venueRepository;

    private Event event;
    private User attendee;

    @BeforeEach
    void setUp() {
        Venue venue = new Venue();
        venue.setName("Test Arena");
        venue.setCity("Tirana");
        venue.setAddress("123 Test St");
        venue.setStatus(VenueStatus.ACTIVE);
        venue.setCreatedAt(LocalDateTime.now());
        venue = venueRepository.save(venue);

        User organizer = new User();
        organizer.setEmail("organizer@integration.test");
        organizer.setUsername("organizer_it");
        organizer.setPassword("hashed");
        organizer.setRole(UserRole.ORGANIZER);
        organizer.setStatus(UserStatus.ACTIVATED);
        organizer.setCreatedAt(LocalDateTime.now());
        organizer = userRepository.save(organizer);

        attendee = new User();
        attendee.setEmail("attendee@integration.test");
        attendee.setUsername("attendee_it");
        attendee.setPassword("hashed");
        attendee.setRole(UserRole.ATTENDEE);
        attendee.setStatus(UserStatus.ACTIVATED);
        attendee.setCreatedAt(LocalDateTime.now());
        attendee = userRepository.save(attendee);

        event = new Event();
        event.setTitle("Test Concert");
        event.setDescription("desc");
        event.setStartDateTime(LocalDateTime.now().plusDays(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(5).plusHours(3));
        event.setPrice(50.0);
        event.setTotalSeats(2);
        event.setAvailableSeats(2);
        event.setStatus(EventStatus.UPCOMING);
        event.setVisibility(EventVisibility.PUBLISHED);
        event.setVenue(venue);
        event.setOrganizer(organizer);
        event.setCreatedAt(LocalDateTime.now());
        event = eventRepository.save(event);
    }

    @Test
    void bookingFlow_confirmsBooking_andDecrementsAvailableSeats() {
        BookingWithPaymentRequestDTO request = BookingWithPaymentRequestDTO.builder()
                .userId(attendee.getId())
                .eventId(event.getId())
                .seatsBooked(2)
                .amount(100.0)
                .paymentType(PaymentType.CREDITCARD)
                .cardLastFour("1234")
                .cardBrand("Visa")
                .build();

        BookingResponseDTO response = bookingService.createBooking(request);

        assertEquals(BookingStatus.CONFIRMED.name(), response.getStatus());

        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertEquals(0, updatedEvent.getAvailableSeats());
    }

    @Test
    void bookingFlow_whenSeatsExceedEventCapacity_goesToWaitlist() {
        bookingService.createBooking(BookingWithPaymentRequestDTO.builder()
                .userId(attendee.getId()).eventId(event.getId())
                .seatsBooked(2).amount(100.0)
                .paymentType(PaymentType.CREDITCARD).cardLastFour("1234").cardBrand("Visa")
                .build());

        User secondAttendee = new User();
        secondAttendee.setEmail("second@integration.test");
        secondAttendee.setUsername("second_it");
        secondAttendee.setPassword("hashed");
        secondAttendee.setRole(UserRole.ATTENDEE);
        secondAttendee.setStatus(UserStatus.ACTIVATED);
        secondAttendee.setCreatedAt(LocalDateTime.now());
        secondAttendee = userRepository.save(secondAttendee);

        BookingResponseDTO response = bookingService.createBooking(BookingWithPaymentRequestDTO.builder()
                .userId(secondAttendee.getId()).eventId(event.getId())
                .seatsBooked(1).amount(50.0)
                .paymentType(PaymentType.CREDITCARD).cardLastFour("5678").cardBrand("Visa")
                .build());

        assertEquals(BookingStatus.WAITLIST.name(), response.getStatus());

        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertEquals(0, updatedEvent.getAvailableSeats());
    }

    @Test
    void bookingFlow_rejectsBookingOverSeatLimit() {
        BookingWithPaymentRequestDTO request = BookingWithPaymentRequestDTO.builder()
                .userId(attendee.getId()).eventId(event.getId())
                .seatsBooked(7)
                .amount(350.0)
                .paymentType(PaymentType.CREDITCARD).cardLastFour("1234").cardBrand("Visa")
                .build();

        assertThrows(SeatLimitExceedingException.class, () -> bookingService.createBooking(request));

        Event unchanged = eventRepository.findById(event.getId()).orElseThrow();
        assertEquals(2, unchanged.getAvailableSeats());
    }

    @Test
    void cancelBooking_promotesWaitlistedBookingAndRestoresSeats() {
        BookingResponseDTO firstBooking = bookingService.createBooking(BookingWithPaymentRequestDTO.builder()
                .userId(attendee.getId()).eventId(event.getId())
                .seatsBooked(2).amount(100.0)
                .paymentType(PaymentType.CREDITCARD).cardLastFour("1234").cardBrand("Visa")
                .build());

        User waitlistedUser = new User();
        waitlistedUser.setEmail("waitlisted@integration.test");
        waitlistedUser.setUsername("waitlisted_it");
        waitlistedUser.setPassword("hashed");
        waitlistedUser.setRole(UserRole.ATTENDEE);
        waitlistedUser.setStatus(UserStatus.ACTIVATED);
        waitlistedUser.setCreatedAt(LocalDateTime.now());
        waitlistedUser = userRepository.save(waitlistedUser);

        BookingResponseDTO waitlisted = bookingService.createBooking(BookingWithPaymentRequestDTO.builder()
                .userId(waitlistedUser.getId()).eventId(event.getId())
                .seatsBooked(1).amount(50.0)
                .paymentType(PaymentType.CREDITCARD).cardLastFour("5678").cardBrand("Visa")
                .build());
        assertEquals(BookingStatus.WAITLIST.name(), waitlisted.getStatus());

        bookingService.cancelBookingByUser(firstBooking.getId(), attendee.getId());

        Event finalEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertEquals(1, finalEvent.getAvailableSeats());
    }
}