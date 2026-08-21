package com.lufthansa.internship.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventBookingsDTO {
    @NotBlank @Size(min = 4, max = 30)
    private String eventTitle;
    private List<BookingResponseDTO> bookings = new ArrayList<>();
}
