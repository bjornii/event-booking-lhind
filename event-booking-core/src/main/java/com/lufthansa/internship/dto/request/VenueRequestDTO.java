package com.lufthansa.internship.dto.request;

import com.lufthansa.internship.entity.Event;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VenueRequestDTO {

    @NotBlank @Size(min = 2, max = 30)
    private String name;
    @NotBlank @Size(min = 5, max = 50)
    private String address;
    @NotBlank @Size(min = 2, max = 40)
    private String city;
    @NotNull @Min(1)
    private Integer capacity;

}
