package com.lufthansa.internship.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BookingEventPublisher.class);
    private final List<NotificationObserver> observers;

    public BookingEventPublisher(List<NotificationObserver> observers) {
        this.observers = observers;
    }

    public void notifyBookingCreated(BookingNotificationEvent event) {
        for (NotificationObserver observer : observers) {
            try {
                observer.notifyBookingCreated(event);
            } catch (Exception e) {
                log.error("Notification observer {} failed for booking {}",
                        observer.getClass().getSimpleName(), event.bookingId(), e);
            }
        }
    }

    public void notifyBookingCancelled(BookingNotificationEvent event) {
        for (NotificationObserver observer : observers) {
            try {
                observer.notifyBookingCancelled(event);
            } catch (Exception e) {
                log.error("Notification observer {} failed for booking {}",
                        observer.getClass().getSimpleName(), event.bookingId(), e);
            }
        }
    }
}
