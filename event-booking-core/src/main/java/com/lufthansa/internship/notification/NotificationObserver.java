package com.lufthansa.internship.notification;

import com.lufthansa.internship.entity.Booking;

public interface NotificationObserver {
    void notifyBookingCreated(Booking booking);
    void notifyBookingCancelled(Booking booking);
}
