package com.lufthansa.internship.notification;

import com.lufthansa.internship.entity.Booking;
import com.lufthansa.internship.entity.BookingStatus;

public record BookingNotificationEvent(
        Integer bookingId,
        BookingStatus status,
        Integer userId,
        String userEmail,
        String userPhone,
        String eventName,
        Integer seatsBooked
) {
    public static BookingNotificationEvent from(Booking booking) {
        return new BookingNotificationEvent(
                booking.getId(),
                booking.getStatus(),
                booking.getUser().getId(),
                booking.getUser().getEmail(),
                booking.getUser().getPhoneNumber(),
                booking.getEvent().getTitle(),
                booking.getSeatsBooked()
        );
    }
}