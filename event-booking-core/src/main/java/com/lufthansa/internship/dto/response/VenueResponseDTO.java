package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.VenueStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VenueResponseDTO {

    @Positive
    private Integer id;
    @NotBlank @Size(min = 4, max = 30)
    private String name;
    @NotBlank @Size(min = 4, max = 50)
    private String address;
    @NotBlank @Size(min = 3, max = 30)
    private String city;
    @NotNull
    private VenueStatus status;
    @NotBlank @Min(1)
    private String capacity;
    @NotNull
    private List<String> events;

}
