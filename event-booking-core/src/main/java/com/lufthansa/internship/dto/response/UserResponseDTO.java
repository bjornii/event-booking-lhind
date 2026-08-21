package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    @Positive
    private Integer id;
    @NotBlank @Size(min = 4, max = 30)
    private String username;
    @NotBlank @Email
    private String email;
    @NotBlank
    private UserRole role;
    @NotNull
    private List<String> events;
    @NotNull
    private List<BookingResponseDTO> bookings;
    @NotNull
    private List<ReviewResponseDTO> reviews;

}
