package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.Booking;
import com.lufthansa.internship.entity.Event;
import com.lufthansa.internship.entity.Review;
import com.lufthansa.internship.entity.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;

}
