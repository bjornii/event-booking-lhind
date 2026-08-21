package com.lufthansa.internship.notification;

import com.lufthansa.internship.entity.Booking;

public interface NotificationObserver {
    void notifyBookingCreated(BookingNotificationEvent event);
    void notifyBookingCancelled(BookingNotificationEvent event);
}
