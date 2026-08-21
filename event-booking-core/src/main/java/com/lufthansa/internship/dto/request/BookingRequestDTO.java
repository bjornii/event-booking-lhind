package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.BookingStatus;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import jakarta.persistence.*;
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
