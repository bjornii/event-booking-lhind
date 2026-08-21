package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.BookingRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.dto.response.EventBookingsDTO;
import com.lufthansa.internship.entity.Booking;
import com.lufthansa.internship.entity.BookingStatus;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import com.lufthansa.internship.exception.*;
import com.lufthansa.internship.mapper.BookingMapper;
import com.lufthansa.internship.notification.BookingEventPublisher;
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

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingEventPublisher publisher;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request){
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new EventNotFoundException("Event does not exist!"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User does not exist!"));
        if (request.getSeatsBooked() > 6){
            throw new SeatLimitExceedingException("Cannot book more than 6 seats per booking");
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setBookingDate(LocalDateTime.now());
        booking.setUser(user);
        booking.setEvent(event);

        if (request.getSeatsBooked() <= event.getAvailableSeats()){
            booking.setStatus(BookingStatus.CONFIRMED);
            event.setAvailableSeats(event.getAvailableSeats() - request.getSeatsBooked());
            eventRepository.save(event);
        } else {
            booking.setStatus(BookingStatus.WAITLIST);
        }

//        SHOULD BE SET TO CONFIRMED AFTER PAYMENT CONFIRMED,
//        MAYBE RETURN CHECK BACK LATER IF NO MORE AVAILABLE SEATS AND CURRENT BOOKING THAT OCCUPIES LAST SEATS IS NOT CONFIRMED YET

        Booking saved = bookingRepository.save(booking);
        publisher.notifyBookingCreated(saved);
        return bookingMapper.toDTO(saved);

    }
    @Transactional
    public Page<BookingResponseDTO> viewMyBookings(Integer userId, String sortVariable){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(sortVariable).descending());
        Page<Booking> bookings = bookingRepository.findByUserId(userId, pageable);

        return bookings.map(bookingMapper::toDTO);
    }

    @Transactional
    public List<EventBookingsDTO> getMyBookingsAsOrganizer(Integer userId){

        List<Event> events = eventRepository.findByOrganizerId(userId);

        return events.stream()
                .map(event -> {
                    List<BookingResponseDTO> bookings = bookingRepository.findByEvent(event).stream()
                            .map(bookingMapper::toDTO)
                            .toList();
                    return new EventBookingsDTO(event.getTitle(), bookings);
                }).toList();

    }

    @Transactional
    public Page<BookingResponseDTO> getAllBookings(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("bookingDate"));
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookings.map(bookingMapper::toDTO);
    }

    @Transactional
    public Page<BookingResponseDTO> getAllBookingsForEvent(Integer eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event does not exist!"));

        Pageable pageable = PageRequest.of(0,10, Sort.by("bookingDate").descending());
        Page<Booking> bookings = bookingRepository.findByEvent(event, pageable);
        return bookings.map(bookingMapper::toDTO);
    }

    @Transactional
    public void cancelBookingByUser (Integer bookingId, Integer userId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking does not exist!"));

        if (!booking.getUser().getId().equals(userId)) throw new UnauthorizedCalcellationException("You can only cancel your own booking");
        if (booking.getStatus() != BookingStatus.CONFIRMED) throw new InvalidCancellationRequestException("Booking cannot be cancelled");

        Event event = booking.getEvent();
        if (event.getStartDateTime().minusHours(24).isBefore(LocalDateTime.now()))
            throw new InvalidCancellationRequestException("You cannot cancel bookings less than 24 hours before event start");

        cancelBooking(booking, event);
    }

    @Transactional
    public BookingResponseDTO cancelBookingByAdmin (Integer bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking does not exist!"));
        Event event = booking.getEvent();
        return cancelBooking(booking, event);
    }

    public BookingResponseDTO cancelBooking(Booking booking, Event event){

        BookingStatus formerStatus = booking.getStatus();
        booking.setStatus(BookingStatus.REFUNDED);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);
        publisher.notifyBookingCancelled(saved);

        if(formerStatus == BookingStatus.CONFIRMED){
            event.setAvailableSeats(event.getAvailableSeats() + booking.getSeatsBooked());

            List<Booking> bookingWaitlist = bookingRepository.findByEventAndStatusOrderByBookingDateAsc(event, BookingStatus.WAITLIST);
            for (Booking waiting : bookingWaitlist){
                if (waiting.getSeatsBooked() <= event.getAvailableSeats()) {
                    waiting.setStatus(BookingStatus.CONFIRMED);
                    event.setAvailableSeats(event.getAvailableSeats() - waiting.getSeatsBooked());
                    bookingRepository.save(waiting);
                    if (event.getAvailableSeats() == 0) break;
                }
            }
            eventRepository.save(event);
        }

        return bookingMapper.toDTO(booking);
    }


}
