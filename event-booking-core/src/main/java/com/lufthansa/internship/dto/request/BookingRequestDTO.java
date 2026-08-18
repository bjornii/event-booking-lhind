package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.BookingStatus;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    @NotNull
    private Integer seatsBooked;
    @NotNull
    private LocalDate bookingDate;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer eventId;

}
