package com.lufthansa.internship.payment;

public class BookingPaymentProcessor {

    private final PaymentStrategy paymentStrategy;

    public BookingPaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(double amount){
        paymentStrategy.pay(amount);
    }

}
