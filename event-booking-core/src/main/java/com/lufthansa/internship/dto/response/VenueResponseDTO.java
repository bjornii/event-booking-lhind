package com.lufthansa.internship.dto.response;

import com.lufthansa.internship.entity.CategoryStatus;
import com.lufthansa.internship.entity.VenueStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VenueResponseDTO {

    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotNull
    private VenueStatus status;
    @NotBlank
    private String capacity;
    @NotNull
    private List<String> events;

}
