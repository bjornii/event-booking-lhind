package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.BookingRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.entity.Booking;
import com.lufthansa.internship.entity.BookingStatus;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import com.lufthansa.internship.exception.EventNotFoundException;
import com.lufthansa.internship.exception.InsufficientSeatsException;
import com.lufthansa.internship.exception.UserNotFoundException;
import com.lufthansa.internship.mapper.BookingMapper;
import com.lufthansa.internship.repository.BookingRepository;
import com.lufthansa.internship.repository.EventRepository;
import com.lufthansa.internship.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request){
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new EventNotFoundException("Event does not exist!"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User does not exist!"));
        if (request.getSeatsBooked() > event.getAvailableSeats()){
            throw new InsufficientSeatsException("Requested number of seats are not available");
        }

        Booking booking = bookingMapper.toEntity(request);

        booking.setBookingDate(LocalDate.now());
        booking.setUser(user);
        booking.setEvent(event);
        booking.setStatus(BookingStatus.PENDING);

//        SHOULD BE SET TO CONFIRMED AFTER PAYMENT CONFIRMED,
//        MAYBE RETURN CHECK BACK LATER IF NO MORE AVAILABLE SEATS AND CURRENT BOOKING THAT OCCUPIES LAST SEATS IS NOT CONFIRMED YET

        Booking saved = bookingRepository.save(booking);
        event.setAvailableSeats(event.getAvailableSeats() - request.getSeatsBooked());
        return bookingMapper.toDTO(saved);

    }

    public Page<BookingResponseDTO> viewMyBookings(Integer userId, String sortVariable){
        if (sortVariable == null || sortVariable.isEmpty())  sortVariable="bookingDate";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable).descending());
        Page<Booking> bookings = bookingRepository.findByUserId(userId, pageable);

        return bookings.map(bookingMapper::toDTO);
    }

}
