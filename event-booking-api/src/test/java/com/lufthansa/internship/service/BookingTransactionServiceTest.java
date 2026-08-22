package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.BookingWithPaymentRequestDTO;
import com.lufthansa.internship.entity.*;
import com.lufthansa.internship.repository.BookingRepository;
import com.lufthansa.internship.repository.EventRepository;
import com.lufthansa.internship.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingTransactionServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private BookingTransactionService bookingTransactionService;

    private Event event;
    private User user;
    private BookingWithPaymentRequestDTO request;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1);
        event.setAvailableSeats(5);

        user = new User();
        user.setId(1);

        request = BookingWithPaymentRequestDTO.builder()
                .eventId(1).userId(1).seatsBooked(2)
                .amount(100.0).paymentType(PaymentType.CREDITCARD)
                .cardBrand("VISA").cardLastFour("1234")
                .build();
    }

    @Test
    void createInitialBooking_whenSeatsAvailable_setsPendingPayment() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(eventRepository.tryReserveSeats(1, 2)).thenReturn(1);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingTransactionService.createInitialBooking(request);

        assertEquals(BookingStatus.PENDING_PAYMENT, result.getStatus());
        verify(eventRepository).tryReserveSeats(1, 2);
    }

    @Test
    void createInitialBooking_whenNoSeatsLeft_setsWaitlist() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(eventRepository.tryReserveSeats(1, 2)).thenReturn(0);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingTransactionService.createInitialBooking(request);

        assertEquals(BookingStatus.WAITLIST, result.getStatus());
    }

    @Test
    void finalizeBooking_whenPaymentFails_releasesSeats() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setSeatsBooked(2);
        booking.setEvent(event);

        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingTransactionService.finalizeBooking(1, false);

        assertEquals(BookingStatus.CANCELLED, result.getStatus());
        verify(eventRepository).releaseSeats(event.getId(), 2);
    }
}