package com.lufthansa.internship.service;

import com.lufthansa.internship.dto.request.PaymentRequestDTO;
import com.lufthansa.internship.dto.response.PaymentResponseDTO;
import com.lufthansa.internship.entity.Booking;
import com.lufthansa.internship.entity.Payment;
import com.lufthansa.internship.entity.PaymentStatus;
import com.lufthansa.internship.exception.BookingNotFoundException;
import com.lufthansa.internship.exception.UnsupportedPaymentTypeException;
import com.lufthansa.internship.payment.PaymentStrategy;
import com.lufthansa.internship.repository.BookingRepository;
import com.lufthansa.internship.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final Map<String, PaymentStrategy> strategies;

    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        PaymentStrategy strategy = strategies.get(request.getType().name().toLowerCase());
        if (strategy == null) {
            log.warn("Unsupported payment type requested: {}", request.getType());
            throw new UnsupportedPaymentTypeException("No strategy for payment type: " + request.getType());
        }
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(request.getAmount());
        payment.setPaymentType(request.getType());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCardLastFour(request.getCardLastFour());
        payment.setCardBrand(request.getCardBrand());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        log.debug("Payment record created for booking {}, amount {}", booking.getId(), request.getAmount());

        try {
            strategy.pay(request.getAmount());
            payment.setStatus(PaymentStatus.SUCCESS);
            log.info("Payment succeeded for booking {} via {}", booking.getId(), request.getType());
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            log.warn("Payment failed for booking {} via {}: {}", booking.getId(), request.getType(), e.getMessage());
        }

        Payment saved = paymentRepository.save(payment);
        return PaymentResponseDTO.builder()
                .id(saved.getId())
                .amount(saved.getAmount())
                .paymentType(saved.getPaymentType())
                .status(saved.getStatus())
                .cardLastFour(saved.getCardLastFour())
                .cardBrand(saved.getCardBrand())
                .bookingId(saved.getBooking().getId())
                .build();
    }
}