package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentRequestDTO {

    @NotNull
    private Integer bookingId;

    @NotNull @Positive
    private Double amount;

    @NotNull
    private PaymentType type;

    @Size(min = 4, max = 4)
    private String cardLastFour;

    private String cardBrand;

}