package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.PaymentStatus;
import com.lufthansa.internship.entity.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {

    private Integer id;

    @NotNull
    private Double amount;

    @NotNull
    private PaymentType paymentType;

    @NotNull
    private PaymentStatus status;

    @Size(min = 4, max = 4)
    private String cardLastFour;

    private String cardBrand;

    @NotNull
    private Integer bookingId;

}
