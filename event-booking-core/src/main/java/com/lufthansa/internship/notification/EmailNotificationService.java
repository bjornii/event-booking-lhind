package com.lufthansa.internship.notification;

import org.springframework.stereotype.Component;

@Component("email")
public class EmailNotificationService implements NotificationObserver{

    @Override
    public void notifyBookingCreated(BookingNotificationEvent event){
        System.out.println("Email sent for booking: " + event.bookingId());
    }

    @Override
    public void notifyBookingCancelled(BookingNotificationEvent event){
        System.out.println("Email sent for booking cancelled: " + event.bookingId());
    }

}
