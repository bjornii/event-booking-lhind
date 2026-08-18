package com.lufthansa.internship.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    private String role;
    @NotNull
    private List<String> events;
    @NotNull
    private List<BookingResponseDTO> bookings;
    @NotNull
    private List<ReviewResponseDTO> reviews;

}
