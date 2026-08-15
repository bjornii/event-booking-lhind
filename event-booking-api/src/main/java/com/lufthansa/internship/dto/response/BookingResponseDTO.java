package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.BookingStatus;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {

    @NotNull
    private Integer seatsBooked;
    @NotBlank
    private String status;
    @NotNull
    private LocalDate bookingDate;
    @NotBlank
    private String userName;
    @NotBlank
    private String eventName;

}
