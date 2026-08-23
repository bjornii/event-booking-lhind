package com.lufthansa.internship.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDTO {

    @NotBlank @Size(min = 2, max = 30)
    private String firstName;
    @NotBlank @Size(min = 2, max = 30)
    private String lastName;
    @NotBlank @Size(min = 2, max = 20)
    private String username;
    @NotBlank @Email
    private String email;
    @NotBlank  @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Invalid phone number")
    private String phoneNumber;
    @NotBlank @Size(min = 8, max = 64)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain upper, lower, digit, and special character")
    private String password;

}
