package com.lufthansa.internship.payment;

import org.springframework.stereotype.Component;

@Component("creditcard")
public class CreditCardPayment implements PaymentStrategy{

    @Override
    public void pay(double amount){
        System.out.println("Paid $" + amount + " using Credit Card");
    }

}
