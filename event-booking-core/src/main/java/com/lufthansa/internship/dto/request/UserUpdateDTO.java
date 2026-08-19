package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.UserRole;
import com.lufthansa.internship.entity.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private String username;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Invalid phone number")
    private String phoneNumber;

    private UserRole role;

    private UserStatus status;

}
