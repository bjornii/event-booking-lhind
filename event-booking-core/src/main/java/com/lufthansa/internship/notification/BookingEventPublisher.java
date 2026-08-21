package com.lufthansa.internship.notification;

import com.lufthansa.internship.entity.Booking;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingEventPublisher {

    private final List<NotificationObserver> observers;

    public BookingEventPublisher(List<NotificationObserver> observers){
        this.observers = observers;
    }

    public void notifyBookingCreated(Booking booking){
        for (NotificationObserver observer : observers){
            observer.notifyBookingCreated(booking);
        }
    }

    public void notifyBookingCancelled(Booking booking){
        for (NotificationObserver observer : observers){
            observer.notifyBookingCancelled(booking);
        }
    }

}
