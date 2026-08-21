package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.Payment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByBooking_Id(@NotNull Integer bookingId);
}
