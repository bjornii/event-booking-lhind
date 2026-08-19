package com.lufthansa.internship.dto.response;

import lombok.*;

import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventBookingsDTO {
    private String eventTitle;
    private List<BookingResponseDTO> bookings;
}
