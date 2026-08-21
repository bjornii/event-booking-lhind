package com.lufthansa.internship.notification;

import com.lufthansa.internship.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationService implements NotificationObserver{

    @Override
    public void notifyBookingCreated(Booking booking){
        System.out.println("SMS sent for booking: " + booking.getId());
    }

    @Override
    public void notifyBookingCancelled(Booking booking){
        System.out.println("SMS sent for booking cancelled: " + booking.getId());
    }

}
