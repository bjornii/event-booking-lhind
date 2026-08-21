package com.lufthansa.internship.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    @NotNull @Min(1)
    private Integer seatsBooked;
    @NotNull @Positive
    private Integer userId;
    @NotNull @Positive
    private Integer eventId;

}
