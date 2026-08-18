package com.lufthansa.internship.repository;

import com.lufthansa.internship.entity.Booking;
import com.lufthansa.internship.entity.BookingStatus;
import com.lufthansa.internship.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByUserId(Integer userId, Pageable pageable);

    Page<Booking> findByEvent(Event event, Pageable pageable);

    List<Booking> findByEventAndStatusOrderByBookingDateAsc(Event event, BookingStatus bookingStatus);
}
