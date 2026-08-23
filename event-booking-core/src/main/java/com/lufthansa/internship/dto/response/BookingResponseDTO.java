package com.lufthansa.internship.dto.response;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    @Positive
    private Integer id;
    @NotNull @Min(1)
    private Integer seatsBooked;
    @NotBlank
    private String status;
    @NotNull
    private LocalDateTime bookingDate;
    @NotBlank @Size(min = 4, max = 30)
    private String userName;
    @NotBlank @Size(min = 4, max = 30)
    private String eventName;

}
