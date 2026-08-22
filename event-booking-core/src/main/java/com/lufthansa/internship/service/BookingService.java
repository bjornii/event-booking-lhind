package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.BookingWithPaymentRequestDTO;
import com.lufthansa.internship.dto.request.PaymentRequestDTO;
import com.lufthansa.internship.dto.response.BookingResponseDTO;
import com.lufthansa.internship.dto.response.EventBookingsDTO;
import com.lufthansa.internship.dto.response.PaymentResponseDTO;
import com.lufthansa.internship.entity.*;
import com.lufthansa.internship.exception.*;
import com.lufthansa.internship.mapper.BookingMapper;
import com.lufthansa.internship.notification.BookingEventPublisher;
import com.lufthansa.internship.notification.BookingNotificationEvent;
import com.lufthansa.internship.repository.BookingRepository;
import com.lufthansa.internship.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EventRepository eventRepository;
    private final BookingEventPublisher publisher;
    private final PaymentService paymentService;
    private final BookingTransactionService bookingTransactionService;

    @Transactional
    public BookingResponseDTO createBooking(BookingWithPaymentRequestDTO request){

        if (request.getSeatsBooked() > 6){
            throw new SeatLimitExceedingException("Cannot book more than 6 seats per booking");
        }
        Booking booking = bookingTransactionService.createInitialBooking(request);

        if (booking.getStatus() == BookingStatus.PENDING_PAYMENT) {
            PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                    .bookingId(booking.getId())
                    .amount(request.getAmount())
                    .type(request.getPaymentType())
                    .cardLastFour(request.getCardLastFour())
                    .cardBrand(request.getCardBrand())
                    .build();

            PaymentResponseDTO paymentResponse = paymentService.processPayment(paymentRequest);
            boolean success = paymentResponse.getStatus() == PaymentStatus.SUCCESS;

            booking = bookingTransactionService.finalizeBooking(booking.getId(), success);
        }

        publisher.notifyBookingCreated(BookingNotificationEvent.from(booking));
        return bookingMapper.toDTO(booking);
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
    public Page<BookingResponseDTO> getAllBookings(String sort, Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size, Sort.by(sort));
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        return bookings.map(bookingMapper::toDTO);
    }

    @Transactional
    public Page<BookingResponseDTO> getAllBookingsForEvent(Integer eventId, String sort, Integer page, Integer size){
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
    public void cancelBookingByAdmin (Integer bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking does not exist!"));
        Event event = booking.getEvent();
        cancelBooking(booking, event);
    }


    public void cancelBooking(Booking booking, Event event){

        BookingStatus formerStatus = booking.getStatus();
        booking.setStatus(BookingStatus.REFUNDED);
        booking.setUpdatedAt(LocalDateTime.now());
        Booking saved = bookingRepository.save(booking);
        publisher.notifyBookingCancelled(BookingNotificationEvent.from(saved));

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

        bookingMapper.toDTO(booking);
    }


}
