package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingWithPaymentRequestDTO {

    @NotNull @Min(1)
    private Integer seatsBooked;

    @NotNull @Positive
    private Integer eventId;

    @NotNull @Positive
    private Double amount;

    @NotNull
    private PaymentType paymentType;

    @Size(min = 4, max = 4)
    private String cardLastFour;

    private String cardBrand;
}
