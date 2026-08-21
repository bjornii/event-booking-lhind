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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BookingTransactionService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public Booking createInitialBooking(BookingWithPaymentRequestDTO request) {
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new EventNotFoundException("Event does not exist!"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        Booking booking = new Booking();
        booking.setSeatsBooked(request.getSeatsBooked());
        booking.setBookingDate(LocalDateTime.now());
        booking.setUser(user);
        booking.setEvent(event);

        int updated = eventRepository.tryReserveSeats(event.getId(), request.getSeatsBooked());

        booking.setStatus(updated > 0 ? BookingStatus.PENDING_PAYMENT : BookingStatus.WAITLIST);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking finalizeBooking(Integer bookingId, boolean paymentSucceeded) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (paymentSucceeded) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            eventRepository.releaseSeats(booking.getEvent().getId(), booking.getSeatsBooked());
        }

        return bookingRepository.save(booking);
    }

}
