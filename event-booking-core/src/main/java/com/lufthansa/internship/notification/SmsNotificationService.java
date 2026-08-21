package com.lufthansa.internship.notification;

import com.lufthansa.internship.entity.Booking;
import org.springframework.stereotype.Component;

@Component("sms")
public class SmsNotificationService implements NotificationObserver{

    @Override
    public void notifyBookingCreated(BookingNotificationEvent event){
        System.out.println("SMS sent for booking: " + event.bookingId());
    }

    @Override
    public void notifyBookingCancelled(BookingNotificationEvent event){
        System.out.println("SMS sent for booking cancelled: " + event.bookingId());
    }

}
