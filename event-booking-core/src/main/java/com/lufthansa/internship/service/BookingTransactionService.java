package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.BookingWithPaymentRequestDTO;
import com.lufthansa.internship.entity.Booking;
import com.lufthansa.internship.entity.BookingStatus;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import com.lufthansa.internship.exception.BookingNotFoundException;
import com.lufthansa.internship.exception.EventNotFoundException;
import com.lufthansa.internship.exception.UserNotFoundException;
import com.lufthansa.internship.repository.BookingRepository;
import com.lufthansa.internship.repository.EventRepository;
import com.lufthansa.internship.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BookingTransactionService {

    private static final Logger log = LoggerFactory.getLogger(BookingTransactionService.class);

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createInitialBooking(BookingWithPaymentRequestDTO request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event does not exist!"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        Booking booking = new Booking();
        booking.setSeatsBooked(request.getSeatsBooked());
        booking.setBookingDate(LocalDateTime.now());
        booking.setUser(user);
        booking.setEvent(event);

        int updated = eventRepository.tryReserveSeats(event.getId(), request.getSeatsBooked());

        if (updated > 0) {
            booking.setStatus(BookingStatus.PENDING_PAYMENT);
            log.info("Reserved {} seats for event {}, booking pending payment", request.getSeatsBooked(), event.getId());
        } else {
            booking.setStatus(BookingStatus.WAITLIST);
            log.info("No seats available for event {}, booking for user {} waitlisted", event.getId(), user.getId());
        }

        Booking saved = bookingRepository.save(booking);
        log.debug("Booking {} created with status {}", saved.getId(), saved.getStatus());
        return saved;
    }

    @Transactional
    public Booking finalizeBooking(Integer bookingId, boolean paymentSucceeded) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (paymentSucceeded) {
            booking.setStatus(BookingStatus.CONFIRMED);
            log.info("Booking {} confirmed after successful payment", bookingId);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            eventRepository.releaseSeats(booking.getEvent().getId(), booking.getSeatsBooked());
            log.warn("Booking {} cancelled after failed payment, {} seats released back to event {}",
                    bookingId, booking.getSeatsBooked(), booking.getEvent().getId());
        }

        return bookingRepository.save(booking);
    }
}